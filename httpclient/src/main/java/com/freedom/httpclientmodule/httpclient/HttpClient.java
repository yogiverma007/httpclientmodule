package com.freedom.httpclientmodule.httpclient;

import com.freedom.httpclientmodule.httpclient.exceptions.UrlSyntaxException;
import com.freedom.httpclientmodule.httpclient.store.HttpClientStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

@Service
@Slf4j
public class HttpClient {

  @Autowired private HttpClientStore clientsStore;

  public <A, B> B request(
      String clientName,
      HttpMethod httpMethod,
      String completeUrl,
      A body,
      Map<String, String> headers,
      Class<B> responseType) {

    log.info(
        "ClientName : {}, Url : {} , Request Object : {}, Headers : {}",
        clientName,
        completeUrl,
        body,
        headers);

    RestTemplate template = clientsStore.getRestTemplateForClient(clientName);

    ResponseEntity<B> responseEntity;
    try {
      URI url = new URI(completeUrl);

      HttpHeaders headersMultiValueMap = new HttpHeaders();

      if (!CollectionUtils.isEmpty(headers)) {
        for (Entry<String, String> headerEntry : headers.entrySet()) {
          headersMultiValueMap.add(headerEntry.getKey(), headerEntry.getValue());
        }
      }

      RequestEntity<A> requestEntity =
          new RequestEntity<>(body, headersMultiValueMap, httpMethod, url);

      responseEntity = template.exchange(requestEntity, responseType);
      log.info("responseEntity {}", responseEntity);

    } catch (HttpStatusCodeException e) {
      log.error("Http Status Code Error: ", e.getRawStatusCode(), e.getResponseBodyAsString());
      throw e;

    } catch (URISyntaxException e) {
      log.error("Wrong URI Syntax: ", e);
      throw new UrlSyntaxException(completeUrl);

    } catch (ResourceAccessException e) {
      log.error("Http ResourceAccessException :  ", e.getMostSpecificCause());
      throw e;

    } catch (Exception e) {
      log.error("Http Error: {} {}", e.getMessage());
      throw e;
    }

    return responseEntity.getBody();
  }
}
