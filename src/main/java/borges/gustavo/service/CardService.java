package borges.gustavo.service;

import borges.gustavo.dto.BoardColumnInfoDTO;
import borges.gustavo.dto.CardDetailsDTO;
import borges.gustavo.exception.CardBlockedException;
import borges.gustavo.exception.CardFinishedException;
import borges.gustavo.exception.EntityNotFoundException;
import borges.gustavo.persistence.dao.BlockDAO;
import borges.gustavo.persistence.dao.CardDAO;
import borges.gustavo.persistence.entity.BoardColumnKindEnum;
import borges.gustavo.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static borges.gustavo.persistence.entity.BoardColumnKindEnum.CANCEL;
import static borges.gustavo.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card %s não encontrado.".formatted(cardId))
            );
            if (dto.blocked()){
                throw new CardBlockedException("Card %s não pode ser movido, pois está bloqueado.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Card pertence a outro board."));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card finalizado.");
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                            .findFirst().orElseThrow(() -> new IllegalStateException("Card cancelado."));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId,
                       final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card %s não encontrado.".formatted(cardId))
            );
            if (dto.blocked()){
                throw new CardBlockedException("Card %s não pode ser movido, pois está bloqueado.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Card pertence a outro board."));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card finalizado.");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card cancelado."));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void block(final String reason, final Long cardId,
                      final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card %s não encontrado.".formatted(cardId))
            );
            if (dto.blocked()){
                throw new CardBlockedException("Card %s já está bloqueado.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                            .filter(bc -> bc.id().equals(dto.columnId()))
                            .findFirst()
                            .orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                var message = "Card não pode ser bloqueado. MOTIVO: Está em %s."
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void unblock(final String reason, final Long cardId) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card %s não encontrado.".formatted(cardId))
            );
            if (!dto.blocked()){
                throw new CardBlockedException("Card %s não está bloqueado.".formatted(cardId));
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

    }
}
