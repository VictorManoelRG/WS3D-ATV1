package WS3DApp.Controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;

public class ControlCreatureByKeyboardFrame extends JFrame implements KeyListener {

    private Creature controlledCreature;
    private boolean isStarted = false;
    private static final double STOPPED_THRESHOLD = 0.1;

    public ControlCreatureByKeyboardFrame(Creature creature) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(null);
        this.addKeyListener(this);
        controlledCreature = creature;
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
            System.out.println("controlando" + controlledCreature.getName());

            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    controlledCreature.move(1, 1, 0);

                    break;
                case KeyEvent.VK_DOWN:
                    controlledCreature.move(-1, -1, 0);
                    break;
                case KeyEvent.VK_LEFT:
                    controlledCreature.move(2, -2, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    controlledCreature.move(-2, 2, 0);
                    break;
                default:
                    break;
            }
            controlledCreature = controlledCreature.updateState();
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

        try {

            controlledCreature.move(0.01, 0.01, 0);
            controlledCreature.stop();
            controlledCreature = controlledCreature.updateState();

            isStarted = false;

        } catch (CommandExecException ex) {
            Logger.getLogger(ControlCreatureByKeyboardFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
