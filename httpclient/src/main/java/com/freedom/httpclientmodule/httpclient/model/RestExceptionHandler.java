package com.freedom.httpclientmodule.httpclient.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class RestExceptionHandler extends DefaultResponseErrorHandler {

  @Override
  public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
    log.error(clientHttpResponse.toString());
    super.handleError(clientHttpResponse);
  }
}
