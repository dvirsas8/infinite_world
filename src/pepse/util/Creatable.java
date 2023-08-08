package pepse.util;

import pepse.world.Block;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for all the objects which may be deleted and crated in range,
 * Using a hash map for deleting objects.
 */
public interface Creatable {
    static final String BIGGER = "bigger";
    static final String SMALLER = "smaller";
    HashMap<Integer, ArrayList<GameObject>> locationMap = new HashMap<>();

    void createInRange(int minX, int maxX);

    static void deleteInRange(int minX, int maxX, int[] layers, GameObjectCollection gameObjects) {
        int newMinX = Block.findClosest(SMALLER, minX);
        int newMaxX = Block.findClosest(BIGGER, maxX);

        for (int i = newMinX; i < newMaxX; i += Block.SIZE) {
            if (locationMap.get(i) != null) {
                ArrayList<GameObject> objArray = locationMap.get(i);
                for (GameObject obj : objArray) {
                    for (int layer : layers) {
                        if (gameObjects.removeGameObject(obj, layer)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    static void addToLocationMap(int x, GameObject obj) {
        if (locationMap.get(x) != null) {
            locationMap.get(x).add(obj);
        } else {
            ArrayList<GameObject> objArray = new ArrayList<>();
            objArray.add(obj);
            locationMap.put(x, objArray);
        }
    }

}
