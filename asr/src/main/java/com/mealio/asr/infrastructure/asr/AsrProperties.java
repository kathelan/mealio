package com.mealio.asr.infrastructure.asr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mealio.asr")
public class AsrProperties {

    private String provider = "mock";
    private OpenAi openai = new OpenAi();

    @Getter
    @Setter
    public static class OpenAi {

        private String apiKey;
        private String baseUrl = "https://api.openai.com/v1";
        private String model = "gpt-4o-mini-transcribe";
    }
}
