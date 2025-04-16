module game {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    opens game to javafx.fxml;
    exports game;
}
