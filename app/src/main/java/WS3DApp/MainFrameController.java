package WS3DApp;

import WS3DApp.Controls.ControlCreatureByKeyboardFrame;
import WS3DApp.Controls.CreateBricksFrameController;
import WS3DApp.Controls.CreateCreatureFrameController;
import WS3DApp.Controls.CreateLeafletFrameController;
import WS3DApp.Controls.CreateThingsFrameController;
import ws3dproxy.model.Creature;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.World;
import javax.swing.*;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Bag;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.util.Constants;
import ws3dproxy.model.WorldPoint;

public class MainFrameController {

    private static final double COLLECTION_DISTANCE_THRESHOLD_JEWEL = 20.0;
    private static final double COLLECTION_DISTANCE_THRESHOLD_DELIVERY_SPOT = 65.0;

    private WS3DProxy proxy = new WS3DProxy();
    public Creature controlledCreature;
    private Map<String, String> creatureMap = new HashMap<>();
    private Set<Long> createdItems = new HashSet<>();
    //private Map<String, List<Leaflet>> creatureLeaflets = new HashMap<>();
    private Map<String, Integer> creaturePoints = new HashMap<>();
    public World w;
    private KeyEvent actualKey;
    private MainFrame mainFrame;
    private ControlCreatureByKeyboardFrame byKeyboardFrame;
    private Set<Long> completedLeaflets = new HashSet<>();
    private int lastTotalCrystalsQuantity = Integer.MIN_VALUE;
    private final Set<String> collectedJewels = new HashSet<>();

    public MainFrameController() {
        createWorld();
        mainFrame = new MainFrame();
        setupFrame();
    }

