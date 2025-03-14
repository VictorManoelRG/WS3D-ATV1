/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WS3DApp.Controls;

import WS3DApp.MainFrameController;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author root
 */
public class CreateCreatureFrameController {

    private static CreateCreatureFrame createCreatureFrame;

    private MainFrameController mainFrameController;

    public CreateCreatureFrameController(double lengthX, double lengthY, MainFrameController frameController) {
        mainFrameController = frameController;
        setupFrame(lengthX, lengthY);
    }

    private void setupFrame(double lengthX, double lengthY) {

        String text = "Coordenadas: X = " + lengthX + ", Y = " + lengthY;
        createCreatureFrame = new CreateCreatureFrame();

        createCreatureFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createCreatureFrame.LabelCreateCoordinates.setText(text);
        createCreatureFrame.LabelCreateCoordinates.setHorizontalAlignment(SwingConstants.CENTER);
        createCreatureFrame.LabelCreateCoordinates.setHorizontalTextPosition(SwingConstants.CENTER);


        createCreatureFrame.CreateCreatureButton.addActionListener((ActionEvent e) -> {
            createNewCreature_ButtonClick(lengthX, lengthY);
        });
        
        createCreatureFrame.CoordinateX.setText(String.valueOf(lengthX));
        createCreatureFrame.CoordinateY.setText(String.valueOf(lengthY));

        createCreatureFrame.setVisible(true);
    }

    private void createNewCreature_ButtonClick(double lengthX, double lengthY) {
        double coordinateX = Double.parseDouble(createCreatureFrame.CoordinateX.getText());
        double coordinateY = Double.parseDouble(createCreatureFrame.CoordinateY.getText());

        if (coordinateX < 0 || coordinateX > lengthX || coordinateY < 0 || coordinateY > lengthY) {
            JOptionPane.showMessageDialog(null,
                    "Coordenadas inválidas.",
                    "Erro ao Criar Criatura",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrameController.createNewCreature(coordinateX, coordinateY);

        createCreatureFrame.setVisible(false);
        createCreatureFrame.dispose();
    }
}
