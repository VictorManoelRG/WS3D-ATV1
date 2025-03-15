/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author root
 */
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
            int quantity = Integer.parseInt(createLeafletFrame.NumberOfJewels.getText());
            int payment = Integer.parseInt(createLeafletFrame.Payment.getText());
            String color = createLeafletFrame.ComboboxJewelColor.getSelectedItem().toString();
            String creature = createLeafletFrame.ComboboxCreatureAssign.getSelectedItem().toString();
            mainFrameController.assignLeafletToCreature(color, quantity, payment, creature);
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
