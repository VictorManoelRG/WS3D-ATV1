package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CreateThingsFrameController {

    private boolean isFruit = false;
    private static MainFrameController mainFrameController;
    private static CreateThingsFrame createThingsFrame;
    private static final List<String> listCrystalColors = List.of("Vermelho", "Verde", "Azul", "Amarelo", "Magenta", "Branco");

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
                    createThingsFrame.ComboboxTypes.setVisible(true);
                    createThingsFrame.ItemTypeLabel.setVisible(true);
                    createThingsFrame.ComboboxTypes.removeAllItems();
                    for (String color : listCrystalColors) {
                        createThingsFrame.ComboboxTypes.addItem(color);
                    }
                } else {
                    isFruit = true;
                    createThingsFrame.ComboboxTypes.setVisible(false);
                    createThingsFrame.ItemTypeLabel.setVisible(false);
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
                int fruitType;
                if (createThingsFrame.ComboboxThings.getSelectedItem().toString().equals("Maça")) {
                    fruitType = 0;
                } else {
                    fruitType = 1;
                }
                mainFrameController.createFood(fruitType, coordinateX, coordinateY);
            } else {
                mainFrameController.createJewel(type, coordinateX, coordinateY);
            }
            createThingsFrame.setVisible(false);
            createThingsFrame.dispose();
        });

        createThingsFrame.setVisible(true);
    }
}
