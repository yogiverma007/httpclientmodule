package com.freedom.httpclientmodule.dto;

import lombok.Data;

import java.util.List;

@Data
public class TouristResponse {

  public String $id;
  public Integer page;
  public Integer perPage;
  public Integer totalrecord;
  public Integer totalPages;
  public List<Tourist> data = null;
}
