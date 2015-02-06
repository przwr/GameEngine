/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.GUI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Wojtek
 */
public class NamingScreen extends javax.swing.JFrame {

    private ArrayList<String> content;
    
    public NamingScreen(ArrayList<String> content) {
        initComponents();
        this.content = content;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        NameTF = new javax.swing.JTextField();
        OKB = new javax.swing.JButton();
        CancelB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

    public static void errMsg(String blad) {
        JOptionPane.showMessageDialog(null, blad, "ERROR!", JOptionPane.ERROR_MESSAGE);
    }

    public static void infoMsg(String info) {
        JOptionPane.showMessageDialog(null, info, "", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean questMsg(String q) {
        return JOptionPane.showConfirmDialog(null, q, "", JOptionPane.YES_NO_OPTION) == 0;
    }
    
    private void OKBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKBActionPerformed
        String name = NameTF.getText();
        if (name.equals("")) {
            return;
        }
        try (BufferedReader wczyt = new BufferedReader(new FileReader("res/objects/" + name + ".o"))) {
            if (!questMsg("File \"" + name + "\" already exists!\nReplace?")) {
                return;
            }
            wczyt.close();
        } catch (IOException e) {
        }

        try (PrintWriter save = new PrintWriter("res/objects/" + name + ".o")) {
            for (String line : content)
                save.println(line);
            infoMsg("Object \"" + name + ".obj\" was saved.");
            save.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
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
