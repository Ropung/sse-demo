package kr.aac.sse.service;

import kr.aac.sse.dto.InquirySseMessage;

public interface InquiryBroadcastUseCase {
    void broadcast(InquirySseMessage messageObject);
}
