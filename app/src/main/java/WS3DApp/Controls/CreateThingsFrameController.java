/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author root
 */
public class CreateThingsFrameController {

    private boolean isFruit = false;
    private static MainFrameController mainFrameController;
    private static CreateThingsFrame createThingsFrame;
    private static final List<String> listCrystalColors = List.of("Vermelho", "Verde", "Azul", "Amarelo", "Magenta", "Branco");
    private static final List<String> fruitTypes = List.of("Perecível", "Não Perecível");

    private double WorldLengthX;
    private double WorldLengthY;

    public CreateThingsFrameController(double lengthX, double lengthY, MainFrameController frameController) {
        mainFrameController = frameController;
        WorldLengthX = lengthX;
        WorldLengthY = lengthY;
        setupPage();
    }

    private void setupPage() {

        createThingsFrame = new CreateThingsFrame();

        createThingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        for (String color : listCrystalColors) {
            createThingsFrame.ComboboxTypes.addItem(color);
        }

        AtomicBoolean isUpdating = new AtomicBoolean(false);

        createThingsFrame.ComboboxThings.addItemListener(e -> {
            if (isUpdating.get()) {
                return;
            }

            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = (String) e.getItem();
                isUpdating.set(true);

                if ("Cristal".equals(selectedItem)) {
                    isFruit = false;
                    createThingsFrame.ComboboxTypes.removeAllItems();
                    for (String color : listCrystalColors) {
                        createThingsFrame.ComboboxTypes.addItem(color);
                    }
                } else {
                    isFruit = true;
                    createThingsFrame.ComboboxTypes.removeAllItems();
                    for (String fruit : fruitTypes) {
                        createThingsFrame.ComboboxTypes.addItem(fruit);
                    }
                }

                isUpdating.set(false);
            }
        });

        createThingsFrame.CreateThingButton.addActionListener((ActionEvent e) -> {
            double coordinateX = Double.parseDouble(createThingsFrame.CoordinateX.getText());
            double coordinateY = Double.parseDouble(createThingsFrame.CoordinateY.getText());

            if (coordinateX < 0 || coordinateX > WorldLengthX || coordinateY < 0 || coordinateY > WorldLengthY) {
                JOptionPane.showMessageDialog(null,
                        "Coordenadas inválidas.",
                        "Erro ao Criar Criatura",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int type = createThingsFrame.ComboboxTypes.getSelectedIndex();

            if (isFruit) {
                mainFrameController.createFood(type, coordinateX, coordinateY);
            } else {
                mainFrameController.createJewel(type, coordinateX, coordinateY);
            }
            createThingsFrame.setVisible(false);
            createThingsFrame.dispose();
        });

        createThingsFrame.setVisible(true);
    }
}
