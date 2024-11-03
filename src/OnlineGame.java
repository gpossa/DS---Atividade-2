import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;

public class OnlineGame {
    private final Stage primaryStage;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private char[] gameArray = new char[9];
    private int numPlays;
    private boolean gameEnded;
    private boolean clientTurn;
    private final String host;
    private UDPComm comm;

    public OnlineGame(Stage primaryStage, Player player1, Player player2, boolean clientTurn, String host) {
        this.primaryStage = primaryStage;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.clientTurn = clientTurn;
        this.host = host;
        initializeGame();
        createGameScene();
    }

    private void initializeGame() {
        comm = new UDPComm(host, 2020);
        numPlays = 0;
        gameEnded = false;
        Arrays.fill(gameArray, ' ');
        System.out.println(host);
    }

    public void createGameScene() {
        VBox vbox = new VBox();
        GridPane gridPane = new GridPane();
        HBox hbox = new HBox();

        Label currentPlayerLabel = new Label(getCurrentPlayerText());
        Label scoreboardLabel = new Label(getScoreboardText());

        Button restartButton  = new Button("Nova partida");
        Button mainMenuButton = new Button("Menu inicial");

        restartButton.setDisable(true);
        mainMenuButton.setDisable(true);

        Button[][] gridButtons = createGridButtons(gridPane, currentPlayerLabel, restartButton, mainMenuButton, scoreboardLabel);

        receivePlay(gridButtons, currentPlayerLabel);

        restartButton.setOnAction(e -> resetGame(gridButtons, currentPlayerLabel, restartButton, mainMenuButton));
        mainMenuButton.setOnAction(e -> new Menu(primaryStage).mainMenu());

        vbox.setStyle("-fx-spacing: 10; -fx-alignment: center;");
        gridPane.setStyle("-fx-alignment: center; -fx-hgap: 5; -fx-vgap: 5; -fx-padding: 20;");
        currentPlayerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        scoreboardLabel.setStyle("-fx-font-size: 14px; -fx-fill: black; -fx-font-weight: bold;");
        hbox.setStyle("-fx-alignment: center; -fx-spacing: 10;");

        hbox.getChildren().addAll(restartButton, mainMenuButton);
        vbox.getChildren().addAll(currentPlayerLabel, scoreboardLabel, gridPane, hbox);

        primaryStage.setScene(new Scene(vbox, 500, 400));
        primaryStage.show();
    }

    private Button[][] createGridButtons(GridPane gridPane, Label currentPlayerLabel, Button restartButton, Button menuButton, Label scoreboardLabel) {
        Button[][] buttons = new Button[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                Button button = new Button();

                button.setPrefSize(70, 70);
                button.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
                button.setOnAction(e -> handleButtonClick(button, index, currentPlayerLabel, restartButton, menuButton, scoreboardLabel));
                buttons[i][j] = button;

                gridPane.add(button, j, i);
            }
        }

        return buttons;
    }

    private void handleButtonClick(Button button, int index, Label currentPlayerLabel, Button restartButton, Button menuButton, Label scoreboardLabel) {
        if (gameArray[index] == ' ' && !gameEnded && clientTurn) {
            button.setText(String.valueOf(currentPlayer.getTeam()));
            gameArray[index] = currentPlayer.getTeam();
            numPlays++;

            sendPlay();

            checkForWinner(restartButton, menuButton, scoreboardLabel);

            if (!gameEnded) {
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
                currentPlayerLabel.setText(getCurrentPlayerText());
            }
        }
    }

    private void sendPlay() {
        if (clientTurn) {
            comm.setMessage(comm.charToByte(gameArray));
            comm.sendMessage();
            System.out.println("Send: " + comm.getMessageStr());
            clientTurn = false;
        }
    }

    private void receivePlay(Button[][] boardButtons, Label currentPlayerLabel) {
        new Thread(() -> {
            while (!gameEnded) {
                if (!clientTurn && comm.receiveMessage()) {
                    Platform.runLater(() -> {
                        System.out.println("Receive: " + comm.getMessageStr());

                        gameArray = comm.getJogada();

                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                boardButtons[i][j].setText(String.valueOf(comm.getJogada()[i * 3 + j]));
                            }
                        }

                        currentPlayer = currentPlayer == player1 ? player2 : player1;
                        currentPlayerLabel.setText(getCurrentPlayerText());
                        clientTurn = true;
                    });
                }
                try {
                    Thread.sleep(100); // Pequena pausa para não sobrecarregar a CPU
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void checkForWinner(Button restartButton, Button menuButton, Label scoreboardLabel) {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] condition : winConditions) {
            if (gameArray[condition[0]] != ' ' && gameArray[condition[0]] == gameArray[condition[1]] && gameArray[condition[1]] == gameArray[condition[2]]) {
                Player winner = (gameArray[condition[0]] == player1.getTeam()) ? player1 : player2;

                showAlert(winner.getName());
                winner.setWins(winner.getWins() + 1);
                gameEnded = true;

                restartButton.setDisable(false);
                menuButton.setDisable(false);

                scoreboardLabel.setText(getScoreboardText());

                return;
            }
        }

        if (numPlays == 9) {
            showAlert("Empate");
            gameEnded = true;

            restartButton.setDisable(false);
            menuButton.setDisable(false);
        }
    }

    private void resetGame(Button[][] boardButtons, Label currentPlayerLabel, Button restartButton, Button menuButton) {
        initializeGame();

        for (Button[] row : boardButtons) {
            for (Button button : row) {
                button.setText(" ");
                button.setDisable(false);
            }
        }

        restartButton.setDisable(true);
        menuButton.setDisable(true);

        currentPlayer = currentPlayer == player1 ? player2 : player1;
        currentPlayerLabel.setText(getCurrentPlayerText());
    }

    private String getCurrentPlayerText() {
        return "JOGADOR ATUAL: " + currentPlayer.getName() + " (" + currentPlayer.getTeam() + ")";
    }

    private String getScoreboardText() {
        return player1.getName() + " " + player1.getWins() + " x " + player2.getWins() + " " + player2.getName();
    }

    private void showAlert(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("FIM DE JOGO");
        alert.setHeaderText(winnerName.equals("Empate") ? "Empate!" : "Temos um vencedor!");
        alert.setContentText(winnerName.equals("Empate") ? "A partida terminou em empate!" : "Parabéns, " + winnerName + "!");
        alert.showAndWait();
    }
}
