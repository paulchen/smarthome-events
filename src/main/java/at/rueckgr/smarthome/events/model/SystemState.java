package at.rueckgr.smarthome.events.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SystemState {
    private Map<Long, SensorDTO> sensors;
}
