package com.rocinante.shops.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyAuthInterceptor implements HandlerInterceptor {
  private static final String API_KEY_HEADER = "apiKey";
  private final String hashedApiKey;

  public ApiKeyAuthInterceptor(@Value("${api.key}") String apiKey) {
    this.hashedApiKey = apiKey;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    final String apiKeySent = request.getHeader(API_KEY_HEADER);
    if (StringUtils.isAllBlank(apiKeySent)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing API Key");
    }
    if (!BCrypt.checkpw(apiKeySent, hashedApiKey)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API Key");
    }
    return true;
  }
}
