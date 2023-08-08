package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * A class representing the sun's halo in game
 */
public class SunHalo {
    private static final Vector2 HALO_SIZE = new Vector2(100, 100);
    private static final String HALO_TAG = "halo";

    /**
     * Creates a sunHalo object
     * @param gameObjects game object collection
     * @param layer layer to add the halo to
     * @param sun a sun object to add the halo to
     * @param color the halo's color
     * @return the created halo object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun,
                                    Color color) {
        OvalRenderable haloRenderable = new OvalRenderable(color);
        GameObject halo = new GameObject(Vector2.ZERO, HALO_SIZE, haloRenderable);
        gameObjects.addGameObject(halo, layer);
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        halo.setTag(HALO_TAG);
        halo.addComponent((x) -> halo.setCenter(sun.getCenter()));
        return halo;
    }
}
