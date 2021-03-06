package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class SensorDTO implements Serializable {
    private Long sensorId;
    private Long deviceId;
    private String name;
}
