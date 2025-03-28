/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package WS3DApp.Controls;

/**
 *
 * @author root
 */
public class CreateCreatureFrame extends javax.swing.JFrame {

    /**
     * Creates new form CreateCreatureFrame
     */
    public CreateCreatureFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LabelCreateCoordinates = new javax.swing.JLabel();
        CreateCreatureButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        CoordinateX = new javax.swing.JTextField();
        CoordinateY = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(null);
        setResizable(false);

        LabelCreateCoordinates.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelCreateCoordinates.setText("Insira as coordenadas:");
        LabelCreateCoordinates.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        CreateCreatureButton.setText("Criar Criatura");
        CreateCreatureButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CreateCreatureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateCreatureButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Coordenada X");

        jLabel2.setText("Coordenada Y");

        CoordinateX.setColumns(5);
        CoordinateX.setText("100");
        CoordinateX.setToolTipText("");
        CoordinateX.setMaximumSize(new java.awt.Dimension(82, 23));
        CoordinateX.setMinimumSize(new java.awt.Dimension(75, 23));
        CoordinateX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CoordinateXActionPerformed(evt);
            }
        });

        CoordinateY.setColumns(5);
        CoordinateY.setText("100");
        CoordinateY.setToolTipText("");
        CoordinateY.setMaximumSize(new java.awt.Dimension(82, 23));
        CoordinateY.setMinimumSize(new java.awt.Dimension(75, 23));
        CoordinateY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CoordinateYActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(LabelCreateCoordinates)
                    .addComponent(CreateCreatureButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(48, 48, 48)
                            .addComponent(CoordinateY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(48, 48, 48)
                            .addComponent(CoordinateX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(LabelCreateCoordinates)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CoordinateX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CoordinateY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(CreateCreatureButton)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void CoordinateXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CoordinateXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CoordinateXActionPerformed

    private void CoordinateYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CoordinateYActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CoordinateYActionPerformed

    private void CreateCreatureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateCreatureButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateCreatureButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CreateCreatureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateCreatureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateCreatureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateCreatureFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateCreatureFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField CoordinateX;
    public javax.swing.JTextField CoordinateY;
    public javax.swing.JButton CreateCreatureButton;
    public javax.swing.JLabel LabelCreateCoordinates;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
