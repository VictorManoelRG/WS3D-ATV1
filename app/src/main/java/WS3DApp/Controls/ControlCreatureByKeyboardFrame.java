package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.util.Constants;

public class ControlCreatureByKeyboardFrame extends JFrame implements KeyListener {

    private static final double COLLECTION_DISTANCE_THRESHOLD_JEWEL = 20.0;
    private static final double COLLECTION_DISTANCE_THRESHOLD_DELIVERY_SPOT = 85.0;
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
            // Move the creature directly
            controlledCreature.move(x, y, 0);
            controlledCreature = controlledCreature.updateState();
            mainFrameController.updateThingsInVision();

            var thingsInVision = controlledCreature.getThingsInVision();
            for (Thing thing : thingsInVision) {
                collectThingIfNear(controlledCreature, thing);
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

    private void collectThingIfNear(Creature creature, Thing thing) {
        if (isNearThing(creature, thing)) {
            try {
                if (thing.getAttributes().getCategory() == Constants.categoryDeliverySPOT) {

                    mainFrameController.canDeliverLeaflet(creature);
                } else if (thing.getAttributes().getCategory() == Constants.categoryJEWEL) {
                    creature.putInSack(thing.getAttributes().getName());
                    creature.updateBag(); // Atualiza a bag após coletar o item
                    controlledCreature = creature.updateState();
                    mainFrameController.updateCreatureBag(controlledCreature, thing.getAttributes().getColor());
                }
            } catch (CommandExecException e) {
                System.err.println("Erro ao coletar o item: " + e.getMessage());
            }
        }
    }

    private boolean isNearThing(Creature creature, Thing thing) {
        double distanceThreshold = COLLECTION_DISTANCE_THRESHOLD_JEWEL;
        if (thing.getAttributes().getCategory() == Constants.categoryDeliverySPOT) {
            distanceThreshold = COLLECTION_DISTANCE_THRESHOLD_DELIVERY_SPOT;
        } else if (thing.getAttributes().getCategory() == Constants.categoryJEWEL) {
            distanceThreshold = COLLECTION_DISTANCE_THRESHOLD_JEWEL;
        }
        double distance = creature.calculateDistanceTo(thing);
        return distance < distanceThreshold;
    }
}
