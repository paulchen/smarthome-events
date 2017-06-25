package at.rueckgr.smarthome.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ObservationDTO implements Serializable {
    private Long sensorId;
    private LocalDateTime timestamp;
    private String value;
}
