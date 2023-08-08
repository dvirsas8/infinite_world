package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * A class representing the sun in-game
 */
public class Sun {
    //Parameters
    private static final Vector2 SUN_DIMENSIONS = new Vector2(50, 50);
    private static final float SUN_LOCATION_RELATIVE_TO_WINDOW_SIZES = 0.5f;
    private static final float INITIAL_DEG = 90f;
    private static final float FINAL_DEG = 450f;
    private static final int OFFSET = 50;
    private static final int ANGLE = 180;

    //Tags
    private static final String SUN_TAG = "sun";

    /**
     * Creates a sun object
     *
     * @param gameObjects      game object collection
     * @param layer            layer to add the sun to
     * @param windowDimensions window dimensions
     * @param cycleLength      defines the length of the sun's trip around the screen
     * @return a GameObject representing the sun
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        OvalRenderable sunRenderable = new OvalRenderable(Color.YELLOW);
        Vector2 sunPosition = windowDimensions.mult(SUN_LOCATION_RELATIVE_TO_WINDOW_SIZES);
        GameObject sun = new GameObject(sunPosition, SUN_DIMENSIONS, sunRenderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        gameObjects.addGameObject(sun, layer);
        new Transition<>(sun, (x) -> sun.setCenter(new Vector2((float)
                (sunPosition.x() - sunPosition.x() * Math.cos(x * Math.PI / ANGLE)),
                (float) (sunPosition.y() - sunPosition.y() * Math.sin(x * Math.PI / ANGLE) + OFFSET))),
                INITIAL_DEG, FINAL_DEG, Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength, Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }
}
