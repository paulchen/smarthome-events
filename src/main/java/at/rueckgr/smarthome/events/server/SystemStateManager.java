package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.entities.Observation;
import at.rueckgr.smarthome.events.entities.Sensor;
import at.rueckgr.smarthome.events.main.ConfigurationManager;
import at.rueckgr.smarthome.events.model.HealthState;
import at.rueckgr.smarthome.events.model.HealthStateDescription;
import at.rueckgr.smarthome.events.model.ObservationDTO;
import at.rueckgr.smarthome.events.model.SensorDTO;
import at.rueckgr.smarthome.events.model.SystemHealth;
import at.rueckgr.smarthome.events.model.SystemState;
import at.rueckgr.smarthome.events.util.TimeHelper;
import org.apache.commons.lang3.Validate;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SystemStateManager {
    private static volatile SystemStateManager instance;

    private SystemStateManager() {
        // private constructor
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

    private SystemState getSystemState() {
        final List<SensorDTO> sensorDTOs = ConfigurationManager.readSensorData();

        final SystemState systemState = new SystemState();
        final Map<Long, SensorDTO> sensorMap = sensorDTOs.stream().collect(Collectors.toMap(SensorDTO::getSensorId, Function.identity()));
        systemState.setSensors(sensorMap);

        final Map<Long, ObservationDTO> lastObservations = new HashMap<>();

        try (final EntityManagerWrapper em = Database.getEm()) {
            em.getTransaction().begin();
            for (Long sensorId : sensorMap.keySet()) {
                final TypedQuery<Observation> query = em.createNamedQuery(Observation.FIND_NEWEST_OBSERVATION, Observation.class);
                query.setParameter("sensorId", sensorId);
                query.setMaxResults(1);

                try {
                    final Observation observation = query.getSingleResult();
                    lastObservations.put(sensorId, transformToDTO(observation));
                } catch (NoResultException e) {
                    // do nothing
                }
            }
            em.getTransaction().rollback();
        }

        systemState.setLastObservations(lastObservations);

        return systemState;
    }

    private ObservationDTO transformToDTO(final Observation observation) {
        return new ObservationDTO(observation.getSensor().getSensorId(),
                TimeHelper.toLocalDateTime(observation.getTimestamp()),
                observation.getValue());
    }

    public boolean submitObservation(final ObservationDTO observationDTO) {
        Validate.notNull(observationDTO, "observationDTO must not be null");

        final SystemState systemState = getSystemState();

        final Long sensorId = observationDTO.getSensorId();
        if(!systemState.getSensors().containsKey(sensorId)) {
            return false;
        }

        systemState.getLastObservations().put(sensorId, observationDTO);

        try (final EntityManagerWrapper em = Database.getEm()) {
            final Sensor sensor = em.getReference(Sensor.class, sensorId);

            Observation observation = new Observation(null,
                    sensor,
                    TimeHelper.toDate(observationDTO.getTimestamp()),
                    observationDTO.getValue());

            em.getTransaction().begin();
            em.persist(observation);
            em.getTransaction().commit();
        }

        return true;
    }

    public SystemHealth getHealth() {
        final SystemState systemState = getSystemState();

        final Map<Long, HealthStateDescription> sensorHealth = new HashMap<>();
        final Map<HealthState, List<HealthStateDescription>> sensorsByState = new TreeMap<>();
        for (HealthState healthState : HealthState.values()) {
            sensorsByState.put(healthState, new ArrayList<>());
        }

        HealthState overallHealthState = HealthState.OK;
        for (Long sensorId : systemState.getSensors().keySet()) {
            final ObservationDTO observationDTO = systemState.getLastObservations().get(sensorId);
            final HealthStateDescription health;
            if(observationDTO == null) {
                health = new HealthStateDescription(sensorId, HealthState.UNKNOWN, "No sensor value recorded");
            }
            else {
                final long age = TimeHelper.getAgeInMinutes(observationDTO.getTimestamp());
                if(age > 10) {
                    health = new HealthStateDescription(sensorId, HealthState.CRITICAL, "Last value older than 10 minutes");
                }
                else if (age > 5) {
                    health = new HealthStateDescription(sensorId, HealthState.WARNING, "Last value older than 5 minutes");
                }
                else {
                    health = new HealthStateDescription(sensorId, HealthState.OK, "Value at most 5 minutes old");
                }
            }

            sensorHealth.put(sensorId, health);
            sensorsByState.get(health.getHealthState()).add(health);

            if(overallHealthState.compareTo(health.getHealthState()) < 0) {
                overallHealthState = health.getHealthState();
            }
        }

        final StringBuilder s = new StringBuilder("Server working; ");
        s.append(sensorHealth.size()).append(" sensors");
        for (final Map.Entry<HealthState, List<HealthStateDescription>> entry : sensorsByState.entrySet()) {
            final List<HealthStateDescription> value = entry.getValue();
            final HealthState key = entry.getKey();

            final String sensorList = value.stream()
                    .map(HealthStateDescription::getSensorId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            s.append("; ")
                    .append(value.size()).append(" ").append(key.name())
                    .append(" (").append(sensorList).append(")");
        }


        final HealthStateDescription overallHealth = new HealthStateDescription(null, overallHealthState, s.toString());
        return new SystemHealth(overallHealth, sensorHealth);

    }
}
