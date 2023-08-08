package pepse.world.trees;

import pepse.util.Strategy;
import pepse.util.ColorSupplier;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;
import java.util.function.Function;

/**
 * A class representing the leaves in game
 */
public class Leaf extends GameObject {
    private static final int INITIAL_SIZE = 28;
    private static final Color BASE_TREE_TOP_COLOR = new Color(50, 200, 30);
    private final Function<Float, Float> groundHeightAt;
    private final Vector2 firstLocation;
    private final Strategy strategy;

    /**
     * Constructor
     *
     * @param topLeftCorner  top leaf corner of the leaf in-game
     * @param groundHeightAt a function for getting the y-value of the ground at a given x-value
     * @param strategy       strategy for the leaf to activate on collision or when falling
     */
    public Leaf(Vector2 topLeftCorner, Function<Float, Float> groundHeightAt,
                Strategy strategy) {
        super(topLeftCorner, Vector2.ONES.mult(INITIAL_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_TREE_TOP_COLOR)));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.groundHeightAt = groundHeightAt;
        this.firstLocation = topLeftCorner;
        this.strategy = strategy;
        startStrategy();
    }

    /**
     * Activates the leaf's strategy
     */
    public void startStrategy() {
        if (strategy != null) {
            strategy.createBehaviour(this);
        }
    }

    /**
     * Getter for the leaf's original location
     *
     * @return leaf's original location
     */
    public Vector2 getFirstLocation() {
        return firstLocation;
    }

    /**
     * Getter for the function matching x-values to ground height on screen
     *
     * @return the function matching x-values to ground height on screen
     */
    public Function<Float, Float> getGroundHeightAt() {
        return groundHeightAt;
    }

    /**
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        strategy.onCollision(this, other);
    }
}
