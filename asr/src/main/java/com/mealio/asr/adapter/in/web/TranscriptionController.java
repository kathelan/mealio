package com.mealio.asr.adapter.in.web;

import com.mealio.asr.adapter.in.web.dto.TranscriptionResponse;
import com.mealio.asr.domain.exception.InvalidTranscriptionRequestException;
import com.mealio.asr.domain.model.AudioContent;
import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.port.in.TranscribeAudioUseCase;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/transcriptions")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscribeAudioUseCase transcribeAudioUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TranscriptionResponse transcribe(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "pl") String language,
            @RequestParam(required = false) String prompt
    ) {
        try {
            var request = new TranscriptionRequest(
                    new AudioContent(file.getOriginalFilename(), file.getContentType(), file.getBytes()),
                    language,
                    prompt
            );
            return TranscriptionResponse.from(transcribeAudioUseCase.transcribe(request));
        } catch (IOException exception) {
            throw new InvalidTranscriptionRequestException("Cannot read uploaded audio file", exception);
        }
    }
}
