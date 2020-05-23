package at.rueckgr.smarthome.events.server.command;

import at.rueckgr.smarthome.events.metrics.Metrics;
import at.rueckgr.smarthome.events.metrics.MetricsService;
import at.rueckgr.smarthome.events.model.HealthState;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MuninCommand implements Command {
    private static final String NAME = "munin";

    @Override
    public String execute(final String[] parts) {
        if(parts.length > 2) {
            return null;
        }
        if(parts.length == 2) {
            switch(parts[1].trim().toLowerCase()) {
                case "autoconf":
                    return "yes";

                case "config":
                    final List<String> items = new ArrayList<>();

                    items.add("graph_order min max avg");
                    items.add("graph_title SmartHome Events processing times");
                    items.add("graph_vtitle Seconds");
                    items.add("graph_category smarthome");

                    addConfig(items, "min", "Minimum processing time");
                    addConfig(items, "max", "Maximum processing time");
                    addConfig(items, "avg", "Average processing time");

                    return String.join("\n", items);

                default:
                    return null;
            }
        }

        final MetricsService metricsService = new MetricsService();
        final Metrics metrics = metricsService.getMetrics();
        final HealthState healthState = metricsService.getHealthState();

        if (healthState == HealthState.UNKNOWN) {
            return null;
        }

        final List<String> items = new ArrayList<>();

        addValue(items, "min", metrics.getMin());
        addValue(items, "max", metrics.getMax());
        addValue(items, "avg", metrics.getAvg());

        return String.join("\n", items);
    }

    private void addConfig(final List<String> items, final String key, final String name) {
        items.add(key + ".label " + name);
        items.add(key + ".min 0");
        items.add(key + ".critical 2");
        items.add(key + ".warning 1");

    }
    private void addValue(final List<String> items, final String key, long value) {
        double outputValue = value / 1000d;
        final MessageFormat format = new MessageFormat("{0}.value {1,number,#.000}", Locale.ENGLISH);
        items.add(format.format(new Object[] { key, outputValue }));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
