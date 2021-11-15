package at.rueckgr.smarthome.events.metrics;

import lombok.Data;

@Data
public class Metrics {
    private final long avg;
    private final long min;
    private final long max;
    private final long count;
}
