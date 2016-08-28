package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Sensor {
    private Long sensorId;
    private Long deviceId;
    private String name;
}
