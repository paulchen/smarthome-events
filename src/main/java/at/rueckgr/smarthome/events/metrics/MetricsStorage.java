package at.rueckgr.smarthome.events.metrics;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.QueueUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Queue;

@Getter
@Setter
public class MetricsStorage {
    private static final int SIZE = 1000;

    private static MetricsStorage instance;

    private final Queue<Long> executionTimes;

    private Metrics metrics;

    private MetricsStorage() {
        executionTimes = QueueUtils.synchronizedQueue(new CircularFifoQueue<>(SIZE));
    }

    public static MetricsStorage getInstance() {
        if(instance == null) {
            synchronized (MetricsStorage.class) {
                if(instance == null) {
                    instance = new MetricsStorage();
                }
            }
        }
        return instance;
    }
}
