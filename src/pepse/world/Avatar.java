package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * A class representing the avatar in-game
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -300;
    private static final float GRAVITY = 600;
    private static final String WALKING_AVATAR1_PATH = "pepse/assets/WalkingCat1.png";
    private static final String WALKING_AVATAR2_PATH = "pepse/assets/WalkingCat2.png";
    private static final String FALLING_AVATAR_PATH = "pepse/assets/FallingCat.png";
    private static final String FLTING_AVATAR_PATH = "pepse/assets/FlyingCat.png";
    private static final String FLYING_AVATAR_PATH1 = FLTING_AVATAR_PATH;
    private static final int AVATAR_SIZE_X = 60;
    private static final int AVATAR_SIZE_Y = 70;
    private static final int SWITCH_LEG_TIME = 6;
    private float energyLevel = 100;
    private final UserInputListener inputListener;
    private Renderable walkingAvatar1;
    private Renderable walkingAvatar2;
    private final Renderable fallingAvatar;
    private final Renderable flyingAvatar;
    private int time = 0;


    /**
     * /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param inputListener an input listener object for getting user input
     * @param reader        an imageReader object for loading images to the game
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions,
                  UserInputListener inputListener, ImageReader reader) {
        super(topLeftCorner, dimensions, reader.readImage(WALKING_AVATAR1_PATH, true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.inputListener = inputListener;
        this.walkingAvatar1 = reader.readImage(WALKING_AVATAR1_PATH, true);
        this.walkingAvatar2 = reader.readImage(WALKING_AVATAR2_PATH, true);
        this.fallingAvatar = reader.readImage(FALLING_AVATAR_PATH, true);
        this.flyingAvatar = reader.readImage(FLYING_AVATAR_PATH1, true);

        transform().setAccelerationY(GRAVITY);
    }

    /**
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        time++;
        super.update(deltaTime);
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) { //walking left
            xVel -= VELOCITY_X;
            renderer().setIsFlippedHorizontally(true);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) { //walking right
            xVel += VELOCITY_X;
            renderer().setIsFlippedHorizontally(false);
        }
        transform().setVelocityX(xVel);
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && //flying
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
            if (energyLevel > 0) {
                energyLevel -= 0.5;
                transform().setVelocityY(VELOCITY_Y);
                renderer().setRenderable(flyingAvatar);
            }
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && //jumping
                !inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && getVelocity().y() == 0) {
            transform().setVelocityY(VELOCITY_Y);
            renderer().setRenderable(flyingAvatar);
        }
        if (getVelocity().y() == 0) { //standing or walking
            if (energyLevel < 100) {
                energyLevel += 0.5;
            }
            if (time % SWITCH_LEG_TIME == 0 && getVelocity().x() != 0) {
                renderer().setRenderable(walkingAvatar2);
                Renderable temp = walkingAvatar1;
                walkingAvatar1 = walkingAvatar2;
                walkingAvatar2 = temp;
            } else {
                renderer().setRenderable(walkingAvatar1);
            }
        } //falling
        if (getVelocity().y() > 0) {
            renderer().setRenderable(fallingAvatar);
        }
        if (getVelocity().y() > 500) {
            transform().setAccelerationY(0);
        } else {
            transform().setAccelerationY(GRAVITY);
        }
    }

    /**
     * Creates and returns a new Avatar object
     *
     * @param gameObjects   game object collection
     * @param layer         layer to add the avatar to
     * @param topLeftCorner top left corner of the avatar on screen
     * @param inputListener an object for getting user input
     * @param imageReader   an object for rendering the avatar images
     * @return the created Avatar object
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        Avatar avatar = new Avatar(topLeftCorner, new Vector2(AVATAR_SIZE_X, AVATAR_SIZE_Y),
                inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * Getter for energy level
     *
     * @return energy level
     */
    public float getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Setter for energy level
     *
     */
    public void setEnergyLevel(int num) {
        energyLevel = num;
    }
}
