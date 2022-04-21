package com.freedom.httpclientmodule.httpclient.store;

import com.freedom.httpclientmodule.httpclient.exceptions.HttpClientNotRegisteredException;
import com.freedom.httpclientmodule.httpclient.model.HttpClientProperties;
import com.freedom.httpclientmodule.httpclient.template.factory.HttpClientFactory;
import com.freedom.httpclientmodule.httpclient.template.properties.RestTemplatePropertiesSpecification;
import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HttpClientStore {

  private Map<String, HttpClientProperties> cache = new HashMap<>();

  @Autowired private HttpClientFactory httpClientFactory;
  private ScheduledExecutorService shutDownExecutors = Executors.newScheduledThreadPool(10);

  /**
   * @param clientName
   * @param templateProperties
   * @return boolean value , whether client was actually updated or not.
   */
  public boolean updateRestTemplateForClient(
      String clientName, RestTemplatePropertiesSpecification templateProperties) {

    if (cache.get(clientName) == null) {

      updateClientInCache(clientName, templateProperties);
      return true;

    } else if (!cache.get(clientName).getTemplateProperties().ifEquals(templateProperties)) {

      HttpClientProperties oldClientProperties = cache.get(clientName);
      updateClientInCache(clientName, templateProperties);
      destroyOldClient(oldClientProperties);
      return true;

    } else {

      return false;
    }
  }

  public RestTemplate getRestTemplateForClient(String clientName) {

    HttpClientProperties clientProperties = cache.get(clientName);
    if (clientProperties != null) {
      return clientProperties.getRestTemplate();
    }

    throw new HttpClientNotRegisteredException(clientName);
  }

  private void updateClientInCache(
      String clientName, RestTemplatePropertiesSpecification templateProperties) {

    HttpClientProperties client = httpClientFactory.createHttpClient(templateProperties);
    cache.put(clientName, client);
  }

  private void destroyOldClient(HttpClientProperties clientProperties) {

    int readTimeout = clientProperties.getTemplateProperties().getReadTimeout();

    HttpClientConnectionManager connManager = clientProperties.getConnManager();
    shutDownExecutors.schedule(connManager::shutdown, 2l * readTimeout, TimeUnit.MILLISECONDS);
  }
}
