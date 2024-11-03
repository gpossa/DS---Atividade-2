import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu {
    private final Stage primaryStage;
    private final int controlsWidth = 200;

    public Menu(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainMenu();
    }

    public void mainMenu() {
        VBox vbox = new VBox();

        Label titleLabel = new Label("JOGO DA VELHA");

        Button newGameButton = new Button("Novo jogo");
        Button rulesButton = new Button("Regras");
        Button creditsButton = new Button("Créditos");

        newGameButton.setOnAction(e -> newGameScene());
        rulesButton.setOnAction(e -> rulesStage());
        creditsButton.setOnAction(e -> creditsStage());

        newGameButton.setMaxWidth(controlsWidth);
        rulesButton.setMaxWidth(controlsWidth);
        creditsButton.setMaxWidth(controlsWidth);

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-fill: black; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, newGameButton, rulesButton, creditsButton);

        primaryStage.setTitle("JOGO DA VELHA");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(vbox, 500, 400));
        primaryStage.show();
    }

    private void newGameScene() {
        VBox vbox = new VBox();

        Label newGameLabel = new Label("NOVO JOGO");

        Button offlineGameButton = new Button("Jogar offline");
        Button onlineGameButton = new Button("Jogar online");
        Button backButton = new Button("Voltar");

        offlineGameButton.setOnAction(e -> offlineGameScene());
        onlineGameButton.setOnAction(e -> connectionTypeScene());
        backButton.setOnAction(e -> mainMenu());

        offlineGameButton.setMaxWidth(controlsWidth);
        onlineGameButton.setMaxWidth(controlsWidth);
        backButton.setMaxWidth(controlsWidth);

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        newGameLabel.setStyle("-fx-font-size: 24px; -fx-fill: black; -fx-font-weight: bold;");

        vbox.getChildren().addAll(newGameLabel, offlineGameButton, onlineGameButton, backButton);

        primaryStage.setScene(new Scene(vbox, 500, 400));
    }

    private void offlineGameScene() {
        VBox vbox = new VBox();

        Label titleLabel = new Label("INFORMAÇÕES DA PARTIDA");
        Label player1Label = new Label("Nome do Jogador 1 (Time X)");
        Label player2Label = new Label("Nome do Jogador 2 (Time O)");

        TextField player1TextField = new TextField();
        TextField player2TextField = new TextField();

        player1TextField.setPromptText("Jogador1");
        player2TextField.setPromptText("Jogador2");

        Button startGameButton = new Button("Iniciar jogo");

        startGameButton.setOnAction(e -> {
            String player1Name = player1TextField.getText().isEmpty() ? "Jogador1" : player1TextField.getText();
            String player2Name = player2TextField.getText().isEmpty() ? "Jogador2" : player2TextField.getText();

            Player player1 = new Player(player1Name, 'X');
            Player player2 = new Player(player2Name, 'O');

            new OfflineGame(primaryStage, player1, player2);
        });

        player1TextField.setMaxWidth(controlsWidth);
        player2TextField.setMaxWidth(controlsWidth);
        startGameButton.setMaxWidth(controlsWidth);

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-fill: black; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, player1Label, player1TextField, player2Label, player2TextField, startGameButton);

        primaryStage.setScene(new Scene(vbox, 500, 400));
    }

    private void connectionTypeScene() {
        VBox vbox = new VBox();

        Label titleLabel = new Label("TIPO DE CONEXÃO");

        Button waitForConnectionButton = new Button("Aguardar conexão (TIME X)");
        Button startConnectionButton = new Button("Iniciar conexão (TIME O)");
        Button backButton = new Button("Voltar");

        waitForConnectionButton.setOnAction(e -> waitForConnectionScene());
        startConnectionButton.setOnAction(e -> startConnectionScene());
        backButton.setOnAction(e -> newGameScene());

        waitForConnectionButton.setMaxWidth(controlsWidth);
        startConnectionButton.setMaxWidth(controlsWidth);
        backButton.setMaxWidth(controlsWidth);

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-fill: black; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, waitForConnectionButton, startConnectionButton, backButton);

        primaryStage.setScene(new Scene(vbox, 500, 400));
    }

    private void waitForConnectionScene() {
        VBox vbox = new VBox();

        Label titleLabel = new Label("AGUARDAR CONEXÃO");
        Label player1Label = new Label("Nome do Jogador 1 (Time X)");
        Label player2IPLabel = new Label("Endereço IP do Jogador 2");

        TextField player1TextField = new TextField();
        TextField player2IPTextField = new TextField();

        player1TextField.setPromptText("Jogador1");
        player2IPTextField.setPromptText("IP");

        Button startListeningButton = new Button("Iniciar espera");

        startListeningButton.setOnAction(e -> {
            UDPComm server = new UDPComm(1332);
            String host = player2IPTextField.getText();

            if (server.receiveMsg()) {
                String message = server.getMsgStr();
                String player1Name = player1TextField.getText().isEmpty() ? "Jogador1" : player1TextField.getText();
                String player2Name = message.substring(1);

                Player player1 = new Player(player1Name, 'X');
                Player player2 = new Player(player2Name, 'O');

                System.out.println("Recebido: " + message);

                server.setMsg(server.charToByte((player1.getTeam() + player1.getName()).toCharArray()));
                server.sendMsg();

                System.out.println(host);

                UDPComm commOut = new UDPComm(host, 1332);
                message = player2.getTeam() + player2.getName();
                commOut.setMsg(commOut.charToByte(message.toCharArray()));
                commOut.sendMsg();
            }
            else {
                System.out.println("fuck");
            }

        });

        player1TextField.setMaxWidth(controlsWidth);
        player2IPTextField.setMaxWidth(controlsWidth);
        startListeningButton.setMaxWidth(controlsWidth);

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-fill: black; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, player1Label, player1TextField, player2IPLabel, player2IPTextField, startListeningButton);

        primaryStage.setScene(new Scene(vbox, 500, 400));
    }

    private void startConnectionScene() {
        VBox vbox = new VBox();

        Label titleLabel = new Label("INICIAR CONEXÃO");
        Label player2Label = new Label("Nome do Jogador 2 (Time O)");
        Label player1IPLabel = new Label("Endereço IP do Jogador 1");

        TextField player2TextField = new TextField();
        TextField player1IPTextField = new TextField();

        player2TextField.setPromptText("Jogador2");
        player1IPTextField.setPromptText("IP");

        Button startSendingButton = new Button("Iniciar conexão");

        startSendingButton.setOnAction(e -> {
            startSendingButton.setDisable(true);
            player2TextField.setDisable(true);
            player1IPTextField.setDisable(true);

            String player2Name = player2TextField.getText().isEmpty() ? "Jogador2" : player2TextField.getText();

            Player player2 = new Player(player2Name, 'O');

            String host = player1IPTextField.getText();

            UDPComm client = new UDPComm(host, 1332);

            String msg = player2.getTeam() + player2.getName();
            client.setMsg(client.charToByte(msg.toCharArray()));
            client.sendMsg();

            UDPComm commIn = new UDPComm(1332);
            if (commIn.receiveMsg()) {
                msg = commIn.getMsgStr();
                String player1Name = msg.substring(1);
                Player player1 = new Player(player1Name, 'X');
            }
        });

        player2TextField.setMaxWidth(controlsWidth);
        player1IPTextField.setMaxWidth(controlsWidth);
        startSendingButton.setMaxWidth(controlsWidth);

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-fill: black; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, player2Label, player2TextField, player1IPLabel, player1IPTextField, startSendingButton);

        primaryStage.setScene(new Scene(vbox, 500, 400));
    }

    private void rulesStage() {
        Stage stage = new Stage();
        VBox vbox = new VBox();

        Label[] rulesLabel = new Label[8];

        String[] strRules = {"1. TABULEIRO: O jogo é jogado em um tabuleiro de 3x3, com nove espaços.",
                "2. JOGADORES: Há dois jogadores. Um usa o símbolo 'X' e o outro usa o símbolo 'O'.",
                "3. OBJETIVO: O objetivo do jogo é ser o primeiro a formar uma linha de três símbolos iguais, que pode ser na horizontal, vertical ou diagonal.",
                "4. TURNOS: Os jogadores se alternam fazendo suas jogadas. O jogador 'X' geralmente começa, mas isso pode ser decidido por acordo prévio.",
                "5. JOGADAS: Em cada turno, um jogador coloca seu símbolo (X ou O) em um dos espaços vazios do tabuleiro.",
                "6. VITÓRIA: Um jogador vence se conseguir alinhar três de seus símbolos (X ou O) consecutivamente em uma linha, seja na horizontal, vertical ou diagonal.",
                "7. EMPATE: Se todos os nove espaços forem preenchidos e nenhum jogador tiver formado uma linha de três símbolos, o jogo termina em empate.",
                "8. FIM DE JOGO: O jogo termina quando um jogador vence ou quando há um empate."};

        for (int i = 0; i < 8; i++) {
            rulesLabel[i] = new Label(strRules[i]);
            rulesLabel[i].setStyle("-fx-wrap-text: true; -fx-text-alignment: justify; -fx-label-padding: 4 0 4 0;");
        }

        vbox.setStyle("-fx-padding: 15; -fx-alignment: center;");

        vbox.getChildren().addAll(rulesLabel);

        stage.setTitle("REGRAS");
        stage.setResizable(false);
        stage.setScene(new Scene(vbox, 435, 370));
        stage.show();
    }

    private void creditsStage() {
        Stage stage = new Stage();
        VBox vbox = new VBox();

        Label developedByLabel = new Label("Desenvolvido por");
        Label nameLabel = new Label("GABRIEL POSSA");

        vbox.setStyle("-fx-padding: 15; -fx-alignment: center;");
        nameLabel.setStyle("-fx-font-weight: bold;");

        vbox.getChildren().addAll(developedByLabel, nameLabel);

        stage.setTitle("CRÉDITOS");
        stage.setResizable(false);
        stage.setScene(new Scene(vbox, 150, 100));
        stage.show();
    }
}
