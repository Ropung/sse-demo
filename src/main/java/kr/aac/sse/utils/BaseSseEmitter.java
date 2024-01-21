package kr.aac.sse.utils;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Iterator;
import java.util.Objects;

@Slf4j
@ToString
public class BaseSseEmitter extends SseEmitter implements Comparable<BaseSseEmitter> {

    private final String clientIdentity;
    @ToString.Exclude
    private final Iterable<? extends BaseSseEmitter>[] storages;

    @SafeVarargs
    public BaseSseEmitter(String clientIdentity, Iterable<? extends BaseSseEmitter>... storages) {
        this(clientIdentity, 3_600_000L, storages);
    }

    @SafeVarargs
    public BaseSseEmitter(String clientIdentity, Long timeout, Iterable<? extends BaseSseEmitter>... storages) {
        super(timeout);

        this.clientIdentity = clientIdentity;
        this.storages = storages;

        if (storages.length == 0) {
            log.warn("보통은 스토리지 있을 텐데, 스토리지 왜 안 넣었누?");
        }

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태)
        this.onCompletion(this::onComplete);

        // Emitter 오류 시
        this.onError(this::onError);

        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때)
        this.onTimeout(this::onTimeout);
    }

    @Override
    public int compareTo(BaseSseEmitter other) {
        if (other == null) {
            return 0;
        }

        return clientIdentity.compareTo(other.clientIdentity);
    }

    private void onComplete() {
        for (var storage : storages) {
            Iterator<? extends BaseSseEmitter> iterator = storage.iterator();
            while (iterator.hasNext()) {
                BaseSseEmitter item = iterator.next();
                if (!Objects.equals(item, this)) {
                    continue;
                }
                iterator.remove();
            }
        }
    }

    private void onError(Throwable e) {
        String stackTrace = ExceptionUtil.stackTraceToString(e);
        log.debug("Emitter 생성 중 오류\n{}", stackTrace);
        this.complete();
    }

    private void onTimeout() {
        String callStack = ThreadUtil.callStack();

        // 사실 error는 아니기 때문에 debug 등이 적당해요. (운영 중엔 타임아웃 관련 메시지 굳이 안 봐도 돼서)
        log.debug("타임아웃 emitter: " + this + "\n" + callStack); // 어느 메서드에서 뜬 건지 보려고.

        this.complete();
    }
}
