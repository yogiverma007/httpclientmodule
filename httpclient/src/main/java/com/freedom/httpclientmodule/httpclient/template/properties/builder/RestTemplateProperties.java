package com.freedom.httpclientmodule.httpclient.template.properties.builder;

import com.freedom.httpclientmodule.httpclient.template.properties.RestTemplatePropertiesSpecification;
import lombok.Data;

@Data
class RestTemplateProperties implements RestTemplatePropertiesSpecification {

  private int connectionRequestTimeout = 1000;

  private int connectionTimeout = 1000;

  private int readTimeout = 5000;

  private int maxTotalConnection = 50;

  private int maxPerChannel = 50;

  private int staleConnectionCheckAfterInactivityPeriod = 500;

  private String clientName;

  RestTemplateProperties() {}

  @Override
  public boolean ifEquals(RestTemplatePropertiesSpecification other) {
    if (this == other) return true;
    else if (other == null) return false;
    else if (clientName == null || other.getClientName() != null) {
      return true;
    }

    if (connectionRequestTimeout != other.getConnectionRequestTimeout()) return false;
    if (connectionTimeout != other.getConnectionTimeout()) return false;
    if (maxPerChannel != other.getMaxPerChannel()) return false;
    if (maxTotalConnection != other.getMaxTotalConnection()) return false;
    if (readTimeout != other.getReadTimeout()) return false;
    if (staleConnectionCheckAfterInactivityPeriod
        != other.getStaleConnectionCheckAfterInactivityPeriod()) return false;

    return clientName.equalsIgnoreCase(other.getClientName());
  }
}
