package kr.aac.sse.controller;

import kr.aac.sse.dto.InquirySseMessage;
import kr.aac.sse.service.InquiryBroadcastUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class FbEventReceiverApi {

    private final InquiryBroadcastUseCase inquiryBroadcastUseCase;

    @PostMapping("/from-firebase")
    public void onFirebaseDbChanged(/* ... */) {
        // ...

        // TODO 파이어베이스는 변경이 있다는 것만 이 API를 통해 알려 주고
        //  API 서버에서 다시 Firebase에게 변경이 무엇인지 따로 추가로 확인. (네트워크 보안 안 돼 있으니까.)
        //  확인 후 SSE를 통해 프론트에 새 내용 전달.
        //  (Firebase에서 온 요청을 신뢰할 수 있게 되면, 변경을 한 번에 받아도 됨. 아니면 이렇게 따로 운용이 안전.)

        InquirySseMessage messageObject = InquirySseMessage.builder()
                // ...
                .content("Example: " + UUID.randomUUID())
                .build();

        inquiryBroadcastUseCase.broadcast(messageObject);
    }
}
