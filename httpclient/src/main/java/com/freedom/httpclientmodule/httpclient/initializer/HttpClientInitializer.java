package com.freedom.httpclientmodule.httpclient.initializer;

import com.freedom.httpclientmodule.httpclient.constants.REST_TEMPLATE_PROPERTY;
import com.freedom.httpclientmodule.httpclient.store.HttpClientStore;
import com.freedom.httpclientmodule.httpclient.template.properties.RestTemplatePropertiesSpecification;
import com.freedom.httpclientmodule.httpclient.template.properties.builder.RestTemplatePropertiesBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.freedom.httpclientmodule.httpclient.constants.Constants.HTTP_CLIENT_NAMES;

@Slf4j
public abstract class HttpClientInitializer {

  @Autowired private HttpClientStore clientStore;

  public void initializeAllHttpClients() {

    String clientsToBeInitializedProperty = HTTP_CLIENT_NAMES;

    String clientsToBeInitializedPropertyValue =
        providePropertyValue(clientsToBeInitializedProperty);

    if (StringUtils.isNotBlank(clientsToBeInitializedPropertyValue)) {

      String[] clientsToBeInitialized = clientsToBeInitializedPropertyValue.split(",");

      for (String clientName : clientsToBeInitialized) {
        initializeHttpClient(clientName.trim());
      }
    }
  }

  public void initializeHttpClient(String clientName) {

    RestTemplatePropertiesBuilder templatePropertiesBuilder =
        RestTemplatePropertiesBuilder.createFor(clientName);

    for (REST_TEMPLATE_PROPERTY property : REST_TEMPLATE_PROPERTY.values()) {

      String propertyName = property.getPropertyName().replace("clientName", clientName);

      String propertyValue = providePropertyValue(propertyName);

      templatePropertiesBuilder.setPropertyValue(property, propertyValue);
    }

    RestTemplatePropertiesSpecification templateProperties = templatePropertiesBuilder.build();

    clientStore.updateRestTemplateForClient(clientName, templateProperties);
  }

  public abstract String providePropertyValue(String propertyName);
}
