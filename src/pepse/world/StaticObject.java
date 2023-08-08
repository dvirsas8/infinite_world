package pepse.world;

import pepse.util.Creatable;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Objects;
import java.util.Random;

/**
 * A class for creating static object in the game
 */
public class StaticObject implements Creatable {
    private final GameObjectCollection gameObjects;
    private final int seed;
    private final Random rand;
    private final int minHeight;
    private final int maxHeight;
    private final Renderable[] renders;
    private final Vector2 dimensions;
    private final int probability;
    private final int cloudLayer;

    /**
     * Constructor
     *
     * @param gameObjects game object collection
     * @param seed        seed for the random object
     * @param minHeight   min height on screen for the static objects
     * @param maxHeight   max height on screen for the static objects
     * @param probability probability of creating an object
     * @param renders     the static object's renders
     * @param dimensions  static object's dimension
     * @param cloudLayer       layer to add the object to
     */
    public StaticObject(GameObjectCollection gameObjects, int seed, int minHeight, int maxHeight,
                        int probability, Renderable[] renders, Vector2 dimensions, int cloudLayer) {
        super();
        this.gameObjects = gameObjects;
        this.cloudLayer = cloudLayer;
        this.seed = seed;
        this.rand = new Random();
        this.minHeight = minHeight - maxHeight;
        this.maxHeight = maxHeight;
        this.renders = renders;
        this.dimensions = dimensions;
        this.probability = probability;
    }

    /**
     * Deletes clouds in a given range
     *
     * @param minX start of range
     * @param maxX end of range
     */
    public void delete(int minX, int maxX) {
        int[] layers = {cloudLayer};
        Creatable.deleteInRange(minX, maxX, layers, gameObjects);
    }

    /**
     * Creates static objects in a given range
     *
     * @param minX start of range
     * @param maxX end of range
     */
    public void createInRange(int minX, int maxX) {
        for (int i = minX; i < maxX; i += Block.SIZE) {
            rand.setSeed(Objects.hash(i, seed));
            if (rand.nextInt(probability) == probability - 1) {
                int height = (rand.nextInt(minHeight) + maxHeight);
                Vector2 location = new Vector2(i, height);
                Renderable img = chooseRandomPic();
                GameObject obj = new GameObject(location, dimensions, img);
                gameObjects.addGameObject(obj, cloudLayer);
                Creatable.addToLocationMap(i, obj);
            }
        }
    }

    private Renderable chooseRandomPic() {
        int type = (rand.nextInt(renders.length));
        return renders[type];
    }
}