    private void setupFrame() {
        mainFrame.CreatureList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = mainFrame.CreatureList.getSelectedItem().toString();
                String id = creatureMap.get(selectedItem);

                try {
                    controlledCreature = proxy.getCreature(id);
                    Thread.sleep(2000);
                    updateCreatureLeafletsList(controlledCreature);
                    updateCreatureBagList(controlledCreature);
                    updateOtherCreatureInfos(controlledCreature);
                } catch (CommandExecException ex) {
                    Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        mainFrame.ControlCreatureByKeyboardButton.addActionListener((ActionEvent e) -> {
            if (controlledCreature != null) {
                try {
                    w = proxy.getWorld();
                    Thread.sleep(1000);
                    var c = proxy.getCreature(creatureMap.get(mainFrame.CreatureList.getSelectedItem()));
                    Thread.sleep(1000);
                    byKeyboardFrame = new ControlCreatureByKeyboardFrame(c, this, mainFrame.ListObservableThings, w);
                    byKeyboardFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            byKeyboardFrame.dispose();
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CommandExecException ex) {
                    Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        mainFrame.MoveToCoordinateButton.addActionListener((ActionEvent e) -> {
            if (controlledCreature != null) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            double x = Double.parseDouble(mainFrame.CoordinateXMove.getText());
                            double y = Double.parseDouble(mainFrame.CoordinateYMove.getText());

                            controlledCreature.start();
                            controlledCreature.moveto(4, x, y);
                            Thread.sleep(5000);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Não foi possível mover a criatura.",
                                    "Erro ao mover a criatura",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            controlledCreature.stop();
                            controlledCreature = controlledCreature.updateState();
                            updateThingsInVision();

                            var thingsInVision = new ArrayList<>(controlledCreature.getThingsInVision());
                            Iterator<Thing> iterator = thingsInVision.iterator();
                            while (iterator.hasNext()) {
                                Thing thing = iterator.next();
                                collectThingIfNear(controlledCreature, thing);
                            }

                            controlledCreature = controlledCreature.updateState();
                            setCreatureEnergy(controlledCreature.getFuel());
                        } catch (CommandExecException ex) {
                            Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.execute();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Nenhuma criatura selecionada.",
                        "Erro ao mover a criatura",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        mainFrame.CreateCreatureButton.addActionListener((ActionEvent e) -> {
            createNewCreature_ButtonClick();
        });

        mainFrame.CreateThingsButton.addActionListener((ActionEvent e) -> {
            createNewThing_ButtonClick();
        });

        mainFrame.CreateBricksButton.addActionListener((ActionEvent e) -> {
            createNewBrick_ButtonClick();
        });

        mainFrame.CreateLeafletButton.addActionListener((ActionEvent e) -> {
            createNewLeaflet_ButtonClick();
        });

        DefaultListModel<String> listModelObservableThings = new DefaultListModel<>();
        mainFrame.ListObservableThings.setModel(listModelObservableThings);

        DefaultListModel<String> listModelLeaflets = new DefaultListModel<>();
        mainFrame.CreatureLeafletList.setModel(listModelLeaflets);

        DefaultListModel<String> listModelBags = new DefaultListModel<>();
        mainFrame.CreatureBagList.setModel(listModelBags);

        mainFrame.setVisible(true);
    }

    public void collectThingIfNear(Creature creature, Thing thing) {
        if (isNearThing(creature, thing)) {
            String thingId = thing.getAttributes().getName();

            // Se a criatura já coletou essa joia, não faz nada
            if (thing.getAttributes().getCategory() == Constants.categoryJEWEL && collectedJewels.contains(thingId)) {
                return;
            }

            try {
                switch (thing.getAttributes().getCategory()) {
                    case Constants.categoryDeliverySPOT ->
                        canDeliverLeaflet(creature);
                    case Constants.categoryPFOOD, Constants.categoryNPFOOD -> {
                        creature.eatIt(thingId);
                        creature = creature.updateState();
                        Thread.sleep(500);
                    }
                    case Constants.categoryJEWEL -> {
                        collectedJewels.add(thingId); // Marca como coletada
                        creature.updateBag();
                        creature.putInSack(thingId);
                        Thread.sleep(500);
                        creature.updateBag();
                        updateCreatureBag(controlledCreature, thing.getAttributes().getColor());
                    }
                    default -> {
                    }
                }

                controlledCreature = creature.updateState();
                if (byKeyboardFrame != null) {
                    byKeyboardFrame.controlledCreature = controlledCreature;
                }

            } catch (CommandExecException e) {
                System.err.println("Erro ao coletar o item: " + e.getMessage());
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isNearThing(Creature creature, Thing thing) {
        double distanceThreshold = COLLECTION_DISTANCE_THRESHOLD_JEWEL;
        if (thing.getAttributes().getCategory() == Constants.categoryDeliverySPOT) {
            distanceThreshold = COLLECTION_DISTANCE_THRESHOLD_DELIVERY_SPOT;
        }

        double distance = creature.calculateDistanceTo(thing);

        return distance < distanceThreshold;
    }

    public void updateThingsInVision() {
        DefaultListModel<String> listModel = (DefaultListModel<String>) mainFrame.ListObservableThings.getModel();

        listModel.clear();

        List<Thing> visibleThings = controlledCreature.getThingsInVision();

        for (Thing thing : visibleThings) {
            listModel.addElement(thing.getName());
        }
    }

    public void updateCreatureLeafletsList(Creature c) {
        DefaultListModel<String> listModel = (DefaultListModel<String>) mainFrame.CreatureLeafletList.getModel();
        listModel.clear();

        List<Leaflet> leaflets = c.getLeaflets();

        if (leaflets == null) {
            return;
        }

        for (var item : leaflets) {
            if (completedLeaflets.contains(item.getID())) {
                continue;
            }
            StringBuilder jewelInfo = new StringBuilder();

            for (var entry : item.getItems().entrySet()) {
                String jewelType = entry.getKey();
                Integer[] valores = entry.getValue();
                jewelInfo.append(" Tipo: ").append(jewelType)
                        .append(" Meta: ").append(valores[0])
                        .append(" Acumulados: ").append(valores[1])
                        .append(" | ");
            }

            listModel.addElement("Id: " + item.getID()
                    + " Pagamento: " + item.getPayment()
                    + " | " + jewelInfo.toString());
        }
    }

    public void canDeliverLeaflet(Creature creature) {
        var leaflets = creature.getLeaflets();

        if (leaflets == null) {
            return;
        }

        for (var leaflet : leaflets) {
            if (leaflet.isCompleted() && !completedLeaflets.contains(leaflet.getID())) {

                try {
                    System.out.println("tentou entregar");
                    completedLeaflets.add(leaflet.getID());
                    creaturePoints.put(creature.getName(), creaturePoints.get(creature.getName()) + leaflet.getPayment());

                    var points = creaturePoints.get(creature.getName());
                    creature.deliverLeaflet(leaflet.getID().toString());
                    leaflet.setSituation(1);
                    creature.updateLeaflet(leaflet.getID(), leaflet.getItems(), leaflet.getSituation());
                    creature = creature.updateState();
                    controlledCreature = creature = removeItensFromBag(leaflet, creature);
                    if (byKeyboardFrame != null) {
                        byKeyboardFrame.controlledCreature = controlledCreature;
                    }

                    if (controlledCreature.getName().equals(creature.getName())) {
                        mainFrame.CreatureTotalPoints.setText(String.valueOf(points));
                    }

                    updateCreatureLeafletsList(controlledCreature);
                    updateCreatureBagList(controlledCreature);
                } catch (CommandExecException ex) {
                    Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public void updateCreatureBag(Creature creature, String color) {
        creature.updateBag();
        var leaflets = creature.getLeaflets();

        if (leaflets != null) {
            for (var leaflet : leaflets) {
                var itemsMap = leaflet.getItems();

                if (itemsMap.containsKey(color)) {
                    Integer[] counts = itemsMap.get(color);

                    // Verifica se o count já atingiu o limite
                    if (counts[0] == counts[1]) {
                        continue; // Pula para o próximo leaflet
                    }

                    System.out.println("Atualizando leaflet: " + leaflet.getID() + " para cor " + color);

                    // Incrementa o count e atualiza o mapa
                    counts[1] += 1;
                    itemsMap.put(color, counts);

                    // Atualiza o leaflet no creature
                    leaflet.setItems(itemsMap);
                    creature.updateLeaflet(leaflet.getID(), leaflet.getItems(), leaflet.getSituation());

                    // Sai do loop após incrementar o count
                    break;
                }
            }
        }

        updateCreatureBagList(creature);
        updateCreatureLeafletsList(creature);
    }

    private void updateCreatureBagList(Creature creature) {
        DefaultListModel<String> listModel = (DefaultListModel<String>) mainFrame.CreatureBagList.getModel();
        listModel.clear();

        Bag bag = creature.getBag();

        if (bag == null) {
            return;
        }

        listModel.addElement("Total de comida: " + bag.getTotalNumberFood());
        listModel.addElement("Comidas perecíveis: " + bag.getNumberPFood());
        listModel.addElement("Comidas não perecíveis: " + bag.getNumberNPFood());
        listModel.addElement("Total de cristais: " + bag.getTotalNumberCrystals());
        listModel.addElement("Total de cristais vermelhos: " + bag.getNumberCrystalPerType(Constants.colorRED));
        listModel.addElement("Total de cristais verdes: " + bag.getNumberCrystalPerType(Constants.colorGREEN));
        listModel.addElement("Total de cristais azuis: " + bag.getNumberCrystalPerType(Constants.colorBLUE));
        listModel.addElement("Total de cristais amarelos: " + bag.getNumberCrystalPerType(Constants.colorYELLOW));
        listModel.addElement("Total de cristais magentas: " + bag.getNumberCrystalPerType(Constants.colorMAGENTA));
        listModel.addElement("Total de cristais brancos: " + bag.getNumberCrystalPerType(Constants.colorWHITE));
    }

    public void assignLeafletToCreature(String jewelRed, int quantityRed, String jewelGreen, int quantityGreen,
            String jewelBlue, int quantityBlue, String jewelYellow, int quantityYellow, String jewelMagenta, int quantityMagenta,
            String jewelWhite, int quantityWhite, int payment, String creatureName) {

        String creatureID = creatureMap.get(creatureName);

        try {
            Creature c = proxy.getCreature(creatureID);
            Thread.sleep(500);
            HashMap<String, Integer[]> mapObjective = new HashMap<>();

            if (quantityRed > 0) {
                mapObjective.put(jewelRed, new Integer[]{quantityRed, 0});
            }
            if (quantityGreen > 0) {
                mapObjective.put(jewelGreen, new Integer[]{quantityGreen, 0});
            }
            if (quantityBlue > 0) {
                mapObjective.put(jewelBlue, new Integer[]{quantityBlue, 0});
            }
            if (quantityYellow > 0) {
                mapObjective.put(jewelYellow, new Integer[]{quantityYellow, 0});
            }
            if (quantityMagenta > 0) {
                mapObjective.put(jewelMagenta, new Integer[]{quantityMagenta, 0});
            }
            if (quantityWhite > 0) {
                mapObjective.put(jewelWhite, new Integer[]{quantityWhite, 0});
            }

            long leafletID;
            Random random = new Random();
            do {
                leafletID = Math.abs(random.nextLong(1000));
            } while (leafletExists(c, leafletID));

            Leaflet l = new Leaflet(leafletID, mapObjective, payment, 0);
            c.addLeaflet(l);

            c = c.updateState();
            createCreatureLeafletMetaInRandomPositions(c.getLeaflets());
            updateCreatureLeafletsList(c);
        } catch (CommandExecException | InterruptedException ex) {
            Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean leafletExists(Creature creature, long id) {
        return creature.getLeaflets().stream().anyMatch(leaflet -> leaflet.getID() == id);
    }

    private void createNewLeaflet_ButtonClick() {
        if (creatureMap.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível criar um Leaflet sem antes criar criaturas.",
                    "Erro ao Criar Leaflet",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        CreateLeafletFrameController controller = new CreateLeafletFrameController(creatureMap, this);
    }

    private void createNewBrick_ButtonClick() {
        CreateBricksFrameController controller = new CreateBricksFrameController(w.getEnvironmentWidth(), w.getEnvironmentHeight(), this);
    }

    public void createBrick(int type, double x1, double y1, double x2, double y2) {
        try {
            World.createBrick(type, x1, y1, x2, y2);
        } catch (CommandExecException ex) {
            Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createNewThing_ButtonClick() {
        CreateThingsFrameController controller = new CreateThingsFrameController(w.getEnvironmentWidth(), w.getEnvironmentHeight(), this);
    }

    public void createFood(int type, double coordinateX, double coordinateY) {
        try {
            World.createFood(type, coordinateX, coordinateY);
        } catch (CommandExecException ex) {
            Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createJewel(int type, double coordinateX, double coordinateY) {
        try {
            World.createJewel(type, coordinateX, coordinateY);
        } catch (CommandExecException ex) {
            Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createNewCreature_ButtonClick() {
        CreateCreatureFrameController controller = new CreateCreatureFrameController(w.getEnvironmentWidth(), w.getEnvironmentHeight(), this);
    }

    public void createNewCreature(double coordinateX, double coordinateY) {
        try {
            Creature c = proxy.createCreature(coordinateX, coordinateY, 0);

            creatureMap.put(c.getName(), c.getIndex());
            creaturePoints.put(c.getName(), 0);
            createCreatureLeafletMetaInRandomPositions(c.getLeaflets());

            SwingUtilities.invokeLater(() -> {
                mainFrame.CreatureList.addItem(c.getName());
                mainFrame.CreatureList.setSelectedItem(c.getName());
                mainFrame.CreatureTotalPoints.setText(creaturePoints.get(c.getName()).toString());
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível criar a criatura.",
                    "Erro ao Criar Criatura",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createWorld() {
        try {
            w = World.getInstance();
            w.reset();

            double worldWidth = w.getEnvironmentWidth();
            double worldHeight = w.getEnvironmentHeight();

            double randomX = Math.random() * worldWidth;
            double randomY = Math.random() * worldHeight;

            w.createDeliverySpot(300, 300);
            w = proxy.getWorld();
        } catch (CommandExecException e) {
        }
    }

    public void setCreatureEnergy(double energy) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.CreatureEnergy.setText(String.valueOf((int) Math.floor(energy)));
        });
    }

    private void updateOtherCreatureInfos(Creature creature) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.CreatureTotalPoints.setText(creaturePoints.get(creature.getName()).toString());
            setCreatureEnergy(creature.getFuel());
        });
    }

    private void createCreatureLeafletMetaInRandomPositions(List<Leaflet> leaflets) {
        for (var item : leaflets) {
            for (var mapEntry : item.getItems().entrySet()) {
                String color = mapEntry.getKey();
                Integer numMeta = mapEntry.getValue()[0];

                int type = 0;
                switch (color) {
                    case "Red" ->
                        type = 0;
                    case "Green" ->
                        type = 1;
                    case "Blue" ->
                        type = 2;
                    case "Yellow" ->
                        type = 3;
                    case "Magenta" ->
                        type = 4;
                    case "White" ->
                        type = 5;
                    default -> {
                    }
                }

                for (int i = 0; i < numMeta; i++) {
                    double[] randomPosition = getRandomPosition();

                    if (!createdItems.contains(item.getID())) {
                        createJewel(type, randomPosition[0], randomPosition[1]);
                    }
                }
            }

            createdItems.add(item.getID());
        }
    }

    private double[] getRandomPosition() {
        Random random = new Random();
        double x, y;
        boolean isPositionValid;

        List<Thing> things = w.getEveryThing();

        do {
            x = random.nextDouble() * w.getEnvironmentWidth();
            y = random.nextDouble() * w.getEnvironmentHeight();

            isPositionValid = true;
            for (Thing thing : things) {
                WorldPoint[] vertices = thing.getVertex();
                double x1 = vertices[0].getX();
                double y1 = vertices[0].getY();
                double x2 = vertices[3].getX();
                double y2 = vertices[3].getY();

                if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
                    isPositionValid = false;
                    break;
                }
            }
        } while (!isPositionValid);

        return new double[]{x, y};
    }

    private Creature removeItensFromBag(Leaflet leaflet, Creature creature) {

        Bag bag = creature.getBag();

        var mapItems = leaflet.getItems();

        Integer redCrystals = mapItems.containsKey("Red") ? mapItems.get("Red")[0] : 0;
        Integer greenCrystals = mapItems.containsKey("Green") ? mapItems.get("Green")[0] : 0;
        Integer blueCrystals = mapItems.containsKey("Blue") ? mapItems.get("Blue")[0] : 0;
        Integer yellowCrystals = mapItems.containsKey("Yellow") ? mapItems.get("Yellow")[0] : 0;
        Integer magentaCrystals = mapItems.containsKey("Magenta") ? mapItems.get("Magenta")[0] : 0;
        Integer whiteCrystals = mapItems.containsKey("White") ? mapItems.get("White")[0] : 0;

        List<Integer> list = new ArrayList<>();
        list.add(bag.getNumberCrystalPerType(Constants.colorRED) - redCrystals);
        list.add(bag.getNumberCrystalPerType(Constants.colorGREEN) - greenCrystals);
        list.add(bag.getNumberCrystalPerType(Constants.colorBLUE) - blueCrystals);
        list.add(bag.getNumberCrystalPerType(Constants.colorYELLOW) - yellowCrystals);
        list.add(bag.getNumberCrystalPerType(Constants.colorMAGENTA) - magentaCrystals);
        list.add(bag.getNumberCrystalPerType(Constants.colorWHITE) - whiteCrystals);

        Integer totalDisc = redCrystals + greenCrystals + blueCrystals + yellowCrystals + magentaCrystals + whiteCrystals;

        if (bag.getTotalNumberCrystals() > 0 && bag.getTotalNumberCrystals() != lastTotalCrystalsQuantity) {
            bag.update(bag.getTotalNumberFood(), bag.getTotalNumberCrystals() - totalDisc, bag.getNumberPFood(), bag.getNumberNPFood(), list);
            lastTotalCrystalsQuantity = bag.getTotalNumberCrystals();
        }

        return creature;
    }
}
