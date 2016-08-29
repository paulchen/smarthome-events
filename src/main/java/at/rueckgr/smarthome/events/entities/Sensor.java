package at.rueckgr.smarthome.events.entities;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "sensor")
@NamedQueries(
        @NamedQuery(name = Sensor.QRY_ALL,
                    query = "SELECT new at.rueckgr.smarthome.events.model.SensorDTO(s.sensorId, s.deviceId, s.name) FROM Sensor s")
)
@Getter
@Setter
public class Sensor {
    public static final String QRY_ALL = "Sensor.qryAll";

    @Id
    @Column(name = "sensor_id", nullable = false, updatable = false)
    private Long sensorId;

    @NotNull
    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
}
