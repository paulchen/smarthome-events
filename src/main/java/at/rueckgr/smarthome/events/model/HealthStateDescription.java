package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class HealthStateDescription implements Serializable, Comparable<HealthStateDescription> {
    private HealthState healthState;
    private String description;

    @Override
    public int compareTo(final HealthStateDescription healthStateDescription) {
        return healthState.compareTo(healthStateDescription.getHealthState());
    }
}
