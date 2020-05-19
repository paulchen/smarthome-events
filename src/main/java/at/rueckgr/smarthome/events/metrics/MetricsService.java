package at.rueckgr.smarthome.events.metrics;

import at.rueckgr.smarthome.events.model.HealthState;
import lombok.extern.log4j.Log4j2;

import java.util.Queue;

@Log4j2
public class MetricsService {
    private static final long CRITICAL_LIMIT = 2000;
    private static final long WARNING_LIMIT = 1000;

    public void recordExecution(final long startTime) {
        final long elapsedTime = System.currentTimeMillis() - startTime;

        log.debug("Command processed in {} ms", elapsedTime);

        final MetricsStorage storage = getStorage();
        final Queue<Long> queue = storage.getExecutionTimes();
        queue.add(elapsedTime);

        long total = 0;
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (queue) {
            for (final long executionTime : queue) {
                total += executionTime;
                max = Math.max(executionTime, max);
                min = Math.min(executionTime, min);
            }

            long avg = total / queue.size();

            storage.setMetrics(new Metrics(avg, min, max, queue.size()));
        }
    }

    private MetricsStorage getStorage() {
        return MetricsStorage.getInstance();
    }

    public Metrics getMetrics() {
        return getStorage().getMetrics();
    }

    public HealthState getHealthState() {
        final Metrics metrics = getMetrics();
        if (metrics == null) {
            return HealthState.UNKNOWN;
        }
        if (metricExceedsLimit(metrics, CRITICAL_LIMIT)) {
            return HealthState.CRITICAL;
        }
        if (metricExceedsLimit(metrics, WARNING_LIMIT)) {
            return HealthState.WARNING;
        }
        return HealthState.OK;
    }

    private boolean metricExceedsLimit(final Metrics metrics, final long limit) {
        return metrics.getMin() > limit || metrics.getAvg() > limit || metrics.getMax() > limit;
    }
}
