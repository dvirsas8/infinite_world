package pepse.util;

import danogl.GameObject;

/**
 * An abstract class for game object that implement collision strategy
 */
public abstract class Strategy {

    public Strategy() {
    }

    /**
     * Defines the object's strategy for collisions
     *
     * @param object the collided object
     * @param other  the collider object
     */
    abstract public void onCollision(GameObject object, GameObject other);


    /**
     * Defines the object's behaviour
     *
     * @param object object to create a strategy for
     */
    abstract public void createBehaviour(GameObject object);


}
