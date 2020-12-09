import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

public abstract class AbstractWorldMap implements IWorldMap{
    protected final LinkedHashMap<Vector2D, ArrayList<Animal>> animals = new LinkedHashMap<>(); //lista zwierzat
    protected final LinkedHashMap<Vector2D, IMapElement> environmentElements = new LinkedHashMap<>(); //lista roslin
    protected final LinkedHashMap<Vector2D, ArrayList<IMapElement>> mapElements = new LinkedHashMap<>(); //lista wszystkich obiektow na mapie
    protected FreeSpaceForGrassObserver grassObserver;
    protected FreeSpaceForAnimalsObserver animalsObserver;

    @Override
    public LinkedHashMap<Vector2D, ArrayList<Animal>> getAnimals() { //zwracam kopie zwierzakow
        return new LinkedHashMap<>(animals);
    }

    public Collection<ArrayList<Animal>> getAnimalsList() { //zwracam kopie zwierzakow
        return animals.values();
    }

    public LinkedHashMap<Vector2D, IMapElement> getEnvironmentElements() { //zwraca kopie roslin
        return new LinkedHashMap<>(environmentElements);
    }

    public ArrayList<IMapElement> getEnvironmentElementsList() { //zwraca kopie roslin
        return (ArrayList<IMapElement>) environmentElements.values();
    }

    @Override
    public void placeMapElement(IMapElement element){ //dodaje do elementow na mapie
        if(element.getIsEnvironmentElement()){ //jesli to jest element otoczenia (trawa)
            if(grassObserver.getFreeSpace().contains(element.getPosition())) {
                mapElements.put(element.getPosition(), new ArrayList<IMapElement>());
                mapElements.get(element.getPosition()).add(element);
                grassObserver.removeSpace(element.getPosition());
                //dodaje do elementow otoczenia
                environmentElements.put(element.getPosition(), element);
            }
        }else{ //jesli to jest zwierze
            if(animalsObserver.getFreeSpace().contains(element.getPosition())){
                mapElements.put(element.getPosition(), new ArrayList<IMapElement>());
                mapElements.get(element.getPosition()).add(element);
                grassObserver.removeSpace(element.getPosition());
                animalsObserver.removeSpace(element.getPosition());
                //dodaje do zwierzakow na mapie
                if(!animals.containsKey(element.getPosition())){ //jesli tam nie ma innych zwierzat
                    animals.put(element.getPosition(), new ArrayList<Animal>());
                }
                animals.get(element.getPosition()).add((Animal) element);
            }
        }
    }

    @Override
    public boolean isOccupied(Vector2D position){
        return mapElements.containsKey(position);
    }

    @Override
    public abstract Optional<Object> objectsAt(Vector2D position);

    @Override
    public boolean isOccupiedByLivingEntity(Vector2D position) {
        return animals.containsKey(position);
    }

}
