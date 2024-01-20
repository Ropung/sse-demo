package kr.aac.sse.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface InquirySubscribeUseCase {
    SseEmitter subscribe(/* 필요하면 커스텀으로 아이디 만들어서 사용 */);
}
