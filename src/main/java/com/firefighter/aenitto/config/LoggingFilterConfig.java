package com.firefighter.aenitto.config;

import com.firefighter.aenitto.common.log.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingFilterConfig {

  @Bean
  public RequestLoggingFilter loggingFilter() {
    RequestLoggingFilter loggingFilter = new RequestLoggingFilter();
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setMaxPayloadLength(1000);
    loggingFilter.setBeforeMessagePrefix("Request [");
    return loggingFilter;
  }
}
