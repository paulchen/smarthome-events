package at.rueckgr.smarthome.events.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "observation")
@NamedQueries({
        @NamedQuery(name = Observation.FIND_NEWEST_OBSERVATION,
                query = "SELECT o FROM Observation o WHERE o.sensor.sensorId = :sensorId ORDER BY o.timestamp DESC"
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Observation {
    public static final String FIND_NEWEST_OBSERVATION = "Observation.findNewestObservation";

    @Id
    @Column(name = "observation_id")
    private Long observationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @NotNull
    private Sensor sensor;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @NotNull
    @Column(name = "value", nullable = false)
    private String value;

}
