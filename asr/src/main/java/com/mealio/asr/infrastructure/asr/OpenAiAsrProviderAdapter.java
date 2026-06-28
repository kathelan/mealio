package com.mealio.asr.infrastructure.asr;

import com.mealio.asr.domain.exception.AsrProviderConfigurationException;
import com.mealio.asr.domain.exception.AsrProviderException;
import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.port.out.AsrProviderPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@ConditionalOnProperty(prefix = "mealio.asr", name = "provider", havingValue = "openai")
public class OpenAiAsrProviderAdapter implements AsrProviderPort {

    private final AsrProperties properties;
    private final RestClient restClient;

    public OpenAiAsrProviderAdapter(AsrProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.getOpenai().getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getOpenai().getApiKey())
                .build();
    }

    @Override
    public String providerName() {
        return "openai";
    }

    @Override
    public String transcribe(TranscriptionRequest request) {
        if (!StringUtils.hasText(properties.getOpenai().getApiKey())) {
            throw new AsrProviderConfigurationException("mealio.asr.openai.api-key is required for OpenAI ASR provider");
        }

        var multipart = new LinkedMultiValueMap<String, Object>();
        multipart.add("model", properties.getOpenai().getModel());
        multipart.add("response_format", "json");
        multipart.add("language", request.language());
        if (StringUtils.hasText(request.prompt())) {
            multipart.add("prompt", request.prompt());
        }
        multipart.add("file", audioPart(request));

        OpenAiTranscriptionResponse response;
        try {
            response = restClient.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipart)
                    .retrieve()
                    .body(OpenAiTranscriptionResponse.class);
        } catch (RestClientResponseException exception) {
            throw new AsrProviderException(
                    "ASR provider request failed",
                    providerName(),
                    exception.getStatusCode().value(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new AsrProviderException("ASR provider request failed", providerName(), null, exception);
        }

        if (response == null || !StringUtils.hasText(response.text())) {
            throw new AsrProviderException("ASR provider returned an empty transcript", providerName(), null, null);
        }
        return response.text();
    }

    private HttpEntity<ByteArrayResource> audioPart(TranscriptionRequest request) {
        var headers = new HttpHeaders();
        headers.setContentType(audioContentType(request));
        headers.setContentDispositionFormData("file", filename(request));
        return new HttpEntity<>(audioResource(request), headers);
    }

    private ByteArrayResource audioResource(TranscriptionRequest request) {
        return new ByteArrayResource(request.audio().bytes()) {
            @Override
            public String getFilename() {
                return filename(request);
            }
        };
    }

    private String filename(TranscriptionRequest request) {
        return StringUtils.hasText(request.audio().filename()) ? request.audio().filename() : "audio.wav";
    }

    private String contentType(TranscriptionRequest request) {
        return StringUtils.hasText(request.audio().contentType())
                ? request.audio().contentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private MediaType audioContentType(TranscriptionRequest request) {
        try {
            return MediaType.parseMediaType(contentType(request));
        } catch (InvalidMediaTypeException exception) {
            throw new AsrProviderException("Uploaded audio content type is invalid", providerName(), null, exception);
        }
    }

    private record OpenAiTranscriptionResponse(String text) {
    }
}
