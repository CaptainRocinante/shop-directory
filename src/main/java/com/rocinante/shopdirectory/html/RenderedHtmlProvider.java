package com.rocinante.shopdirectory.html;

import com.rocinante.shopdirectory.util.UserAgentProvider;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class RenderedHtmlProvider {
  private final UserAgentProvider userAgentProvider;

  private WebDriver initWebDriver() {
    String chromeDriverPath = "/usr/local/bin/chromedriver" ;
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

    while (currentScrollCount < maxScrollBudget &&
        (scrollTo > initScrollTo || currentScrollJitter < scrollJitterBudget)) {
      long scrollInterval = (scrollTo - initScrollTo) / 10;
      long currentScrollTo = initScrollTo + scrollInterval;
      do {
        System.out.printf("Scrolling to %s\n", currentScrollTo);
        Thread.sleep(100L);
        js.executeScript(String.format("window.scrollTo(0, %s);", currentScrollTo));
        currentScrollTo = currentScrollTo + scrollInterval;
      } while (currentScrollTo < scrollTo && scrollInterval > 0);

      initScrollTo = scrollTo;
      scrollTo = (long) js.executeScript("return document.body.scrollHeight;");
      Thread.sleep(200L);
      ++currentScrollCount;

      if (scrollTo != initScrollTo) {
        currentScrollJitter = 0;
      } else {
        ++currentScrollJitter;
      }
    }
  }

  public Map<String, int[]> getImageSrcToRenderedDimensionsMap(WebDriver webDriver) {
    final List<WebElement> img = webDriver.findElements(By.tagName("img"));
    return img
        .stream()
        .filter(webElement -> {
          final String src = webElement.getAttribute("src");
          return src != null && !src.isBlank();
        })
        .collect(Collectors.toMap(we -> we.getAttribute("src"), we -> {
          final Dimension d = we.getSize();
          return new int[] {d.height, d.width};
        }, (v1, v2) -> {
          if (v1[0] * v1[1] >= v2[0] * v2[1]) {
            return v1;
          } else {
            return v2;
          }
        }));
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
    final RenderedHtml renderedHtml = new RenderedHtml(webDriver.getPageSource(),
        getImageSrcToRenderedDimensionsMap(webDriver));
    webDriver.close();
    webDriver.quit();
    return renderedHtml;
  }
}
