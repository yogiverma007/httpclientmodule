package com.freedom.httpclientmodule.httpclient.test.store;

import com.freedom.httpclientmodule.httpclient.exceptions.HttpClientNotRegisteredException;
import com.freedom.httpclientmodule.httpclient.store.HttpClientStore;
import com.freedom.httpclientmodule.httpclient.template.factory.HttpClientFactory;
import com.freedom.httpclientmodule.httpclient.template.properties.RestTemplatePropertiesSpecification;
import com.freedom.httpclientmodule.httpclient.template.properties.builder.RestTemplatePropertiesBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientStoreTest {

  @InjectMocks private HttpClientStore httpClientStore;

  @Mock private HttpClientFactory httpClientFactory;

  @Test
  public void
      updateRestTemplateForClient_ClientPropertiesNotPresentInCache_CacheUpdatedWithClientPropertiesAndTrueResponse() {
    String clientName = "CHECKOUT";
    RestTemplatePropertiesSpecification templateProperties =
        getRestTemplatePropertiesSpecification(clientName);
    doCallRealMethod().when(httpClientFactory).createHttpClient(templateProperties);
    assertTrue(httpClientStore.updateRestTemplateForClient(clientName, templateProperties));
  }

  @Test
  public void
      updateRestTemplateForClient_ClientPropertiesPresentInCacheAndPropertiesNotUpdated_CacheNotUpdatedAndFalseResponse() {
    String clientName = "CHECKOUT";
    RestTemplatePropertiesSpecification templateProperties =
        getRestTemplatePropertiesSpecification(clientName);
    doCallRealMethod().when(httpClientFactory).createHttpClient(templateProperties);
    assertTrue(httpClientStore.updateRestTemplateForClient(clientName, templateProperties));
    assertFalse(httpClientStore.updateRestTemplateForClient(clientName, templateProperties));
  }

  @Test
  public void
      updateRestTemplateForClient_ClientPropertiesPresentInCacheAndPropertiesUpdated_CacheUpdatedAndTrueResponse() {
    String clientName = "CHECKOUT";
    RestTemplatePropertiesSpecification templateProperties =
        getRestTemplatePropertiesSpecification(clientName);
    doCallRealMethod().when(httpClientFactory).createHttpClient(templateProperties);
    assertTrue(httpClientStore.updateRestTemplateForClient(clientName, templateProperties));
    assertTrue(httpClientStore.updateRestTemplateForClient(clientName, null));
  }

  @Test(expected = HttpClientNotRegisteredException.class)
  public void
      getRestTemplateForClient_ClientPropertiesNotPresentInCache_HttpClientNotRegisteredException() {
    String clientName = "CHECKOUT";
    httpClientStore.getRestTemplateForClient(clientName);
  }

  @Test
  public void getRestTemplateForClient_ClientPropertiesPresentInCache_PropertiesReturned() {
    String clientName = "CHECKOUT";
    RestTemplatePropertiesSpecification templateProperties =
        getRestTemplatePropertiesSpecification(clientName);
    doCallRealMethod().when(httpClientFactory).createHttpClient(templateProperties);
    httpClientStore.updateRestTemplateForClient(clientName, templateProperties);
    assertNotNull(httpClientStore.getRestTemplateForClient(clientName));
  }

  private RestTemplatePropertiesSpecification getRestTemplatePropertiesSpecification(
      String clientName) {
    RestTemplatePropertiesBuilder templatePropertiesBuilder =
        RestTemplatePropertiesBuilder.createFor(clientName);
    return templatePropertiesBuilder.build();
  }
}
