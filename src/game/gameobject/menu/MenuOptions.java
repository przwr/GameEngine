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
public class MenuOptions {

    private final String label;
    private final MenuChoice[] choices;
    private int optionsCount;
    private int choosenOption;

    public MenuOptions(int size, String label) {
        choices = new MenuChoice[size];
        this.label = label;
    }

    public void addChoice(MenuChoice choice) {
        choices[optionsCount++] = choice;
    }

    public void setChoosen(int i) {
        choosenOption += i;
        if (choosenOption > optionsCount - 1) {
            choosenOption = 0;
        } else if (choosenOption < 0) {
            choosenOption = optionsCount - 1;
        }
    }

    public MenuChoice getChoosen() {
        return choices[choosenOption];
    }

    public MenuChoice getChoice(int i) {
        return choices[i];
    }

    public String getLabel() {
        return label;
    }

    public int getOptionsNumber() {
        return optionsCount;
    }
}
