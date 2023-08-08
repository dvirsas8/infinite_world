package pepse.util;

import pepse.PepseGameManager;
import pepse.world.trees.Leaf;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.util.Vector2;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A class defining the leaf strategy for different scenarios
 */
public class FallingLeafStrategy extends Strategy {
    private static final int INITIAL_SIZE = 28;
    private static final int FINAL_SIZE = 32;
    private static final int FADEOUT_TIME = 8;
    private static final int MIN_LIFE_TIME = 3;
    private static final int MIN_TIME_UNTIL_REVIVE = 3;
    private static final int MAX_TIME_UNTIL_REVIVE = 9;
    private static final int MAX_LIFE_TIME = 47;
    private static final int MIN_SWING_TIME = 1;
    private static final int MAX_SWING_TIME = 19;
    private static final float INITIAL_ANGLE = 80f;
    private static final float FINAL_ANGLE = 95f;
    private static final int LEAF_FALLING_VELOCITY = 20;
    private static final float LEAF_INITIAL_X_VEL = 40f;
    private static final float LEAF_FINAL_X_VEL = -40f;
    private static final int TRANSITION_TIME = 4;
    private static final float WAIT_TIME = 0.1f;
    private static final int TRANSITION_MIN = 1;
    private static final int TRANSITION_MAX = 3;

    private static final String LEAF_TAG = "leaf";
    private static BiConsumer<Integer, GameObject> updateLocation;
    private final int trunkX;

    private Function<Float, Float> groundHeightAt;
    private final GameObjectCollection gameObjects;
    private Vector2 firstLocation;
    private Transition<Float> swingTransition;
    private Transition<Vector2> sizeTransition;
    private Transition<Float> flyTransition;
    private Leaf leaf;
    private final Random rand;

    /**
     * Constructor
     *
     * @param gameObjects game object collection
     */
    public FallingLeafStrategy(GameObjectCollection gameObjects) {
        this.gameObjects = gameObjects;
        this.rand = new Random();
        this.trunkX = 0;
    }

    /**
     * Another constructor- this one receives a function to update the new leaves locations.
     *
     * @param gameObjects game object collection
     */
    public FallingLeafStrategy(GameObjectCollection gameObjects, BiConsumer<Integer,
            GameObject> updateLocation, int trunkX) {
        this.gameObjects = gameObjects;
        this.rand = new Random();
        this.updateLocation = updateLocation;
        this.trunkX = trunkX;
    }

    /**
     * Defines the leaf's behaviour
     *
     * @param leaf leaf to create a strategy for
     */
    public void createBehaviour(GameObject leaf) {
        this.leaf = (Leaf) leaf;
        this.firstLocation = ((Leaf) leaf).getFirstLocation();
        this.groundHeightAt = ((Leaf) leaf).getGroundHeightAt();
        int lifeTime = rand.nextInt(MAX_LIFE_TIME) + MIN_LIFE_TIME;
        new ScheduledTask(leaf, lifeTime, false, this::leafBehaviour);

        float swingTime = (rand.nextInt(MAX_SWING_TIME) + MIN_SWING_TIME) / 10f;
        new ScheduledTask(leaf, swingTime, false, this::swingingLeaves);
    }

    private void delayedReviveLeaf() {
        int deathTime = rand.nextInt(MAX_TIME_UNTIL_REVIVE) + MIN_TIME_UNTIL_REVIVE;
        new ScheduledTask(leaf, deathTime, false, this::reviveLeaf);
    }

    private void swingingLeaves() {
        int transitionTime = rand.nextInt(TRANSITION_MAX) + TRANSITION_MIN;
        swingTransition = new Transition<>(leaf, (x) -> leaf.renderer().setRenderableAngle(x),
                INITIAL_ANGLE, FINAL_ANGLE, Transition.CUBIC_INTERPOLATOR_FLOAT,
                transitionTime, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        transitionTime = rand.nextInt(TRANSITION_MAX) + TRANSITION_MIN;
        sizeTransition = new Transition<>(leaf, leaf::setDimensions,
                new Vector2(INITIAL_SIZE, INITIAL_SIZE), new Vector2(FINAL_SIZE, FINAL_SIZE),
                Transition.CUBIC_INTERPOLATOR_VECTOR, transitionTime,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void leafBehaviour() {
        leaf.transform().setVelocityY(LEAF_FALLING_VELOCITY);
        this.flyTransition = new Transition<>(leaf, leaf.transform()::setVelocityX,
                LEAF_INITIAL_X_VEL, LEAF_FINAL_X_VEL, Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_TIME, Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        leaf.renderer().fadeOut(FADEOUT_TIME, this::delayedReviveLeaf);
    }

    private void reviveLeaf() {
        Leaf leaf = new Leaf(firstLocation, groundHeightAt,
                new FallingLeafStrategy(gameObjects, updateLocation, trunkX));
        leaf.setTag(LEAF_TAG);
        createBehaviour(leaf);
        gameObjects.addGameObject(leaf, PepseGameManager.LEAF_LAYER);
        updateLocation.accept(trunkX, leaf);
    }

    /**
     * Defines the leaf's strategy for collisions
     *
     * @param object the leaf object
     * @param other  object the leaf collided with
     */
    public void onCollision(GameObject object, GameObject other) {
        leaf.removeComponent(flyTransition);
        leaf.removeComponent(swingTransition);
        leaf.removeComponent(sizeTransition);
        new ScheduledTask(leaf, WAIT_TIME, false, () -> leaf.transform().setVelocityX(0));
    }
}
