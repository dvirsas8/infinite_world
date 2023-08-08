package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * A class representing the night in-game
 */
public class Night {
    private static final float MIDNIGHT_OPACITY = 0.65f;
    private static final String NIGHT_TAG = "night";
    private static final float NIGHT_INITIAL_TRANSITION = 0f;
    private static final int CYCLE_PART_FOR_TRANSITION = 2;

    /**
     * Creates a night object
     *
     * @param gameObjects      game object collection
     * @param layer            layer to add the night object to
     * @param windowDimensions window dimensions
     * @param cycleLength      defines the night length
     * @return a GameObject representing night
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);
        new Transition<>(night, night.renderer()::setOpaqueness,
                NIGHT_INITIAL_TRANSITION, MIDNIGHT_OPACITY, Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / CYCLE_PART_FOR_TRANSITION,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }
}
