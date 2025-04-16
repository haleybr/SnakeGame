package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Controller{

    private Snake snake;

    @FXML
    private GridPane gameBoardGrid;
    private final Pane[][] paneArray;
    private final List<Pane> paneList;
    private boolean gameOn = false;

    private KeyCode currentDir = KeyCode.RIGHT;
    private KeyCode changeDir = KeyCode.RIGHT;

    private Circle food;
    private Random random;
    private Timeline gameLoop;
    private int score;
    private String scoreString;
    private int speed = 250;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private Button startButton;
    Label face = new Label(":)");

    //spawns the food in a random position on the board
    private void foodSpawn() {
        if (random == null) {
            random = new Random();
        }
        int randomRow = random.nextInt(14) ;
        int randomColumn = random.nextInt(14) ;
        //checks if the food spawned on the snake body
        while (foodOverSnake(randomRow, randomColumn)) {
            //if the food /does/ spawn on the snakes body, it runs again until it doesnt
            randomRow = random.nextInt(14) ;
            randomColumn = random.nextInt(14) ;
        }
        //actually spawns in the food(red circle)
        food = new Circle(15);
        food.setFill(Color.RED);
        Pane foodPane = paneArray[randomRow][randomColumn];
        //gets rid of the old food, also centers the food on the tile
        foodPane.getChildren().clear();
        foodPane.getChildren().add(food);
        foodPane.layout();
        food.centerXProperty().bind(foodPane.widthProperty().divide(2));
        food.centerYProperty().bind(foodPane.heightProperty().divide(2));
    }
    //checks to see if the food spawns on the snake body(returns true or false)
    private boolean foodOverSnake(int row, int column) {
        for (Pane snakeSegment : snake.getSnake()) {
            int snakeRow = GridPane.getRowIndex(snakeSegment);
            int snakeColumn = GridPane.getColumnIndex(snakeSegment);
            if (snakeRow == row && snakeColumn == column) {
                return true;
            }
        }
        return false;
    }
    //grid
    public Controller() {
        paneList = new ArrayList<>();
        paneArray = new Pane[15][15];
    }
    //initializes the game board
    public void initGame() {
        for (int i = 0; i < 15; i++) {
            for (int f = 0; f <15; f++) {
                Pane pane = new Pane();
                paneList.add(pane);
                paneArray[i][f] = pane;
                String green = "-fx-background-color: limegreen;",
                lightGreen = "-fx-background-color: lightgreen;";
                if (i % 2 ==0) {
                    if (f % 2 == 0) {
                        pane.setStyle(green);
                    } else {
                        pane.setStyle(lightGreen);
                    }
                } else {
                    if (f % 2 != 0) {
                        pane.setStyle(green);
                    } else {
                        pane.setStyle(lightGreen);
                    }
                }
                GridPane.setRowIndex(pane, i);
                GridPane.setColumnIndex(pane, f);
                gameBoardGrid.add(pane, f, i); 
        }
    }   
    }
    //starts the game when the button is clicked
    @FXML
    public void handleStartClick() {
        startButton.setVisible(false);
        gameOverLabel.setVisible(false);
        initGame();
        startGame();
        gameBoardGrid.requestFocus();
}
//handles moving, speed, direction, eating(growing), ending the game, and updates the score
private void startGame() {
    gameOverLabel.setText("Game Over!");
    speed = 250;
    snake = new Snake(paneArray);
    Pane startHead = snake.head();
    snakeDirection(startHead, currentDir);
    scoreLabel.setText("Score: ");
    changeDir = KeyCode.RIGHT;
    currentDir = KeyCode.RIGHT;
    score = 0;
    random = new Random();
    gameOn = true;
    foodSpawn();
    gameLoop = new Timeline(new KeyFrame(Duration.millis(speed), event -> {
        if (!gameOn) return;
        Pane head = snake.head();
        int row = GridPane.getRowIndex(head);
        int column = GridPane.getColumnIndex(head);
        currentDir = changeDir;
        switch(currentDir) {
            case UP:
                row--;
                break;
            case DOWN:
                row++;
                break;
            case LEFT:
                column--;
                break;
            case RIGHT:
                column++;
                break;
        }
        if (row < 0 || row >= 15 || column < 0 || column >= 15) {
            gameOver();
            gameLoop.stop();
            return;
        }
        Pane growHead = paneArray[row][column];
        if (snake.contains(growHead)) {
            gameOver();
            gameLoop.stop();
            return;
        }
        if (growHead.getChildren().contains(food)) {
            ((Pane) food.getParent()).getChildren().remove(food);
            score++;
            scoreString = Integer.toString(score);
            scoreLabel.setText("Score: " + scoreString);
            Pane changeHead = snake.head();
            changeHead.getChildren().clear();
            snake.grow(growHead);
            snakeDirection(growHead, currentDir);
            foodSpawn();
            if (score% 10 == 0 && speed > 50) {
                speed -= 25;
                gameLoop.stop();
                gameLoop = new Timeline(new KeyFrame(Duration.millis(speed), this::gameStep));
                gameLoop.setCycleCount(Timeline.INDEFINITE);
                gameLoop.play();
            }
        } else {
            Pane changeHead = snake.head();
            changeHead.getChildren().clear();
            snake.move(growHead);
            snakeDirection(growHead, currentDir);
        }
    }));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.play();
}
//moves the snake when direction changes
@FXML 
void move(KeyEvent e) {
    if (e.getCode().equals(KeyCode.UP) && currentDir != KeyCode.DOWN) {
        changeDir = KeyCode.UP;
    } else if (e.getCode().equals(KeyCode.DOWN) && currentDir != KeyCode.UP) {
        changeDir = KeyCode.DOWN;
    } else if (e.getCode().equals(KeyCode.LEFT) && currentDir != KeyCode.RIGHT) {
        changeDir = KeyCode.LEFT;
    } else if (e.getCode().equals(KeyCode.RIGHT) && currentDir != KeyCode.LEFT) {
        changeDir = KeyCode.RIGHT;
    }
}
//game ends, displays Game Over text and displays the start button again to restart
private void gameOver() {
    gameOn = false;
    if (score == 222) {
        gameOverLabel.setText("YOU WIN!!!!");
        gameOverLabel.setVisible(true);
        startButton.setVisible(true);
    } else {
        gameOverLabel.setAlignment(Pos.CENTER);
        gameOverLabel.setVisible(true);
        startButton.setVisible(true);
    }
}
//when a key is pressed, it changes direction
@FXML
void onKeyPressed(KeyEvent e) {
    move(e);
}
//keeps the startGame() method going(moving, speed, direction, eating(growing), ending the game, and updates the score)
private void gameStep(javafx.event.ActionEvent event) {
    if (!gameOn) return;
    Pane head = snake.head();
    int row = GridPane.getRowIndex(head);
    int column = GridPane.getColumnIndex(head);
    currentDir = changeDir;
    switch(currentDir) {
        case UP:
            row--;
            break;
        case DOWN:
            row++;
            break;
        case LEFT:
            column--;
            break;
        case RIGHT:
            column++;
            break;
    }
    if (row < 0 || row >= 15 || column < 0 || column >= 15) {
        gameOver();
        gameLoop.stop();
        return;
    }
    Pane growHead = paneArray[row][column];
    if (snake.contains(growHead)) {
        gameOver();
        gameLoop.stop();
        return;
    }
    if (growHead.getChildren().contains(food)) {
        ((Pane) food.getParent()).getChildren().remove(food);
        score++;
        Pane changeHead = snake.head();
        changeHead.getChildren().clear();
        snake.grow(growHead);
        snakeDirection(growHead, currentDir);
        scoreString = Integer.toString(score);
        scoreLabel.setText("Score: " + scoreString);
        foodSpawn();
        if (score % 10 == 0 && speed > 50) {
            speed -= 25;
            gameLoop.stop();
            gameLoop = new Timeline(new KeyFrame(Duration.millis(speed), this::gameStep));
            gameLoop.setCycleCount(Timeline.INDEFINITE);
            gameLoop.play();
        }
    } else {
        Pane changeHead = snake.head();
        changeHead.getChildren().clear();
        snake.move(growHead);
        snakeDirection(growHead, currentDir);
    }
}
//chnanges the direction of the face if the currect direction of the snake moves
private double faceRotation(KeyCode direction) {
    switch(direction) {
        case UP:
            return 270;
        case DOWN:
            return 90;
        case LEFT:
            return 180;
        case RIGHT:
            return 0;
        default: 
        return 0;
    }
}
//moves the snake's face (":)") with the snakes head
private void snakeDirection(Pane pane, KeyCode direction) {
    double faceX = pane.getWidth() / 2;
    double faceY = pane.getHeight() / 2;
    face.setLayoutX(faceX * 2 -15);
    face.setLayoutY(faceY - 20);
    face.setFont(javafx.scene.text.Font.font(25));
    face.setStyle("-fx-text-fill: white;");
    face.setRotate(faceRotation(direction));
    pane.getChildren().clear();
    pane.getChildren().add(face);
    if (faceRotation(direction) == 0) {
        face.setLayoutX(faceX * 2 -15);
        face.setLayoutY(faceY - 20);
    } else if (faceRotation(direction) == 90) {
        face.setLayoutX(faceX-5);
        face.setLayoutY(faceY - 5);
    } else if (faceRotation(direction) == 180) {
        face.setLayoutX(0);
        face.setLayoutY(faceY - 15);
    } else if (faceRotation(direction) == 270) {
        face.setLayoutX(faceX-10);
        face.setLayoutY(- 10);
    }
}
}
