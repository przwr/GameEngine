/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameDesigner;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Wojtek
 */
public class SpriteLoader extends javax.swing.JFrame {

    private BufferedImage img = null;
    private File file = null;
    private File dir = null;
    private File output = null;

    private int x, y, w, h; //Polozenie i rozmiar
    private int xc, yc; //Punkt centralny
    private int xm, ym; //Rozmiar kafelka
    private int xPocz, yPocz, xPop, yPop, roz; //Przemieszczenie i skalowanie

    private boolean grid, black, sprSh;

    public SpriteLoader() {
        initComponents();
        x = 5;
        y = 5;
        w = 0;
        h = 0;
        xm = 16;
        ym = 16;
        xPocz = 0;
        yPocz = 0;
        xPop = 0;
        yPop = 0;
        roz = 1;
        grid = true;
        black = true;
        sprSh = false;
        try (BufferedReader wczyt = new BufferedReader(new FileReader("SpLoadPref.txt"))) {
            String linia = wczyt.readLine();
            if (linia.equals("0")) {
                black = false;
                BlackCB.setSelected(false);
            }
            linia = wczyt.readLine();
            if (!linia.equals("err")) {
                dir = new File(linia);
            }
            linia = wczyt.readLine();
            if (!linia.equals("err")) {
                output = new File(linia);
                PathTF.setText(output.getPath() + "\\...");
            }
        } catch (IOException e) {
            System.err.println("File not found!");
        }
        repaint();
    }

