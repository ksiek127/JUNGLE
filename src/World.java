import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

public class World {
    private static int energyRequiredToBreed; //ilosc energii potrzebnej do rozmnazania

    public static int getEnergyRequiredToBreed() {
        return energyRequiredToBreed;
    }

    public static void main(String[] args) {
        int plantEnergy = 2;
        int moveEnergy = 1;
        int startEnergy = 8;
        int nrOfAnimalsInTheBeginning = 7;
        int width = 50;
        int height = 60;
        double jungleRatio = 0.2;
        WorldMap map = new WorldMap(width, height, jungleRatio, plantEnergy);
        int era = 1;
        while(map.getAnimals().size() > 0){ //dopoki jakies zwierze zyje
            Collection<ArrayList<Animal>> animalsList = map.getAnimals().values();
            ArrayList<Animal> animals = new ArrayList<>(); //lista wszystkich zwierzat
            for(ArrayList<Animal> animalList: animalsList){
                for(Animal animal: animalList)
                    animals.add((Animal) animal);
            }
            for(Animal animal : animals){ //usuwam martwe zwierzeta z mapy
                if(animal.getIsDead())
                    map.removeAnimal(animal);
            }
            for(Animal animal: animals){
                animal.update(); //skret i przemieszczenie
            }
            //jedzenie
            for(IMapElement plant: map.getEnvironmentElements().values()){ //dla kazdej rosliny sprawdzam, czy jakies zwierze na niej stoi, jesli tak, to zjada ja zwierze z najwieksza energia na tym polu
                if(map.getAnimals().containsKey(plant.getPosition())){
                    ArrayList<Animal> animalsAtCurrentPosition = map.getAnimals().get(plant.getPosition());
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
            for(Animal animal: animals)
                animal.checkIfCanCanBreed(); //po jedzeniu aktualizuje informacje o zdolnosci zwierzat do rozmnazania
            //rozmnazanie
            for(Animal animal: animals){ //dla kazdego zwierzaka
                ArrayList<Animal> animalsAtThisPosition = map.getAnimals().get(animal.getPosition()); //lista zwierzakow na tej pozycji
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
            //dodanie nowych roslin do mapy
            if(map.isFreeSpaceInTheSteppe()){ //jesli jest wolne miejsce na stepie, pojawia sie tam roslina
                Random random = new Random();
                int plantX = random.nextInt(width);
                int plantY = random.nextInt(height);
                Vector2D plantPos = new Vector2D(plantX, plantY);
                while(map.isPositionInsideTheJungle(plantPos) || map.isOccupied(plantPos)){ //szukam wolnego miejsca na stepie
                    plantX = random.nextInt(width);
                    plantY = random.nextInt(height);
                    plantPos.setX(plantX);
                    plantPos.setY(plantY);
                }
                IMapElement plant = new Plant(plantPos); //umieszczam rosline na mapie
                map.placeMapElement(plant);
            }
            if(map.isFreeSpaceInTheJungle()){ //podobnie umieszczam w dzungli, jesli jest wolne miejsce
                Random random = new Random();
                int plantX = random.nextInt(width);
                int plantY = random.nextInt(height);
                Vector2D plantPos = new Vector2D(plantX, plantY);
                while(!map.isPositionInsideTheJungle(plantPos) || map.isOccupied(plantPos)){ //szukam wolnego miejsca na stepie
                    plantX = random.nextInt(width);
                    plantY = random.nextInt(height);
                    plantPos.setX(plantX);
                    plantPos.setY(plantY);
                }
                IMapElement plant = new Plant(plantPos); //umieszczam rosline na mapie
                map.placeMapElement(plant);
            }
            era++; //nowa epoka
        }
    }
}
