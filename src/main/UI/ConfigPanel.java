import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigPanel extends JPanel implements ActionListener {
    private final JTextField startEnergy; //poczatkowa energia zwierzat, ktore pojawiaja sie na mapie na poczatku
    private final JTextField moveEnergy; //energia potrzebna na wykonanie ruchu
    private final JTextField plantEnergy; //energia uzyskana po zjedzeniu rosliny
    private final JTextField nrOfAnimalsInTheBeginning; //ilosc zwierzat na poczatku
    private final JTextField delay; //przerwa miedzy kolejnymi dniami
    private final JTextField leftStatsUntilWhen;
    private final JTextField rightStatsUntilWhen;

    private final Vector2D mapPosition;
    private final Vector2D secondMapPosition;
    private final Vector2D statsPanelPosition;
    private final Vector2D secondStatsPanelPosition;
    private final int scale;
    private final WorldMap map;
    private final WorldMap secondMap;

    private final JButton pauseLeftSimulation;
    private final JButton pauseRightSimulation;
    private final JButton highlightAnimalsWithDominatingGenotypeOnTheLeftMap;
    private final JButton highlightAnimalsWithDominatingGenotypeOnTheRightMap;
    private final JButton getLeftStats;
    private final JButton getRightStats;

    public ConfigPanel(LoadJson parameters){
        JLabel widthLabel = new JLabel("Width: "); //inicjalizacja podpisow
        JLabel heightLabel = new JLabel("Height: ");
        JLabel startEnergyLabel = new JLabel("Start energy: ");
        JLabel moveEnergyLabel = new JLabel("Energy required to move: ");
        JLabel plantEnergyLabel = new JLabel("Energy gained by eating a plant: ");
        JLabel jungleRatioLabel = new JLabel("Jungle to steppe ratio: ");
        JLabel nrOfAnimalsInTheBeginningLabel = new JLabel("Number of animals in the beginning: ");
        JLabel delayLabel = new JLabel("Time between displaying the next day [ms]: ");
        JLabel leftStatsLabel = new JLabel("Until which era shall the statistics for the left map be printed? ");
        JLabel rightStatsLabel = new JLabel("Until which era shall the statistics for the right map be printed? ");
        //szerokosc mapy
        JTextField width = new JTextField(); //inicjalizacja pol
        //wysokosc mapy
        JTextField height = new JTextField();
        startEnergy = new JTextField();
        moveEnergy = new JTextField();
        plantEnergy = new JTextField();
        //proporcje dzungli do sawanny
        JTextField jungleRatio = new JTextField();
        nrOfAnimalsInTheBeginning = new JTextField();
        delay = new JTextField();
        leftStatsUntilWhen = new JTextField();
        rightStatsUntilWhen = new JTextField();
        width.setText(String.valueOf(parameters.getWidth()));
        height.setText(String.valueOf(parameters.getHeight()));
        startEnergy.setText(String.valueOf(parameters.getStartEnergy()));
        moveEnergy.setText(String.valueOf(parameters.getMoveEnergy()));
        plantEnergy.setText(String.valueOf(parameters.getPlantEnergy()));
        jungleRatio.setText(String.valueOf(parameters.getJungleRatio()));
        nrOfAnimalsInTheBeginning.setText(String.valueOf(parameters.getNrOfAnimalsInTheBeginning()));
        delay.setText(String.valueOf(parameters.getDelay()));
        leftStatsUntilWhen.setText(String.valueOf(1));
        rightStatsUntilWhen.setText(String.valueOf(1));
        widthLabel.setLabelFor(width);
        heightLabel.setLabelFor(height);
        startEnergyLabel.setLabelFor(startEnergy);
        moveEnergyLabel.setLabelFor(moveEnergy);
        plantEnergyLabel.setLabelFor(plantEnergy);
        jungleRatioLabel.setLabelFor(jungleRatio);
        nrOfAnimalsInTheBeginningLabel.setLabelFor(nrOfAnimalsInTheBeginning);
        delayLabel.setLabelFor(delay);
        leftStatsLabel.setLabelFor(leftStatsUntilWhen);
        rightStatsLabel.setLabelFor(rightStatsUntilWhen);
        JButton start = new JButton("Start the simulations");
        pauseLeftSimulation = new JButton("Pause left simulation");
        pauseRightSimulation = new JButton("Pause right simulation");
        highlightAnimalsWithDominatingGenotypeOnTheLeftMap = new JButton("Highlight animals with dominating genotype on the left map");
        highlightAnimalsWithDominatingGenotypeOnTheRightMap = new JButton("Highlight animals with dominating genotype on the right map");

        getLeftStats = new JButton("Get left map's stats as txt file");
        getRightStats = new JButton("Get right map's stats as txt file");
        start.addActionListener(this);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        scale = Integer.parseInt(width.getText()) / 10;
        mapPosition = new Vector2D(screenWidth / 2 - 500 - (Integer.parseInt(height.getText()) * scale), 300 + screenHeight / 2 - 500 - Integer.parseInt(height.getText()));
        map = new WorldMap(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Double.parseDouble(jungleRatio.getText()), Integer.parseInt(plantEnergy.getText()));
        secondMapPosition = new Vector2D(screenWidth / 2 + 500, 300 + screenHeight / 2 - 500 - Integer.parseInt(height.getText()));
        secondMap = new WorldMap(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Double.parseDouble(jungleRatio.getText()), Integer.parseInt(plantEnergy.getText()));
        statsPanelPosition = new Vector2D(mapPosition.getX(), mapPosition.getY() + Integer.parseInt(height.getText()) * scale);
        secondStatsPanelPosition = new Vector2D(secondMapPosition.getX(), secondMapPosition.getY() + Integer.parseInt(height.getText()) * scale);

        pauseLeftSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!map.getPaused().get()){
                    pauseLeftSimulation.setText("Resume left simulation");
                    map.setPaused(true);
                }else{
                    pauseLeftSimulation.setText("Pause left simulation");
                    map.setPaused(false);
                    map.setHighlightDominatingGenotype(false);
                }
            }
        });

        pauseRightSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!secondMap.getPaused().get()){
                    pauseRightSimulation.setText("Resume right simulation");
                    secondMap.setPaused(true);
                }else{
                    pauseRightSimulation.setText("Pause right simulation");
                    secondMap.setPaused(false);
                    secondMap.setHighlightDominatingGenotype(false);
                }
            }
        });

        highlightAnimalsWithDominatingGenotypeOnTheLeftMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!map.getHighlightDominatingGenotype().get()){
                    map.setHighlightDominatingGenotype(true);
                    highlightAnimalsWithDominatingGenotypeOnTheLeftMap.setText("Stop highlighting animals on the left map");
                }else{
                    map.setHighlightDominatingGenotype(false);
                    highlightAnimalsWithDominatingGenotypeOnTheLeftMap.setText("Highlight animals with dominating genotype on the left map");
                }
            }
        });

        highlightAnimalsWithDominatingGenotypeOnTheRightMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!secondMap.getHighlightDominatingGenotype().get()){
                    secondMap.setHighlightDominatingGenotype(true);
                    highlightAnimalsWithDominatingGenotypeOnTheRightMap.setText("Stop highlighting animals on the right map");
                }else{
                    secondMap.setHighlightDominatingGenotype(false);
                    highlightAnimalsWithDominatingGenotypeOnTheRightMap.setText("Highlight animals with dominating genotype on the right map");
                }
            }
        });

        getLeftStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map.getAverageStats(Integer.parseInt(leftStatsUntilWhen.getText()));
            }
        });

        getRightStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondMap.getAverageStats(Integer.parseInt(rightStatsUntilWhen.getText()));
            }
        });

        setPreferredSize(new Dimension(getWidth(), getHeight()));
        width.setColumns(8);
        height.setColumns(8);
        startEnergy.setColumns(8);
        moveEnergy.setColumns(8);
        plantEnergy.setColumns(8);
        jungleRatio.setColumns(8);
        nrOfAnimalsInTheBeginning.setColumns(8);
        delay.setColumns(8);
        leftStatsUntilWhen.setColumns(8);
        rightStatsUntilWhen.setColumns(8);
        JPanel widthPanel = new JPanel();
        JPanel heightPanel = new JPanel();
        JPanel startEnergyPanel = new JPanel();
        JPanel moveEnergyPanel = new JPanel();
        JPanel plantEnergyPanel = new JPanel();
        JPanel jungleRatioPanel = new JPanel();
        JPanel nrOfAnimalsInTheBeginningPanel = new JPanel();
        JPanel delayPanel = new JPanel();
        JPanel startPanel = new JPanel();
        JPanel leftPausePanel = new JPanel();
        JPanel rightPausePanel = new JPanel();
        JPanel leftHighlightPanel = new JPanel();
        JPanel rightHighlightPanel = new JPanel();
        widthPanel.add(widthLabel);
        widthPanel.add(width);
        heightPanel.add(heightLabel);
        heightPanel.add(height);
        startEnergyPanel.add(startEnergyLabel);
        startEnergyPanel.add(startEnergy);
        leftPausePanel.add(pauseLeftSimulation);
        rightPausePanel.add(pauseRightSimulation);
        leftHighlightPanel.add(highlightAnimalsWithDominatingGenotypeOnTheLeftMap);
        rightHighlightPanel.add(highlightAnimalsWithDominatingGenotypeOnTheRightMap);
        moveEnergyPanel.add(moveEnergyLabel);
        moveEnergyPanel.add(moveEnergy);
        plantEnergyPanel.add(plantEnergyLabel);
        plantEnergyPanel.add(plantEnergy);
        jungleRatioPanel.add(jungleRatioLabel);
        jungleRatioPanel.add(jungleRatio);
        nrOfAnimalsInTheBeginningPanel.add(nrOfAnimalsInTheBeginningLabel);
        nrOfAnimalsInTheBeginningPanel.add(nrOfAnimalsInTheBeginning);
        delayPanel.add(delayLabel);
        delayPanel.add(delay);
        startPanel.add(start);
        add(widthPanel);
        add(heightPanel);
        add(startEnergyPanel);
        add(moveEnergyPanel);
        add(plantEnergyPanel);
        add(jungleRatioPanel);
        add(nrOfAnimalsInTheBeginningPanel);
        add(delayPanel);
        add(startPanel);
        add(leftPausePanel);
        add(rightPausePanel);
        add(leftHighlightPanel);
        add(rightHighlightPanel);
        add(leftStatsLabel);
        add(leftStatsUntilWhen);
        add(getLeftStats);
        add(rightStatsLabel);
        add(rightStatsUntilWhen);
        add(getRightStats);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Simulation simulation = new Simulation(map, Integer.parseInt(nrOfAnimalsInTheBeginning.getText()), Integer.parseInt(delay.getText()), Integer.parseInt(startEnergy.getText()), Integer.parseInt(plantEnergy.getText()), Integer.parseInt(moveEnergy.getText()), mapPosition, statsPanelPosition, scale);
        Simulation secondSimulation = new Simulation(secondMap, Integer.parseInt(nrOfAnimalsInTheBeginning.getText()), Integer.parseInt(delay.getText()), Integer.parseInt(startEnergy.getText()), Integer.parseInt(plantEnergy.getText()), Integer.parseInt(moveEnergy.getText()), secondMapPosition, secondStatsPanelPosition, scale);
        simulation.start();
        secondSimulation.start();
    }
}