package pepse;

import pepse.util.*;
import pepse.world.*;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import static pepse.world.Block.findClosest;

/**
 * A class for initializing and managing the simulation's run
 */
public class PepseGameManager extends GameManager {
    //Paths
    private static final String[] CLOUD_PATHS = new
            String[]{"pepse/assets/cloud.png", "pepse/assets/cloud1.png"};
    private static final String[] GREEN_BIRD_PATHS = new
            String[]{"pepse/assets/greenBird2.png", "pepse/assets/greenBird1.png"};
    private static final String[] PINK_BIRD_PATHS = new
            String[]{"pepse/assets/pinkBird2.png", "pepse/assets/pinkBird1.png"};

    //Layers
    public static final int LEAF_LAYER = 164;
    public static final int BIRD_LAYER = 152;
    private static final int TERRAIN_LAYER = 161;
    private static final int AVATAR_LAYER = 160;
    private static final int NIGHT_LAYER = 190;
    public static final int TRUNK_LAYER = 163;
    private static final int RANDOM_SEED = 220;
    private static final int STATIC_LAYER = 159;
    private static final int CYCLE_LENGTH = 30;

    //Dimensions
    private static final Vector2 CLOUD_DIMENSIONS = new Vector2(70, 60);
    private static final Vector2 BIRD_DIMENSIONS = new Vector2(50, 45);
    private static final Vector2 BIRD_VELOCITY = new Vector2(80, 0);

    //Codes and tags
    private static final String SMALLER = "smaller";
    private static final String BIGGER = "bigger";
    private static final String TERRAIN = "Terrain";
    private static final String TREE = "Tree";
    private static final String SKY = "Sky";
    private static final String NIGHT = "Night";
    private static final String SUN = "Sun";
    private static final String HALO = "Halo";
    private static final String MOON = "Moon";
    private static final String AVATAR = "Avatar";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String LEAF = "Leaf";
    private static final String TRUNK = "Trunk";

    //Bounds and parameters
    private static final int MIN_CLOUD_HEIGHT = 200;
    private static final int MAX_CLOUD_HEIGHT = 50;
    private static final int CLOUD_PROB = 19;
    private static final int MIN_BIRD_HEIGHT = 300;
    private static final int MAX_BIRD_HEIGHT = 20;
    private static final int BIRD_CYCLE = 7;
    private static final String ENERGY_LEVEL = "Energy level: ";
    private static final String FONT_NAME = "Consolas";
    private static final Vector2 METER_TOP_LEFT_CORNER = new Vector2(10, 20);
    private static final Vector2 METER_DIMENSIONS = new Vector2(100, 30);
    private static final float LOAD_FACTOR = 0.7f;
    private static final int SAFE_ZONE = Block.SIZE * 5;
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
    public static final int BLOCK_SIZE_OFFSET = 3;
    public static final Vector2 WINDOW_DIMENSIONS = new Vector2(1300, 700);

    //Game objects
    private Avatar avatar;
    private Terrain terrain;
    private Tree tree;
    private StaticObject cloud;
    private WindowController windowController;

    //Data structures
    private HashMap<String, Integer> layerMap;
    public static final HashSet<Integer> createdXValSet = new HashSet<>();

    //Tools and inner fields
    private ImageReader imageReader;
    private int lastScreenStart;
    private int lastScreenEnd;
    private int screenSize;

    /**
     * Main method
     *
     * @param args arguments from command line
     */
    public static void main(String[] args) {
        PepseGameManager manager = new PepseGameManager("OOP5", WINDOW_DIMENSIONS);
        manager.run();
    }

    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.windowController = windowController;
        this.layerMap = createLayerMap();

        GameObjectFactory factory = new GameObjectFactory(gameObjects(), windowController, layerMap,
                inputListener, imageReader, CYCLE_LENGTH, RANDOM_SEED);
        createSingleObjects(factory);

        float avatarX = findClosest(BIGGER, (int) avatar.getCenter().x());
        float windowX = windowController.getWindowDimensions().x();
        this.screenSize = findClosest(BIGGER, (int) windowX) + 2 * SAFE_ZONE;
        this.lastScreenStart = (int) (avatarX - screenSize / 2);
        this.lastScreenEnd = (int) (avatarX + screenSize / 2);
        this.terrain = (Terrain) factory.create(TERRAIN);
        this.tree = (Tree) factory.create(TREE);

        createStaticAndDynamicObjects(factory);
        createObjectsInRange(lastScreenStart, lastScreenEnd);

        defineCollisions();

