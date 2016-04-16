/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.utilities.ErrorHandler;

import static engine.Main.cleanUp;

/**
 * @author przemek
 */
public class Launcher {

    public static boolean restart = true;

    public static void main(String[] args) {
        try {
            while (restart) {
                restart = false;
                Main.run();
                System.gc();
            }
        } catch (Exception exception) {
            ErrorHandler.javaException(exception);
            cleanUp();
        }
        System.exit(0);
    }
}
