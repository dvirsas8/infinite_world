package pepse.world;

import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.util.*;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * A class for creating and maintaining blocks of terrain
 */
public class Terrain implements Creatable{
    private static final int TERRAIN_DEPTH = 28;
    private static final float groundHeightAsWindowPercentage = 2f / 3;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int VALUE_OFFSET = 300;
    private static final String SMALLER = "smaller";
    private static final String BIGGER = "bigger";
    private static final String TERRAIN_TAG = "terrain";
    private final int groundHeightAtX0;
    private final GameObjectCollection gameObjects;
    private final int seed;
    private final int groundLayer;

    /**
     * Constructor
     *
     * @param gameObjects      collection of in-game objects
     * @param groundLayer      layer to add the terrain blocks to
     * @param windowDimensions window dimensions
     * @param seed             seed for the random function
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer,
                   Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        float firstGroundHeight = windowDimensions.y() * groundHeightAsWindowPercentage;
        this.groundHeightAtX0 = (int) Math.floor(firstGroundHeight / Block.SIZE) * Block.SIZE;
        this.seed = seed;
        this.groundLayer = groundLayer;
    }

    /**
     * Returns the y value of the ground layer for a given x value
     *
     * @param x x-axis coordinate on the screen
     * @return y value matching the given x value
     */
    public float groundHeightAt(float x) {
        NoiseGenerator noiseGenerator = new NoiseGenerator(seed);
        float value = (float) noiseGenerator.noise(x / Block.SIZE);
        value = groundHeightAtX0 + VALUE_OFFSET * value;
        value = (int) Math.floor(value / Block.SIZE) * Block.SIZE;
        return value;
    }

    /**
     * Deletes blocks in a given range
     *
     * @param minX start of range
     * @param maxX end of range
     */
    public void delete(int minX, int maxX) {
        int[] layers = {groundLayer, groundLayer + 1};
        Creatable.deleteInRange(minX, maxX, layers, gameObjects);
    }

    /**
     * Creates blocks in a given range
     *
     * @param minX start of range
     * @param maxX end of range
     */
    public void createInRange(int minX, int maxX) {
        int newMinX = Block.findClosest(SMALLER, minX);
        int newMaxX = Block.findClosest(BIGGER, maxX);

        for (int i = newMinX; i < newMaxX; i += Block.SIZE) {
            PepseGameManager.createdXValSet.add(i);
            int blockY = (int) groundHeightAt(i);
            createColl(i, blockY);
        }
    }

    private void createColl(int x, int y) {
        for (int i = y; i < y + TERRAIN_DEPTH * Block.SIZE; i += Block.SIZE) {
            RectangleRenderable renderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
            Vector2 blockLocation = new Vector2(x, i);
            Block block = new Block(blockLocation, renderable);
            Creatable.addToLocationMap(x, block);
            block.setTag(TERRAIN_TAG);
            if (i == y || i == y + Block.SIZE) {
                gameObjects.addGameObject(block, groundLayer);
            } else {
                gameObjects.addGameObject(block, groundLayer + 1);
            }
        }
    }
}

