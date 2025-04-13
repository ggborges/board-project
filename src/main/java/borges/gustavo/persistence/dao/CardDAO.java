package borges.gustavo.persistence.dao;

import borges.gustavo.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardDetailsDTO findById(final Long id) throws SQLException {
        return null;
    }
}
