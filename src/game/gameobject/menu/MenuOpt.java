/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

/**
 *
 * @author przemek
 */
public class MenuOpt {

    private final String label;
    private final AbstractMenuChoice[] choices;
    private int nr;
    private int choosenNr;

    public MenuOpt(int size, String label) {
        choices = new AbstractMenuChoice[size];
        this.label = label;
    }

    public void addChoice(AbstractMenuChoice choice) {
        choices[nr++] = choice;
    }

    public void setChoosen(int i) {
        choosenNr += i;
        if (choosenNr > nr - 1) {
            choosenNr = 0;
        } else if (choosenNr < 0) {
            choosenNr = nr - 1;
        }
    }

    public AbstractMenuChoice getChoosen() {
        return choices[choosenNr];
    }

    public AbstractMenuChoice getChoice(int i) {
        return choices[i];
    }

    public String getLabel() {
        return label;
    }

    public int getNr() {
        return nr;
    }
}
