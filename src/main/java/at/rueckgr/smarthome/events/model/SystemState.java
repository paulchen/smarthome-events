package at.rueckgr.smarthome.events.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class SystemState implements Serializable {
    private Map<Long, SensorDTO> sensors;
    private Map<Long, ObservationDTO> lastObservations;
}