        setCamera(new Camera(avatar, Vector2.ZERO, windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    private void createStaticAndDynamicObjects(GameObjectFactory factory) {
        this.cloud = factory.createStaticObject(MIN_CLOUD_HEIGHT, MAX_CLOUD_HEIGHT,
                CLOUD_PROB, CLOUD_PATHS, CLOUD_DIMENSIONS, STATIC_LAYER);

        BirdStrategy birdStrategy = new BirdStrategy(imageReader, gameObjects());
        factory.createBird(MIN_BIRD_HEIGHT, MAX_BIRD_HEIGHT, BIRD_CYCLE,
                GREEN_BIRD_PATHS, this::getAvatarSide,
                BIRD_DIMENSIONS, BIRD_VELOCITY, birdStrategy, BIRD_LAYER);
        factory.createBird(MIN_BIRD_HEIGHT, MAX_BIRD_HEIGHT, BIRD_CYCLE - 2,
                PINK_BIRD_PATHS, this::getAvatarSide,
                BIRD_DIMENSIONS, BIRD_VELOCITY, birdStrategy, BIRD_LAYER);
    }

    private void createSingleObjects(GameObjectFactory factory) {
        GameObject sky = (GameObject) factory.create(SKY);
        GameObject night = (GameObject) factory.create(NIGHT);
        GameObject sun = (GameObject) factory.create(SUN);
        GameObject halo = SunHalo.create(gameObjects(), layerMap.get(HALO),
                sun, HALO_COLOR);
        GameObject moon = (GameObject) factory.create(MOON);
        this.avatar = (Avatar) factory.create(AVATAR);
        TextRenderable energyRenderable = new TextRenderable(ENERGY_LEVEL +
                (int) avatar.getEnergyLevel(), FONT_NAME);
        EnergyLevelMeter meter = new EnergyLevelMeter(METER_TOP_LEFT_CORNER, METER_DIMENSIONS,
                energyRenderable, avatar);
        gameObjects().addGameObject(meter, LEAF_LAYER + 1);
        meter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    private float getAvatarSide(String leftOrRight) {
        if (leftOrRight.equals(LEFT)) {
            return avatar.getCenter().x() - windowController.getWindowDimensions().x() * LOAD_FACTOR;
        } else if (leftOrRight.equals(RIGHT)) {
            return avatar.getCenter().x() + windowController.getWindowDimensions().x() * LOAD_FACTOR;
        } else {
            return 0;
        }
    }

    private void defineCollisions() {
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER + 1, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TRUNK_LAYER, false);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, BIRD_LAYER, true);
        gameObjects().layers().shouldLayersCollide(TERRAIN_LAYER, BIRD_LAYER, true);
        gameObjects().layers().shouldLayersCollide(TERRAIN_LAYER + 1, BIRD_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TRUNK_LAYER, true);
    }

    /**
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float avatarX = avatar.getCenter().x();

        if (avatarX + screenSize / 2 > lastScreenEnd + Block.SIZE) {
            createObjectsInRange(lastScreenEnd, lastScreenEnd + Block.SIZE);
            deleteObjectsInRange(lastScreenStart - BLOCK_SIZE_OFFSET * Block.SIZE, lastScreenStart);
            lastScreenStart = lastScreenStart + Block.SIZE;
            lastScreenEnd = lastScreenEnd + Block.SIZE;
        }

        if (avatarX - screenSize / 2 < lastScreenStart - Block.SIZE) {
            createObjectsInRange(lastScreenStart - Block.SIZE, lastScreenStart);
            deleteObjectsInRange(lastScreenEnd, lastScreenEnd + BLOCK_SIZE_OFFSET * Block.SIZE);
            lastScreenStart = lastScreenStart - Block.SIZE;
            lastScreenEnd = lastScreenEnd - Block.SIZE;
        }
    }

    private void createObjectsInRange(int start, int end) {
        terrain.createInRange(start, end);
        tree.createInRange(start, end);
        cloud.createInRange(start, end);
    }

    private void deleteObjectsInRange(int start, int end) {
        terrain.delete(start, end);
        tree.delete(start, end);
        cloud.delete(start, end);
    }

    private HashMap<String, Integer> createLayerMap() {
        HashMap<String, Integer> layerMap = new HashMap<>();
        layerMap.put(LEAF, LEAF_LAYER);
        layerMap.put(TRUNK, TRUNK_LAYER);
        layerMap.put(TERRAIN, TERRAIN_LAYER);
        layerMap.put(AVATAR, AVATAR_LAYER);
        layerMap.put(SKY, Layer.BACKGROUND);
        layerMap.put(SUN, Layer.BACKGROUND);
        layerMap.put(MOON, Layer.BACKGROUND);
        layerMap.put(HALO, Layer.BACKGROUND + 1);
        layerMap.put(NIGHT, NIGHT_LAYER);
        return layerMap;
    }
}
