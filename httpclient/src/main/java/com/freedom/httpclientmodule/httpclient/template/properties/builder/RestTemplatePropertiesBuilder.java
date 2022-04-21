package com.freedom.httpclientmodule.httpclient.template.properties.builder;

import com.freedom.httpclientmodule.httpclient.constants.REST_TEMPLATE_PROPERTY;
import com.freedom.httpclientmodule.httpclient.template.properties.RestTemplatePropertiesSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
public class RestTemplatePropertiesBuilder {

  private RestTemplateProperties properties = new RestTemplateProperties();

  private Map<REST_TEMPLATE_PROPERTY, String> propertiesMap =
      new EnumMap<>(REST_TEMPLATE_PROPERTY.class);

  private RestTemplatePropertiesBuilder() {}

  public static RestTemplatePropertiesBuilder createFor(String clientName) {

    RestTemplatePropertiesBuilder builder = new RestTemplatePropertiesBuilder();
    builder.properties.setClientName(clientName);
    return builder;
  }

  public RestTemplatePropertiesBuilder setPropertyValue(
      REST_TEMPLATE_PROPERTY property, String propertyValue) {
    propertiesMap.put(property, propertyValue);
    return this;
  }

  public RestTemplatePropertiesSpecification build() {

    for (Entry<REST_TEMPLATE_PROPERTY, String> entry : propertiesMap.entrySet()) {

      REST_TEMPLATE_PROPERTY property = entry.getKey();
      String defaultPropertyValue = property.getDefaultValue();
      String propertyValue = entry.getValue();
      Integer integerPropValue = null;

      switch (property) {
        case CONNECTION_REQUEST_TIMEOUT:
          integerPropValue = getIntegerPropertyValue(propertyValue, defaultPropertyValue);
          setConnectionRequestTimeOut(integerPropValue);
          break;

        case CONNECTION_TIMEOUT:
          integerPropValue = getIntegerPropertyValue(propertyValue, defaultPropertyValue);
          setConnectionTimeOut(integerPropValue);
          break;

        case MAX_CONNECTIONS:
          integerPropValue = getIntegerPropertyValue(propertyValue, defaultPropertyValue);
          setMaxTotalConnection(integerPropValue);
          break;

        case MAX_CONNECTIONS_PER_ROUTE:
          integerPropValue = getIntegerPropertyValue(propertyValue, defaultPropertyValue);
          setMaxPerChannel(integerPropValue);
          break;

        case SOCKET_TIMEOUT:
          integerPropValue = getIntegerPropertyValue(propertyValue, defaultPropertyValue);
          setReadTimeout(integerPropValue);
          break;

        case STALE_CONNECTIONS_CHECK_PERIOD:
          integerPropValue = getIntegerPropertyValue(propertyValue, defaultPropertyValue);
          setStaleConnectionCheckAfterInactivityPeriod(integerPropValue);
          break;

        default:
          break;
      }
    }

    return properties;
  }

  private Integer getIntegerPropertyValue(String value, String defaultValue) {

    Integer returnVal = null;

    try {
      returnVal = Integer.parseInt(defaultValue);
    } catch (Exception e) {
      log.error("Default Value of property could not be parsed into Integer.");
    }

    try {
      if (value != null) returnVal = Integer.parseInt(value);
    } catch (Exception e) {
      log.error("Value of property could not be parsed into Integer.");
    }
    return returnVal;
  }

  private RestTemplatePropertiesBuilder setConnectionRequestTimeOut(Integer propertyValue) {

    if (propertyValue != null) {
      this.properties.setConnectionRequestTimeout(propertyValue);
    }
    return this;
  }

  private RestTemplatePropertiesBuilder setConnectionTimeOut(Integer propertyValue) {

    if (propertyValue != null) {
      this.properties.setConnectionTimeout(propertyValue);
    }
    return this;
  }

  private RestTemplatePropertiesBuilder setMaxPerChannel(Integer propertyValue) {

    if (propertyValue != null) {
      this.properties.setMaxPerChannel(propertyValue);
    }
    return this;
  }

  private RestTemplatePropertiesBuilder setMaxTotalConnection(Integer propertyValue) {

    if (propertyValue != null) {
      this.properties.setMaxTotalConnection(propertyValue);
    }
    return this;
  }

  private RestTemplatePropertiesBuilder setReadTimeout(Integer propertyValue) {

    if (propertyValue != null) {
      this.properties.setReadTimeout(propertyValue);
    }
    return this;
  }

  private RestTemplatePropertiesBuilder setStaleConnectionCheckAfterInactivityPeriod(
      Integer propertyValue) {

    if (propertyValue != null) {
      this.properties.setStaleConnectionCheckAfterInactivityPeriod(propertyValue);
    }
    return this;
  }
}
