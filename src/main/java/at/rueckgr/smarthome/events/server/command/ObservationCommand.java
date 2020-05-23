package at.rueckgr.smarthome.events.server.command;

import at.rueckgr.smarthome.events.model.ObservationDTO;
import at.rueckgr.smarthome.events.server.SystemStateManager;
import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

public class ObservationCommand implements Command {
    private static final String NAME = "observation";

    @Override
    public String execute(final String[] parts) {
        // OBSERVATION sensorId unixTimestamp value
        if(parts.length != 4) {
            return null;
        }

        final ObservationDTO observationDTO = parseObservation(parts);
        if(observationDTO == null) {
            return null;
        }

        SystemStateManager systemStateManager = SystemStateManager.getInstance();
        return systemStateManager.submitObservation(observationDTO) ? "OK" : "NOK";
    }

    @Override
    public String getName() {
        return NAME;
    }

    private ObservationDTO parseObservation(final String[] parts) {
        final String sensorIdString = parts[1];
        if(!StringUtils.isNumeric(sensorIdString)) {
            return null;
        }
        final Long sensorId = Long.valueOf(sensorIdString);

        final String unixTimestampString = parts[2];
        if(!StringUtils.isNumeric(unixTimestampString)) {
            return null;
        }
        final LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(unixTimestampString)), Clock.systemDefaultZone().getZone());

        final String value = parts[3];

        return new ObservationDTO(sensorId, timestamp, value);
    }
}
