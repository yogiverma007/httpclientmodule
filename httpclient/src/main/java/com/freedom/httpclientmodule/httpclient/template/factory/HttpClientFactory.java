package com.freedom.httpclientmodule.httpclient.template.factory;

import com.freedom.httpclientmodule.httpclient.model.HttpClientProperties;
import com.freedom.httpclientmodule.httpclient.model.RestExceptionHandler;
import com.freedom.httpclientmodule.httpclient.template.properties.RestTemplatePropertiesSpecification;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class HttpClientFactory {

  private static List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

  @PostConstruct
  public void init() {
    messageConverters.add(new FormHttpMessageConverter());
  }

  public HttpClientProperties createHttpClient(
      RestTemplatePropertiesSpecification templateProperties) {

    PoolingHttpClientConnectionManager connectionManager = getConnectionManager(templateProperties);
    RequestConfig config = getRequestConfig(templateProperties);
    ClientHttpRequestFactory requestFactory = createRequestFactory(connectionManager, config);

    RestTemplate restTemplate = new RestTemplate(requestFactory);
    restTemplate.getMessageConverters().addAll(messageConverters);
    restTemplate.setErrorHandler(getRestExceptionHandler());

    HttpClientProperties clientProperties = new HttpClientProperties();
    clientProperties.setClientName(templateProperties.getClientName());
    clientProperties.setRestTemplate(restTemplate);
    clientProperties.setTemplateProperties(templateProperties);
    clientProperties.setConnManager(connectionManager);

    return clientProperties;
  }

  private PoolingHttpClientConnectionManager getConnectionManager(
      RestTemplatePropertiesSpecification templateProperties) {

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(templateProperties.getMaxTotalConnection());
    connectionManager.setDefaultMaxPerRoute(templateProperties.getMaxPerChannel());
    connectionManager.setValidateAfterInactivity(
        templateProperties.getStaleConnectionCheckAfterInactivityPeriod());

    return connectionManager;
  }

  private RequestConfig getRequestConfig(RestTemplatePropertiesSpecification templateProperties) {

    return RequestConfig.custom()
        .setConnectTimeout(templateProperties.getConnectionTimeout())
        .setConnectionRequestTimeout(templateProperties.getConnectionRequestTimeout())
        .setSocketTimeout(templateProperties.getReadTimeout())
        .build();
  }

  private ClientHttpRequestFactory createRequestFactory(
      HttpClientConnectionManager connectionManager, RequestConfig config) {

    CloseableHttpClient httpClient =
        HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(config)
            .disableCookieManagement()
            .build();

    return new HttpComponentsClientHttpRequestFactory(httpClient);
  }

  private RestExceptionHandler getRestExceptionHandler() {
    return new RestExceptionHandler();
  }
}
