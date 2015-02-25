/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import static engine.Main.cleanUp;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author przemek
 */
public class Launcher {

    public static void main(String[] args) {
        try {
            Main.run();
        } catch (Exception exception) {
            Methods.exception(exception);
        }
        while (Main.pop.getId() != -1) {
            if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                break;
            }
            
        }
        cleanUp();
    }
}
