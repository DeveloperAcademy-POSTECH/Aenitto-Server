package com.firefighter.aenitto.common.log;

import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

  @Override
  protected boolean shouldLog(@NotNull HttpServletRequest request) {
    return logger.isInfoEnabled();
  }

  @Override
  protected void beforeRequest(@NotNull HttpServletRequest request, @NotNull String message) {
    logger.info(message);
  }

  @Override
  protected void afterRequest(@NotNull HttpServletRequest request, @NotNull String message) {
  }
}
