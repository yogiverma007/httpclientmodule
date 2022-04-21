package com.freedom.httpclientmodule.httpclient.test;

import com.freedom.httpclientmodule.httpclient.HttpClient;
import com.freedom.httpclientmodule.httpclient.exceptions.UrlSyntaxException;
import com.freedom.httpclientmodule.httpclient.store.HttpClientStore;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientTest {

  @InjectMocks private HttpClient httpClient;

  @Mock private HttpClientStore httpClientStore;

  @Mock private RestTemplate restTemplate;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void request_HttpStatusCodeException_ExceptionThrown() {
    Object body = new Object();
    String url = "/mock/url";
    RequestEntity<Object> requestEntity = getRequestEntity(body, url);
    when(httpClientStore.getRestTemplateForClient(any())).thenReturn(restTemplate);
    doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
        .when(restTemplate)
        .exchange(requestEntity, Object.class);
    expectedException.expect(HttpStatusCodeException.class);
    httpClient.request(StringUtils.EMPTY, HttpMethod.POST, url, body, null, Object.class);
  }

  @Test
  public void request_ResourceAccessException_ExceptionThrown() {
    Object body = new Object();
    String url = "/mock/url";
    ResourceAccessException resourceAccessException =
        new ResourceAccessException("read time out", new SocketTimeoutException());
    RequestEntity<Object> requestEntity = getRequestEntity(body, url);
    when(httpClientStore.getRestTemplateForClient(any())).thenReturn(restTemplate);
    doThrow(resourceAccessException).when(restTemplate).exchange(requestEntity, Object.class);
    expectedException.expect(ResourceAccessException.class);
    httpClient.request(StringUtils.EMPTY, HttpMethod.POST, url, body, null, Object.class);
  }

  @Test(expected = UrlSyntaxException.class)
  public void request_UrlSyntaxException_ExceptionThrown() {
    Object body = new Object();
    String url = "/mock/url";
    String invalidUrl = "http://www. google.com/";
    RequestEntity<Object> requestEntity = getRequestEntity(body, url);
    when(httpClientStore.getRestTemplateForClient(any())).thenReturn(restTemplate);
    httpClient.request(StringUtils.EMPTY, HttpMethod.POST, invalidUrl, body, null, Object.class);
  }

  @Test(expected = RuntimeException.class)
  public void request_RuntimeException_ExceptionThrown() {
    Object body = new Object();
    String url = "/mock/url";
    RequestEntity<Object> requestEntity = getRequestEntity(body, url);
    when(httpClientStore.getRestTemplateForClient(any())).thenReturn(restTemplate);
    doThrow(new RuntimeException(url)).when(restTemplate).exchange(requestEntity, Object.class);
    httpClient.request(StringUtils.EMPTY, HttpMethod.POST, url, body, null, Object.class);
  }

  @Test
  public void request_SuccessResponse_SuccessResponseReturned() {
    Object body = new Object();
    String url = "/mock/url";
    RequestEntity<Object> requestEntity = getRequestEntity(body, url);
    ResponseEntity<Object> responseEntity = getResponseEntity();
    when(httpClientStore.getRestTemplateForClient(any())).thenReturn(restTemplate);
    when(restTemplate.exchange(requestEntity, Object.class)).thenReturn(responseEntity);
    assertNotNull(
        httpClient.request(StringUtils.EMPTY, HttpMethod.POST, url, body, null, Object.class));
  }

  private ResponseEntity<Object> getResponseEntity() {
    return new ResponseEntity<>("success", HttpStatus.ACCEPTED);
  }

  private RequestEntity<Object> getRequestEntity(Object body, String url) {
    RequestEntity<Object> requestEntity;
    try {
      requestEntity = new RequestEntity<>(body, null, HttpMethod.POST, new URI(url));
    } catch (URISyntaxException e) {
      return null;
    }
    return requestEntity;
  }
}
