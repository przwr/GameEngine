/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.Game;
import game.Settings;
import game.gameobject.Player;
import game.gameobject.menu.choices.*;
import game.place.cameras.Camera;
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

    private int cur;  

    private MenuOpt[] menus;

    public MyMenu(Game game, int width, int height, int tileSize, Settings settings) {
        super(game, width, height, tileSize, settings);
        generate();
    }

    @Override
    public final void generate() {
        menus = new MenuOpt[5];
        menus[0] = new MenuOpt(10, "Menu");
        menus[0].addChoice(new ChoiceStart("Start", this, settings));
        menus[0].addChoice(new ChoiceSettings("Opcje", this, settings));
        menus[0].addChoice(new ChoiceStop("Zakończ", this, settings));
        menus[0].addChoice(new ChoiceExit("Wyjdź", this, settings));
        menus[1] = new MenuOpt(10, "Opcje");
        menus[1].addChoice(new ChoicePlayers("Liczba graczy: ", this, settings));
        menus[1].addChoice(new ChoiceSplitScreen("Dzielenie Ekranu: ", this, settings));
        menus[1].addChoice(new ChoiceBrightness("Jasność: ", this, settings));
        menus[1].addChoice(new ChoiceVolume("Głośność: ", this, settings));
        menus[1].addChoice(new ChoiceResolution("Rozdzielczość: ", this, settings));
        menus[1].addChoice(new ChoiceDesktopFullScreen("Pełny Ekran: ", this, settings));
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

    public void setCurrent(int i) {
        cur = i;
    }

    public void addPlayer() {
        settings.nrPlayers++;
    }

    public void setToOnePlayer() {
        settings.nrPlayers = 1;
    }

    public void back() {
        if (cur != 0) {
            cur = 0;
        } else {
            game.resume();
        }
    }
}
