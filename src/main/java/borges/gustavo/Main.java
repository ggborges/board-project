package borges.gustavo;

import borges.gustavo.persistence.migration.MigrationStrategy;
import borges.gustavo.ui.MainMenu;

import java.sql.SQLException;

import static borges.gustavo.persistence.config.ConnectionConfig.getConnection;


public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }

        new MainMenu().execute();
    }
}