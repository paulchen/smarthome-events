package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.model.HealthState;
import at.rueckgr.smarthome.events.model.HealthStateDescription;
import at.rueckgr.smarthome.events.model.SystemHealth;

import java.text.MessageFormat;
import java.util.Map;

public class StatusCommand implements Command {
    @Override
    public String execute(final String[] parts) {
        if(parts.length > 1) {
            return null;
        }

        final SystemStateManager systemStateManager = SystemStateManager.getInstance();
        SystemHealth systemHealth = systemStateManager.getHealth();

        HealthStateDescription healthStateDescription = systemHealth.getOverallHealth();
        if(systemHealth.getOverallHealth().getHealthState() != HealthState.OK) {
            for (Map.Entry<Long, HealthStateDescription> entry : systemHealth.getSensorHealth().entrySet()) {
                final Long sensorId = entry.getKey();
                final HealthStateDescription sensorHealth = entry.getValue();

                if(healthStateDescription == null || healthStateDescription.compareTo(sensorHealth) > 0) {
                    sensorHealth.setDescription("Sensor " + sensorId + ": " + sensorHealth.getDescription());
                    healthStateDescription = sensorHealth;
                }
            }
        }

        return MessageFormat.format("{0} {1}", healthStateDescription.getHealthState().ordinal(), healthStateDescription.getDescription());
    }
}
