import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class WorldMap extends AbstractWorldMap implements IWorldMap{
    private final int width; //szerokosc mapy
    private final int height; //wysokosc mapy
    private final double jungleRatio; //proporcje dzungli do sawanny
    private final int plantEnergy; //ilosc energii dodawana po zjedzeniu rosliny
    private final Vector2D jgBottomLeft; //lewy dolny rog dzungli
    private final Vector2D jgTopRight; //prawy gorny rog dzungli

    public WorldMap(int width, int height, double jungleRatio, int plantEnergy) {
        this.width = width;
        this.height = height;
        this.jungleRatio = jungleRatio;
        this.plantEnergy = plantEnergy;
        this.jgBottomLeft = new Vector2D((int) (width * (1 - jungleRatio) / 2), (int) (height * (1 - jungleRatio) / 2));
        this.jgTopRight = new Vector2D((int) (width * (1 - jungleRatio) / 2), (int) (height * (1 - jungleRatio) / 2));
    }

    @Override
    public Optional<Object> objectsAt(Vector2D position) {
        if(isOccupied(position))
            return Optional.ofNullable(mapElements.get(position));
        return Optional.empty();
    }

    private boolean isFreeSpaceInTheArea(Vector2D bottomLeft, Vector2D topRight){ //pomocnicza funkcja sprawdzajaca, czy jest wolne miejsce w prostokacie
        for(int i = bottomLeft.getX(); i < topRight.getX(); i++){
            for(int j = bottomLeft.getY(); j < topRight.getY(); j++){
                if(!isOccupied(new Vector2D(i,j)))
                    return true;
            }
        }
        return false;
    }

    public boolean isFreeSpaceInTheJungle(){ //czy jest wolne miejsce w dzungli
        return isFreeSpaceInTheArea(jgBottomLeft, jgTopRight);
    }

    public boolean isFreeSpaceInTheSteppe(){ //czy jest wolne miejsce na stepie
        return isFreeSpaceInTheArea(new Vector2D(0,0), new Vector2D(jgBottomLeft.getX(), height)) || isFreeSpaceInTheArea(new Vector2D(jgTopRight.getX(), 0), new Vector2D(width, height)) || isFreeSpaceInTheArea(new Vector2D(jgBottomLeft.getX(), jgTopRight.getY()), new Vector2D(jgTopRight.getX(), height)) || isFreeSpaceInTheArea(new Vector2D(jgBottomLeft.getX(), 0), new Vector2D(jgTopRight.getX(), jgBottomLeft.getY()));
    }

    public void spawnPlants(){ //pojawiaja sie dwie rosliny, jedna w dzungli, druga na stepie
        Random random = new Random(); //losowanie pozycji
        //dodaje rosline do dzungli, jesli jest w niej wolne miejsce
        if(isFreeSpaceInTheJungle()){
            Vector2D positionInsideJungle = new Vector2D(random.nextInt(jgTopRight.getX() - jgBottomLeft.getX()) + jgBottomLeft.getX(), random.nextInt(jgTopRight.getY() - jgBottomLeft.getY()) + jgBottomLeft.getY());
            while(isOccupied(positionInsideJungle)){ //losuje dopoki nie natrafie na wolne pole
                positionInsideJungle.setX(random.nextInt(jgTopRight.getX() - jgBottomLeft.getX()) + jgBottomLeft.getX());
                positionInsideJungle.setY(random.nextInt(jgTopRight.getY() - jgBottomLeft.getY()) + jgBottomLeft.getY());
            }
            placeMapElement(new Plant(positionInsideJungle));
        }
        //podobnie dodaje na step
        if(isFreeSpaceInTheSteppe()){
            Vector2D positionInsideSteppe = new Vector2D(random.nextInt(width), random.nextInt(height));
            while(positionInsideSteppe.follows(jgBottomLeft) && positionInsideSteppe.precedes(jgTopRight) || isOccupied(positionInsideSteppe)){
                positionInsideSteppe.setX(random.nextInt(width));
                positionInsideSteppe.setY(random.nextInt(height));
            }
            placeMapElement(new Plant(positionInsideSteppe));
        }
    }

    @Override
    public boolean isAnyAdjacentPositionFree(Vector2D position){
        for(int i=position.getX() - 1; i <= position.getX() + 2; i++){
            for(int j=position.getY() - 1; j <= position.getY() + 2; j++){
                if(i != position.getX() && j != position.getY()){ //sprawdzam sasiednie pola, bez tego, na ktorym stoje
                    if(!isOccupiedByLivingEntity(new Vector2D(i % width, j % height))) //modulo, bo brzegi mapy zawijaja sie na druga strone
                        return true;
                }
            }
        }
        return false;
    }

    public void removeAnimal(Animal animal){ //usuwanie martwego zwierzecia
        animals.get(animal.getPosition()).remove(animal);
    }

    public Boolean isPositionInsideTheJungle(Vector2D position){
        return position.follows(jgBottomLeft) && position.precedes(jgTopRight);
    }
}