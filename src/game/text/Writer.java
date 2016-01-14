/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 *
 * @author Wojtek
 */
public abstract class Writer {
    String name;

    public Writer(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract String add();
}
