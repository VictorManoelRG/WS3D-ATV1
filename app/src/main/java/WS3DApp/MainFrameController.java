package WS3DApp;

import WS3DApp.Controls.ControlCreatureByKeyboardFrame;
import WS3DApp.Controls.CreateBricksFrameController;
import WS3DApp.Controls.CreateCreatureFrameController;
import WS3DApp.Controls.CreateThingsFrameController;
import java.util.ArrayList;
import java.util.List;
import ws3dproxy.model.Creature;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.World;
import javax.swing.*;
import ws3dproxy.CommandExecException;

public class MainFrameController {

    private WS3DProxy proxy = new WS3DProxy();

    public Creature controlledCreature;

    public World w;

    private KeyEvent actualKey;

    private List<Creature> listCreatures = new ArrayList<>();
    private MainFrame mainFrame;

    public MainFrameController() {
        createWorld();
        mainFrame = new MainFrame();
        setupFrame();
    }

    private void setupFrame() {

        mainFrame.ControlCreatureByKeyboardButton.addActionListener((ActionEvent e) -> {
            ControlCreatureByKeyboardFrame byKeyboardFrame = new ControlCreatureByKeyboardFrame(controlledCreature);
        });

        mainFrame.CreatureList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedName = e.getItem().toString();

                for (Creature creature : listCreatures) {
                    if (creature.getName().equals(selectedName)) {
                        controlledCreature = creature;
                        break;
                    }
                }
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

        mainFrame.setVisible(true);
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
            listCreatures.add(c);
            mainFrame.CreatureList.addItem(c.getName());
            mainFrame.CreatureList.setSelectedItem(c.getName());
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
            System.out.println("Erro capaturado");
        }
    }
}
