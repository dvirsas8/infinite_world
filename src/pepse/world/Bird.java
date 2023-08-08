package pepse.world;

import pepse.util.BirdStrategy;
import pepse.util.Strategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;
import java.util.function.Function;

/**
 * A class representing a bird in game
 */
public class Bird extends GameObject {
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final int SWITCH_WINGS_TIME = 6;
    private int layer;
    private int lastImg = 0;
    private final Renderable[] renderable;
    private final GameObjectCollection gameObjects;
    private final Vector2 velocity;
    private final Function<String, Float> birdFlightLocation;
    private final Random rand;
    private final int minHeight;
    private final int maxHeight;
    private final Vector2 dimmensions;
    private final int cycle;
    private int time;
    private String direction;
    private Strategy strategy;
    private int gotHit = 0;
    private static final String SMALLER = "smaller";
    private static final String BIGGER = "bigger";


    /**
     * Constructor
     *
     * @param gameObjects   game object collection
     * @param dimmensions   bird dimensions
     * @param renderable    renderable for the bird
     * @param birdFlightLocation function for determining which side the bird flies to
     * @param minHeight     min bird height
     * @param maxHeight     max bird height
     * @param cycle         amount of time the bird lives
     * @param velocity      bird's velocity
     * @param topLeftCorner bird's top left corner on screen
     * @param strategy      bird strategy
     */
    public Bird(GameObjectCollection gameObjects, Vector2 dimmensions,
                Renderable[] renderable, Function<String, Float> birdFlightLocation,
                int minHeight, int maxHeight, int cycle, Vector2 velocity, Vector2 topLeftCorner,
                Strategy strategy){
        super(topLeftCorner, dimmensions, renderable[0]);
        this.renderable = renderable;
        this.gameObjects = gameObjects;
        this.velocity = velocity;
        this.birdFlightLocation = birdFlightLocation;
        this.rand = new Random();
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.dimmensions = dimmensions;
        this.cycle = cycle;
        this.direction = "";
        this.setVelocity(velocity);
        this.strategy = strategy;
    }

    /**
     * Constructor
     *
     * @param gameObjects   game object collection
     * @param dimmensions   bird dimensions
     * @param renderable    renderable for the bird
     * @param whichSideFunc function for determining which side the bird flies to
     * @param minHeight     min bird height
     * @param maxHeight     max bird height
     * @param cycle         amount of time the bird lives
     * @param velocity      bird's velocity
     * @param topLeftCorner bird's top left corner on screen
     * @param strategy      bird strategy
     * @param layer         layer to add the bird to
     */
    public Bird(GameObjectCollection gameObjects, Vector2 dimmensions,
                Renderable[] renderable, Function<String, Float> whichSideFunc,
                int minHeight, int maxHeight, int cycle, Vector2 velocity, Vector2 topLeftCorner,
                Strategy strategy, int layer) {
        super(topLeftCorner, dimmensions, renderable[0]);
        this.renderable = renderable;
        this.gameObjects = gameObjects;
        this.velocity = velocity;
        this.birdFlightLocation = whichSideFunc;
        this.rand = new Random();
        this.minHeight = minHeight - maxHeight;
        this.maxHeight = maxHeight;
        this.dimmensions = dimmensions;
        this.cycle = cycle;
        this.direction = "";
        this.setVelocity(velocity);
        this.strategy = strategy;
        this.layer = layer;
    }

    /**
     * Creates a bird object
     */
    public void create() {
        int height = (rand.nextInt(minHeight)) + maxHeight;
        int randChoise = rand.nextInt(2);
        String[] choises = {LEFT, "right"};
        Vector2 newVelocity = velocity;

        float width = birdFlightLocation.apply(LEFT);

        if (choises[randChoise].equals(LEFT)) {
            renderer().setIsFlippedHorizontally(true);
            newVelocity = velocity.multX(-1);
            width = birdFlightLocation.apply(RIGHT);
        }

        Vector2 location = new Vector2(width, height);
        strategy = new BirdStrategy();
        Bird obj = new Bird(gameObjects, dimmensions, renderable,
                birdFlightLocation, minHeight, maxHeight, cycle, newVelocity, location, strategy, layer);
        obj.setDirection(choises[randChoise]);
        gameObjects.addGameObject(obj, layer);

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
        if(this.getCenter().x() >  birdFlightLocation.apply(RIGHT) + 5 * Block.SIZE||
                this.getCenter().x() < birdFlightLocation.apply(LEFT) - 5 * Block.SIZE){
            gameObjects.removeGameObject(this, layer);
        }
        super.update(deltaTime);
        if (direction.equals(LEFT)) {
            renderer().setIsFlippedHorizontally(true);
        }

        time++;
        if (time % SWITCH_WINGS_TIME == 0 && gotHit < 1) {
            flipImg();
        }

        if (getVelocity().y() > 400) {
            transform().setAccelerationY(0);
        }
    }

    private void flipImg() {
        lastImg += 1;
        if (lastImg >= renderable.length) {
            lastImg = 0;
        }
        renderer().setRenderable(renderable[lastImg]);
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
        if (strategy != null) {
            strategy.onCollision(this, other);
        }
        if (other instanceof Block) {
            this.setVelocity(Vector2.ZERO);
            this.transform().setAccelerationY(0);
        }
    }

    /**
     * Updates the number of times the bird got hit
     */
    public void gotHit() {
        this.gotHit++;
    }

    /**
     * Returns the number of time the bird got hit by an Avatar object
     */
    public int getGotHit() {
        return gotHit;
    }

    private void setDirection(String direction) {
        this.direction = direction;
    }

}
