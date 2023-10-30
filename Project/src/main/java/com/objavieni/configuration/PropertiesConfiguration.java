package com.objavieni.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Getter
@Setter
@Configuration
@ConfigurationProperties("request.data")
public class PropertiesConfiguration {

    private String url;
    private String appID;
    private String appKey;
    private String query;
    private int acceptableCaloriesDifferencePerDay;

}
