import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Animal extends AbstractMapElement implements IMapElement{
    private Direction orientation; //orientacja na mapie
    private final WorldMap map; //mapa, na ktorej znajduje sie dane zwierze
    private int energy; //ilosc energii
    private boolean isDead; //czy zwierze jest martwe
    private final byte[] genes;
    private Boolean canBreed; //czy moze sie rozmnazac, aktualizowane codziennie po jedzeniu
    private int age;
    private int nrOfChildren;
    private int nrOfDescendants;
    private Animal mom;
    private Animal dad;

    public Animal(WorldMap map, Vector2D initialPosition, byte[] genes, int energy) {
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
        age = 0;
        nrOfChildren = 0;
        nrOfDescendants = 0;
        canBreed = energy >= World.getEnergyRequiredToBreed() / 2;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public int getEnergy() {
        return energy;
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

    public int getNrOfChildren() {
        return nrOfChildren;
    }

    public int getNrOfDescendants() {
        return nrOfDescendants;
    }

    public int getAge() {
        return age;
    }

    private void changeOrientation(){ //zmiana orientacji na podstawie genow
        Random random = new Random();
        int changeOrientationIdx = random.nextInt(genes.length); //losuje indeks
        byte changeOrientation = genes[changeOrientationIdx]; //zmiana orientacji zgodna z genem o wylosowanym indeksie
        orientation = Direction.intToDirection((orientation.directionToInt() + changeOrientation) % 8);
    }

    private void move(int cost){ //wykonanie pojedynczego ruchu
        Vector2D prevPosition = position;
        position = position.add(orientation.toUnitVector());
        position.translateLocationIfOutOfMap(map);
        loseEnergy(cost); //utrata energii zuzytej na poruszanie
        map.moveAnimal(this, prevPosition);
    }

    public void gainEnergy(int amount){ //uzyskanie energii
        energy += amount;
    }

    public void loseEnergy(int amount){ //utrata energii
        energy -= amount;
        if(energy <= 0) //zwierze ginie
            this.isDead = true;
    }

    public void update(int cost){ //wykonanie akcji zwiazanych z koncem dnia
        age ++;
        changeOrientation();
        move(cost);
    }

    public void setParents(Animal mom, Animal dad){
        this.mom = mom;
        this.dad = dad;
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

    private static Vector2D drawPositionToSpawn(Vector2D parentPosition, WorldMap map){
        Random random = new Random();
        int positionX = random.nextInt(3) + parentPosition.getX() - 1;
        int positionY = random.nextInt(3) + parentPosition.getY() - 1;
        Vector2D babyPosition = new Vector2D(positionX, positionY);
        babyPosition.translateLocationIfOutOfMap(map);
        return babyPosition;
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
        Vector2D babyPosition = drawPositionToSpawn(position, map);
        int babyEnergy = this.energy / 4 + other.energy / 4; //dziecko dostaje energie od rodzicow
        this.loseEnergy(this.energy / 4); //rodzice traca energie na rzecz dziecka
        other.loseEnergy(other.energy / 4);
        Animal baby = new Animal(map, babyPosition, babyGenes, babyEnergy);
        if(map.isAnyAdjacentPositionFree(position)){
            while (map.isOccupiedByLivingEntity(babyPosition)){ //losowanie dopoki wylosuje sie wolne pole
                baby.setPosition(drawPositionToSpawn(position, map));
            }
        }else{
            while (babyPosition.getX() == position.getX() && babyPosition.getY() == position.getY()){ //dziecko nie moze sie pojawic na tym samym polu, na ktorym stoja jego rodzice
                baby.setPosition(drawPositionToSpawn(position, map));
            }
        }
        baby.setPosition(babyPosition);
        //musi byc co najmniej jeden gen kazdego kierunku
        baby.sortGenes();
        fixGenes(babyGenes);
        baby.sortGenes(); //sortuje jeszcze raz, teraz juz jest co najmniej jeden gen kazdego typu
        this.addChildToNrOfChildrenTracker();
        other.addChildToNrOfChildrenTracker();
        baby.setParents(this, other);
        return baby;
    }

    public Color getColor(){ //kolor zwierzecia zalezy od ilosci energii
        if(hasDominatingGenotype((byte) map.getDominatingGenotype()) && map.getHighlightDominatingGenotype().get())
            return new Color(0,0,255);
        if(energy > 900)
            return new Color(0,0,0);
        if(energy > 700)
            return new Color(255, 0, 0);
        if(energy > 500)
            return new Color(255,69,0);
        if(energy > 250)
            return new Color(255,160,122);
        return new Color(219,112,147);
    }

    public void addDescendant(){
        nrOfDescendants++;
        if(mom != null)
            mom.addDescendant();
        if(dad != null)
            dad.addDescendant();
    }

    public void addChildToNrOfChildrenTracker(){
        nrOfChildren++;
        addDescendant();
    }

    public boolean hasDominatingGenotype(byte dominating){
        byte mainGene = 0;
        int maxGeneOccurrences = 1;
        int currentGeneOccurrences = 1;
        boolean hasDominatingGene = true; //jesli dane zwierze ma 'remis' jesli chodzi o gen, ktorego ma najwiecej, to nie ma dominujacego genu
        for(int i=1; i<genes.length; i++){
            if(genes[i-1] == genes[i]){
                currentGeneOccurrences++;
            }else{
                if(currentGeneOccurrences > maxGeneOccurrences){
                    maxGeneOccurrences = currentGeneOccurrences;
                    mainGene = genes[i - 1];
                    hasDominatingGene = true;
                }else if(currentGeneOccurrences == maxGeneOccurrences){
                    hasDominatingGene = false;
                }
            }
        }
        return hasDominatingGene && mainGene == dominating;
    }
}