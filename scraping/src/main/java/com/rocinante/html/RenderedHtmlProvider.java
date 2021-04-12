package com.rocinante.html;

import com.rocinante.util.UrlUtils;
import com.rocinante.util.UserAgentProvider;
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
/*
 This class stores and uses an instance of the Chrome driver from the ThreadLocal, so it should
 be used from a smaller dedicated thread-pool.
*/
public class RenderedHtmlProvider {
  private final ThreadLocal<WebDriver> webDriverThreadLocal;
  private final UserAgentProvider userAgentProvider;

  public RenderedHtmlProvider(String proxyServer, String chromeDriverPath) {
    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    this.userAgentProvider = new UserAgentProvider();
    this.webDriverThreadLocal =
        new ThreadLocal<>() {
          private WebDriver initWebDriver() {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                "--headless",
                "--window-size=1920,1080",
                "--ignore-certificate-errors",
                "--silent",
                "--enable-javascript",
                String.format("--proxy-server=%s", proxyServer),
                String.format("--user-agent=%s", userAgentProvider.getRandomUserAgent()));
            return new ChromeDriver(options);
          }

          @Override
          protected WebDriver initialValue() {
            return initWebDriver();
          }

          @Override
          public void remove() {
            WebDriver driver = get();
            if (driver != null) driver.quit();
            super.remove();
          }

          @Override
          public void set(WebDriver value) {
            throw new UnsupportedOperationException();
          }
        };
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
        Thread.sleep(2000L);
        js.executeScript(String.format("window.scrollTo(0, %s);", currentScrollTo));
        currentScrollTo = currentScrollTo + scrollInterval;
      } while (currentScrollTo < scrollTo && scrollInterval > 0);

      initScrollTo = scrollTo;
      scrollTo = (long) js.executeScript("return document.body.scrollHeight;");
      Thread.sleep(3000L);
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
            UrlUtils.extractImageUrlsFromSrcSet(srcSet).stream()
                .map(UrlUtils::getUrlOrUriStringRepresentation)
                .forEach(uriString -> result.put(uriString, dimensions));
          }
          if (src != null && !src.isBlank()) {
            result.put(UrlUtils.getUrlOrUriStringRepresentation(src), dimensions);
          }
        });
    return result;
  }

  public RenderedHtml downloadHtml(String url) {
    log.info("Downloading html content from {}", url);
    final WebDriver webDriver = webDriverThreadLocal.get();
    webDriver.get(url);
    try {
      Thread.sleep(3000L); // Wait for any init JS to execute
      scrollToBottom(webDriver);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    final Map<String, int[]> imageDimensionsMap = getImagePathsToRenderedDimensionsMap(webDriver);
    final RenderedHtml renderedHtml =
        new RenderedHtml(webDriver.getPageSource(), imageDimensionsMap);
    log.info("Current web-driver window handles count {}", webDriver.getWindowHandles().size());
    if (webDriver.getWindowHandles().size() > 2) {
      log.info("Closing web-driver window {}", url);
      webDriver.close();
    } else {
      log.info("Leaving web-driver window open {}", url);
    }
    log.info("Finished downloading html content from {}", url);
    return renderedHtml;
  }
}
