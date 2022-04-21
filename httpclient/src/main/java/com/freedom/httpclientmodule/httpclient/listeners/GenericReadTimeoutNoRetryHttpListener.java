package com.freedom.httpclientmodule.httpclient.listeners;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;

@Component
@Slf4j
public class GenericReadTimeoutNoRetryHttpListener extends RetryListenerSupport {

  @SneakyThrows
  @Override
  public <T, E extends Throwable> void onError(
      RetryContext retryContext, RetryCallback<T, E> callback, Throwable throwable) {

    log.error(
        "Entering Generic  http Retry listener for retry attempt #{} and exception: {}",
        retryContext.getRetryCount(),
        throwable);
    if (throwable instanceof ResourceAccessException
        && ((throwable.getCause() instanceof ConnectTimeoutException)
            || (throwable.getCause() instanceof ConnectException)
            || (throwable.getCause() instanceof NoHttpResponseException))) {
      log.warn(
          "Retrying http request due connectivity issue. Exception occurred:",
          throwable.getCause());
    } else {
      log.warn("Retry not configured  for exception:{}", throwable.getCause());
      retryContext.setExhaustedOnly();
    }
    super.onError(retryContext, callback, throwable);
  }
}
