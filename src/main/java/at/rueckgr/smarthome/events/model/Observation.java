package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Observation {
    private Long sensorId;
    private LocalDateTime timestamp;
    private String value;
}
