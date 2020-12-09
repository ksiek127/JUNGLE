import java.util.*;

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
        grassObserver = new FreeSpaceForGrassObserver(this);
        animalsObserver = new FreeSpaceForAnimalsObserver(this);
    }

    @Override
    public Optional<Object> objectsAt(Vector2D position) {
        if(isOccupied(position))
            return Optional.ofNullable(mapElements.get(position));
        return Optional.empty();
    }

    private boolean isFreeSpaceInTheArea(Vector2D bottomLeft, Vector2D topRight){ //pomocnicza funkcja sprawdzajaca, czy jest wolne miejsce w prostokacie
        ArrayList<Vector2D> freeSpace = grassObserver.getFreeSpace();
        for(Vector2D space: freeSpace){
            if(space.follows(bottomLeft) && space.precedes(topRight))
                return true; //jest wolne miejsce
        }
        return false;
    }

    public boolean isFreeSpaceInTheJungle(){ //czy jest wolne miejsce w dzungli
        return isFreeSpaceInTheArea(jgBottomLeft, jgTopRight);
    }

    public boolean isFreeSpaceInTheSteppe(){ //czy jest wolne miejsce na stepie
        return isFreeSpaceInTheArea(new Vector2D(0,0), new Vector2D(jgBottomLeft.getX(), height)) || isFreeSpaceInTheArea(new Vector2D(jgTopRight.getX(), 0), new Vector2D(width, height)) || isFreeSpaceInTheArea(new Vector2D(jgBottomLeft.getX(), jgTopRight.getY()), new Vector2D(jgTopRight.getX(), height)) || isFreeSpaceInTheArea(new Vector2D(jgBottomLeft.getX(), 0), new Vector2D(jgTopRight.getX(), jgBottomLeft.getY()));
    }

    public boolean isFreeSpaceOnTheMap(){
        return grassObserver.isFreeSpace();
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

    public void removeAnimal(Animal animal) { //usuwanie martwego zwierzecia
        Vector2D animalPosition = animal.getPosition();
        animals.get(animal.getPosition()).remove(animal);
        if (!isOccupied(animalPosition)) { //jesli nie ma tam innych zwierzat, to pole staje sie wolne
            grassObserver.addSpace(animalPosition);
            animalsObserver.addSpace(animalPosition);
        }
    }

    public Boolean isPositionInsideTheJungle(Vector2D position){
        return position.follows(jgBottomLeft) && position.precedes(jgTopRight);
    }

    public Vector2D getJgBottomLeft() {
        return jgBottomLeft;
    }

    public Vector2D getJgTopRight() {
        return jgTopRight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void eating(){
        for(IMapElement plant: environmentElements.values()){ //dla kazdej rosliny sprawdzam, czy jakies zwierze na niej stoi, jesli tak, to zjada ja zwierze z najwieksza energia na tym polu
            if(animals.containsKey(plant.getPosition())){
                ArrayList<Animal> animalsAtCurrentPosition = animals.get(plant.getPosition());
                if(animalsAtCurrentPosition.size() == 1) //jesli jest tylko jedno zwierze, po prostu zjada rosline
                    animalsAtCurrentPosition.get(0).gainEnergy(plantEnergy);
                else{
                    int maxEnergy = animalsAtCurrentPosition.get(0).getEnergy();
                    int nrOfAnimalsWithMaxEnergy = 1;
                    for(int i=1; i<animalsAtCurrentPosition.size(); i++){
                        if(animalsAtCurrentPosition.get(i).getEnergy() == maxEnergy){
                            nrOfAnimalsWithMaxEnergy++;
                        }
                        else if(animalsAtCurrentPosition.get(i).getEnergy() > maxEnergy){
                            maxEnergy = animalsAtCurrentPosition.get(i).getEnergy();
                            nrOfAnimalsWithMaxEnergy = 1;
                        }
                    }
                    for(Animal animal: animalsAtCurrentPosition){
                        if(animal.getEnergy() == maxEnergy)
                            animal.gainEnergy(maxEnergy / nrOfAnimalsWithMaxEnergy); //jesli jest kilka zwierzat z najwyzsza energia, dziela energie z rosliny miedzy soba
                    }
                }
            }
        }
    }

    public void breeding(ArrayList<Animal> animals1){
        for(Animal animal: animals1){ //dla kazdego zwierzaka
            ArrayList<Animal> animalsAtThisPosition = animals.get(animal.getPosition()); //lista zwierzakow na tej pozycji
            if(animalsAtThisPosition.size() > 1){ //musi byc wiecej niz jedno zwierze, zeby sie rozmnazac
                ArrayList<Animal> capableOfBreeding = new ArrayList<>();
                for(Animal animal1: animalsAtThisPosition){ //sprawdzam, czy co najmniej dwa zwierzeta moga sie rozmnazac na tym polu
                    if(animal1.getCanBreed())
                        capableOfBreeding.add(animal1);
                }
                if(capableOfBreeding.size() == 2){ //jesli dokladnie dwa zwierzeta moga sie rozmnazac, po prostu to robia
                    capableOfBreeding.get(0).reproduce(capableOfBreeding.get(1));
                }else if(capableOfBreeding.size() > 2){ //jesli jest ich wiecej, robia to dwa z najwieksza energia, remisy rozstrzygane losowo
                    capableOfBreeding.sort(new Comparator<Animal>() { //sortowanie malejaco po ilosci energii
                        @Override
                        public int compare(Animal o1, Animal o2) {
                            if(o1.getEnergy() <= o2.getEnergy())
                                return 1;
                            return -1;
                        }
                    });
                    int nrOfAnimalsThatCanBreed = 2;
                    int k = 2;
                    while(k < capableOfBreeding.size()){ //sprawdzam, ile zwierzat ma tyle samo energii, co drugie w kolejnosci
                        if(capableOfBreeding.get(k) == capableOfBreeding.get(k-1)){
                            k++;
                            nrOfAnimalsThatCanBreed++;
                        } else
                            break;
                    }
                    if(nrOfAnimalsThatCanBreed == 2){ //jesli nie ma remisow, rozmnazaja sie dwa z najwyzsza energia
                        capableOfBreeding.get(0).reproduce(capableOfBreeding.get(1));
                    }else{
                        Random random = new Random();
                        if(capableOfBreeding.get(0).getEnergy() > capableOfBreeding.get(1).getEnergy()){ //jesli pierwsze miejsce ma wiecej energii, niz drugie
                            int secondIdx = random.nextInt(nrOfAnimalsThatCanBreed - 1) + 1;
                            capableOfBreeding.get(0).reproduce(capableOfBreeding.get(secondIdx));
                        }else{ //jesli pierwsze i drugie miejsce maja tyle samo energii, losuje dwa zwierzeta sposrod tych, ktore maja najwiecej energii
                            int firstIdx = random.nextInt(nrOfAnimalsThatCanBreed);
                            int secondIdx = random.nextInt(nrOfAnimalsThatCanBreed);
                            while(secondIdx == firstIdx) //jesli dwa razy wylosuje to samo zwierze, losuje jeszcze raz
                                secondIdx = random.nextInt(nrOfAnimalsThatCanBreed);
                            capableOfBreeding.get(firstIdx).reproduce(capableOfBreeding.get(secondIdx));
                        }
                    }
                }

            }
            for(Animal animal1: animalsAtThisPosition)
                animal1.setCanBreed(false); //na tej pozycji juz zadne zwierze nie moze sie rozmnazac tego dnia
        }
    }

    public void spawnPlants(){
        if(isFreeSpaceInTheSteppe()){ //jesli jest wolne miejsce na stepie, pojawia sie tam roslina
            Random random = new Random();
            int plantX = random.nextInt(width);
            int plantY = random.nextInt(height);
            Vector2D plantPos = new Vector2D(plantX, plantY);
            while(isPositionInsideTheJungle(plantPos) || isOccupied(plantPos)){ //szukam wolnego miejsca na stepie
                plantX = random.nextInt(width);
                plantY = random.nextInt(height);
                plantPos.setX(plantX);
                plantPos.setY(plantY);
            }
            IMapElement plant = new Plant(plantPos); //umieszczam rosline na mapie
            placeMapElement(plant);
        }
        if(isFreeSpaceInTheJungle()){ //podobnie umieszczam w dzungli, jesli jest wolne miejsce
            Random random = new Random();
            int plantX = random.nextInt(width);
            int plantY = random.nextInt(height);
            Vector2D plantPos = new Vector2D(plantX, plantY);
            while(!isPositionInsideTheJungle(plantPos) || isOccupied(plantPos)){ //szukam wolnego miejsca na stepie
                plantX = random.nextInt(width);
                plantY = random.nextInt(height);
                plantPos.setX(plantX);
                plantPos.setY(plantY);
            }
            IMapElement plant = new Plant(plantPos); //umieszczam rosline na mapie
            placeMapElement(plant);
        }
    }
}