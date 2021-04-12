package com.rocinante.shops.config;

import com.rocinante.shops.interceptors.ApiKeyAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  private final ApiKeyAuthInterceptor apiKeyAuthInterceptor;

  public WebMvcConfig(ApiKeyAuthInterceptor apiKeyAuthInterceptor) {
    this.apiKeyAuthInterceptor = apiKeyAuthInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(apiKeyAuthInterceptor)
        .addPathPatterns("/bootstrap/**", "/crawl/**", "/index/**", "/merchant/**", "/product/**");
  }
}
