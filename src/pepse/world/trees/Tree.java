package pepse.world.trees;

import pepse.util.ColorSupplier;
import pepse.util.Creatable;
import pepse.util.FallingLeafStrategy;
import pepse.world.Block;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * A class representing a tree (trunk and leaves) in-game
 */
public class Tree implements Creatable {
    //Game parameters
    private static final int MIN_TREE_RAND = 1;
    private static final int MAX_TREE_RAND = 11;
    private static final int MIN_TREE_HEIGHT = 5;
    private static final int MAX_TREE_HEIGHT = 10;
    private static final int MIN_TREE_TOP = 3;
    private static final int MAX_TREE_TOP = 5;
    private static final Color BASE_TRUNK_COLOR = new Color(100, 50, 20);

    //Tags
    private static final String BIGGER = "bigger";
    private static final String SMALLER = "smaller";
    private static final String TREE_TAG = "tree";
    public static final String LEAF_TAG = "leaf";

    //Class parameters
    private final Random rand;
    private final Function<Float, Float> groundHeightAt;
    private final GameObjectCollection gameObjects;
    private final int seed;
    private final int leafLayer;
    private final int trunkLayer;

    /**
     * Constructor
     *
     * @param groundHeightAt a function for getting the y-value of the ground at a given x-value
     * @param gameObjects    game object collection
     * @param seed           seed for the random object
     */
    public Tree(Function<Float, Float> groundHeightAt, GameObjectCollection gameObjects, int seed,
                int leafLayer, int trunkLayer) {
        this.groundHeightAt = groundHeightAt;
        this.gameObjects = gameObjects;
        this.seed = seed;
        this.rand = new Random();
        this.leafLayer = leafLayer;
        this.trunkLayer = trunkLayer;
    }

    /**
     * Creates trees in a given range
     *
     * @param minX start of range
     * @param maxX end of range
     */
    public void createInRange(int minX, int maxX) {
        int newMinX = Block.findClosest(SMALLER, minX);
        for (int i = newMinX; i < maxX; i += Block.SIZE) {
            rand.setSeed(Objects.hash(i, seed));
            if (rand.nextInt(MAX_TREE_RAND) + MIN_TREE_RAND == 1) {
                int height = ((rand.nextInt(MAX_TREE_HEIGHT) + MIN_TREE_HEIGHT)* Block.SIZE);
                int treetopY = createTrunk(i, height);
                int side = rand.nextInt(MAX_TREE_TOP) + MIN_TREE_TOP;
                while (side % 2 == 0) {
                    side = rand.nextInt(MAX_TREE_TOP) + MIN_TREE_TOP;
                }
                createTreeTop(i, treetopY, side);
            }
        }
    }

    /**
     * Deletes trees in a given range
     *
     * @param minX start of range
     * @param maxX end of range
     */
    public void delete(int minX, int maxX) {
        int[] layers = {trunkLayer, leafLayer};
        Creatable.deleteInRange(minX, maxX, layers, gameObjects);
    }

    private int createTrunk(float cordX, int height) {
        int cordY = (int) Math.floor(groundHeightAt.apply(cordX) / Block.SIZE) * Block.SIZE;

        for (int i = cordY - height; i < cordY; i += Block.SIZE) {
            Vector2 blockLocation = new Vector2(cordX, i);
            RectangleRenderable renderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_TRUNK_COLOR));
            Block block = new Block(blockLocation, renderable);
            block.setTag(TREE_TAG);
            Creatable.addToLocationMap((int)cordX, block);
            gameObjects.addGameObject(block, trunkLayer);
        }
        return cordY - height;
    }

    private void createTreeTop(int trunkX, int trunkY, int side) {
        int startY = trunkY - Block.SIZE * (side / 2);
        int startX = trunkX - Block.SIZE * (side / 2);
        for (int i = startY; i < startY + side * Block.SIZE; i += Block.SIZE) {
            for (int j = startX; j < startX + side * Block.SIZE; j += Block.SIZE) {
                defineLeaves(j, i, trunkX);
            }
        }
    }

    private void defineLeaves(int x, int y, int trunkX) {
        Vector2 blockLocation = new Vector2(x, y);
        Leaf leaf;
        FallingLeafStrategy strategy = new FallingLeafStrategy(gameObjects, Creatable::addToLocationMap, trunkX);
        leaf = new Leaf(blockLocation, groundHeightAt, strategy);
        leaf.setTag(LEAF_TAG);
        gameObjects.addGameObject(leaf, leafLayer);
        Creatable.addToLocationMap(trunkX, leaf);
    }
}
