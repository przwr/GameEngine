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

    protected Entity inControl;

    public Controler(Entity inControl) {
        this.inControl = inControl;
    }

    protected abstract void getInput();
    
    protected abstract boolean isMenuOn();
    
    protected abstract void getMenuInput();
}
