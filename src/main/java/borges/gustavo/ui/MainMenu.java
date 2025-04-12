package borges.gustavo.ui;

import borges.gustavo.persistence.entity.BoardColumnEntity;
import borges.gustavo.persistence.entity.BoardColumnKindEnum;
import borges.gustavo.persistence.entity.BoardEntity;
import borges.gustavo.service.BoardQueryService;
import borges.gustavo.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static borges.gustavo.persistence.config.ConnectionConfig.getConnection;
import static borges.gustavo.persistence.entity.BoardColumnKindEnum.*;


public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards e cards desenvolvido durante o DecolaTech2025!\nEscolha a opção desejada");
        var option = -1;
        while(true){
            System.out.println("1 - Criar um novo Board.");
            System.out.println("2 - Selecionar um board existente.");
            System.out.println("3 - Excluir um board existente.");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch(option){
                case 1: createBoard();
                case 2: selectBoard();
                case 3: deleteBoard();
                case 4: System.exit(0);
                default: System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Digite o nome do novo board: ");
        var name = scanner.next();
        entity.setName(name);

        System.out.println("Quantas colunas deseja no seu Board? Digite 0 para usar apenas as 3 por padrão.");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Digite o nome da coluna inicial:");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Digite o nome da coluna de tarefa pendente:");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i+1);
            columns.add(pendingColumn);
        }

        System.out.println("Digite o nome da coluna final:");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns+1);
        columns.add(finalColumn);

        System.out.println("Digite o nome da coluna de cancelamento do Board:");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns+1);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Digite o id do Board para selecionar: ");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Board %s não encontrado!\n", id));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja excluir: ");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("Board %s excluido com sucesso!\n", id);
            } else {
                System.out.printf("Board %s não encontrado!\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
