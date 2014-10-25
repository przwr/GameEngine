/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import engine.Delay;
import game.gameobject.Player;
import game.Game;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MenuOpt;
import game.gameobject.menu.choices.*;
import game.place.cameras.Camera;
import game.place.Place;
import java.awt.Font;
import engine.FontsHandler;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
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

    public boolean isMapping;
    public Delay delay = new Delay(25);

    public MyMenu(Game game, int width, int height, int tileSize, Settings settings) {
        super(game, width, height, tileSize, settings);
        generate();
    }

    @Override
    public final void generate() {
        delay.restart();
        menus = new MenuOpt[10];
        menus[0] = new MenuOpt(10, settings.language.Menu);
        menus[0].addChoice(new ChoiceStart(settings.language.Start, this, settings));
        menus[0].addChoice(new ChoiceSettings(settings.language.Options, this, settings));
        menus[0].addChoice(new ChoiceStop(settings.language.End, this, settings));
        menus[0].addChoice(new ChoiceExit(settings.language.Quit, this, settings));
        menus[1] = new MenuOpt(10, settings.language.Options);
        menus[1].addChoice(new ChoicePlayers(settings.language.Number_Of_Players, this, settings));
        menus[1].addChoice(new ChoiceSplitScreen(settings.language.SplitScreen, this, settings));
        menus[1].addChoice(new ChoiceLanguage(settings.language.Language, this, settings));
        menus[1].addChoice(new ChoiceControls(settings.language.Controls, this, settings));
        menus[1].addChoice(new ChoiceBrightness(settings.language.Brigthness, this, settings));
        menus[1].addChoice(new ChoiceVolume(settings.language.Volume, this, settings));
        menus[1].addChoice(new ChoiceResolution(settings.language.Resolution, this, settings));
        menus[1].addChoice(new ChoiceFullScreen(settings.language.FullScreen, this, settings));
        menus[1].addChoice(new ChoiceVSync(settings.language.VSync, this, settings));
        menus[1].addChoice(new ChoiceAntiAliasing(settings.language.AA, this, settings));
        menus[2] = new MenuOpt(10, settings.language.Controls);
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player1, this, settings));
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player2, this, settings));
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player3, this, settings));
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player4, this, settings));
        menus[3] = new MenuOpt(16, settings.language.Player1);
        int i = 0;
        for (; i < 4; i++) {
            menus[3].addChoice(new ChoiceNMapButton(settings.language.Actions[i], this, settings, settings.players[0], i));
        }
        for (; i < settings.actionsNr; i++) {
            menus[3].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[0], i));
        }
        menus[4] = new MenuOpt(16, settings.language.Player2);
        for (i = 0; i < settings.actionsNr; i++) {
            menus[4].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[1], i));
        }
        menus[5] = new MenuOpt(16, settings.language.Player3);
        for (i = 0; i < settings.actionsNr; i++) {
            menus[5].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[2], i));
        }
        menus[6] = new MenuOpt(16, settings.language.Player4);
        for (i = 0; i < settings.actionsNr; i++) {
            menus[6].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[3], i));
        }
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
        renderMessage(1, Display.getWidth() / 2, Display.getHeight() / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1)) * fonts.write(0).getHeight() * 0.7), menus[cur].getLabel(), new Color(r, g, b));
        positions--;
        for (int i = 0; i < menus[cur].getNr(); i++) {
            renderMessage(0, Display.getWidth() / 2, Display.getHeight() / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1)) * fonts.write(0).getHeight() * 0.7), menus[cur].getChoice(i).getLabel(), getColor(menus[cur].getChoice(i)));
            positions--;
        }
    }

    @Override
    public void render() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
        if (!isMapping && delay.isOver()) {
            menus[cur].setChoosen(i);
        }
    }

    public void choice() {
        if (!isMapping && delay.isOver()) {
            menus[cur].getChoosen().action();
        }
    }

    public void setCurrent(int i) {
        if (!isMapping && delay.isOver()) {
            cur = i;
        }
    }

    public void addPlayer() {
        settings.nrPlayers++;
    }

    public void setToOnePlayer() {
        settings.nrPlayers = 1;
    }

    public void back() {
        if (!isMapping && delay.isOver()) {
            if (cur > 2) {
                cur = 2;
            } else if (cur == 2) {
                cur = 1;
            } else if (cur != 0) {
                cur = 0;
            } else if (game.getPlace() != null) {
                game.resume();
            }
        }
    }
}
