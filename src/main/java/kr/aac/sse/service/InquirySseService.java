package kr.aac.sse.service;

import kr.aac.sse.dto.InquirySseMessage;
import kr.aac.sse.utils.BaseSseEmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
@Slf4j
@RequiredArgsConstructor
public final class InquirySseService implements InquirySubscribeUseCase, InquiryBroadcastUseCase {

    private final SseTestSender testSender;
    private final Set<BaseSseEmitter> inquirySseEmitters = new ConcurrentSkipListSet<>();

    @Override
    public SseEmitter subscribe() {
        String clientId = UUID.randomUUID().toString();
        BaseSseEmitter emitter = new BaseSseEmitter(clientId, 3_600_000L, inquirySseEmitters);

        // 클라이언트에서 subscribe 메서드에서 처음 구독 시에 sendTest()를 통해 데이터를 전송하는 이유는
        // 처음 SSE 응답을 할 때 아무런 이벤트도 보내지 않으면(=> 타임아웃) 재연결 요청을 보내거나(브라우저에 의해 자동으로 수행),
        // 연결 요청 자체에서 오류가 발생하기 때문
        // (타임아웃이나 complete 된 상태에서 set 등에 살아 있던 sse emitter에게 데이터 보내려 하면 오류)
        this.sendTest(emitter);

        inquirySseEmitters.add(emitter); // <<< 이 부분만 우리가 emitter를 기억하기 위한 것, 다른 부분은 SSE 공통.

        return emitter;
    }

    @Override
    public void broadcast(InquirySseMessage messageObject) {
        // 각 Emitters가 사용자를 기억하고 있다고 보면 됨.
        InquirySseSender sender = InquirySseSender.with(messageObject);
        sender.sendTo(inquirySseEmitters);
    }

    private void sendTest(SseEmitter emitter) {
        log.info("Connected Emitter(during sending for test): " + emitter);
        InquirySseMessage messageObject = InquirySseMessage.builder()
                .content("test connection: " + emitter)
                .time(Instant.now())
                .build();

        testSender.sendTestTo(emitter);
    }
}
