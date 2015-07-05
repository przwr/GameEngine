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
public abstract class TextEvent {

    int start, lineNum;

    protected TextEvent(int start, int lineNum) {
        this.start = start;
        this.lineNum = lineNum;
    }

    abstract void event(int index, int lineNum);
}
