package com.mealio.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mealio")
public class MealioApplicationProperties {

    private String applicationName = "Mealio";
}
