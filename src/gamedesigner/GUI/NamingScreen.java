/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.GUI;

import gamedesigner.ObjectPlace;
import javax.swing.JOptionPane;

/**
 *
 * @author Wojtek
 */
public class NamingScreen extends javax.swing.JFrame {
    private final ObjectPlace objPlace;
    
    public NamingScreen(ObjectPlace objPlace) {
        initComponents();
        this.objPlace = objPlace;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        NameTF = new javax.swing.JTextField();
        OKB = new javax.swing.JButton();
        CancelB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SAVE AS");

        jLabel1.setText("Name:");

        OKB.setText("Ok");
        OKB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKBActionPerformed(evt);
            }
        });

        CancelB.setText("Cancel");
        CancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NameTF))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(OKB, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelB)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(NameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKB)
                    .addComponent(CancelB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void errMsg(String blad) {
        JOptionPane.showMessageDialog(this, blad, "ERROR!", JOptionPane.ERROR_MESSAGE);
    }

    public void infoMsg(String info) {
        JOptionPane.showMessageDialog(this, info, "", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean questMsg(String q) {
        return JOptionPane.showConfirmDialog(this, q, "", JOptionPane.YES_NO_OPTION) == 0;
    }
    
    private void OKBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKBActionPerformed
        String name = NameTF.getText();
        if (name.equals("") || !objPlace.saveObject(name, this, true)) {
            return;
        }
        dispose();
    }//GEN-LAST:event_OKBActionPerformed

    private void CancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelBActionPerformed
        dispose();
    }//GEN-LAST:event_CancelBActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelB;
    private javax.swing.JTextField NameTF;
    private javax.swing.JButton OKB;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
