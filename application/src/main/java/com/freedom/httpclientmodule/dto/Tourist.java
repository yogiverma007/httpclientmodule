package com.freedom.httpclientmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Tourist {

  public String $id;
  public Integer id;
  public String touristName;
  public String touristEmail;
  public String touristProfilepicture;
  public String touristLocation;
  public String createdat;
}
