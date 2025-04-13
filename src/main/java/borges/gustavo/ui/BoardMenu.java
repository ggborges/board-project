package borges.gustavo.ui;

import borges.gustavo.dto.BoardColumnInfoDTO;
import borges.gustavo.persistence.entity.BoardColumnEntity;
import borges.gustavo.persistence.entity.BoardEntity;
import borges.gustavo.persistence.entity.CardEntity;
import borges.gustavo.service.BoardColumnQueryService;
import borges.gustavo.service.BoardQueryService;
import borges.gustavo.service.CardQueryService;
import borges.gustavo.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static borges.gustavo.persistence.config.ConnectionConfig.getConnection;
import static borges.gustavo.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem vindo ao Board %s, selecione a operação desejada:\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um novo Card.");
                System.out.println("2 - Mover um Card.");
                System.out.println("3 - Bloquear um Card.");
                System.out.println("4 - Desbloquear um Card.");
                System.out.println("5 - Cancelar um Card.");
                System.out.println("6 - Visualizar Board.");
                System.out.println("7 - Visualizar colunas com Cards.");
                System.out.println("8 - Visualizar Cards.");
                System.out.println("9 - Voltar ao menu anterior.");
                System.out.println("10 - Sair.");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltar ao menu anterior.");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida, tente novamente.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Digite o título do novo Card: ");
        card.setTitle(scanner.next());
        System.out.println("Digite a descrição do novo Card: ");
        card.setDescription(scanner.next());
        var initialColumn = entity.getInitialColumn();
        card.setBoardColumn(initialColumn);
        try (var connection = getConnection()) {
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Digite ID do Card que deseja mover:");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc ->  new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Digite o ID do Card que deseja bloquear:");
        var cardId = scanner.nextLong();
        System.out.println("Digite o motivo do bloqueio:");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc ->  new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).block(reason, cardId, boardColumnsInfo);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Digite o ID do Card que deseja bloquear:");
        var cardId = scanner.nextLong();
        System.out.println("Digite o motivo do bloqueio:");
        var reason = scanner.next();
        try (var connection = getConnection()) {
            new CardService(connection).unblock(reason, cardId);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Digite ID do Card que deseja cancelar:");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc ->  new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(),boardColumnsInfo);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
               System.out.printf("Board [%s,%s]\n", b.id(), b.name());
               b.columns().forEach(column -> {
                   System.out.printf("Coluna [%s] do tipo \"[%s]\" tem [%s] cards\n", column.name(), column.kind(), column.cards_amount());
               });
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do Board %s\n", entity.getName());
            entity.getBoardColumns().forEach(column -> System.out.printf("%s - %s [%s]\n", column.getId(), column.getName(), column.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(b -> {
                System.out.printf("Coluna %s tipo %s\n", b.getName(), b.getKind());
                b.getCards().forEach(card -> System.out.printf("Card [%s] - %s\nDescrição: %s\n",
                        card.getId(), card.getTitle(), card.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Digite o id do card que deseja visualizar: ");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - [%s]\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ? "Bloqueado! Motivo: " + c.blockReason() : "Não bloqueado.");
                                System.out.printf("Já foi bloqueado %s vezes.\n", c.blocksAmount());
                                System.out.printf("No momento encontra-se %s - %s.\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Card %s não encontrado!\n", selectedCardId));
        }
    }
}
