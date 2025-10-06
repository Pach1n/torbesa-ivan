package net.ausiasmarch.shared.connection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cant load MySQL driver", e);
        }
        // Inicializar pool global
        HikariPool.initGlobalPool();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cerrar pool global
        HikariPool.closeGlobalPool();
    }
    
}
