package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;

public class ControlCreatureByKeyboardFrame extends JFrame implements KeyListener {

    private Creature controlledCreature;
    private boolean isStarted = false;
    private static final double STOPPED_THRESHOLD = 0.1;
    private JList<String> ListObservableThings;
    private MainFrameController mainFrameController;

    public ControlCreatureByKeyboardFrame(Creature creature, MainFrameController controller, JList<String> list) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(null);
        this.addKeyListener(this);
        controlledCreature = creature;
        ListObservableThings = list;
        mainFrameController = controller;
        this.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN
                && e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT) {
            return;
        }

        try {
            if (!isStarted) {
                controlledCreature.start();
                isStarted = true;
            }

            if (controlledCreature.getWheel() > STOPPED_THRESHOLD) {
                return;
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
        } catch (CommandExecException ex) {
            Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN
                && e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT) {
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controlledCreature.move(0.01, 0.01, 0);
                controlledCreature.stop();
                controlledCreature = controlledCreature.updateState();
                return null;
            }

            @Override
            protected void done() {
                isStarted = false;
            }
        }.execute();
    }

    private void moveCreature(double x, double y) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controlledCreature.move(x, y, 0);
                Thread.sleep(100);
                controlledCreature = controlledCreature.updateState();
                return null;
            }

            @Override
            protected void done() {
                mainFrameController.updateThingsInVision();
            }
        }.execute();
    }
}
