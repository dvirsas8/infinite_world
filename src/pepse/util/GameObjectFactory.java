package pepse.util;

import pepse.world.*;
import pepse.world.daynight.Moon;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.trees.Tree;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.util.HashMap;
import java.util.function.Function;

/**
 * A factory class for creating game objects
 */
public class GameObjectFactory {
    //Tags
    private static final String LEAF = "Leaf";
    private static final String TRUNK = "Trunk";
    private static final String SKY = "Sky";
    private static final String NIGHT = "Night";
    private static final String TERRAIN = "Terrain";
    private static final String SUN = "Sun";
    private static final String MOON = "Moon";
    private static final String TREE = "Tree";
    private static final String AVATAR = "Avatar";

    //Paths
    private static final String MOON_PATH = "pepse/assets/NewMoon.png";

    //Factors
    private static final float SCREEN_FACTOR = 0.2f;

    //Class parameters and data structures
    private final GameObjectCollection gameObjects;
    private final WindowController windowController;
    private final HashMap<String, Integer> layerMap;
    private final int cycleLength;
    private final int randomSeed;
    private final ImageReader imageReader;
    private final UserInputListener inputListener;
    private Terrain terrain;

    /**
     * Constructor
     *
     * @param gameObjects      game object collection
     * @param windowController a window controller object
     * @param layerMap         a map matching game objects to their layer
     * @param inputListener    input listener to get user input
     * @param imageReader      object to read the objects' images
     * @param cycleLength      the game's cycle length
     * @param randomSeed       seed for random objects
     */
    public GameObjectFactory(GameObjectCollection gameObjects, WindowController windowController,
                             HashMap<String, Integer> layerMap, UserInputListener inputListener,
                             ImageReader imageReader, int cycleLength, int randomSeed) {
        this.gameObjects = gameObjects;
        this.windowController = windowController;
        this.layerMap = layerMap;
        this.cycleLength = cycleLength;
        this.randomSeed = randomSeed;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
    }

    /**
     * Creates an object matching the given string
     *
     * @param obj type of object to create
     * @return the created object
     */
    public Object create(String obj) {
        if (obj.equals(SKY)) {
            return Sky.create(gameObjects, windowController.getWindowDimensions(), layerMap.get(obj));
        }
        if (obj.equals(NIGHT)) {
            return Night.create(gameObjects, layerMap.get(obj),
                    windowController.getWindowDimensions(), cycleLength);
        }
        if (obj.equals(TERRAIN)) {
            this.terrain = new Terrain(gameObjects, layerMap.get(obj),
                    windowController.getWindowDimensions(), randomSeed);
            return this.terrain;
        }
        if (obj.equals(SUN)) {
            return Sun.create(gameObjects, layerMap.get(obj),
                    windowController.getWindowDimensions(), cycleLength);
        }
        if (obj.equals(MOON)) {
            ImageRenderable moonImage = imageReader.readImage(MOON_PATH, true);
            return Moon.create(gameObjects, layerMap.get(obj),
                    windowController.getWindowDimensions(), cycleLength, moonImage);
        }
        if (obj.equals(TREE)) {
            return new Tree(terrain::groundHeightAt, gameObjects, randomSeed,
                    layerMap.get(LEAF), layerMap.get(TRUNK));
        }
        if (obj.equals(AVATAR)) {
            return Avatar.create(gameObjects, layerMap.get(obj),
                    windowController.getWindowDimensions().mult(SCREEN_FACTOR), inputListener, imageReader);
        }
        return null;
    }

    /**
     * Creates a static object
     *
     * @param minHeight   min height for the object on screen
     * @param maxHeight   max height for the object on screen
     * @param probability probability for creation of the object
     * @param paths       an array of paths for the objects' images
     * @param dimensions  dimension of the static object
     * @param layer       layer to add the object to
     * @return the created static object
     */
    public StaticObject createStaticObject(int minHeight, int maxHeight, int probability,
                                           String[] paths, Vector2 dimensions, int layer) {
        Renderable[] renders = createRendersListFromPathsList(paths);
        return new StaticObject(gameObjects, randomSeed, minHeight, maxHeight,
                probability, renders, dimensions, layer);
    }

    /**
     * Creates birds that implement a collision strategy
     *
     * @param minHeight         min height for the bird on screen
     * @param maxHeight         max height for the bird on screen
     * @param cycle             cycle length of the bird
     * @param paths             an array of paths for the birds' images
     * @param determineSideFunc function that defines which side the bird moves to
     * @param dimensions        dimension of the bird object
     * @param velocity          velocity of the bird
     * @param strategy          the bird's strategy to activate
     * @param layer             layer to add the bird to
     * @return a bird object
     */
    public Bird createBird(int minHeight, int maxHeight, int cycle, String[] paths,
                           Function<String, Float> determineSideFunc,
                           Vector2 dimensions, Vector2 velocity,
                           Strategy strategy, int layer) {
        Renderable[] renders = createRendersListFromPathsList(paths);
        Bird obj = new Bird(gameObjects, dimensions, renders, determineSideFunc,
                minHeight, maxHeight, cycle, velocity, Vector2.ZERO, strategy, layer);
        GameObject generator = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
        GameObject bla = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
        gameObjects.addGameObject(bla, layer);
        gameObjects.addGameObject(generator, layer + 1);
        new ScheduledTask(generator, cycle, true, obj::create);
        return obj;
    }

    private Renderable[] createRendersListFromPathsList(String[] paths) {
        Renderable[] renders = new Renderable[paths.length];
        for (int i = 0; i < paths.length; i++) {
            renders[i] = imageReader.readImage(paths[i], true);
        }
        return renders;
    }
}
