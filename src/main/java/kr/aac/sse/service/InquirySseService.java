package kr.aac.sse.service;

import kr.aac.sse.dto.InquirySseMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Set;

@Service
public final class InquirySseService implements InquirySubscribeUseCase, InquiryBroadcastUseCase {

    private final Set<SseEmitter> inquirySseEmitters = new HashSet<>();
    private static final long TIMEOUT = 60 * 1000; /* [ms] */
    @Override
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(TIMEOUT );

        emitter.onCompletion(() -> {
            synchronized (this.inquirySseEmitters) {
                this.inquirySseEmitters.remove(emitter);
            }
        });
        emitter.onError((e) -> {
            emitter.complete();
        });
        emitter.onTimeout(() -> {
            // ...
            emitter.complete();
        });

        inquirySseEmitters.add(emitter); // <<< 이 부분만 우리가 emitter를 기억하기 위한 것
        
        return emitter;
    }

    @Override
    public void broadcast(InquirySseMessage messageObject) {
        // 각 Emitters가 사용자도 기억하고 있다고 보면 됨.
        InquirySseSender sender = InquirySseSender.with(messageObject);
        sender.sendTo(inquirySseEmitters);
    }
}
