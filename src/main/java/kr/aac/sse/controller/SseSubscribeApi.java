package kr.aac.sse.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.aac.sse.service.InquirySubscribeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public final class SseSubscribeApi {

    private final InquirySubscribeUseCase inquirySubscribeUseCase;

    @GetMapping(path = "/v1/subscribe/inquiry", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeForInquiry(/* (기본적으로) 파라미터 불필요 */ HttpServletResponse response) {
        // 리버스 프록시에서 불필요한 버퍼링을 방지
        response.addHeader("X-Accel-Buffering", "no");

        // response 때 클라이언트 정보와 연계되는 것으로 추정
        return inquirySubscribeUseCase.subscribe();
    }
}
