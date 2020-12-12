import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Animal extends AbstractMapElement implements IMapElement{
    private Direction orientation; //orientacja na mapie
    private final IWorldMap map; //mapa, na ktorej znajduje sie dane zwierze
    private int energy; //ilosc energii
    private boolean isDead; //czy zwierze jest martwe
    private final byte[] genes;
    private Boolean canBreed; //czy moze sie rozmnazac, aktualizowane codziennie po jedzeniu

    public Animal(IWorldMap map, Vector2D initialPosition, byte[] genes, int energy) {
        this.map = map;
        this.position = initialPosition;
        this.isEnvironmentElement = false;
        this.isDead = false;
        this.genes = genes;
        Random random = new Random();
        int orientationIdx = random.nextInt(genes.length);
        this.orientation = Direction.intToDirection(genes[orientationIdx]);
        this.energy = energy;
        this.isEnvironmentElement = false;
    }

    private void changeOrientation(){ //zmiana orientacji na podstawie genow
        Random random = new Random();
        int changeOrientationIdx = random.nextInt(genes.length); //losuje indeks
        byte changeOrientation = genes[changeOrientationIdx]; //zmiana orientacji zgodna z genem o wylosowanym indeksie
        orientation = Direction.intToDirection(orientation.directionToInt() + changeOrientation);
    }

    private void move(int cost){ //wykonanie pojedynczego ruchu
        this.position = this.position.add(this.orientation.toUnitVector());
        loseEnergy(cost); //utrata energii zuzytej na poruszanie
    }

    public void gainEnergy(int amount){ //uzyskanie energii
        energy += amount;
    }

    public void loseEnergy(int amount){ //utrata energii
        energy -= amount;
        if(energy <= 0) //zwierze ginie
            this.isDead = true;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public int getEnergy() {
        return energy;
    }

    public void update(int cost){ //wykonanie akcji zwiazanych z koncem dnia
        changeOrientation();
        move(cost);
    }

    public static void fixGenes(byte[] genes){ //przy losowaniu genow zwierzecia, moze ktoregos zabraknac i to jest funkcja, ktora zapewnia, ze nowo powstale zwierze ma co najmniej jeden gen kazdego kierunku
        ArrayList<Integer> duplicateIndices = new ArrayList<>();
        ArrayList<Byte> missingGenes = new ArrayList<>();
        for(int i=0; i<genes.length - 1; i++){
            if(genes[i+1] == genes[i])
                duplicateIndices.add(i);
            if(genes[i+1] - genes[i] > 1){
                for(byte j = (byte) (genes[i] + 1); j<genes[i+1]; j++)
                    missingGenes.add(j);
            }
        }
        for (Byte missingGene : missingGenes) {
            Random random = new Random();
            int idx = random.nextInt(duplicateIndices.size()); //losuje indeks duplikatu do wymiany
            genes[duplicateIndices.get(idx)] = missingGene; //zmieniam na brakujacy gen
            duplicateIndices.remove(idx); //usuwam z duplikatow
        }
    }

    public void sortGenes(){
        Arrays.sort(genes);
    }

    public Animal reproduce(Animal other){
        Random random = new Random();
        int split1 = random.nextInt(genes.length - 2); //losowy podzial genow rodzicow na trzy czesci
        //pierwsza czesc to geny od indeksu 0 do split1
        int split2 = random.nextInt((genes.length - 1) - split1) + split1;
        byte[] babyGenes = new byte[this.genes.length];
        System.arraycopy(this.genes, 0, babyGenes, 0, split1);
        System.arraycopy(this.genes, split1, babyGenes, split1, split2 - split1);
        System.arraycopy(other.genes, split2, babyGenes, split2, genes.length - split2);
        int babyX = random.nextInt(3) + position.getX() - 1;
        int babyY = random.nextInt(3) + position.getY() - 1;
        Vector2D babyPosition = new Vector2D(babyX, babyY);
        if(map.isAnyAdjacentPositionFree(position)){
            while (map.isOccupiedByLivingEntity(babyPosition)){ //losowanie dopoki wylosuje sie wolne pole
                babyPosition.setX(random.nextInt(3) + position.getX() - 1);
                babyPosition.setY(random.nextInt(3) + position.getY() - 1);
            }
        }else{
            while (babyPosition.getX() == position.getX() && babyPosition.getY() == position.getY()){ //dziecko nie moze sie pojawic na tym samym polu, na ktorym stoja jego rodzice
                babyPosition.setX(random.nextInt(3) + position.getX() - 1);
                babyPosition.setY(random.nextInt(3) + position.getY() - 1);
            }
        }
        int babyEnergy = this.energy / 4 + other.energy / 4; //dziecko dostaje energie od rodzicow
        this.loseEnergy(this.energy / 4); //rodzice traca energie na rzecz dziecka
        other.loseEnergy(other.energy / 4);
        Animal baby = new Animal(map, babyPosition, babyGenes, babyEnergy);
        //musi byc co najmniej jeden gen kazdego kierunku
        baby.sortGenes();
        fixGenes(babyGenes);
        baby.sortGenes(); //sortuje jeszcze raz, teraz juz jest co najmniej jeden gen kazdego typu
        return baby;
    }

    public void checkIfCanCanBreed(){ //sprawdzam, czy zwierze moze sie rozmnazac tego dnia
        canBreed = energy >= World.getEnergyRequiredToBreed();
    }

    public void setCanBreed(Boolean canBreed){
        this.canBreed = canBreed;
    }

    public Boolean getCanBreed() {
        return canBreed;
    }

    public byte[] getGenes() {
        return genes;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }
}
