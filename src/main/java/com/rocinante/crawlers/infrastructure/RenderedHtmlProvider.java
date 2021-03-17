package com.rocinante.crawlers.infrastructure;

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

  public String downloadHtml(String url) throws InterruptedException {
    webDriver.get(url);
    Thread.sleep(5000L);
    return webDriver.getPageSource();
  }
}
