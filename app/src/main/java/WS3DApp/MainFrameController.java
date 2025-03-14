package WS3DApp;

import WS3DApp.Controls.CreateCreatureFrameController;
import java.util.ArrayList;
import java.util.List;
import ws3dproxy.model.Creature;
import java.awt.event.*;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.World;
import javax.swing.*;

public class MainFrameController {

    private WS3DProxy proxy = new WS3DProxy();

    public Creature c;
    public World w;
    public int width;
    public int height;

    private List<Creature> listCreatures = new ArrayList<>();
    private MainFrame mainFrame;

    public MainFrameController() {
        //createWorld();
        mainFrame = new MainFrame();
        setupFrame();
    }

    private void setupFrame() {
        if (listCreatures.isEmpty()) {
            mainFrame.LabelIsCreaturesExists.setVisible(true);
        } else {
            mainFrame.LabelIsCreaturesExists.setVisible(false);
        }

        mainFrame.CreateCreatureButton.addActionListener((ActionEvent e) -> {
            createNewCreature_ButtonClick();
        });
        
        mainFrame.setVisible(true);
    }

    private void createNewCreature_ButtonClick() {
        CreateCreatureFrameController controller = new CreateCreatureFrameController(200, 200, this);
    }
    
    public void createNewCreature(double coordinateX, double coordinateY){
        try {
            Creature c = proxy.createCreature(coordinateX, coordinateY, 0);
            listCreatures.add(c);
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
            width = w.getEnvironmentWidth();
            height = w.getEnvironmentHeight();

            w.reset();
            World.createFood(0, 350, 75);
            World.createFood(0, 100, 220);
            World.createFood(1, 250, 210);
            World.createDeliverySpot(250, 250);
            World.createJewel(0, 10, 50);
            World.createJewel(1, 100, 500);
            World.createBrick(3, 500, 200, 505, 300);
            c = proxy.createCreature(100, 450, 0);
            c.start();

        } catch (Exception e) {
            System.out.println("Erro capaturado");
        }
    }
}
