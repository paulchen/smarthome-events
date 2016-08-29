package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.entities.Observation;
import at.rueckgr.smarthome.events.entities.Sensor;
import at.rueckgr.smarthome.events.model.ObservationDTO;
import at.rueckgr.smarthome.events.model.SensorDTO;
import at.rueckgr.smarthome.events.model.SystemState;
import org.apache.commons.lang3.Validate;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
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

    public void setSensorData(final List<SensorDTO> sensorDTOs) {
        Validate.notNull(sensorDTOs);

        final Map<Long, SensorDTO> sensorMap = new HashMap<>();
        for (SensorDTO sensorDTO : sensorDTOs) {
            sensorMap.put(sensorDTO.getSensorId(), sensorDTO);
        }
        systemState.setSensors(sensorMap);
    }

    public boolean submitObservation(final ObservationDTO observationDTO) {
        Validate.notNull(observationDTO);

        if(!systemState.getSensors().containsKey(observationDTO.getSensorId())) {
            return false;
        }

        final EntityManager em = Database.getEm();
        final Sensor sensor = em.getReference(Sensor.class, observationDTO.getSensorId());

        final Instant instant = observationDTO.getTimestamp().atZone(ZoneId.systemDefault()).toInstant();
        Observation observation = new Observation(null, sensor, Date.from(instant), observationDTO.getValue());

        em.getTransaction().begin();
        em.persist(observation);
        em.getTransaction().commit();

        return true;
    }
}
