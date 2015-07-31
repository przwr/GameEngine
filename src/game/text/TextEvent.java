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

    final int start;
    private final int lineNum;

    TextEvent(int start, int lineNum) {
        this.start = start;
        this.lineNum = lineNum;
    }

    abstract void event(int index, int lineNum);
}
