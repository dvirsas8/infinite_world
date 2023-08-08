package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

/**
 * A class for the energy level meter
 */
public class EnergyLevelMeter extends GameObject {
    private static final String ENERGY_LEVEL = "Energy level: ";
    private final TextRenderable renderable;
    private final Avatar avatar;

    /**
     * Constructor
     *
     * @param topLeftCorner top left corner of the meter
     * @param dimensions    dimensions of the meter
     * @param renderable    a text renderable for the meter
     * @param avatar        avatar with energy level to display
     */
    public EnergyLevelMeter(Vector2 topLeftCorner, Vector2 dimensions, TextRenderable renderable, Avatar avatar) {
        super(topLeftCorner, dimensions, renderable);
        this.renderable = renderable;
        this.avatar = avatar;
    }

    /**
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        renderable.setString(ENERGY_LEVEL + (int) avatar.getEnergyLevel());
    }
}
