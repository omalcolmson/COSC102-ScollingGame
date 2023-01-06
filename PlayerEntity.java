//The entity that the human player controls in the game window
//The player moves in reaction to player input
public class PlayerEntity extends Entity {

    // original sprite
    private static final String DEFAULT_PLAYER_IMAGE_FILE = "assets/original.gif";
    // invincible appearance
    private static final String INVINCIBLE_PLAYER_IMAGE_FILE = "assets/technyancolor.gif";
    // Dimensions of the PlayerEntity
    private static final int PLAYER_WIDTH = 70;
    private static final int PLAYER_HEIGHT = 60;
    // Default speed that the PlayerEntity moves (in pixels) each time the user
    private static final int DEFAULT_MOVEMENT_SPEED = 7;
    // Starting hit points
    private static final int STARTING_HP = 5;
    private static final int MAX_FOOD_STAMINA = 5;
    private boolean isInvincible = false;
    private boolean isHungry = false;
    // Variable that tracks the tick time when the power up begins
    private int INVINCIBILITY_START_TICK = 0;
    private int foodStamina = 0; // stamina starts at 0

    // Current movement speed
    private int movementSpeed;
    // Remaining Hit Points (HP) -- indicates the number of "hits" (ie collisions
    // with AvoidEntities) that the player can take before the game is over
    private int hp;

    public PlayerEntity() {
        this(0, 0);
    }

    public PlayerEntity(int x, int y) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, DEFAULT_PLAYER_IMAGE_FILE);
        this.hp = STARTING_HP;
        this.movementSpeed = DEFAULT_MOVEMENT_SPEED;
    }

    // Retrieve and set the PlayerEntity's current movement speed
    public int getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(int newSpeed) {
        this.movementSpeed = newSpeed;
    }

    // Retrieve the PlayerEntity's current HP
    public int getHP() {
        return hp;
    }

    // Set the player's HP to a specific value.
    // Returns an boolean indicating if PlayerEntity still has HP remaining
    public boolean setHP(int newHP) {
        this.hp = newHP;
        return (this.hp > 0);
    }

    // Set the player's HP to a specific value.
    // Returns an boolean indicating if PlayerEntity still has HP remaining
    public boolean modifyHP(int delta) {
        this.hp += delta;
        this.hp = Math.min(this.hp, 5); // prevents the Player from infinite health
        return (this.hp > 0);
    }

    // creates aesthetic health bar string
    public String updateHealth() {
        String hearts = "♥";
        String health = "";
        for (int i = 1; i <= hp; i++) {
            health += hearts;
        }
        return health;
    }

    // changes invincibility status
    public void setInvincible() {
        this.setX(this.getX());
        this.setY(this.getY());
        this.setImageName(INVINCIBLE_PLAYER_IMAGE_FILE);
        this.isInvincible = true;
    }

    // returns player appearance to default (once power up )
    public void setDefaultAppearnace() {
        this.setX(this.getX());
        this.setY(this.getY());
        this.setImageName(DEFAULT_PLAYER_IMAGE_FILE);
        this.isInvincible = false;
    }

    public boolean returnInvincibilityStatus() {
        return this.isInvincible;
    }

    public int returnInvincibilityStart() {
        return this.INVINCIBILITY_START_TICK;
    }

    public void startPowerTimer(int ticklsElapsed) { // needs to be called as soon as setInvincible has been called
        this.INVINCIBILITY_START_TICK = ticklsElapsed;
    }

    public boolean isHungry() {
        return this.isHungry = this.foodStamina == MAX_FOOD_STAMINA;
    }

    public int getStamina() {
        return this.foodStamina;
    }

    // handles consumption and is called when collided with a food entity
    public void processFood(int foodpts) {
        this.foodStamina += foodpts; // updates stamina
        this.foodStamina = Math.min(this.foodStamina, MAX_FOOD_STAMINA);
        if (this.foodStamina >= 3)
            this.modifyHP(1);
        this.isHungry();
    }

    public void increaseHunger() { // affects stamina, called every x ticks
        if (foodStamina > 0) {
            this.foodStamina -= 1;
        } else if (foodStamina == 0) {
            this.modifyHP(-1);
            this.isHungry = true;
        }
    }

}
