import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

public class Simulation implements ActionListener {

    private final WorldMap map; //mapa swiata
    private final JFrame frame;
    private final JFrame statsFrame;
    private final DisplayPanel displayPanel;
    private final StatsPanel statsPanel;
    private final int nrOfAnimalsInTheBeginning; //ilosc zwierzat na poczatku
    private final int startEnergy;
    private final int moveEnergy;
    private final Timer timer;

    public Simulation(WorldMap map, int nrOfAnimalsInTheBeginning, int delay, int startEnergy, int plantEnergy, int moveEnergy, Vector2D location, Vector2D statsPanelPosition, int scale){
        this.map = map;
        this.nrOfAnimalsInTheBeginning = nrOfAnimalsInTheBeginning;
        //przerwa pomiedzy kolejnymi dniami, zeby mozna bylo sledzic symulacje w normalnym tempie
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.timer = new Timer(delay, this);
        frame = new JFrame("JUNGLE");
        frame.setLayout(null);
        statsPanel = new StatsPanel(map);
        statsPanel.setLayout(null);
        statsPanel.setSize(map.getWidth() * scale, 350);
        frame.setSize(map.getWidth() * scale, map.getHeight() * scale);
        frame.setLocation(location.getX(), location.getY());
        frame.setVisible(true);
        displayPanel = new DisplayPanel(map, scale);
        displayPanel.setSize(new Dimension(1,1));
        frame.add(displayPanel);
        statsFrame = new JFrame("STATS");
        statsFrame.setLayout(null);
        statsFrame.setSize(map.getWidth() * scale, 350);
        statsFrame.setLocation(statsPanelPosition.getX(), statsPanelPosition.getY());
        statsFrame.setVisible(true);
        statsFrame.add(statsPanel);
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
        if(!map.getPaused().get()){
            statsPanel.repaint();
            ArrayList<Animal> animals = map.getAnimalsList(); //lista wszystkich zwierzat
            if(animals.size() == 0){ //jesli nie ma juz zywych zwierzat, koncze symulacje
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                statsFrame.dispatchEvent(new WindowEvent(statsFrame, WindowEvent.WINDOW_CLOSING));
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
            map.breeding();
            //dodanie nowych roslin do mapy
            map.spawnPlants();
            map.updateEra(); //nowa epoka
            ArrayList<Integer> currentStats = new ArrayList<>();
            currentStats.add(map.getAnimalsList().size());
            currentStats.add(map.getEnvironmentElementsList().size());
            currentStats.add(map.getDominatingGenotype());
            currentStats.add(map.getAverageEnergyLevel());
            currentStats.add(map.getAverageLongevityForDeadAnimals());
            currentStats.add(map.getAverageNrOfChildrenForAliveAnimals());
            map.updateStats(currentStats);
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}
