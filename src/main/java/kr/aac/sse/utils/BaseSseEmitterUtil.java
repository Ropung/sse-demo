package kr.aac.sse.utils;

import java.util.Iterator;

/**
 * @deprecated concurrent hash map, conccurent skip list set 등으로 대체함.
 */
@Deprecated(since = "0.1.0", forRemoval = true)
public class BaseSseEmitterUtil { // -> 이거 자체를 안 써요 이제.ㅇㅋ요
    public static void clearExpiredEmitters(Iterable<? extends BaseSseEmitter> emitters) {
        // forEach 내부에서 emitters의 원소가 삭제되면 forEach 길이가 변경되어 예외 발생
        //  java.util.ConcurrentModificationException: null
        Iterator<? extends BaseSseEmitter> iterator = emitters.iterator();
        while (iterator.hasNext()) {
            BaseSseEmitter emitter = iterator.next(); // must be called before you can call iteratorItem.remove()
//            if (emitter.isCompleted()) { // 원래 이거 때문에 isCompleted를 썼어요.
//                iterator.remove();
//            }
        }
    }
}
