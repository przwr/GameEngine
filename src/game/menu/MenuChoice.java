/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.menu;

import java.util.ArrayList;

/**
 * @author przemek
 */
public class MenuChoice {

	protected final String label;
	protected final Menu menu;
	protected MenuChoice previous;
	protected final ArrayList<MenuChoice> choices = new ArrayList<>(1);
	protected int current;

	public MenuChoice(String label, Menu menu) {
		this.label = label;
		this.menu = menu;
	}

	public void action() {
		menu.setRoot(this);
	}

	public void addChoice(MenuChoice choice) {
		choice.setPrevious(this);
		this.choices.add(choice);
	}

	public void setPrevious(MenuChoice previous) {
		this.previous = previous;
	}

	public String getLabel() {
		return label;
	}

	public MenuChoice getPrevious() {
		return previous;
	}

	public void changeCurrent(int i) {
		current += i;
		if (current < 0) {
			current = choices.size() - 1;
		} else if (current >= choices.size()) {
			current = 0;
		}
	}

	public int getCurrent() {
		return current;
	}

	public int getSize() {
		return choices.size();
	}

	public MenuChoice getChoice(int i) {
		return choices.get(i);
	}

	public void actionCurrent() {
		choices.get(current).action();
	}
}
