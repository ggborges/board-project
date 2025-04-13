package borges.gustavo.ui;

import borges.gustavo.persistence.entity.BoardEntity;
import borges.gustavo.service.BoardQueryService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static borges.gustavo.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem vindo ao Board %s, selecione a operação desejada: ", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um novo Card.");
                System.out.println("2 - Mover um Card.");
                System.out.println("3 - Bloquear um Card.");
                System.out.println("4 - Desbloquear um Card.");
                System.out.println("5 - Cancelar um Card.");
                System.out.println("6 - Voltar ao menu anterior.");
                System.out.println("7 - Visualizar Board.");
                System.out.println("8 - Visualizar colunas com Cards.");
                System.out.println("9 - Visualizar Cards.");
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

    private void createCard() {

    }

    private void moveCardToNextColumn() {

    }

    private void blockCard() {

    }

    private void unblockCard() {

    }

    private void cancelCard() {

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

    private void showColumn() {

    }

    private void showCard() {

    }
}
