package at.rueckgr.smarthome.events.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
