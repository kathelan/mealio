package com.mealio.asr.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.asr.adapter.in.web.dto.ApiErrorResponse;
import com.mealio.asr.adapter.in.web.dto.TranscriptionResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "mealio.asr.provider=mock",
        "spring.datasource.url=jdbc:h2:mem:mealio_asr_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.liquibase.enabled=false"
})
class TranscriptionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void transcribesMultipartTextFileThroughMockProvider() throws IOException {
        var multipart = multipartRequest(
                "meal.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "zjadlem trzy jajka".getBytes(StandardCharsets.UTF_8)
        );

        var response = RestClient.create().post()
                .uri(url())
                .contentType(multipart.contentType())
                .body(multipart.body())
                .retrieve()
                .toEntity(TranscriptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().transcript()).isEqualTo("zjadlem trzy jajka");
        assertThat(response.getBody().language()).isEqualTo("pl");
        assertThat(response.getBody().provider()).isEqualTo("mock");
        assertThat(response.getBody().confidence()).isNull();
        assertThat(response.getBody().transcribedAt()).isNotNull();
    }

    @Test
    void returnsBadRequestForEmptyAudioFile() throws IOException {
        var multipart = multipartRequest("empty.wav", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[] {});

        var response = RestClient.create().post()
                .uri(url())
                .contentType(multipart.contentType())
                .body(multipart.body())
                .retrieve()
                .onStatus(status -> status.value() == 400, (request, clientResponse) -> {
                })
                .toEntity(ApiErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_TRANSCRIPTION_REQUEST");
        assertThat(response.getBody().message()).isEqualTo("Audio file is required");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    private MultipartRequest multipartRequest(String filename, String contentType, byte[] content) throws IOException {
        var boundary = "MealioAsrBoundary";
        var output = new ByteArrayOutputStream();
        writePart(output, boundary, "language", "pl");
        writeFilePart(output, boundary, "file", filename, contentType, content);
        output.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return new MultipartRequest(
                output.toByteArray(),
                MediaType.parseMediaType("multipart/form-data; boundary=" + boundary)
        );
    }

    private String url() {
        return "http://localhost:" + port + "/api/v1/transcriptions";
    }

    private void writePart(ByteArrayOutputStream output, String boundary, String name, String value) throws IOException {
        output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(value.getBytes(StandardCharsets.UTF_8));
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private void writeFilePart(
            ByteArrayOutputStream output,
            String boundary,
            String name,
            String filename,
            String contentType,
            byte[] content
    ) throws IOException {
        output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n")
                .getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(content);
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private record MultipartRequest(byte[] body, MediaType contentType) {
    }
}
