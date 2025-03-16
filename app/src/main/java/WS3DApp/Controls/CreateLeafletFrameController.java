package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CreateLeafletFrameController {

    private Map<String, String> creaturesMap = new HashMap<>();
    private MainFrameController mainFrameController;
    private CreateLeafletFrame createLeafletFrame;

    public CreateLeafletFrameController(Map<String, String> creatures, MainFrameController controller) {
        creaturesMap = creatures;
        mainFrameController = controller;
        createLeafletFrame = new CreateLeafletFrame();
        createLeafletFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setupFrame();
    }

    private void setupFrame() {
        for (var item : creaturesMap.keySet()) {
            createLeafletFrame.ComboboxCreatureAssign.addItem(item);
        }
        createLeafletFrame.ComboboxCreatureAssign.setSelectedIndex(0);

        createLeafletFrame.CreateLeaflet.addActionListener((ActionEvent e) -> {
            createLeaflet_ButtonClick();
        });

        createLeafletFrame.setVisible(true);
    }

    private void createLeaflet_ButtonClick() {

        try {
            int payment = Integer.parseInt(createLeafletFrame.Payment.getText());
            int colorRedQuantity = Integer.parseInt(createLeafletFrame.NumberOfRedJewels.getText());
            int colorGreenQuantity = Integer.parseInt(createLeafletFrame.NumberOfGreenJewels.getText());
            int colorBlueQuantity = Integer.parseInt(createLeafletFrame.NumberOfBlueJewels.getText());
            int colorYellowQuantity = Integer.parseInt(createLeafletFrame.NumberOfYellowJewels.getText());
            int colorMagentaQuantity = Integer.parseInt(createLeafletFrame.NumberOfMagentaJewels.getText());
            int colorWhiteQuantity = Integer.parseInt(createLeafletFrame.NumberOfWhiteJewels.getText());

            String creature = createLeafletFrame.ComboboxCreatureAssign.getSelectedItem().toString();
            mainFrameController.assignLeafletToCreature("Red", colorRedQuantity, "Green", colorGreenQuantity, "Blue", colorBlueQuantity,
                    "Yellow", colorYellowQuantity, "Magenta", colorMagentaQuantity, "White", colorWhiteQuantity, payment, creature);
            createLeafletFrame.setVisible(false);
            createLeafletFrame.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível criar o Leaflet.",
                    "Erro ao Criar Leaflet",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
