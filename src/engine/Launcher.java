/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import static engine.Main.cleanUp;

/**
 *
 * @author przemek
 */
public class Launcher {

    public static void main(String[] args) {
        try {
            Main.run();
        } catch (Exception ex) {
            Methods.Exception(ex);
            cleanUp();
            System.exit(0);
        }
        cleanUp();
        if (!Main.gameStop) {
            System.exit(0);
        }
    }
}