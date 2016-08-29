package at.rueckgr.smarthome.events.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Database {
    private static final String PERSISTENCE_UNIT_NAME = "Database";

    private static EntityManagerFactory factory;

    public static EntityManager getEm() {
        if(factory == null) {
            synchronized (Database.class) {
                if(factory == null) {
                    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                }
            }
        }

        return factory.createEntityManager();
    }
}
