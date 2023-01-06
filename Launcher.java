//Contains main... run me to launch the game!
public class Launcher {

    // Initializes and launches the game
    public static void main(String[] args) {
        // ScrollingGameEngine game = new SimpleGame(1000, 1000);
        // ScrollingGameEngine game = new SimpleGame();
        // game.play();
        SimpleGame game = new MalcolmsonGame();
        game.play();

    }

}
