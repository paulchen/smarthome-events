package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.main.DatabaseCredentials;
import org.apache.commons.lang3.Validate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

public class Database {
    private static final String PERSISTENCE_UNIT_NAME = "Database";

    private static EntityManagerFactory factory;

    private static DatabaseCredentials databaseCredentials;

    public static EntityManager getEm() {
        Validate.notNull(databaseCredentials);

        if(factory == null) {
            synchronized (Database.class) {
                if(factory == null) {
                    final Properties properties = new Properties();
                    properties.put("javax.persistence.jdbc.url", databaseCredentials.getUrl());
                    properties.put("javax.persistence.jdbc.user", databaseCredentials.getUsername());
                    properties.put("javax.persistence.jdbc.password", databaseCredentials.getPassword());
                    properties.put("javax.persistence.jdbc.driver", databaseCredentials.getDriver());

                    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
                }


            }
        }

        return factory.createEntityManager();
    }

    public static void setCredentials(DatabaseCredentials credentials) {
        Database.databaseCredentials = credentials;
    }
}
