/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
abstract class TextEvent {

    int start;
    final int unalteredStart;
    private final int lineNum;

    TextEvent(int start, int lineNum) {
        unalteredStart = this.start = start;
        this.lineNum = lineNum;
    }
    
    public void alterStart(int added) {
        start = unalteredStart + added;
    }

    abstract void event(int index, int lineNum);
}
