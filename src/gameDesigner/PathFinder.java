/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameDesigner;

import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Wojtek
 */
public class PathFinder extends javax.swing.JFrame {

    private final FileGetter cel;
    private final String ext;

    public PathFinder(FileGetter cel, File dir, FileNameExtensionFilter filter, int mode) {   // mode : 0 - wczytanie obrazka, 1 - "wyjście" zapisywanych plików, 2 - wczytanie zapisanej tekstury
        initComponents();
        this.cel = cel;
        FilesFC.setCurrentDirectory(dir);
        cel.setEnabled(false);
        FilesFC.setFileSelectionMode(mode);
        if (mode == javax.swing.JFileChooser.DIRECTORIES_ONLY) {
            ext = "dir";
        } else {
            FilesFC.setFileFilter(filter);
            ext = filter.getExtensions()[0];
        }
    }

    @Override
    public void dispose() {
        cel.setEnabled(true);
        super.dispose();
    }

    public javax.swing.JFileChooser getFC() {
        return FilesFC;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FilesFC = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Path Finder");
        setResizable(false);

        FilesFC.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        FilesFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilesFCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(FilesFC, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(FilesFC, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FilesFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilesFCActionPerformed
        if (evt.getActionCommand().equals("ApproveSelection")) {
            cel.getFile(new FileBox(ext, FilesFC.getSelectedFile(), FilesFC.getCurrentDirectory()));
            this.dispose();
        }
        if (evt.getActionCommand().equals("CancelSelection")) {
            this.dispose();
        }
    }//GEN-LAST:event_FilesFCActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser FilesFC;
    // End of variables declaration//GEN-END:variables
}
