package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.metrics.Metrics;
import at.rueckgr.smarthome.events.metrics.MetricsService;
import at.rueckgr.smarthome.events.model.HealthState;

import java.text.MessageFormat;

public class MetricsCommand implements Command {
    private static final String NAME = "metrics";

    @Override
    public String execute(final String[] parts) {
        if(parts.length > 1) {
            return null;
        }

        final MetricsService metricsService = new MetricsService();
        final Metrics metrics = metricsService.getMetrics();
        final HealthState healthState = metricsService.getHealthState();

        if (healthState == HealthState.UNKNOWN) {
            return MessageFormat.format("{0,number,#} Not enough data", healthState.ordinal());
        }
        return MessageFormat.format("{0} MIN {1,number,#} ms, MAX {2,number,#} ms, AVG {3,number,#} ms, last {4,number,#} requests",
                healthState.ordinal(), metrics.getMin(), metrics.getMax(), metrics.getAvg(), metrics.getCount());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
