package kr.aac.sse.service;

import kr.aac.sse.dto.InquirySseMessage;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;

public record InquirySseSender(InquirySseMessage messageObject) {
    public static InquirySseSender with(InquirySseMessage messageObject) {
        return new InquirySseSender(messageObject);
    }

    public void sendTo(SseEmitter emitter) {
        try {
            SseEventBuilder builder = SseEmitter.event()
                    .name("broadcast event")
                    .id("broadcast event 1")
                    .reconnectTime(60000L)
                    .data(messageObject, MediaType.APPLICATION_JSON);

            emitter.send(builder);
            emitter.complete(); // <<< no
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTo(Iterable<SseEmitter> emitters) {
        emitters.forEach(this::sendTo);
    }
}