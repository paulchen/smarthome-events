package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.entities.Observation;
import at.rueckgr.smarthome.events.entities.Sensor;
import at.rueckgr.smarthome.events.model.HealthState;
import at.rueckgr.smarthome.events.model.HealthStateDescription;
import at.rueckgr.smarthome.events.model.ObservationDTO;
import at.rueckgr.smarthome.events.model.SensorDTO;
import at.rueckgr.smarthome.events.model.SystemHealth;
import at.rueckgr.smarthome.events.model.SystemState;
import at.rueckgr.smarthome.events.util.TimeHelper;
import org.apache.commons.lang3.Validate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SystemStateManager {
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

        final Map<Long, SensorDTO> sensorMap = sensorDTOs.stream().collect(Collectors.toMap(SensorDTO::getSensorId, Function.identity()));
        systemState.setSensors(sensorMap);

        final Map<Long, ObservationDTO> lastObservations = new HashMap<>();

        final EntityManager em = Database.getEm();
        em.getTransaction().begin();
        for (Long sensorId : sensorMap.keySet()) {
            final TypedQuery<Observation> query = em.createNamedQuery(Observation.FIND_NEWEST_OBSERVATION, Observation.class);
            query.setParameter("sensorId", sensorId);
            query.setMaxResults(1);

            try {
                final Observation observation = query.getSingleResult();
                lastObservations.put(sensorId, transformToDTO(observation));
            }
            catch (NoResultException e) {
                // do nothing
            }
        }
        em.getTransaction().rollback();

        systemState.setLastObservations(lastObservations);
    }

    private ObservationDTO transformToDTO(final Observation observation) {
        return new ObservationDTO(observation.getSensor().getSensorId(),
                TimeHelper.toLocalDateTime(observation.getTimestamp()),
                observation.getValue());
    }

    public boolean submitObservation(final ObservationDTO observationDTO) {
        Validate.notNull(observationDTO);

        final Long sensorId = observationDTO.getSensorId();
        if(!systemState.getSensors().containsKey(sensorId)) {
            return false;
        }

        systemState.getLastObservations().put(sensorId, observationDTO);

        final EntityManager em = Database.getEm();
        final Sensor sensor = em.getReference(Sensor.class, sensorId);

        Observation observation = new Observation(null,
                sensor,
                TimeHelper.toDate(observationDTO.getTimestamp()),
                observationDTO.getValue());

        em.getTransaction().begin();
        em.persist(observation);
        em.getTransaction().commit();

    	em.close();

        System.gc();

        return true;
    }

    public SystemHealth getHealth() {


        final Map<Long, HealthStateDescription> sensorHealth = new HashMap<>();
        for (Long sensorId : systemState.getSensors().keySet()) {
            final ObservationDTO observationDTO = systemState.getLastObservations().get(sensorId);
            final HealthStateDescription health;
            if(observationDTO == null) {
                health = new HealthStateDescription(HealthState.UNKNOWN, "No sensor value recorded");
            }
            else {
                final long age = TimeHelper.getAgeInMinutes(observationDTO.getTimestamp());
                if(age > 10) {
                    health = new HealthStateDescription(HealthState.CRITIAL, "Last value older than 10 minutes");
                }
                else if (age > 5) {
                    health = new HealthStateDescription(HealthState.WARNING, "Last value older than 5 minutes");
                }
                else {
                    health = new HealthStateDescription(HealthState.OK, "Value at most 5 minutes old");
                }
            }

            sensorHealth.put(sensorId, health);
        }

        final HealthStateDescription overallHealth = new HealthStateDescription(HealthState.OK, "Server working");
        return new SystemHealth(overallHealth, sensorHealth);

    }
}
