package com.freedom.httpclientmodule.client;

import com.freedom.httpclientmodule.config.TouristConfig;
import com.freedom.httpclientmodule.dto.CreateTouristRequest;
import com.freedom.httpclientmodule.dto.Tourist;
import com.freedom.httpclientmodule.dto.TouristResponse;
import com.freedom.httpclientmodule.httpclient.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import static com.freedom.httpclientmodule.constants.Enums.HTTP_CLIENTS.TOURIST;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class TouristHttpClient {

  private static final long HTTP_CLIENT_COMMUNICATION_RETRY_BACKOFF = 1000l;

  private final HttpClient httpClient;
  private final TouristConfig touristConfig;

  @Autowired
  public TouristHttpClient(HttpClient httpClient, TouristConfig touristConfig) {
    this.httpClient = httpClient;
    this.touristConfig = touristConfig;
  }

  @Retryable(
      value = {Exception.class},
      maxAttemptsExpression = "3",
      backoff = @Backoff(delay = HTTP_CLIENT_COMMUNICATION_RETRY_BACKOFF),
      listeners = "genericReadTimeoutRetryHttpListener",
      recover = "createTouristRecover")
  public Tourist createTourist(CreateTouristRequest requestDto) {
    log.info("Calling create tourist api");

    Map<String, String> headers = createHeaders();
    return httpClient.request(
        TOURIST.name(),
        POST,
        touristConfig.getCreateTouristsUrl(),
        requestDto,
        headers,
        Tourist.class);
  }

  @Recover
  private String createTouristRecover(Throwable throwable, CreateTouristRequest requestDto) {
    log.info("Reached recover method for create tourist");
    if (throwable instanceof ResourceAccessException
        && throwable.getCause() instanceof SocketTimeoutException) {
      log.info("CREATE_TOURIST_READ_TIMEOUT: {}");
    } else if (throwable instanceof ResourceAccessException
        && (throwable.getCause() instanceof ConnectTimeoutException
            || throwable.getCause() instanceof ConnectException)) {
      log.info("CREATE_TOURIST_CONNECTION_FAILED: {}");
    }

    if (throwable instanceof HttpClientErrorException) {
      // 4xx
      HttpClientErrorException exception = (HttpClientErrorException) throwable;
      log.info("CREATE_TOURIST_FAILED_{}_ERROR", exception.getRawStatusCode());

    } else if (throwable instanceof HttpServerErrorException) {
      // 5xx
      HttpServerErrorException exception = (HttpServerErrorException) throwable;
      log.info("CREATE_TOURIST_FAILED_{}_ERROR", exception.getRawStatusCode());
    } else {
      log.info("CREATE_TOURIST_FAILED");
    }

    return null;
  }

  @Retryable(
      value = {Exception.class},
      maxAttemptsExpression = "3",
      backoff = @Backoff(delay = HTTP_CLIENT_COMMUNICATION_RETRY_BACKOFF),
      listeners = "genericReadTimeoutRetryHttpListener",
      recover = "getTouristRecover")
  public TouristResponse getTourist() {

    log.info("Fetching all users");

    Map<String, String> headers = createHeaders();

    return httpClient.request(
        TOURIST.name(),
        GET,
        touristConfig.getFetchTouristsUrl(),
        null,
        headers,
        TouristResponse.class);
  }

  @Recover
  private TouristResponse getTouristRecover(Throwable throwable) {
    log.info("Reached recover method for get users");
    if (throwable instanceof ResourceAccessException
        && throwable.getCause() instanceof SocketTimeoutException) {
      log.info("GET_USERS_READ_TIMEOUT");

    } else if (throwable instanceof ResourceAccessException
        && (throwable.getCause() instanceof ConnectTimeoutException
            || throwable.getCause() instanceof ConnectException)) {
      log.info("GET_USERS_CONNECTION_FAILED");
    }
    if (throwable instanceof HttpClientErrorException) {
      // 4xx
      HttpClientErrorException exception = (HttpClientErrorException) throwable;
      log.info("GET_USERS_FAILED_{}_ERROR", exception.getRawStatusCode());

    } else if (throwable instanceof HttpServerErrorException) {
      // 5xx
      HttpServerErrorException exception = (HttpServerErrorException) throwable;
      log.info("GET_USERS_FAILED_{}_ERROR", exception.getRawStatusCode());
    } else {
      log.info("GET_USERS_FAILED");
    }

    return null;
  }

  private Map<String, String> createHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    return headers;
  }
}
