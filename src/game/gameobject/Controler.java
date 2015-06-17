/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 *
 * @author przemek
 */
public abstract class Controler {

    public Action[] actions;
    public AnyInput[] inputs;
    protected final Entity inControl;
    protected byte[] states;
    
    public static final byte KEY_NO_INPUT = 0, KEY_PRESSED = 1, KEY_CLICKED = 2, KEY_RELEASED = -1;

    public Controler(Entity inControl) {
        this.inControl = inControl;
    }
    
    public void showInput() {
        for (byte b : states)
            System.out.print(b + " ");
        System.out.println("");
    }

    public boolean isKeyReleased(int key) {
        return states[key] == KEY_RELEASED;
    }
    
    public boolean isKeyClicked(int key) {
        return states[key] == KEY_CLICKED;
    }
    
    public boolean isKeyPressed(int key) {
        return states[key] > KEY_NO_INPUT;
    }    
    
    public abstract void getInput();

    public abstract boolean isMenuOn();

    public abstract void getMenuInput();

    public abstract int getActionsCount();

    public abstract void initialize();
}
