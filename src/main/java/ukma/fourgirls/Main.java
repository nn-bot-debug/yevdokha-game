package ukma.fourgirls;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ukma.fourgirls.presentation.view.menu.MainMenuScreen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameContext context = new GameContext(primaryStage);

        primaryStage.setTitle("YEVDOKHA-GAME");
        primaryStage.setFullScreen(true);

        MainMenuScreen mainMenu = new MainMenuScreen(context);
        Scene mainScene = new Scene(mainMenu.getRoot());

        context.getScene().setMainMenuRoot(mainMenu.getRoot());

        primaryStage.setScene(mainScene);
        context.getAudio().playBackgroundMusic("/music/background.mp3");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}