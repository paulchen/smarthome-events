package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.model.Observation;
import at.rueckgr.smarthome.events.model.Sensor;
import at.rueckgr.smarthome.events.model.SystemState;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemStateManager {
    private static volatile SystemStateManager instance;

    private final SystemState systemState;

    private SystemStateManager() {
        // private constructor

        systemState = new SystemState();
    }

    public static SystemStateManager getInstance() {
        if(instance == null) {
            synchronized (SystemStateManager.class) {
                if(instance == null) {
                    instance = new SystemStateManager();
                }
            }
        }
        return instance;
    }

    public void setSensorData(final List<Sensor> sensors) {
        Validate.notNull(sensors);

        final Map<Long, Sensor> sensorMap = new HashMap<>();
        for (Sensor sensor : sensors) {
            sensorMap.put(sensor.getSensorId(), sensor);
        }
        systemState.setSensors(sensorMap);
    }

    public boolean submitObservation(final Observation observation) {
        Validate.notNull(observation);

        return systemState.getSensors().containsKey(observation.getSensorId());
    }
}
