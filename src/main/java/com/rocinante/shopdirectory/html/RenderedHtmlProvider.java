package com.rocinante.shopdirectory.html;

import com.rocinante.shopdirectory.util.HtmlUtils;
import com.rocinante.shopdirectory.util.UserAgentProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
public class RenderedHtmlProvider {
  private final UserAgentProvider userAgentProvider;

  private WebDriver initWebDriver() {
    String chromeDriverPath = "/usr/local/bin/chromedriver";
    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    ChromeOptions options = new ChromeOptions();
    options.addArguments(
        "--headless",
        "--window-size=1920,1080",
        "--ignore-certificate-errors",
        "--silent",
        "--enable-javascript",
        String.format("--user-agent=%s", userAgentProvider.getRandomUserAgent()));
    return new ChromeDriver(options);
  }

  public RenderedHtmlProvider() {
    this.userAgentProvider = new UserAgentProvider();
  }

  private void scrollToBottom(WebDriver webDriver) throws InterruptedException {
    final JavascriptExecutor js = (JavascriptExecutor) webDriver;

    int scrollJitterBudget = 3;
    int currentScrollJitter = 0;
    int maxScrollBudget = 25;
    int currentScrollCount = 0;
    long initScrollTo = 0;
    long scrollTo = (long) js.executeScript("return document.body.scrollHeight;");

    while (currentScrollCount < maxScrollBudget
        && (scrollTo > initScrollTo || currentScrollJitter < scrollJitterBudget)) {
      long scrollInterval = (scrollTo - initScrollTo) / 10;
      long currentScrollTo = initScrollTo + scrollInterval;
      do {
        log.info("Scrolling to {}", currentScrollTo);
        Thread.sleep(1000L);
        js.executeScript(String.format("window.scrollTo(0, %s);", currentScrollTo));
        currentScrollTo = currentScrollTo + scrollInterval;
      } while (currentScrollTo < scrollTo && scrollInterval > 0);

      initScrollTo = scrollTo;
      scrollTo = (long) js.executeScript("return document.body.scrollHeight;");
      Thread.sleep(2000L);
      ++currentScrollCount;

      if (scrollTo != initScrollTo) {
        currentScrollJitter = 0;
      } else {
        ++currentScrollJitter;
      }
    }
  }

  private Map<String, int[]> getImagePathsToRenderedDimensionsMap(WebDriver webDriver) {
    final List<WebElement> img = webDriver.findElements(By.tagName("img"));
    final Map<String, int[]> result = new HashMap<>();
    img.forEach(
        webElement -> {
          final int height = webElement.getSize().height;
          final int width = webElement.getSize().width;
          final int[] dimensions = new int[] {height, width};
          final String srcSet = webElement.getAttribute("srcset");
          final String src = webElement.getAttribute("src");
          if (srcSet != null && !srcSet.isBlank()) {
            HtmlUtils.extractImageUrlsFromSrcSet(srcSet).stream()
                .map(
                    s -> {
                      try {
                        return new URI(s);
                      } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                      }
                    })
                .forEach(uri -> result.put(HtmlUtils.normalizedUriRepresentation(uri), dimensions));
          }
          if (src != null && !src.isBlank()) {
            try {
              final URI uri = new URI(src);
              result.put(HtmlUtils.normalizedUriRepresentation(uri), dimensions);
            } catch (URISyntaxException e) {
              throw new RuntimeException(e);
            }
          }
        });
    return result;
  }

  public RenderedHtml downloadHtml(String url) {
    final WebDriver webDriver = initWebDriver();
    webDriver.get(url);
    try {
      Thread.sleep(2000L); // Wait for any init JS to execute
      scrollToBottom(webDriver);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    final Map<String, int[]> imageDimensionsMap = getImagePathsToRenderedDimensionsMap(webDriver);
    final RenderedHtml renderedHtml =
        new RenderedHtml(webDriver.getPageSource(), imageDimensionsMap);
    webDriver.close();
    webDriver.quit();
    return renderedHtml;
  }
}
