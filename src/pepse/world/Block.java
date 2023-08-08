package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A class representing a single block element in the game
 */
public class Block extends GameObject {
    public static final int SIZE = 30;
    private static final String SMALLER = "smaller";
    private static final String BIGGER = "bigger";

    /**
     * Constructor
     *
     * @param topLeftCorner top left corner of the created block
     * @param renderable    a renderable object to represent the block in game
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Finds the closest x-axis value that new blocks should be built in
     *
     * @param smallerOrBigger defines if we want to find bigger or smaller x-axis values
     *                        that new blocks should be built in
     * @param num             current x-axis value
     * @return closest x-axis value that new blocks should be built in
     */
    public static int findClosest(String smallerOrBigger, int num) {
        int add = 0;
        if (smallerOrBigger.equals(SMALLER)) {
            add = -1;
        } else if (smallerOrBigger.equals(BIGGER)) {
            add = 1;
        }
        while (num % Block.SIZE != 0) {
            num += add;
        }
        return num;
    }
}
