package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CreateBricksFrameController {

    private static MainFrameController mainFrameController;
    private static CreateBricksFrame createBricksFrame;

    private double WorldLengthX;
    private double WorldLengthY;

    public CreateBricksFrameController(double lengthX, double lengthY, MainFrameController frameController) {
        mainFrameController = frameController;
        WorldLengthX = lengthX;
        WorldLengthY = lengthY;
        setupFrame();
    }

    private void setupFrame() {
        createBricksFrame = new CreateBricksFrame();

        createBricksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createBricksFrame.CreateBrickButton.addActionListener((ActionEvent e) -> {
            CreateBrickButton_ButtonClick();
        });

        createBricksFrame.setVisible(true);
    }

    private void CreateBrickButton_ButtonClick() {
        int type = createBricksFrame.ComboboxBrickTypes.getSelectedIndex();

        double x1 = Double.parseDouble(createBricksFrame.CoordinateX1.getText());
        double y1 = Double.parseDouble(createBricksFrame.CoordinateY1.getText());
        double x2 = Double.parseDouble(createBricksFrame.CoordinateX2.getText());
        double y2 = Double.parseDouble(createBricksFrame.CoordinateY2.getText());

        if (x1 < 0 || x1 > WorldLengthX || y1 < 0 || y1 > WorldLengthY
                || x2 < 0 || x2 > WorldLengthX || y2 < 0 || y2 > WorldLengthY) {
            JOptionPane.showMessageDialog(null,
                    "Coordenadas inválidas.",
                    "Erro ao Criar Criatura",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrameController.createBrick(type, x1, y1, x2, y2);
        createBricksFrame.setVisible(false);
        createBricksFrame.dispose();
    }

}
