package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SensorDTO {
    private Long sensorId;
    private Long deviceId;
    private String name;
}
