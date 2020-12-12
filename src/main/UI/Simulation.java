import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

public class Simulation implements ActionListener {

    private final WorldMap map; //mapa swiata
    private final JFrame frame;
    private final DisplayPanel displayPanel;
    private final int nrOfAnimalsInTheBeginning; //ilosc zwierzat na poczatku
    private final int delay; //przerwa pomiedzy kolejnymi dniami, zeby mozna bylo sledzic symulacje w normalnym tempie
    private final int startEnergy;
    private int plantEnergy;
    private int moveEnergy;
    private int era;

    public Simulation(WorldMap map, int nrOfAnimalsInTheBeginning, int delay, int startEnergy){
        this.map = map;
        this.nrOfAnimalsInTheBeginning = nrOfAnimalsInTheBeginning;
        this.delay = delay;
        this.startEnergy = startEnergy;
        frame = new JFrame("JUNGLE");
        frame.setSize(map.getWidth(), map.getHeight());
        frame.setVisible(true);
        displayPanel = new DisplayPanel(map, this);
        displayPanel.setSize(new Dimension(1,1));
        frame.add(displayPanel);
        era = 1;
    }

    public void start(){ //umieszczenie zwierzat na startowych pozycjach i rozpoczecie symulacji
        for(int i=0; i<nrOfAnimalsInTheBeginning; i++){
            Random random = new Random();
            byte[] genes = new byte[32];
            for(int j=0; j<32; j++)
                genes[j] = (byte)random.nextInt(8);
            Animal animal = new Animal(map, new Vector2D(random.nextInt(map.getWidth()), random.nextInt(map.getHeight())), genes, startEnergy);
            animal.sortGenes();
            Animal.fixGenes(animal.getGenes());
            animal.sortGenes();
            while(map.isOccupiedByLivingEntity(animal.getPosition())) //losuje pozycje, dopoki nie trafie na wolne pole
                animal.setPosition(new Vector2D(random.nextInt(map.getWidth()), random.nextInt(map.getHeight())));
            map.placeMapElement(animal);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        displayPanel.repaint();
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
            animal.update(moveEnergy); //skret i przemieszczenie
        }
        //jedzenie
        map.eating();
        for(Animal animal: animals)
            animal.checkIfCanCanBreed(); //po jedzeniu aktualizuje informacje o zdolnosci zwierzat do rozmnazania
        //rozmnazanie
        map.breeding(animals);
        //dodanie nowych roslin do mapy
        map.spawnPlants();
        era++; //nowa epoka
    }

    public JFrame getFrame() {
        return frame;
    }
}
