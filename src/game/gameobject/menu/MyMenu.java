/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.Game;
import game.gameobject.Player;
import game.gameobject.menu.choices.ChoiceExit;
import game.gameobject.menu.choices.ChoiceResume;
import game.gameobject.menu.choices.ChoiceStart;
import game.place.Camera;
import game.place.Place;
import java.awt.Font;
import openGLEngine.FontsHandler;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glViewport;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class MyMenu extends Place {

    public Game game;
    private int cur;

    private MenuOpt[] menus;

    public MyMenu(int width, int height, int tileSize, Game game) {
        super(width, height, tileSize);
        this.game = game;
        generate();
    }

    @Override
    public void generate() {
        menus = new MenuOpt[5];
        menus[0] = new MenuOpt(4, "Menu");
        menus[0].addChoice(new ChoiceStart("Start", this));
        menus[0].addChoice(new ChoiceResume("Wznów", this));
        menus[0].addChoice(new ChoiceExit("Wyjdź", this));
        this.r = 1f;
        this.g = 1f;
        this.b = 1f;
        fonts = new FontsHandler(20);
        fonts.add("Arial", Font.PLAIN, 24);
        fonts.add("Arial", Font.PLAIN, 36);
    }

    @Override
    public void update() {

    }

    @Override
    protected void renderText(Camera cam) {
        int positions = menus[cur].getNr() + 1;
        renderMessage(1, Display.getWidth() / 2, Display.getHeight() / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1)) * fonts.write(0).getHeight()), menus[cur].getLabel(), new Color(r, g, b));
        positions--;
        for (int i = 0; i < menus[cur].getNr(); i++) {
            renderMessage(0, Display.getWidth() / 2, Display.getHeight() / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1)) * fonts.write(0).getHeight()), menus[cur].getChoice(i).getLabel(), getColor(menus[cur].getChoice(i)));
            positions--;
        }
    }

    @Override
    public void render() {
        Camera cam;
        cam = (((Player) players.get(0)).getCam());
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glColor3f(r, g, b);
        renderText(cam);
    }

    public Color getColor(MenuChoice choice) {
        if (choice == menus[cur].getChoosen()) {
            return new Color(1f, 1f, 0.5f);
        } else {
            return new Color(1f, 1f, 1f);
        }
    }

    public void setChoosen(int i) {
        menus[cur].setChoosen(i);
    }

    public void choice() {
        menus[cur].getChoosen().action();
    }
}