    @Override
    public void dispose() {
        try (PrintWriter save = new PrintWriter("SpLoadPref.txt")) {
            save.println(black ? 1 : 0);
            save.println(dir != null ? dir.getPath() : "err");
            save.println(output != null ? output.getPath() : "err");
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        super.dispose();
    }

    public static void errMsg(String blad) {
        JOptionPane.showMessageDialog(null, blad, "ERROR!", JOptionPane.ERROR_MESSAGE);
    }

    public static void infoMsg(String info) {
        JOptionPane.showMessageDialog(null, info, "", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean questMsg(String q) {
        return JOptionPane.showConfirmDialog(null, q, "", JOptionPane.YES_NO_OPTION) == 0;
    }

    public void setOutput(File dir) {
        output = dir;
        PathTF.setText(output.getPath() + "\\...");
        repaint();
    }

    public void ostDir(File dir) {
        this.dir = dir;
    }

    public void setImg(File f) {
        try {
            img = ImageIO.read(f);

            String[] tmp = f.getName().split("\\.");
            NameTF.setText(tmp[0]);
            this.file = f;
            x = 5;
            y = 5;
            w = img.getWidth();
            h = img.getHeight();
            WymiaryL.setText("Dimensions: " + w + " x " + h);
        } catch (IOException e) {
            errMsg("Selected file cannot be read!");
        }
        repaint();
    }

    public void openTex(File f) {
        if (questMsg("Are you sure you want to open \"" + f.getName() + "\"?")) {
            try (BufferedReader wczyt = new BufferedReader(new FileReader(f))) {
                String line = wczyt.readLine();
                String[] t = line.split(";");
                if (t[1].equals("1")) {
                    SpriteSheetCB.setSelected(true);
                    SSPanelP.setEnabled(false);
                    sprSh = true;
                } else {
                    SpriteSheetCB.setSelected(false);
                    SSPanelP.setEnabled(true);
                    sprSh = false;
                }

                line = wczyt.readLine();
                setImg(new File(line));

                line = wczyt.readLine();
                System.err.println(line);

                line = wczyt.readLine();
                t = line.split(";");
                XCSp.setValue(Integer.parseInt(t[0]));
                YCSp.setValue(Integer.parseInt(t[1]));

                line = wczyt.readLine();
                t = line.split(";");
                int ssw = Integer.parseInt(t[0]);
                int ssh = Integer.parseInt(t[1]);
                xm = ssw;
                ym = ssh;
                SSWidthSp.setValue(ssw);
                SSHeightSp.setValue(ssh);
            } catch (IOException e) {
                System.err.println("File not found!");
            }
        }
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        FileP = new SpriteDisplayer();
        LoadBt = new javax.swing.JButton();
        SaveBt = new javax.swing.JButton();
        ExitBt = new javax.swing.JButton();
        WymiaryL = new javax.swing.JLabel();
        SSPanelP = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        GridCB = new javax.swing.JCheckBox();
        SSWidthSp = new javax.swing.JSpinner();
        SSHeightSp = new javax.swing.JSpinner();
        SpriteSheetCB = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        CentralizeBt = new javax.swing.JButton();
        XCSp = new javax.swing.JSpinner();
        YCSp = new javax.swing.JSpinner();
        OutputBt = new javax.swing.JButton();
        PathTF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        NameTF = new javax.swing.JTextField();
        BlackCB = new javax.swing.JCheckBox();
        EditBt = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        FileP.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        FileP.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                FilePMouseDragged(evt);
            }
        });
        FileP.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                FilePMouseWheelMoved(evt);
            }
        });
        FileP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FilePMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                FilePMousePressed(evt);
            }
        });

        javax.swing.GroupLayout FilePLayout = new javax.swing.GroupLayout(FileP);
        FileP.setLayout(FilePLayout);
        FilePLayout.setHorizontalGroup(
            FilePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        FilePLayout.setVerticalGroup(
            FilePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        LoadBt.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        LoadBt.setText("Load");
        LoadBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadBtActionPerformed(evt);
            }
        });

        SaveBt.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        SaveBt.setText("Save");
        SaveBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveBtActionPerformed(evt);
            }
        });

        ExitBt.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        ExitBt.setText("Exit");
        ExitBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitBtActionPerformed(evt);
            }
        });

        WymiaryL.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        WymiaryL.setText("Dimensions:");

        SSPanelP.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        SSPanelP.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SSPanelPPropertyChange(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        jLabel2.setText("Width:");

        jLabel3.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        jLabel3.setText("Height:");

        GridCB.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        GridCB.setSelected(true);
        GridCB.setText("Grid");
        GridCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GridCBActionPerformed(evt);
            }
        });

        SSWidthSp.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(16), Integer.valueOf(0), null, Integer.valueOf(1)));
        SSWidthSp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SSWidthSpStateChanged(evt);
            }
        });

        SSHeightSp.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(16), Integer.valueOf(0), null, Integer.valueOf(1)));
        SSHeightSp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SSHeightSpStateChanged(evt);
            }
        });

        javax.swing.GroupLayout SSPanelPLayout = new javax.swing.GroupLayout(SSPanelP);
        SSPanelP.setLayout(SSPanelPLayout);
        SSPanelPLayout.setHorizontalGroup(
            SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SSPanelPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SSPanelPLayout.createSequentialGroup()
                        .addComponent(GridCB)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(SSPanelPLayout.createSequentialGroup()
                        .addGroup(SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(4, 4, 4)
                        .addGroup(SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SSWidthSp)
                            .addComponent(SSHeightSp, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))))
                .addContainerGap())
        );
        SSPanelPLayout.setVerticalGroup(
            SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SSPanelPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(SSWidthSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SSPanelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(SSHeightSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GridCB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SpriteSheetCB.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        SpriteSheetCB.setText("SpriteSheet");
        SpriteSheetCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SpriteSheetCBActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        jLabel5.setText("Central X:");

        jLabel6.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        jLabel6.setText("Central Y:");

        CentralizeBt.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        CentralizeBt.setText("Centralization");
        CentralizeBt.setToolTipText("");
        CentralizeBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CentralizeBtActionPerformed(evt);
            }
        });

        XCSp.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        XCSp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                XCSpStateChanged(evt);
            }
        });

        YCSp.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        YCSp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                YCSpStateChanged(evt);
            }
        });

        OutputBt.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        OutputBt.setText("Output");
        OutputBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OutputBtActionPerformed(evt);
            }
        });

        PathTF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        PathTF.setFocusable(false);

        jLabel4.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        jLabel4.setText("Name:");

        NameTF.setFont(new java.awt.Font("GulimChe", 0, 18)); // NOI18N

        BlackCB.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        BlackCB.setSelected(true);
        BlackCB.setText("Black Lines");
        BlackCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BlackCBActionPerformed(evt);
            }
        });

        EditBt.setFont(new java.awt.Font("GulimChe", 0, 13)); // NOI18N
        EditBt.setText("Edit");
        EditBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditBtActionPerformed(evt);
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
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NameTF))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpriteSheetCB)
                                    .addComponent(BlackCB))
                                .addGap(101, 101, 101))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ExitBt, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(OutputBt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(PathTF))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LoadBt, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SaveBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EditBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(WymiaryL)
                        .addGap(0, 383, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(XCSp, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(YCSp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(CentralizeBt, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SSPanelP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(FileP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(WymiaryL)
                    .addComponent(LoadBt)
                    .addComponent(SaveBt)
                    .addComponent(EditBt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(NameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(XCSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(YCSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(CentralizeBt)
                        .addGap(9, 9, 9)
                        .addComponent(SpriteSheetCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SSPanelP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BlackCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FileP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(OutputBt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ExitBt)
                        .addComponent(PathTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LoadBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadBtActionPerformed
        PathFinder okno = new PathFinder(this, dir, 0);
        okno.setVisible(true);
    }//GEN-LAST:event_LoadBtActionPerformed

    private void ExitBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitBtActionPerformed
        dispose();
    }//GEN-LAST:event_ExitBtActionPerformed

    private void FilePMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FilePMouseDragged
        x = xPop + evt.getX() - xPocz;
        y = yPop + evt.getY() - yPocz;
        repaint();
    }//GEN-LAST:event_FilePMouseDragged

    private void FilePMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FilePMousePressed
        xPocz = evt.getX();
        yPocz = evt.getY();
        xPop = x;
        yPop = y;
    }//GEN-LAST:event_FilePMousePressed

    private void FilePMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_FilePMouseWheelMoved
        int ruch = (int) evt.getPreciseWheelRotation();
        roz -= ruch;
        if (roz < 1) {
            roz = 1;
        } else {
            if (x < 0) {
                x += ruch * w / 2;
            }
            if (y < 0) {
                y *= 1 + 1 / (roz + ruch);
            }
        }
        repaint();
    }//GEN-LAST:event_FilePMouseWheelMoved

    private void FilePMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FilePMouseClicked
        if (evt.getClickCount() == 2) {
            x = 5;
            y = 5;
            roz = 1;
            repaint();
        }
    }//GEN-LAST:event_FilePMouseClicked

    private void GridCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GridCBActionPerformed
        grid = !grid;
        repaint();
    }//GEN-LAST:event_GridCBActionPerformed

    private void BlackCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BlackCBActionPerformed
        black = !black;
        repaint();
    }//GEN-LAST:event_BlackCBActionPerformed

    private void SaveBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveBtActionPerformed
        if (output == null) {
            errMsg("The Output is not defined!");
            return;
        }
        String p = output.getPath() + "\\" + NameTF.getText();
        try (BufferedReader wczyt = new BufferedReader(new FileReader(p + ".spr"))) {
            if (!questMsg("Texture \"" + NameTF.getText() + "\" already exists in the selected folder!\n"
                    + "Replace?")) {
                return;
            }
        } catch (IOException e) {
        }

        try (PrintWriter save = new PrintWriter(p + ".spr")) {
            Files.copy(file.toPath(), Paths.get(p + ".png"), REPLACE_EXISTING);
            save.println(NameTF.getText() + ";" + (SpriteSheetCB.isSelected() ? 1 : 0));
            save.println(p + ".png");
            save.println(w + ";" + h);
            save.println(xc + ";" + yc);
            save.println(xm + ";" + ym);
            infoMsg("Texture \"" + NameTF.getText() + "\" was saved.");
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            infoMsg("I CAN'T LET YOU DO THAT, STARFOX!");
        }
    }//GEN-LAST:event_SaveBtActionPerformed

    private void SpriteSheetCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SpriteSheetCBActionPerformed
        SSPanelP.setEnabled(!SSPanelP.isEnabled());
        sprSh = !sprSh;
        repaint();
    }//GEN-LAST:event_SpriteSheetCBActionPerformed

    private void SSPanelPPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SSPanelPPropertyChange
        for (Component com : SSPanelP.getComponents()) {
            com.setEnabled(!com.isEnabled());
        }
    }//GEN-LAST:event_SSPanelPPropertyChange

    private void SSWidthSpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SSWidthSpStateChanged
        int tmp = (int) SSWidthSp.getValue();
        if (xm - tmp > 0) {
            if (xm > 2) {
                xm /= 2;
            }
        } else if (xm - tmp < 0) {
            if (xm < 512) {
                xm *= 2;
            }
        }
        SSWidthSp.setValue(xm);
        repaint();
    }//GEN-LAST:event_SSWidthSpStateChanged

    private void SSHeightSpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SSHeightSpStateChanged
        int tmp = (int) SSHeightSp.getValue();
        if (ym - tmp > 0) {
            if (ym > 2) {
                ym /= 2;
            }
        } else if (ym - tmp < 0) {
            if (ym < 512) {
                ym *= 2;
            }
        }
        SSHeightSp.setValue(ym);
        repaint();
    }//GEN-LAST:event_SSHeightSpStateChanged

    private void XCSpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_XCSpStateChanged
        xc = (int) XCSp.getValue();
        repaint();
    }//GEN-LAST:event_XCSpStateChanged

    private void YCSpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_YCSpStateChanged
        yc = (int) YCSp.getValue();
        repaint();
    }//GEN-LAST:event_YCSpStateChanged

    private void CentralizeBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CentralizeBtActionPerformed
        XCSp.setValue(w / 2);
        YCSp.setValue(h / 2);
    }//GEN-LAST:event_CentralizeBtActionPerformed

    private void OutputBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OutputBtActionPerformed
        PathFinder okno = new PathFinder(this, output, 1);
        okno.setVisible(true);
    }//GEN-LAST:event_OutputBtActionPerformed

    private void EditBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditBtActionPerformed
        PathFinder okno = new PathFinder(this, output, 2);
        okno.setVisible(true);
    }//GEN-LAST:event_EditBtActionPerformed

    /*-----------------------------------------------*/
    class SpriteDisplayer extends javax.swing.JPanel {

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, x + 3, y + 3, w * roz, h * roz, this);
            if (black) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);
            }
            //g.drawRect(x + 2, y + 2, w * roz + 1, h * roz + 1);
            int xz = 0;
            int yz;
            if (grid && sprSh) {
                while (xz < w * roz) {
                    yz = 0;
                    while (yz < h * roz) {
                        g.drawRect(x + xz + 3, y + yz + 3, xm * roz, ym * roz);
                        yz += ym * roz;
                    }
                    xz += xm * roz;
                }
            }
            if (xc != 0 || yc != 0) {
                if (black) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.green);
                }
                g.drawLine(x + xc * roz + 3, y + 3, x + xc * roz + 3, y + h * roz + 3);
                g.drawLine(x + 3, y + yc * roz + 3, x + w * roz + 3, y + yc * roz + 3);
                g.fillRect(x + xc * roz + 3, y + yc * roz + 3, roz, roz);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BlackCB;
    private javax.swing.JButton CentralizeBt;
    private javax.swing.JButton EditBt;
    private javax.swing.JButton ExitBt;
    private javax.swing.JPanel FileP;
    private javax.swing.JCheckBox GridCB;
    private javax.swing.JButton LoadBt;
    private javax.swing.JTextField NameTF;
    private javax.swing.JButton OutputBt;
    private javax.swing.JTextField PathTF;
    private javax.swing.JSpinner SSHeightSp;
    private javax.swing.JPanel SSPanelP;
    private javax.swing.JSpinner SSWidthSp;
    private javax.swing.JButton SaveBt;
    private javax.swing.JCheckBox SpriteSheetCB;
    private javax.swing.JLabel WymiaryL;
    private javax.swing.JSpinner XCSp;
    private javax.swing.JSpinner YCSp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
