import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MalcolmsonGame extends SimpleGame {
    // Dimensions of game window
    private static final int DEFAULT_WIDTH = 960;
    private static final int DEFAULT_HEIGHT = 540;

    // Starting PlayerEntity coordinates
    private static final int STARTING_PLAYER_X = 0;
    private static final int STARTING_PLAYER_Y = 100;

    // Score needed to win the game
    private static final int SCORE_TO_WIN = 300; // 15 get objects

    // Maximum that the game speed can be increased to
    // (a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    private static final int MAX_GAME_SPEED = 300;
    // Interval that the speed changes when pressing speed up/down keys
    private static final int SPEED_CHANGE = 20;

    private static final String INTRO_SPLASH_FILE = "assets/STATIC_STARTER.png";
    private static final String INFO_SPLASH_FILE = "assets/GAME_INTRO.png";
    // Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;

    // Interval that Entities get spawned in the game window
    // ie: once every how many ticks does the game attempt to spawn new Entities
    private static final int SPAWN_INTERVAL = 45;

    // A Random object for all your random number generation needs!
    public static final Random rand = new Random();

    // MY VARIABLES .......................................
    private int count = 1;
    private static final int AVOID_CHANCE = 60;
    private static final int GET_CHANCE = 75;
    private static final int RARE_GET_CHANCE = 90;
    private static final int MIN_GAME_SPEED = 20;
    // DURATION OF TIME POWER UP LASTS
    private static final int RESET_APPEARENCE_INTERVAL = 200;
    private static final int HUNGER_TICKS = 450;

    // Player's current score
    private int score;
    private int num_enter = 0;

    // Stores a reference to game's PlayerEntity object for quick reference
    // (This PlayerEntity will also be in the displayList)
    private PlayerEntity player;

    public MalcolmsonGame() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public MalcolmsonGame(int gameWidth, int gameHeight) {
        super(gameWidth, gameHeight);
    }

    // Performs all of the initialization operations
    protected void preGame() {
        // this.setBackgroundColor(Color.BLACK);
        this.setBackgroundImage("assets/testbackground.png");
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
        if (ticksElapsed % HUNGER_TICKS == 0) {
            player.increaseHunger();
        }
        // checks if player is currently Invincible and if 120 ticks have passed
        if (player.returnInvincibilityStatus()
                && (player.returnInvincibilityStart() + RESET_APPEARENCE_INTERVAL == ticksElapsed)) {
            player.setDefaultAppearnace();
        }
        // Update the title text on the top of the window
        setTitleText("HP: " + player.updateHealth() + " pts |  Crystals: " + Math.abs(score / 20) + " | Stamina: "
                + player.getStamina() + " pts");
    }

    // Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities() {
        for (int i = 1; i < displayList.size(); i++) {
            if (displayList.get(i) instanceof Scrollable) {
                ((Scrollable) displayList.get(i)).scroll();
            }
        }
    }

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
        if (!player.returnInvincibilityStatus() && (collidedWith instanceof AvoidEntity)) {
            if (!player.isHungry()) {
                player.modifyHP(collidedWith.getDamageValue());
                score -= 20; // anytime they collide with asteroid, they lose a crystal
            } else if (player.isHungry() && player.getStamina() == 5)
                player.increaseHunger(); // allows collision, but doesn't modify health only decrements stamina
            // System.out.println("Ouch!");
        } else if (collidedWith instanceof RegFoodEntity) {
            player.processFood(((RegFoodEntity) collidedWith).getPointsValue());
        } else if (collidedWith instanceof RareGetEntity) {
            player.modifyHP(((RareGetEntity) collidedWith).getHealthPts());
            player.processFood(((RareGetEntity) collidedWith).getPointsValue());
            player.setInvincible();
            player.startPowerTimer(ticksElapsed);
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
                if (chances % 5 == 0) {
                    displayList.add(new RegFoodEntity(this.getWindowWidth(), randY));
                } else {
                    displayList.add(new AvoidEntity(this.getWindowWidth(), randY));
                }
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
        int avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 69))); // these numbers are for the max height of
                                                                           // any entity
        while (avoidRandY >= prevY && avoidRandY <= prevYHeight) { // if new Y is inside prev entity
            avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 69)));
        }
        return avoidRandY;
    }

    private int gen3rdValidY(int y1, int yH1, int y2, int yH2) {
        int avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 69)));
        // prevents new Y from spawning inside either previously generated entities
        while ((avoidRandY >= y1 && avoidRandY <= yH1) || ((avoidRandY >= y2 && avoidRandY <= yH2))) {
            avoidRandY = rand.nextInt(0, ((this.getWindowHeight() - 69)));
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
        if (getSplashImage() != null) {
            if (key == ADVANCE_SPLASH_KEY && num_enter == 0) {
                num_enter += 1;
                this.setSplashImage(INFO_SPLASH_FILE);
            } else if (key == ADVANCE_SPLASH_KEY && num_enter == 1)
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
            player.setY((Math.min((player.getY() + player.getMovementSpeed()), (this.getWindowHeight() - 60))));
        }
        if (key == RIGHT_KEY) { // X gets larger
            player.setX(Math.min((player.getX() + player.getMovementSpeed()), (this.getWindowWidth() - 60)));
        }
        if (key == LEFT_KEY) { // X gets smaller
            player.setX(Math.max((player.getX() - player.getMovementSpeed()), -1));
        }
    }

    protected MouseEvent handleMouseClick(MouseEvent click) {
        if (click != null) { // ensure a mouse click occurred
            int clickX = click.getX();
            int clickY = click.getY();
            setDebugText("Click at: " + clickX + ", " + clickY);
        }
        return click;// returns the mouse event for any child classes overriding this method
    }
}
