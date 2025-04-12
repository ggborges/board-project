package borges.gustavo.service;

import borges.gustavo.persistence.dao.BoardColumnDAO;
import borges.gustavo.persistence.dao.BoardDAO;
import borges.gustavo.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var boardDao = new BoardDAO(connection);
        var boardColumnDao = new BoardColumnDAO(connection);
        var optional = boardDao.findById(id);
        if (optional.isPresent()) {
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDao.findByBoardId(entity.getId()));
            return Optional.of(entity);
        } else {

        }

        return Optional.empty();
    }
}
