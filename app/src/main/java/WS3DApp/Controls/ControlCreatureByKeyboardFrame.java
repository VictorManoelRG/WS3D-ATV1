package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;

public class ControlCreatureByKeyboardFrame extends JFrame implements KeyListener {

    private static final double COLLECTION_DISTANCE_THRESHOLD = 20.0;
    private Creature controlledCreature;
    private boolean isStarted = false;
    private static final double STOPPED_THRESHOLD = 0.1;
    private JList<String> ListObservableThings;
    private MainFrameController mainFrameController;
    private World world;

    public ControlCreatureByKeyboardFrame(Creature creature, MainFrameController controller, JList<String> list, World w) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(null);
        this.addKeyListener(this);
        controlledCreature = creature;
        ListObservableThings = list;
        mainFrameController = controller;
        world = w;
        this.setVisible(true);
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

                moveCreature(1, 1);
                break;
            case KeyEvent.VK_DOWN:

                moveCreature(-1, -1);
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
            // Move the creature directly
            controlledCreature.move(x, y, 0);
            controlledCreature = controlledCreature.updateState();
            mainFrameController.updateThingsInVision();

            var thingsInVision = controlledCreature.getThingsInVision();
            System.out.println("Things perto na visão: " + thingsInVision.size());
            for (Thing thing : thingsInVision) {
                collectThingIfNear(controlledCreature, thing, COLLECTION_DISTANCE_THRESHOLD);
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

    private void collectThingIfNear(Creature creature, Thing thing, double distanceThreshold) {
    if (isNearThing(creature, thing, distanceThreshold)) {
        try {
            creature.putInSack(thing.getAttributes().getName());
            creature.updateBag(); // Atualiza a bag após coletar o item
            controlledCreature = creature.updateState();
            mainFrameController.updateCreatureBag(controlledCreature);
        } catch (CommandExecException e) {
            System.err.println("Erro ao coletar o item: " + e.getMessage());
        }
    }
}

    private boolean isNearThing(Creature creature, Thing thing, double distanceThreshold) {
        double distance = creature.calculateDistanceTo(thing);
        System.out.println("Distância até o item " + thing.getAttributes().getName() + ": " + distance);
        return distance < distanceThreshold;
    }
}
