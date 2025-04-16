package game;
import java.util.LinkedList;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Snake {
    private final LinkedList<Pane> snake;
    private final Pane[][] paneArray;

    //sets up the snake when it starts(3 squares long and blue)
    public Snake(Pane[][] paneArray) {
        this.paneArray = paneArray;
        this.snake = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Pane pane = paneArray[7] [i + 3];
            pane.setStyle("-fx-background-color: blue;");
            snake.add(pane);
        }
    }
    public LinkedList<Pane> getSnake() {
        return snake;
    }
    public Pane head() {
        return snake.getLast();
    }
    public boolean contains(Pane pane) {
        return snake.contains(pane);
    }
    //when the snake eats food it grows
    public void grow(Pane next) {
        next.setStyle("-fx-background-color: blue;");
        snake.add(next);
    }
    //moves the head forward and gets rid of the tail on the previous tile(sets it back to the green color that corresponds with it)
    public void move(Pane next) {
        grow(next);
        Pane tail = snake.removeFirst();
        int row = GridPane.getRowIndex(tail);
        int column = GridPane.getColumnIndex(tail);
        if ((row + column) % 2 == 0) {
            tail.setStyle("-fx-background-color: limegreen;");
        } else {
            tail.setStyle("-fx-background-color: lightgreen;");
        }
    }
}
