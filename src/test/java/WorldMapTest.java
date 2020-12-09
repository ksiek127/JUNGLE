import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    @org.junit.jupiter.api.Test
    void isFreeSpaceInTheJungleShouldReturnTrueIfThereIsFreeSpaceInTheJungle() {
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit = new Animal(testMap, new Vector2D(1,2), genes, 30);
        testMap.placeMapElement(experimentalRabbit);
        assertTrue(testMap.isFreeSpaceInTheJungle());
    }

    @org.junit.jupiter.api.Test
    void isFreeSpaceInTheJungleShouldReturnFalseIfThereIsNoFreeSpaceInTheJungle() {
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        Animal experimentalRabbit2 = new Animal(testMap, new Vector2D(1,2), genes, 30);
        Animal experimentalRabbit3 = new Animal(testMap, new Vector2D(2,1), genes, 30);
        Animal experimentalRabbit4 = new Animal(testMap, new Vector2D(2,2), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        testMap.placeMapElement(experimentalRabbit2);
        testMap.placeMapElement(experimentalRabbit3);
        testMap.placeMapElement(experimentalRabbit4);
        assertFalse(testMap.isFreeSpaceInTheJungle());
    }

    @org.junit.jupiter.api.Test
    void isAnyAdjacentPositionFreeShouldReturnTrueIfThereIsAFreeAdjacentPosition() {
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        Animal experimentalRabbit2 = new Animal(testMap, new Vector2D(1,2), genes, 30);
        Animal experimentalRabbit3 = new Animal(testMap, new Vector2D(2,1), genes, 30);
        Animal experimentalRabbit4 = new Animal(testMap, new Vector2D(2,2), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        testMap.placeMapElement(experimentalRabbit2);
        testMap.placeMapElement(experimentalRabbit3);
        testMap.placeMapElement(experimentalRabbit4);
        assertTrue(testMap.isAnyAdjacentPositionFree(new Vector2D(1,1)));
    }

    @org.junit.jupiter.api.Test
    void isAnyAdjacentPositionFreeShouldReturnFalseIfThereIsNoFreeAdjacentPosition() {
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        Animal experimentalRabbit2 = new Animal(testMap, new Vector2D(1,2), genes, 30);
        Animal experimentalRabbit3 = new Animal(testMap, new Vector2D(2,1), genes, 30);
        Animal experimentalRabbit4 = new Animal(testMap, new Vector2D(3,1), genes, 30);
        Animal experimentalRabbit5 = new Animal(testMap, new Vector2D(1,3), genes, 30);
        Animal experimentalRabbit6 = new Animal(testMap, new Vector2D(3,2), genes, 30);
        Animal experimentalRabbit7 = new Animal(testMap, new Vector2D(2,3), genes, 30);
        Animal experimentalRabbit8 = new Animal(testMap, new Vector2D(3,3), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        testMap.placeMapElement(experimentalRabbit2);
        testMap.placeMapElement(experimentalRabbit3);
        testMap.placeMapElement(experimentalRabbit4);
        testMap.placeMapElement(experimentalRabbit5);
        testMap.placeMapElement(experimentalRabbit6);
        testMap.placeMapElement(experimentalRabbit7);
        testMap.placeMapElement(experimentalRabbit8);
        assertFalse(testMap.isAnyAdjacentPositionFree(new Vector2D(2,2)));
    }

    @org.junit.jupiter.api.Test
    void isAnyAdjacentPositionFreeShouldReturnTrueIfThereIsAFreeAdjacentPositionAndTheArgumentPositionItselfIsOccupied() {
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        Animal experimentalRabbit2 = new Animal(testMap, new Vector2D(1,2), genes, 30);
        Animal experimentalRabbit3 = new Animal(testMap, new Vector2D(2,1), genes, 30);
        Animal experimentalRabbit4 = new Animal(testMap, new Vector2D(3,1), genes, 30);
        Animal experimentalRabbit5 = new Animal(testMap, new Vector2D(1,3), genes, 30);
        Animal experimentalRabbit6 = new Animal(testMap, new Vector2D(3,2), genes, 30);
        Animal experimentalRabbit7 = new Animal(testMap, new Vector2D(2,2), genes, 30);
        Animal experimentalRabbit8 = new Animal(testMap, new Vector2D(3,3), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        testMap.placeMapElement(experimentalRabbit2);
        testMap.placeMapElement(experimentalRabbit3);
        testMap.placeMapElement(experimentalRabbit4);
        testMap.placeMapElement(experimentalRabbit5);
        testMap.placeMapElement(experimentalRabbit6);
        testMap.placeMapElement(experimentalRabbit7);
        testMap.placeMapElement(experimentalRabbit8);
        assertTrue(testMap.isAnyAdjacentPositionFree(new Vector2D(2,2)));
    }

    @Test
    public void placeMapElementShouldPlaceAPlantOnTheMap(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        Plant testPlant = new Plant(new Vector2D(0,0));
        testMap.placeMapElement(testPlant);
        assertEquals(testPlant, testMap.getEnvironmentElements().get(testPlant.getPosition()));
    }

    @Test
    public void placeMapElementShouldPlaceAnAnimalOnTheMap(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        assertEquals(experimentalRabbit1, testMap.getAnimals().get(experimentalRabbit1.getPosition()).get(0));
    }

    @Test
    public void isOccupiedShouldReturnTrueIfThePlaceIsOccupied(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        assertTrue(testMap.isOccupied(new Vector2D(1,1)));
    }

    @Test
    public void isOccupiedByLivingEntityShouldReturnTrueIfThePlaceIsOccupiedByAnAnimal(){
        WorldMap testMap = new WorldMap(4,4,0.5,2);
        byte[] genes = new byte[]{1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,3,2,3,2,1,2,3,4,5,6,7,8};
        Animal experimentalRabbit1 = new Animal(testMap, new Vector2D(1,1), genes, 30);
        testMap.placeMapElement(experimentalRabbit1);
        assertTrue(testMap.isOccupiedByLivingEntity(new Vector2D(1,1)));
    }
}