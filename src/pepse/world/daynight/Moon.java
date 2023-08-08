package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;

/**
 * A class representing the moon in-game
 */
public class Moon {
    private static final Vector2 MOON_DIMENSIONS = new Vector2(70, 70);
    private static final float MOON_LOCATION_RELATIVE_TO_WINDOW_SIZES = 0.5f;
    private static final String MOON_TAG = "moon";
    private static final float INITIAL_DEG = -90f;
    private static final float FINAL_DEG = 270f;
    private static final int OFFSET = 50;
    private static final int ANGLE = 180;

    /**
     * Creates a moon object
     *
     * @param gameObjects      game object collection
     * @param layer            layer to add the moon to
     * @param windowDimensions window dimensions
     * @param cycleLength      defines the length of the moon's trip around the screen
     * @param image            the moon's image
     * @return a GameObject representing the moon
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength, ImageRenderable image) {
        Vector2 moonPosition = windowDimensions.mult(MOON_LOCATION_RELATIVE_TO_WINDOW_SIZES);
        GameObject moon = new GameObject(moonPosition, MOON_DIMENSIONS, image);
        moon.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        moon.setTag(MOON_TAG);
        gameObjects.addGameObject(moon, layer);
        new Transition<>(moon, (x) -> moon.setCenter(new Vector2((float)
                (moonPosition.x() - moonPosition.x() * Math.cos(x * Math.PI / ANGLE)),
                (float) (moonPosition.y() - moonPosition.y() * Math.sin(x * Math.PI / ANGLE) + OFFSET))),
                INITIAL_DEG, FINAL_DEG, Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength, Transition.TransitionType.TRANSITION_LOOP,
                null);
        return moon;
    }

}
