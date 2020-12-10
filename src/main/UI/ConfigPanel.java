import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigPanel extends JPanel implements ActionListener {
    private final JTextField width; //szerokosc mapy
    private final JTextField height; //wysokosc mapy
    private final JTextField startEnergy; //poczatkowa energia zwierzat, ktore pojawiaja sie na mapie na poczatku
    private final JTextField moveEnergy; //energia potrzebna na wykonanie ruchu
    private final JTextField plantEnergy; //energia uzyskana po zjedzeniu rosliny
    private final JTextField jungleRatio; //proporcje dzungli do sawanny
    private final JTextField nrOfAnimalsInTheBeginning; //ilosc zwierzat na poczatku
    private final JTextField delay; //przerwa miedzy kolejnymi dniami

    private final JLabel widthLabel;
    private final JLabel heightLabel;
    private final JLabel startEnergyLabel;
    private final JLabel moveEnergyLabel;
    private final JLabel plantEnergyLabel;
    private final JLabel jungleRatioLabel;
    private final JLabel nrOfAnimalsInTheBeginningLabel;
    private final JLabel delayLabel;

    private final JButton start;

    public ConfigPanel(LoadJson parameters){
        widthLabel = new JLabel("Width: "); //inicjalizacja podpisow
        heightLabel = new JLabel("Height: ");
        startEnergyLabel = new JLabel("Start energy: ");
        moveEnergyLabel = new JLabel("Energy required to move: ");
        plantEnergyLabel = new JLabel("Energy gained by eating a plant: ");
        jungleRatioLabel = new JLabel("Jungle to steppe ratio: ");
        nrOfAnimalsInTheBeginningLabel = new JLabel("Number of animals in the beginning: ");
        delayLabel = new JLabel("Time between displaying the next day [ms]: ");
        width = new JTextField(); //inicjalizacja pol
        height = new JTextField();
        startEnergy = new JTextField();
        moveEnergy = new JTextField();
        plantEnergy = new JTextField();
        jungleRatio = new JTextField();
        nrOfAnimalsInTheBeginning = new JTextField();
        delay = new JTextField();
        width.setText(String.valueOf(parameters.getWidth()));
        height.setText(String.valueOf(parameters.getHeight()));
        startEnergy.setText(String.valueOf(parameters.getStartEnergy()));
        moveEnergy.setText(String.valueOf(parameters.getMoveEnergy()));
        plantEnergy.setText(String.valueOf(parameters.getPlantEnergy()));
        jungleRatio.setText(String.valueOf(parameters.getJungleRatio()));
        nrOfAnimalsInTheBeginning.setText(String.valueOf(parameters.getNrOfAnimalsInTheBeginning()));
        delay.setText(String.valueOf(parameters.getDelay()));
        widthLabel.setLabelFor(width);
        heightLabel.setLabelFor(height);
        startEnergyLabel.setLabelFor(startEnergy);
        moveEnergyLabel.setLabelFor(moveEnergy);
        plantEnergyLabel.setLabelFor(plantEnergy);
        jungleRatioLabel.setLabelFor(jungleRatio);
        nrOfAnimalsInTheBeginningLabel.setLabelFor(nrOfAnimalsInTheBeginning);
        delayLabel.setLabelFor(delay);
        start = new JButton("Start");
        start.addActionListener(this);
        setPreferredSize(new Dimension(parameters.getWidth(), parameters.getHeight()));
        width.setColumns(8);
        height.setColumns(8);
        startEnergy.setColumns(8);
        moveEnergy.setColumns(8);
        plantEnergy.setColumns(8);
        jungleRatio.setColumns(8);
        nrOfAnimalsInTheBeginning.setColumns(8);
        delay.setColumns(8);
        JPanel widthPanel = new JPanel();
        JPanel heightPanel = new JPanel();
        JPanel startEnergyPanel = new JPanel();
        JPanel moveEnergyPanel = new JPanel();
        JPanel plantEnergyPanel = new JPanel();
        JPanel jungleRatioPanel = new JPanel();
        JPanel nrOfAnimalsInTheBeginningPanel = new JPanel();
        JPanel delayPanel = new JPanel();
        JPanel startPanel = new JPanel();
        widthPanel.add(width);
        widthPanel.add(widthLabel);
        heightPanel.add(height);
        heightPanel.add(heightLabel);
        startEnergyPanel.add(startEnergy);
        startEnergyPanel.add(startEnergyLabel);
        moveEnergyPanel.add(moveEnergy);
        moveEnergyPanel.add(moveEnergyLabel);
        plantEnergyPanel.add(plantEnergy);
        plantEnergyPanel.add(plantEnergyLabel);
        jungleRatioPanel.add(jungleRatio);
        jungleRatioPanel.add(jungleRatioLabel);
        nrOfAnimalsInTheBeginningPanel.add(nrOfAnimalsInTheBeginning);
        nrOfAnimalsInTheBeginningPanel.add(nrOfAnimalsInTheBeginningLabel);
        delayPanel.add(delay);
        delayPanel.add(delayLabel);
        startPanel.add(start);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WorldMap map = new WorldMap(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Integer.parseInt(jungleRatio.getText()), Integer.parseInt(plantEnergy.getText()));
        Simulation simulation = new Simulation(map, Integer.parseInt(nrOfAnimalsInTheBeginning.getText()), Integer.parseInt(delay.getText()), Integer.parseInt(startEnergy.getText()));
        simulation.start();
    }
}