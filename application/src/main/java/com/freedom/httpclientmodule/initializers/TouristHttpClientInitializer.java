package com.freedom.httpclientmodule.initializers;

import com.freedom.httpclientmodule.httpclient.initializer.HttpClientInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.freedom.httpclientmodule.constants.Enums.HTTP_CLIENTS.TOURIST;

@Slf4j
@Component
public class TouristHttpClientInitializer extends HttpClientInitializer {

  private final Environment environment;

  @Autowired
  public TouristHttpClientInitializer(Environment environment) {
    this.environment = environment;
  }

  @PostConstruct
  private void init() {
    refreshClient();
  }

  // refresh this http client from some scheduler to refresh client according to your requirements.
  public void refreshClient() {
    log.info("Refreshing TOURIST - Http Client");
    initializeHttpClient(TOURIST.name());
    log.info("Http Client TOURIST refreshed");
  }

  @Override
  public String providePropertyValue(String propertyName) {
    // return value for all the properties from your data source: DB/CACHE/SPRING, etc
    return environment.getProperty(propertyName);
  }
}
