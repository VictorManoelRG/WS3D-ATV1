package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;

public class ControlCreatureByKeyboardFrame extends JFrame implements KeyListener {

    private static final double COLLECTION_DISTANCE_THRESHOLD_JEWEL = 20.0;
    private static final double COLLECTION_DISTANCE_THRESHOLD_DELIVERY_SPOT = 85.0;
    private static final double STOPPED_THRESHOLD = 0.1;

    private Creature controlledCreature;
    private boolean isStarted = false;
    private JList<String> ListObservableThings;
    private MainFrameController mainFrameController;
    private World world;

    private Timer energyMonitor;

    public ControlCreatureByKeyboardFrame(Creature creature, MainFrameController controller, JList<String> list, World w) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(new GridBagLayout());
        addJLabel();
        this.addKeyListener(this);
        
        controlledCreature = creature;
        ListObservableThings = list;
        mainFrameController = controller;
        world = w;

        startFuelMonitor();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closeFrame();
            }
        });

        this.setVisible(true);
    }
    
    private void addJLabel(){
        JLabel label = new JLabel("Controle a criatura pelo teclado");
        label.setFont(new Font("Arial", Font.BOLD, 16));

        this.add(label, new GridBagConstraints());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isStarted) {
            try {
                controlledCreature.start();
                isStarted = true;
            } catch (CommandExecException ex) {
                Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                moveCreature(3, 3);
                break;
            case KeyEvent.VK_DOWN:
                moveCreature(-3, -3);
                break;
            case KeyEvent.VK_LEFT:
                moveCreature(2, -2);
                break;
            case KeyEvent.VK_RIGHT:
                moveCreature(-2, 2);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        stopMovement();
    }

    private void moveCreature(double x, double y) {
        try {
            controlledCreature.move(x, y, 0);
            controlledCreature = controlledCreature.updateState();
            mainFrameController.updateThingsInVision();

            var thingsInVision = new ArrayList<>(controlledCreature.getThingsInVision());
            for (Thing thing : thingsInVision) {
                mainFrameController.collectThingIfNear(controlledCreature, thing);
            }

            controlledCreature = controlledCreature.updateState();

        } catch (CommandExecException ex) {
            Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void stopMovement() {
        try {
            controlledCreature.move(0, 0, 0);
            controlledCreature.stop();
            controlledCreature = controlledCreature.updateState();
        } catch (CommandExecException ex) {
            Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startFuelMonitor() {
        energyMonitor = new Timer(250, e -> updateFuelDisplay());
        energyMonitor.start();
    }

    private void updateFuelDisplay() {
        SwingUtilities.invokeLater(() -> {
            controlledCreature = controlledCreature.updateState();
            double energy = controlledCreature.getFuel();
            mainFrameController.setCreatureEnergy(energy);
        });
    }

    private void closeFrame() {
        if (energyMonitor != null) {
            energyMonitor.stop();
        }

        if (controlledCreature != null) {
            try {
                controlledCreature.move(0, 0, 0);
                controlledCreature.stop();
                Thread.sleep(500);
            } catch (CommandExecException ex) {
                Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
