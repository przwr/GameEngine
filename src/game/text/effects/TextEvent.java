/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

/**
 * @author Wojtek
 */
abstract class TextEvent {

    TextEvent previous;
    int length;
    int start;
    int end;

    TextEvent(TextEvent previous, int length) {
        this.previous = previous;
        this.length = length;
    }

    public int getStart() {
        if (previous == null) {
            return 0;
        } else {
            return previous.getEnd();
        }
    }

    int getX(int y) {
        if (previous == null) {
            return 0;
        } else {
            return previous.getX(y);
        }
    }

    public int getEnd() {
        return getStart() + length;
    }

    public void setStart() {
        start = getStart();
    }

    public void setEnd() {
        end = getEnd();
    }

    public void event(int index, int lineNum, int xPosition, int yPosition) {
        setStart();
        setEnd();
        innerEvent(index, lineNum, xPosition, yPosition);
    }

    abstract void innerEvent(int index, int lineNum, int xPosition, int yPositiony);
}
