import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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
    private Timer timer;

    public Simulation(WorldMap map, int nrOfAnimalsInTheBeginning, int delay, int startEnergy, int plantEnergy, int moveEnergy){
        this.map = map;
        this.nrOfAnimalsInTheBeginning = nrOfAnimalsInTheBeginning;
        this.delay = delay;
        this.startEnergy = startEnergy;
        this.plantEnergy = plantEnergy;
        this.moveEnergy = moveEnergy;
        this.era = 1;
        this.timer = new Timer(delay, this);
        frame = new JFrame("JUNGLE");
        frame.setSize(map.getWidth(), map.getHeight());
        frame.setLocationRelativeTo(null);
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
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        displayPanel.repaint();
        ArrayList<Animal> animals = map.getAnimalsList(); //lista wszystkich zwierzat
        if(animals.size() == 0){ //jesli nie ma juz zywych zwierzat, koncze symulacje
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        for(Animal animal : animals){ //usuwam martwe zwierzeta z mapy
            if(animal.getIsDead())
                map.removeAnimal(animal);
        }
        animals = map.getAnimalsList(); //aktualna lista zwierzat po usunieciu martwych
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
