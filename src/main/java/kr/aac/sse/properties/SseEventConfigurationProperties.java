package kr.aac.sse.properties;

import lombok.Builder;

import java.util.Objects;

@Builder
public record SseEventConfigurationProperties(
        String id,
        String eventName,
        Long reconnectTime,
        Object payload
) {
    public SseEventConfigurationProperties {
        Objects.requireNonNull(id);
        Objects.requireNonNull(eventName);

        if (reconnectTime == null) {
            reconnectTime = 60_000L;
        }
    }
}
