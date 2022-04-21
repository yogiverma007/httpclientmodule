package com.freedom.httpclientmodule.httpclient.listeners;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenericNoRetryHttpListener extends RetryListenerSupport {

  @SneakyThrows
  @Override
  public <T, E extends Throwable> void onError(
      RetryContext retryContext, RetryCallback<T, E> callback, Throwable throwable) {

    log.error("Entering Generic http No Retry listener for exception: {}", throwable);

    retryContext.setExhaustedOnly();
    super.onError(retryContext, callback, throwable);
  }
}
