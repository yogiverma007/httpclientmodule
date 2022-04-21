package com.freedom.httpclientmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateTouristRequest {
  @JsonProperty("tourist_name")
  private String touristName;

  @JsonProperty("tourist_email")
  private String touristEmail;

  @JsonProperty("tourist_location")
  private String touristLocation;
}
