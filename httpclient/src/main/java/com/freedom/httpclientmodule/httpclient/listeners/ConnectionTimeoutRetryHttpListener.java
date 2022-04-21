package com.freedom.httpclientmodule.httpclient.listeners;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Component
public class ConnectionTimeoutRetryHttpListener extends RetryListenerSupport {

  @SneakyThrows
  @Override
  public <T, E extends Throwable> void onError(
      RetryContext retryContext, RetryCallback<T, E> callback, Throwable throwable) {

    log.error(
        "Entering Generic Connection Timeout http Retry listener for retry attempt #{} and exception: {}",
        retryContext.getRetryCount(),
        throwable);
    if (throwable instanceof ResourceAccessException
        && (throwable.getCause() instanceof ConnectTimeoutException)) {
      log.warn(
          "Retrying http request due connection timeout issue. Exception occurred:",
          throwable.getCause());
    } else {
      log.warn("Retry not configured  for exception:{}", throwable.getCause());
      retryContext.setExhaustedOnly();
    }
    super.onError(retryContext, callback, throwable);
  }
}
