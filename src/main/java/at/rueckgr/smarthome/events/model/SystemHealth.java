package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public final class SystemHealth implements Serializable {
    private HealthStateDescription overallHealth;
    private Map<Long, HealthStateDescription> sensorHealth;
}
