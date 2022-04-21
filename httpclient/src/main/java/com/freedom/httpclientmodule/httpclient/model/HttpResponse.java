package com.freedom.httpclientmodule.httpclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonIgnoreProperties({"httpStatus", "isConnectionRefused"})
public class HttpResponse {

  protected HttpStatus httpStatus;
}
