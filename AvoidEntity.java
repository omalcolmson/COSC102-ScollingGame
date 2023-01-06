//AvoidEntities are entities the player needs to avoid colliding with (asteroids).
//If a player collides with an avoid, it reduces the players Hit Points (HP) and causes the player to lose one get entity. 
public class AvoidEntity extends Entity implements Consumable, Scrollable {

    // Location of image file to be drawn for an AvoidEntity
    private static final String AVOID_IMAGE_FILE = "assets/asteroid.png";
    // Dimensions of the AvoidEntity
    private static final int AVOID_WIDTH = 100;
    private static final int AVOID_HEIGHT = 75;
    // Speed that the avoid moves each time the game scrolls
    private static final int AVOID_SCROLL_SPEED = 5;

    public AvoidEntity() {
        this(0, 0);
    }

    public AvoidEntity(int x, int y) {
        super(x, y, AVOID_WIDTH, AVOID_HEIGHT, AVOID_IMAGE_FILE);
    }

    public int getScrollSpeed() {
        return AVOID_SCROLL_SPEED;
    }

    // Move the avoid left by the scroll speed
    public void scroll() {
        setX(getX() - AVOID_SCROLL_SPEED);
    }

    // Colliding with an AvoidEntity does not affect the player's score
    public int getPointsValue() {
        return 0;
    }

    // Colliding with an AvoidEntity Reduces players HP by 1
    public int getDamageValue() {
        return -1;
    }

}
