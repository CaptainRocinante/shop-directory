package com.rocinante.crawlers.infrastructure;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class RenderedHtmlProvider {
  private final WebDriver webDriver;

  private WebDriver initWebDriver(UserAgentProvider userAgentProvider) {
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
    this.webDriver = initWebDriver(new UserAgentProvider());
  }

  private void scrollToBottom() throws InterruptedException {
    final JavascriptExecutor js = (JavascriptExecutor) webDriver;

    int scrollJitterBudget = 3;
    int currentScrollJitter = 0;
    int maxScrollBudget = 25;
    int currentScrollCount = 0;
    long initScrollTo = 0;
    long scrollTo = (long) js.executeScript("return document.body.scrollHeight;");

    while (currentScrollCount < maxScrollBudget &&
        (scrollTo != initScrollTo || currentScrollJitter < scrollJitterBudget)) {
      initScrollTo = scrollTo;
      System.out.printf("Scrolling to %s\n", scrollTo);
      js.executeScript(String.format("window.scrollTo(0, %s);", scrollTo));
      scrollTo = (long) js.executeScript("return document.body.scrollHeight;");
      Thread.sleep(500L);
      ++currentScrollCount;

      if (scrollTo != initScrollTo) {
        currentScrollJitter = 0;
      } else {
        ++currentScrollJitter;
      }
    }
  }

  public String downloadHtml(String url) throws InterruptedException {
    webDriver.get(url);
    Thread.sleep(2000L); // Wait for any init JS to execute
    scrollToBottom();
    final String pageSource = webDriver.getPageSource();
    webDriver.close();
    webDriver.quit();
    return pageSource;
  }
}
