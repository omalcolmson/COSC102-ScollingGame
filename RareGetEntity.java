//A RareGetEntity is a special kind of GetEntity that spawns more infrequently than the regular GetEntity
//When consumed, RareGetEntities (fish) supplies the PlayerEntity's HP with one additional point. 
// RareGets also give the player temporary invincibility to withstand the damage of asteroids. 
public class RareGetEntity extends GetEntity {

    // Location of image file to be drawn for a RareGetEntity
    private static final String RAREGET_IMAGE_FILE = "assets/fish.png";
    private static final int RARE_GET_WIDTH = 27; // for some reason these had to be swapped
    private static final int RARE_GET_HEIGHT = 43;
    private static final int FOOD_POINTS = 5;

    public RareGetEntity() {
        this(0, 0);
    }

    public RareGetEntity(int x, int y) {
        super(x, y, RARE_GET_WIDTH, RARE_GET_HEIGHT, RAREGET_IMAGE_FILE);
    }

    public int getHealthPts() {
        return 1;
    }

    public int getFoodPoints() {
        return FOOD_POINTS;
    }

}
