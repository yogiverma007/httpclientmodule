package com.freedom.httpclientmodule.service;

import com.freedom.httpclientmodule.client.TouristHttpClient;
import com.freedom.httpclientmodule.dto.CreateTouristRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TouristService {

  private final TouristHttpClient touristHttpClient;

  @Autowired
  public TouristService(TouristHttpClient touristHttpClient) {
    this.touristHttpClient = touristHttpClient;
  }

  @Scheduled(fixedDelay = 10000L)
  public void getTourist() {
    System.out.println(touristHttpClient.getTourist());
  }

  @Scheduled(fixedDelay = 12000L)
  public void createTourist() {
    CreateTouristRequest createTouristRequest = new CreateTouristRequest();
    createTouristRequest.setTouristEmail(new Random().nextInt(5) + "abc@abc.com");
    createTouristRequest.setTouristLocation("DELHI");
    createTouristRequest.setTouristName("Neil Modi");
    System.out.println(touristHttpClient.createTourist(createTouristRequest));
  }
}
