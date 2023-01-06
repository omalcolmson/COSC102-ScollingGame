// Food entities don't provide any power ups, but increase the player's stamina. If the player consumes 3+ apples, this increases the player's HP
public class RegFoodEntity extends GetEntity implements Consumable, Scrollable {

    // Location of image file to be drawn for a Food Entity
    private static final String FOOD_IMAGE_FILE = "assets/food.png";
    // Dimensions of the Food Entity
    private static final int FOOD_WIDTH = 43;
    private static final int FOOD_HEIGHT = 45;
    // Amount of points received when player collides with a Food
    private static final int FOOD_POINT_VALUE = 1;

    public RegFoodEntity() {
        this(0, 0);
    }

    public RegFoodEntity(int x, int y) {
        super(x, y, FOOD_WIDTH, FOOD_HEIGHT, FOOD_IMAGE_FILE);
    }

    public RegFoodEntity(int x, int y, String imageFileName) {
        super(x, y, FOOD_WIDTH, FOOD_HEIGHT, imageFileName);
    }

    public RegFoodEntity(int x, int y, int height, int width, String imageFileName) {
        super(x, y, height, width, imageFileName);
    }

    public int getPointsValue() {
        return FOOD_POINT_VALUE;
    }

    public int getScrollSpeed() {
        return super.getScrollSpeed();
    }

}
