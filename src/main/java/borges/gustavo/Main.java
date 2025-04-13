package borges.gustavo;

import borges.gustavo.persistence.migration.MigrationStrategy;
import borges.gustavo.ui.MainMenu;

import java.sql.SQLException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static borges.gustavo.persistence.config.ConnectionConfig.getConnection;


public class Main {
    public static void main(String[] args) throws SQLException {
        /*
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.ALL);
        }
         */
        try(var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }

        new MainMenu().execute();
    }
}