/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.GUI;

/**
 *
 * @author Wojtek
 */
public class Help extends javax.swing.JFrame {

    public Help() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("H : \t\tHelp\nM:\t\tZmiana trybu\nS:\t\tZapisz jako\ncltr + S:\t\tZapisz\nL:\t\tŁaduj objekt\n\nTAB:\t\tZniknięcie tła\ncltr : \t\tPrzeciąganie\ncltr + Z : \t\tZeruj przeciągnięcie\nSPACE: \t\tStwórz\nDELETE:\t\tUsuń\nHOME:\t\tUstaw punkt poczatkowy\n\n//TRYB TILE'OWY\n\nSHIFT + strzałki : \tZmiana tile'a\nT : \t\tŁaduj spritesheeta\n\n//TRYB BLOKOWY\n\nSHIFT + strzałki:\tZmiana wysokości\n");
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
