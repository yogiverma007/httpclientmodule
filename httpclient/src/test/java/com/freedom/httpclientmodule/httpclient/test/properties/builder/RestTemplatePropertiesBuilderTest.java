package com.freedom.httpclientmodule.httpclient.test.properties.builder;

import com.freedom.httpclientmodule.httpclient.constants.REST_TEMPLATE_PROPERTY;
import com.freedom.httpclientmodule.httpclient.template.properties.builder.RestTemplatePropertiesBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RestTemplatePropertiesBuilderTest {

  @Test
  public void build_UnparseableIntProperty_DefaultPropertiesAreSet() {
    RestTemplatePropertiesBuilder restTemplatePropertiesBuilder =
        getRestTemplatePropertiesBuilder();
    assertNotNull(restTemplatePropertiesBuilder.build());
  }

  private RestTemplatePropertiesBuilder getRestTemplatePropertiesBuilder() {

    String clientName = "CHECKOUT";
    RestTemplatePropertiesBuilder restTemplatePropertiesBuilder =
        RestTemplatePropertiesBuilder.createFor(clientName);
    for (REST_TEMPLATE_PROPERTY rest_template_property : REST_TEMPLATE_PROPERTY.values()) {
      restTemplatePropertiesBuilder.setPropertyValue(rest_template_property, "abcd");
    }
    return restTemplatePropertiesBuilder;
  }
}
