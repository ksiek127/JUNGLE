import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorldMap implements IWorldMap{
    private final int width; //szerokosc mapy
    private final int height; //wysokosc mapyprivate final int width; //szerokosc mapy
    private final double jungleRatio; //proporcje dzungli do sawanny
    private final int plantEnergy; //ilosc energii dodawana po zjedzeniu rosliny
    private final Vector2D jgBottomLeft; //lewy dolny rog dzungli
    private final Vector2D jgTopRight; //prawy gorny rog dzungli
    private final LinkedHashMap<Vector2D, ArrayList<Animal>> animals = new LinkedHashMap<>(); //lista zwierzat
    private final LinkedHashMap<Vector2D, IMapElement> environmentElements = new LinkedHashMap<>(); //lista roslin
    private final LinkedHashMap<Vector2D, ArrayList<IMapElement>> mapElements = new LinkedHashMap<>(); //lista wszystkich obiektow na mapie
    private final FreeSpaceObserver grassObserver;
    private final FreeSpaceObserver animalsObserver;
    private int totalLongevityForDeadAnimals;
    private int nrOfDeadAnimals;
    private int era;
    private AtomicBoolean paused;

    public WorldMap(int width, int height, double jungleRatio, int plantEnergy) {
        this.width = width;
        this.height = height;
        this.jungleRatio = jungleRatio;
        this.plantEnergy = plantEnergy;
        this.jgBottomLeft = new Vector2D((int) (width * (1 - jungleRatio) / 2), (int) (height * (1 - jungleRatio) / 2));
        this.jgTopRight = new Vector2D((int) (width * (1 - jungleRatio) / 2) + (int) (jungleRatio * width), (int) (height * (1 - jungleRatio) / 2) + (int) (jungleRatio * height));
        grassObserver = new FreeSpaceObserver(width, height);
        animalsObserver = new FreeSpaceObserver(width, height);
        totalLongevityForDeadAnimals = 0;
        nrOfDeadAnimals = 0;
        era = 1;
        paused = new AtomicBoolean();
        paused.set(false);
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
                if(animalsObserver.isTheSpaceFree(new Vector2D(i, j)))
                    return true;
            }
        }
        return false;
    }

    private void updateDeadAnimalsLongevity(Animal animal){
        totalLongevityForDeadAnimals += animal.getAge();
        nrOfDeadAnimals++;
    }

    public void removeAnimal(Animal animal) { //usuwanie martwego zwierzecia
        updateDeadAnimalsLongevity(animal);
        Vector2D animalPosition = animal.getPosition();
        animals.get(animalPosition).remove(animal);
        mapElements.get(animalPosition).remove(animal);
        if (!isOccupied(animalPosition)) { //jesli nie ma tam innych zwierzat, to pole staje sie wolne
            animals.remove(animalPosition);
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

    private ArrayList<Animal> animalsAt(Vector2D position){
        ArrayList<Animal> animalsAtGivenPosition = new ArrayList<>();
        for(Animal animal: getAnimalsList()){
            if(animal.getPosition().equals(position))
                animalsAtGivenPosition.add(animal);
        }
        return animalsAtGivenPosition;
    }

    @Override
    public void placeMapElement(IMapElement element){ //dodaje do elementow na mapie
        if(element.getIsEnvironmentElement()){ //jesli to jest element otoczenia (trawa)
            if (grassObserver.isTheSpaceFree(element.getPosition())) {
                mapElements.put(element.getPosition(), new ArrayList<IMapElement>());
                mapElements.get(element.getPosition()).add(element);
                grassObserver.removeSpace(element.getPosition());
                //dodaje do elementow otoczenia
                environmentElements.put(element.getPosition(), element);
            }
        }if(!element.getIsEnvironmentElement()){ //jesli to jest zwierze
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

    private void removeMapElement(IMapElement element){
        Vector2D elementPosition = element.getPosition();
        for(Vector2D position: mapElements.keySet()){
            if(position.equals(elementPosition)){
                mapElements.get(position).removeIf(element1 -> element1.equals(element));
            }
        }
    }

    private void removePlant(IMapElement plant){
        Vector2D plantPosition = plant.getPosition();
        Set<Vector2D> plantPositions = environmentElements.keySet();
        for(Vector2D position: plantPositions) {
            if (position.equals(plantPosition)){
                environmentElements.remove(position);
                return;
            }
        }
        removeMapElement(plant);
    }

//    public void eating(){
//        ArrayList<IMapElement> environmentElementsList = getEnvironmentElementsList();
//        for(IMapElement plant: environmentElementsList){ //dla kazdej rosliny sprawdzam, czy jakies zwierze na niej stoi, jesli tak, to zjada ja zwierze z najwieksza energia na tym polu
//            Vector2D plantPosition = plant.getPosition();
//            if(isOccupiedByLivingEntity(plantPosition)){
//                ArrayList<Animal> animalsAtCurrentPosition = animalsAt(plantPosition);
//                if(animalsAtCurrentPosition.size() == 1) //jesli jest tylko jedno zwierze, po prostu zjada rosline
//                    animalsAtCurrentPosition.get(0).gainEnergy(plantEnergy);
//                else{
//                    int maxEnergy = animalsAtCurrentPosition.get(0).getEnergy();
//                    int nrOfAnimalsWithMaxEnergy = 1;
//                    for(int i=1; i<animalsAtCurrentPosition.size(); i++){
//                        if(animalsAtCurrentPosition.get(i).getEnergy() == maxEnergy){
//                            nrOfAnimalsWithMaxEnergy++;
//                        }
//                        else if(animalsAtCurrentPosition.get(i).getEnergy() > maxEnergy){
//                            maxEnergy = animalsAtCurrentPosition.get(i).getEnergy();
//                            nrOfAnimalsWithMaxEnergy = 1;
//                        }
//                    }
//                    for(Animal animal: animalsAtCurrentPosition){
//                        if(animal.getEnergy() == maxEnergy)
//                            animal.gainEnergy(maxEnergy / nrOfAnimalsWithMaxEnergy); //jesli jest kilka zwierzat z najwyzsza energia, dziela energie z rosliny miedzy soba
//                    }
//                }
//                removePlant(plant); //usuwam zjedzona rosline z mapy
//            }
//        }
//    }
    public void eating(){
        ArrayList<IMapElement> environmentElementsList = getEnvironmentElementsList();
        for(IMapElement plant: environmentElementsList){ //dla kazdej rosliny sprawdzam, czy jakies zwierze na niej stoi, jesli tak, to zjada ja zwierze z najwieksza energia na tym polu
            Vector2D plantPosition = plant.getPosition();
            ArrayList<Animal> animalsAtCurrentPosition = animalsAt(plantPosition);
            if(animalsAtCurrentPosition.size() > 0) {
                if (animalsAtCurrentPosition.size() == 1) //jesli jest tylko jedno zwierze, po prostu zjada rosline
                    animalsAtCurrentPosition.get(0).gainEnergy(plantEnergy);
                else {
                    int maxEnergy = animalsAtCurrentPosition.get(0).getEnergy();
                    int nrOfAnimalsWithMaxEnergy = 1;
                    for (int i = 1; i < animalsAtCurrentPosition.size(); i++) {
                        if (animalsAtCurrentPosition.get(i).getEnergy() == maxEnergy) {
                            nrOfAnimalsWithMaxEnergy++;
                        } else if (animalsAtCurrentPosition.get(i).getEnergy() > maxEnergy) {
                            maxEnergy = animalsAtCurrentPosition.get(i).getEnergy();
                            nrOfAnimalsWithMaxEnergy = 1;
                        }
                    }
                    for (Animal animal : animalsAtCurrentPosition) {
                        if (animal.getEnergy() == maxEnergy)
                            animal.gainEnergy(maxEnergy / nrOfAnimalsWithMaxEnergy); //jesli jest kilka zwierzat z najwyzsza energia, dziela energie z rosliny miedzy soba
                    }
                }
                removePlant(plant); //usuwam zjedzona rosline z mapy
            }
        }
    }

    public void breeding(){
        ArrayList<Animal> animalsArrayList = getAnimalsList();
        for(Animal animal: animalsArrayList){ //dla kazdego zwierzaka
            ArrayList<Animal> animalsAtThisPosition = animalsAt(animal.getPosition()); //lista zwierzakow na tej pozycji
            if(animalsAtThisPosition.size() > 1){ //musi byc wiecej niz jedno zwierze, zeby sie rozmnazac
                ArrayList<Animal> capableOfBreeding = new ArrayList<>();
                for(Animal animal1: animalsAtThisPosition){ //sprawdzam, czy co najmniej dwa zwierzeta moga sie rozmnazac na tym polu
                    if(animal1.getCanBreed())
                        capableOfBreeding.add(animal1);
                }
                if(capableOfBreeding.size() == 2){ //jesli dokladnie dwa zwierzeta moga sie rozmnazac, po prostu to robia
                    placeMapElement(capableOfBreeding.get(0).reproduce(capableOfBreeding.get(1)));
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
                        placeMapElement(capableOfBreeding.get(0).reproduce(capableOfBreeding.get(1)));
                    }else{
                        Random random = new Random();
                        if(capableOfBreeding.get(0).getEnergy() > capableOfBreeding.get(1).getEnergy()){ //jesli pierwsze miejsce ma wiecej energii, niz drugie
                            int secondIdx = random.nextInt(nrOfAnimalsThatCanBreed - 1) + 1;
                            placeMapElement(capableOfBreeding.get(0).reproduce(capableOfBreeding.get(secondIdx)));
                        }else{ //jesli pierwsze i drugie miejsce maja tyle samo energii, losuje dwa zwierzeta sposrod tych, ktore maja najwiecej energii
                            int firstIdx = random.nextInt(nrOfAnimalsThatCanBreed);
                            int secondIdx = random.nextInt(nrOfAnimalsThatCanBreed);
                            while(secondIdx == firstIdx) //jesli dwa razy wylosuje to samo zwierze, losuje jeszcze raz
                                secondIdx = random.nextInt(nrOfAnimalsThatCanBreed);
                            placeMapElement(capableOfBreeding.get(firstIdx).reproduce(capableOfBreeding.get(secondIdx)));
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

    @Override
    public LinkedHashMap<Vector2D, ArrayList<Animal>> getAnimals() { //zwracam kopie zwierzakow
        return new LinkedHashMap<>(animals);
    }

    public ArrayList<Animal> getAnimalsList() { //zwracam kopie zwierzakow
        ArrayList<Animal> animalsArrayList = new ArrayList<>(); //lista wszystkich zwierzat
        for(ArrayList<Animal> animalList: animals.values()){
            animalsArrayList.addAll(animalList);
        }
        return animalsArrayList;
    }

    public LinkedHashMap<Vector2D, IMapElement> getEnvironmentElements() { //zwraca kopie roslin
        return new LinkedHashMap<>(environmentElements);
    }

    public ArrayList<IMapElement> getEnvironmentElementsList() { //zwraca kopie roslin
        //return (ArrayList<IMapElement>) environmentElements.values();
        return new ArrayList<IMapElement>(environmentElements.values());
    }

    @Override
    public boolean isOccupied(Vector2D position){
        for(Vector2D space: grassObserver.getFreeSpace()){ //sprawdzam, czy dane miejsce jest wolne
            if(space.equals(position)) //jesli jest wolne, to zwracam false (bo nie jest zajete)
                return false;
        }
        return true;
    }

    @Override
    public boolean isOccupiedByLivingEntity(Vector2D position) {
        for(Vector2D space: animalsObserver.getFreeSpace()){ //sprawdzam, czy dane miejsce jest wolne
            if(space.equals(position)) //jesli jest wolne, to zwracam false (bo nie jest zajete)
                return false;
        }
        return true;
    }

    @Override
    public void moveAnimal(Animal animal, Vector2D oldPosition){ //aktualizuje pozycje zwierzecia na mapie
        Vector2D animalPosition = animal.getPosition();
        animals.get(oldPosition).remove(animal);
        if(animals.get(oldPosition).size() == 0) {
            grassObserver.addSpace(oldPosition);
            animalsObserver.addSpace(oldPosition);
        }
        //jesli na tej pozycji nie ma innych zwierzat, dodaje nowa liste
        animals.computeIfAbsent(animalPosition, k -> new ArrayList<Animal>());
        animals.get(animalPosition).add(animal);
        mapElements.get(oldPosition).remove(animal);
        mapElements.computeIfAbsent(animalPosition, k -> new ArrayList<IMapElement>());
        mapElements.get(animalPosition).add(animal);
        grassObserver.removeSpace(oldPosition);
        animalsObserver.removeSpace(oldPosition);
    }

    public FreeSpaceObserver getGrassObserver() {
        return grassObserver;
    }

    public FreeSpaceObserver getAnimalsObserver() {
        return animalsObserver;
    }

    public int getDominatingGenotype(){
        byte[] geneCounter = new byte[8];
        ArrayList<Animal> animalsList = getAnimalsList();
        for(Animal animal: animalsList){
            for(byte gene: animal.getGenes()){
                geneCounter[gene] ++;
            }
        }
        int maxIdx = 0;
        for(int i=1; i<geneCounter.length; i++){
            if(geneCounter[i] > geneCounter[maxIdx])
                maxIdx = i;
        }
        return maxIdx;
    }

    public int getAverageEnergyLevel(){
        if(getAnimalsList().size() == 0) //jesli nie ma zwierzat, sredni poziom energii wynosi 0
            return 0;
        int sumOfEnergy = 0;
        ArrayList<Animal> animalList = getAnimalsList();
        for(Animal animal: animalList)
            sumOfEnergy += animal.getEnergy();
        return (int) (sumOfEnergy / animalList.size());
    }

    public int getAverageLongevityForDeadAnimals(){
        if(nrOfDeadAnimals > 0)
            return (int) (totalLongevityForDeadAnimals / nrOfDeadAnimals);
        return 0;
    }

    public int getAverageNrOfChildrenForAliveAnimals(){
        int totalNrOfChildren = 0;
        ArrayList<Animal> animalsList = getAnimalsList();
        if(animalsList.size() == 0)
            return 0;
        for(Animal animal: animalsList)
            totalNrOfChildren += animal.getNrOfChildren();
        return (int) (totalNrOfChildren / animalsList.size());
    }

    public void updateEra(){
        era++;
    }

    public int getEra() {
        return era;
    }

    public void setPaused(boolean value){
        paused.set(value);
    }

    public AtomicBoolean getPaused(){
        return paused;
    }
}