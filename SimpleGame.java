import java.awt.*;
import java.awt.event.*;
import java.util.*;

//A Simple version of the scrolling game, featuring Avoids, Gets, and RareGets
//Players must reach a score threshold to win
//If player runs out of HP (via too many Avoid collisions) they lose
public class SimpleGame extends ScrollingGameEngine {

    // Dimensions of game window
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 600;

    // Starting PlayerEntity coordinates
    private static final int STARTING_PLAYER_X = 0;
    private static final int STARTING_PLAYER_Y = 100;

    // Score needed to win the game
    private static final int SCORE_TO_WIN = 300;

    // Maximum that the game speed can be increased to
    // (a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    private static final int MAX_GAME_SPEED = 300;
    // Interval that the speed changes when pressing speed up/down keys
    private static final int SPEED_CHANGE = 20;

    private static final String INTRO_SPLASH_FILE = "assets/splash.gif";
    // Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;

    // Interval that Entities get spawned in the game window
    // ie: once every how many ticks does the game attempt to spawn new Entities
    private static final int SPAWN_INTERVAL = 45;

    // A Random object for all your random number generation needs!
    public static final Random rand = new Random();

    // MY VARIABLES
    private int count = 1;
    private static final int AVOID_CHANCE = 60;
    private static final int GET_CHANCE = 75;
    private static final int RARE_GET_CHANCE = 90;
    private static final int MIN_GAME_SPEED = 20;

    // Player's current score
    private int score;

    // Stores a reference to game's PlayerEntity object for quick reference
    // (This PlayerEntity will also be in the displayList)
    private PlayerEntity player;

    public SimpleGame() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public SimpleGame(int gameWidth, int gameHeight) {
        super(gameWidth, gameHeight);
    }

    // Performs all of the initialization operations that need to be done before the
    // game starts
    protected void preGame() {
        this.setBackgroundColor(Color.BLACK);
        this.setSplashImage(INTRO_SPLASH_FILE);
        player = new PlayerEntity(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player);
        score = 0;
    }

    protected void updateGame() {
        scrollEntities();
        ArrayList<Entity> collidedEntities = this.checkCollision(player);
        sendToHandleCollision(collidedEntities);

        deleteCollidedEntities();
        if (ticksElapsed % SPAWN_INTERVAL == 0) {
            spawnNewEntities();
            garbageCollectEntities();
        }

        // Update the title text on the top of the window
        setTitleText("HP: " + player.getHP() + " Score: " + score);
    }

    // Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities() {
        for (int i = 1; i < displayList.size(); i++) {
            if (displayList.get(i) instanceof Scrollable) {
                ((Scrollable) displayList.get(i)).scroll();
            }
        }
    }

    // Handles "garbage collection" of the displayList
    // Removes entities from the displayList that are no longer relevant
    // (i.e. will no longer need to be drawn in the game window).
    protected void garbageCollectEntities() {
        for (int i = 1; i < displayList.size();) {
            if (displayList.get(i).getX() < -75) {
                displayList.remove(i);
            }
            i++; // avoids skipping elements in the list
        }
    }

    protected void deleteCollidedEntities() {
        for (int i = 1; i < displayList.size();) {
            if (displayList.get(i).isCollidingWith(player)) {
                displayList.remove(i);
            }
            i++;
        }
    }

    protected void sendToHandleCollision(ArrayList<Entity> collidedEntities) {
        for (Entity collided : collidedEntities) {
            handlePlayerCollision((Consumable) collided);
        }
        collidedEntities.clear();
    }

    // Called whenever it has been determined that the PlayerEntity collided with a
    // consumable
    private void handlePlayerCollision(Consumable collidedWith) {
        if (collidedWith instanceof AvoidEntity) {
            player.modifyHP(collidedWith.getDamageValue());
            // System.out.println("Ouch!");
        } else if (collidedWith instanceof RareGetEntity) {
            score += (collidedWith.getPointsValue());
            player.modifyHP(((RareGetEntity) collidedWith).getHealthPts());
        } else if (collidedWith instanceof GetEntity) {
            score += (collidedWith.getPointsValue());
        }
    }

    // Spawn new Entities on the right edge of the game board
    private void spawnNewEntities() {
        int bound = rand.nextInt(3);
        int chances = rand.nextInt(0, 100);
        int y1 = 0;
        int y1Height = 0;
        int y2 = 0;
        int y2Height = 0;
        for (int i = 0; i <= bound; i++) {
            int randY = generateValidY(y1, y1Height);
            if (bound == 2 && i == 2) // re-route randY to different fn
                randY = gen3rdValidY(y1, y1Height, y2, y2Height);
            if (chances <= AVOID_CHANCE) {
                displayList.add(new AvoidEntity(this.getWindowWidth(), randY));
            } else if ((chances >= GET_CHANCE) && (chances < RARE_GET_CHANCE)) {
                displayList.add(new GetEntity(this.getWindowWidth(), randY));
            } else if ((chances > RARE_GET_CHANCE) && (i == 1)) {
                displayList.add(new RareGetEntity(this.getWindowWidth(), randY));
            }
            if (bound != 2) {
                y1 = randY - 75;
                y1Height = randY + 75;
            } else if (bound == 2) {
                if (i == 0) {
                    y1 = randY - 75;
                    y1Height = randY + 75;
                } else if (i == 1) {
                    y2 = randY - 75;
                    y2Height = randY + 75;
                }
            }
        }
    }

    private int generateValidY(int prevY, int prevYHeight) {
        int avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 74)));
        while (avoidRandY >= prevY && avoidRandY <= prevYHeight) { // if new Y is inside prev entity
            avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 74)));
        }
        return avoidRandY;
    }

    private int gen3rdValidY(int y1, int yH1, int y2, int yH2) {
        int avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 74)));
        // prevents new Y from spawning inside either previously generated entities
        while ((avoidRandY >= y1 && avoidRandY <= yH1) || ((avoidRandY >= y2 && avoidRandY <= yH2))) {
            avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 74)));
        }
        return avoidRandY;
    }

    // Called once the game is over, performs any end-of-game operations
    protected void postGame() {
        if (score >= SCORE_TO_WIN)
            super.setTitleText("GAME OVER! - YOU WIN");
        else
            super.setTitleText("GAME OVER! - YOU LOSE");
    }

    // Determines if the game is over or not
    // Game can be over due to either a win or lose state
    protected boolean isGameOver() {
        if (score >= SCORE_TO_WIN || player.getHP() <= 0) // game over if no HP or max score
            return true;
        else
            return false;

    }

    // Reacts to a single key press on the keyboard
    protected void handleKeyPress(int key) {

        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key)); // confused how this is getting toggled...

        // if a splash screen is active, only react to the "advance splash" key...
        // nothing else!
        if (getSplashImage() != null) {
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            return;
        }
        if (key == KEY_PAUSE_GAME) {
            isPaused = ((count + 1) % 2 == 0);
            count++;
        }
        if (!isPaused) {
            movePlayer(key);
            changeSpeed(key);
        }
    }

    private void changeSpeed(int key) {
        if (key == SPEED_UP_KEY) {
            this.setGameSpeed(Math.min((this.getGameSpeed() + SPEED_CHANGE), MAX_GAME_SPEED));
        }
        if (key == SPEED_DOWN_KEY) {
            this.setGameSpeed(Math.max((this.getGameSpeed() - SPEED_CHANGE), MIN_GAME_SPEED));
        }
    }

    private void movePlayer(int key) {
        if (key == UP_KEY) { // Y gets smaller
            player.setY(Math.max((player.getY() - player.getMovementSpeed()), -1));
        }
        if (key == DOWN_KEY) { // Y gets larger
            player.setY((Math.min((player.getY() + player.getMovementSpeed()), (this.getWindowHeight() - 75))));
        }
        if (key == RIGHT_KEY) { // X gets larger
            player.setX(Math.min((player.getX() + player.getMovementSpeed()), (this.getWindowWidth() - 75)));
        }
        if (key == LEFT_KEY) { // X gets smaller
            player.setX(Math.max((player.getX() - player.getMovementSpeed()), -1));
        }
    }

    // Handles reacting to a single mouse click in the game window
    // Won't be used in Simple Game... you could use it in Creative Game though!
    protected MouseEvent handleMouseClick(MouseEvent click) {
        if (click != null) { // ensure a mouse click occurred
            int clickX = click.getX();
            int clickY = click.getY();
            setDebugText("Click at: " + clickX + ", " + clickY);
        }
        return click;// returns the mouse event for any child classes overriding this method
    }

}
