package com.freedom.httpclientmodule.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties("tourist")
public class TouristConfig {

  private String fetchTouristsUrl;
  private String createTouristsUrl;
}
