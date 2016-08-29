package at.rueckgr.smarthome.events.main;

import at.rueckgr.smarthome.events.entities.Sensor;
import at.rueckgr.smarthome.events.model.SensorDTO;
import at.rueckgr.smarthome.events.server.Database;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by paulchen on 29.08.16.
 */
public class ConfigurationManager {
    public static List<SensorDTO> readSensorData() {
        final EntityManager em = Database.getEm();

        return em.createNamedQuery(Sensor.QRY_ALL, SensorDTO.class).getResultList();
    }
}
