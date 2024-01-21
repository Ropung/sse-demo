package kr.aac.sse.service;

import kr.aac.sse.dto.InquirySseMessage;
import kr.aac.sse.utils.BaseSseEmitter;
import kr.aac.sse.utils.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Slf4j
public record InquirySseSender(InquirySseMessage messageObject) {

    // Compact 생성자 (최종 생성자 가기 전에 파라미터 유효성이나 전처리 등 가능)
    public InquirySseSender {
        Objects.requireNonNull(messageObject);
    }

    public static InquirySseSender with(InquirySseMessage messageObject) {
        return new InquirySseSender(messageObject);
    }

    public int sendTo(SseEmitter emitter) {
        try {
            SseEventBuilder event = SseEmitter.event().id("sse-id")
                            .name("sse") // FE event name: ~.addEventListner("sse", ...)
                            .reconnectTime(60000L)
                            .data(messageObject);
            
            emitter.send(event);
            return 1;
        } catch (IOException | IllegalStateException e) {
            String message = ExceptionUtil.stackTraceToString(e);
            log.error("만료된 emitter: " + emitter);
            log.debug("""
                    (stack trace) emitter 예외 정보
                    Emitter: {}
                    stack trace
                    {}""", emitter, message);
        }

        return 0;
    }

    public void sendTo(Iterable<? extends BaseSseEmitter> emitters) {
        AtomicInteger count = new AtomicInteger();

        // foreach 내부에서 emitters의 원소가 삭제되면 forEach 길이가 변경되어 예외 발생
        //  java.util.ConcurrentModificationException: null  => solved
        // 일반 HashSet 등 사용 시.
        StreamSupport.stream(emitters.spliterator(), true)
                .parallel()
                .forEach((emitter) -> {
                    count.addAndGet(this.sendTo(emitter));
                });

        log.info("Inquiry SSE 전송 완료\n 수신 대상 수: {}\n message: {}", count.get(), messageObject);
    }
}