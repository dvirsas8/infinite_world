package pepse.util;

import pepse.world.Bird;
import pepse.world.Avatar;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;

import static pepse.PepseGameManager.BIRD_LAYER;

/**
 * A strategy class for birds
 */
public class BirdStrategy extends Strategy {
    private static final int GRAVITY = 600;
    private static final String EXPLOSION_PATH = "pepse/assets/Explosion.png";
    private static final String CHICKEN_PATH = "pepse/assets/Chicken.jpg";
    private static final float EXPLOSION_TIME = 0.4f;
    private static final Vector2 EXPLOSION_DIMENSIONS = new Vector2(40, 30);
    private static final int FULL_ENERGY = 100;
    private static GameObjectCollection gameObjects;
    private static ImageRenderable explosion;
    private static ImageRenderable chicken;

    /**
     * Constructor
     *
     * @param reader      image reader to read the different bird states
     * @param gameObjects game object collection
     */
    public BirdStrategy(ImageReader reader, GameObjectCollection gameObjects) {
        BirdStrategy.gameObjects = gameObjects;
        explosion = reader.readImage(EXPLOSION_PATH, true);
        chicken = reader.readImage(CHICKEN_PATH, true);
    }

    /**
     * Constructor
     */
    public BirdStrategy() {
    }

    /**
     * Defines the bird's action on collision
     * @param object the collided object
     * @param other  the collider object
     */
    @Override
    public void onCollision(GameObject object, GameObject other) {
        if (other instanceof Avatar) {
            ((Bird) object).gotHit();
        }
        if (other instanceof Avatar && ((Bird) object).getGotHit() == 1) {
            object.renderer().setRenderable(explosion);
            new ScheduledTask(object, EXPLOSION_TIME, false, () ->
                    object.renderer().setRenderable(chicken));
            object.setDimensions(EXPLOSION_DIMENSIONS);
            object.setVelocity(Vector2.ZERO);
            object.transform().setAccelerationY(GRAVITY);
        } else if (other instanceof Avatar && ((Bird) object).getGotHit() > 1
                && object.renderer().getRenderable() == chicken) {
            gameObjects.removeGameObject(object, BIRD_LAYER);
            ((Avatar) other).setEnergyLevel(FULL_ENERGY);
        }
    }

    /**
     * Creates a recurring behaviour for the bird
     * @param object object to create a strategy for
     */
    @Override
    public void createBehaviour(GameObject object) {
    }
}
