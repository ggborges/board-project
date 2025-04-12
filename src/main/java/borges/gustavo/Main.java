package borges.gustavo;

import borges.gustavo.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import static borges.gustavo.persistence.config.ConnectionConfig.getConnection;


public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}