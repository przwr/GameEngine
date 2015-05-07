/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Methods;
import static engine.Methods.roundDouble;
import engine.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 *
 * @author WROBELP1
 */
public class Window extends javax.swing.JFrame {

    /**
     * Creates new form Window
     */
    public Window() {
        initComponents();
    }

    public void addVariables(NavigationMesh mesh, Point start, Point end, Point[] path) {
        this.mesh = mesh;
        this.start = start;
        this.end = end;
        this.destination = path;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        canvas = new MyCanvas();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setFocusTraversalPolicyProvider(true);

        canvas.setBackground(new java.awt.Color(153, 153, 255));
        canvas.setPreferredSize(new java.awt.Dimension(1024, 640));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 791, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 579, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		//</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window().setVisible(true);
            }
        });
    }

    NavigationMesh mesh;
    Point start, end;
    Point[] destination;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas;
    // End of variables declaration//GEN-END:variables
    class MyCanvas extends java.awt.Canvas {

        MyCanvas() {
            super();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2;
            g2 = (Graphics2D) g;
            double SCALE = 0.5;
            Polygon poly = new Polygon();
            for (Triangle triangle : mesh.getTriangles()) {
                for (int j = 0; j < 3; j++) {
                    Node nd = triangle.getNode(j);
                    poly.addPoint(roundDouble(nd.getX() * SCALE), roundDouble(nd.getY() * SCALE));
                }
                g2.setColor(Color.lightGray);
                g2.fill(poly);
                g2.setColor(Color.gray);
                g2.draw(poly);
                poly.reset();
            }
            g2.setColor(Color.BLACK);
            for (Bound line : mesh.bounds) {
                g2.drawLine(roundDouble(line.getStart().getX() * SCALE), roundDouble(line.getStart().getY() * SCALE), roundDouble(line.getEnd().getX() * SCALE), roundDouble(line.getEnd().getY() * SCALE));
            }
            g2.setColor(Color.yellow);
            if (destination != null) {
                for (int i = 0; i < destination.length - 1; i++) {
                    g2.drawLine(roundDouble(destination[i].getX() * SCALE), roundDouble(destination[i].getY() * SCALE), roundDouble(destination[i + 1].getX() * SCALE), roundDouble(destination[i + 1].getY() * SCALE));

                }
            }
            if (start != null) {
                g2.setColor(Color.green);
                g2.fillOval(roundDouble(start.getX() * SCALE - 3), roundDouble(start.getY() * SCALE - 3), 6, 6);
            }
            if (end != null) {
                g2.setColor(Color.red);
                g2.fillOval(roundDouble(end.getX() * SCALE - 3), roundDouble(end.getY() * SCALE - 3), 6, 6);
            }
        }
    }
}
