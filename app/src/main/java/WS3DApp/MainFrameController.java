package WS3DApp;

import WS3DApp.Controls.ControlCreatureByKeyboardFrame;
import WS3DApp.Controls.CreateBricksFrameController;
import WS3DApp.Controls.CreateCreatureFrameController;
import WS3DApp.Controls.CreateLeafletFrameController;
import WS3DApp.Controls.CreateThingsFrameController;
import ws3dproxy.model.Creature;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.World;
import javax.swing.*;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;

public class MainFrameController {

    private WS3DProxy proxy = new WS3DProxy();
    public Creature controlledCreature;
    private Map<String, String> creatureMap = new HashMap<>();
    public World w;
    private KeyEvent actualKey;
    private MainFrame mainFrame;

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
                System.out.println("Item selecionado: " + selectedItem + " com ID: " + id);

                try {
                    controlledCreature = proxy.getCreature(id);
                } catch (CommandExecException ex) {
                    Logger.getLogger(MainFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        mainFrame.ControlCreatureByKeyboardButton.addActionListener((ActionEvent e) -> {
            if (controlledCreature != null) {
                ControlCreatureByKeyboardFrame byKeyboardFrame = new ControlCreatureByKeyboardFrame(controlledCreature, this, mainFrame.ListObservableThings);
                byKeyboardFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        byKeyboardFrame.dispose();
                        System.out.println("Frame fechado.");
                    }
                });
            } else {
                System.out.println("Nenhuma criatura selecionada.");
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

        DefaultListModel<String> listModel = new DefaultListModel<>();
        mainFrame.ListObservableThings.setModel(listModel);

        mainFrame.setVisible(true);
    }

    public void updateThingsInVision() {
        DefaultListModel<String> listModel = (DefaultListModel<String>) mainFrame.ListObservableThings.getModel();

        listModel.clear();

        List<Thing> visibleThings = controlledCreature.getThingsInVision();

        for (Thing thing : visibleThings) {
            listModel.addElement(thing.getName());
        }
    }

    public void assignLeafletToCreature(String jewelColor, int quantity, int payment, String creatureName) {
        String creatureID = creatureMap.get(creatureName);

        try {
            Creature c = proxy.getCreature(creatureID);
            HashMap<String, Integer[]> mapObjective = new HashMap<>();
            mapObjective.put(jewelColor, new Integer[]{quantity, 0});

            long leafletID;
            Random random = new Random();
            do {
                leafletID = Math.abs(random.nextLong(1000)); 
            } while (leafletExists(c, leafletID));

            Leaflet l = new Leaflet(leafletID, mapObjective, payment, 0);
            c.addLeaflet(l);
            System.out.println(c.getLeaflets());
        } catch (CommandExecException ex) {
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
            System.out.println("Criatura criada com nome: " + c.getName() + " com ID: " + c.getIndex());

            creatureMap.put(c.getName(), c.getIndex());

            SwingUtilities.invokeLater(() -> {
                mainFrame.CreatureList.addItem(c.getName());
                mainFrame.CreatureList.setSelectedItem(c.getName());
            });

            System.out.println("Criatura adicionada: " + c.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível criar a criatura.",
                    "Erro ao Criar Criatura",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createWorld() {
        WS3DProxy proxy = new WS3DProxy();
        try {
            w = World.getInstance();
            w.reset();
        } catch (Exception e) {
            System.out.println("Erro capturado");
        }
    }

}
