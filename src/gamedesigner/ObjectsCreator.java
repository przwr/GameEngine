/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Main;
import static gamedesigner.SpriteLoader.errMsg;
import gamedesigner.editorgame.EditorGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Wojtek
 */
public class ObjectsCreator extends AbstractFileGetter {

    private BufferedImage img = null;
    private File dir = new File("res");
    private double w, h, wm, hm;
    private int xs, ys;
    private boolean spriteSheet;
    private float roz = 1;
    private final SSDisplayer drawer;

    public ObjectsCreator() {
        drawer = new SSDisplayer();
        drawer.setBackground(Color.white);
        initComponents();
        ImageSP.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                repaint();
            }
        });
        ImageSP.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                repaint();
            }
        });
        drawer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                selectTile(evt);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        ImageSP = new javax.swing.JScrollPane(drawer);
        LoadBt = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Objects Creator");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ImageSP.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        ImageSP.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        ImageSP.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                ImageSPMouseWheelMoved(evt);
            }
        });
        ImageSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ImageSPMouseClicked(evt);
            }
        });

        LoadBt.setText("Load SpriteSheet");
        LoadBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadBtActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Foreground Tile");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ImageSP)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(LoadBt))
                        .addGap(0, 174, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LoadBt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ImageSP, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LoadBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadBtActionPerformed
        PathFinder okno = new PathFinder(this, dir, new FileNameExtensionFilter(
                "Textures (.spr)", "spr"), javax.swing.JFileChooser.FILES_ONLY);
        okno.setVisible(true);
    }//GEN-LAST:event_LoadBtActionPerformed

    private void ImageSPMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_ImageSPMouseWheelMoved
        int ruch = (int) evt.getPreciseWheelRotation();
        roz -= (float) ruch / 4;
        if (roz < 1) {
            roz = 1;
        } else if (roz > 10) {
            roz = 10;
        }
        repaint();
    }//GEN-LAST:event_ImageSPMouseWheelMoved

    private void ImageSPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ImageSPMouseClicked

    }//GEN-LAST:event_ImageSPMouseClicked

    private void selectTile(java.awt.event.MouseEvent evt) {
        double wt = wm / roz;
        double ht = hm / roz;
        xs = (int) ((evt.getX() - 3) / wt);
        ys = (int) ((evt.getY() - 3) / ht);
        repaint();
    }

    @Override
    public void setVisible(boolean set) {
        super.setVisible(set);
        Main.gameStop = !set;
        if (set) {
            Main.game = new EditorGame("Object Maker", Main.settings, Main.controllers);
            Main.game.startGame();
        }
        System.err.println(Main.gameStop);
    }

    public void setImg(File f) {
        try {
            img = ImageIO.read(f);
            w = img.getWidth();
            h = img.getHeight();
        } catch (IOException e) {
            errMsg("Selected file cannot be read!\n" + e.getMessage() + "\n" + f);
        }
        repaint();
    }

    @Override
    public void getFile(FileBox f) {
        dir = f.getDirectory();
        try (BufferedReader wczyt = new BufferedReader(new FileReader(f.getSelectedFile()))) {
            String line = wczyt.readLine();
            String[] t = line.split(";");
            spriteSheet = t[1].equals("1");

            line = wczyt.readLine();
            setImg(new File(line));

            wczyt.readLine();
            wczyt.readLine();

            line = wczyt.readLine();
            t = line.split(";");
            wm = Integer.parseInt(t[0]);
            hm = Integer.parseInt(t[1]);
            wczyt.close();
            xs = 0;
            ys = 0;
        } catch (IOException e) {
            System.err.println("File not found!");
        }
        repaint();
    }

    @Override
    public void repaint() {
        drawer.setPreferredSize(new Dimension((int) (w / roz), (int) (h / roz)));
        drawer.revalidate();
        super.repaint();
    }

    @Override
    public void dispose() {
        Main.gameStop = true;
        super.dispose();
    }

    /*-----------------------------------------------*/
    private class SSDisplayer extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            double wr = w / roz;
            double hr = h / roz;
            double wmr = wm / roz;
            double hmr = hm / roz;
            g.drawImage(img, 3, 3, (int) wr, (int) hr, this);
            if (spriteSheet) {
                g.setColor(new Color(0, 0, 255, 100));
                g.fillRect((int) (xs * wmr + 3), (int) (ys * hmr + 3), (int) wmr, (int) hmr);
                g.setColor(Color.black);
                g.drawRect((int) (xs * wmr + 3), (int) (ys * hmr + 3), (int) wmr, (int) hmr);
            } else {
                g.setColor(new Color(0, 0, 255, 100));
                g.fillRect(3, 3, (int) wr, (int) hr);
                g.setColor(Color.black);
                g.drawRect(3, 3, (int) wr, (int) hr);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ImageSP;
    private javax.swing.JButton LoadBt;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
