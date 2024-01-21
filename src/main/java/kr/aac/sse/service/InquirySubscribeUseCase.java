package kr.aac.sse.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface InquirySubscribeUseCase {
    SseEmitter subscribe();

}
