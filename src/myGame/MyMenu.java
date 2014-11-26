/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import engine.Delay;
import engine.FontsHandler;
import game.Game;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MenuOpt;
import myGame.choices.ChoiceBrightness;
import myGame.choices.ChoiceControls;
import myGame.choices.ChoiceExit;
import myGame.choices.ChoiceFullScreen;
import myGame.choices.ChoiceJoinSS;
import myGame.choices.ChoiceLanguage;
import myGame.choices.ChoiceMapButton;
import myGame.choices.ChoiceNMapButton;
import myGame.choices.ChoicePlayerCtrl;
import myGame.choices.ChoicePlayers;
import myGame.choices.ChoiceResolution;
import myGame.choices.ChoiceSettings;
import myGame.choices.ChoiceSmoothShadows;
import myGame.choices.ChoiceSplitScreen;
import myGame.choices.ChoiceStartLocalGame;
import myGame.choices.ChoiceStop;
import myGame.choices.ChoiceVSync;
import myGame.choices.ChoiceVolume;
import game.place.Menu;
import myGame.choices.ChoiceFindServer;
import myGame.choices.ChoiceOnlineGameSettings;
import myGame.choices.ChoiceStart;
import myGame.choices.ChoiceJoinServer;
import myGame.choices.ChoiceRunServer;
import myGame.choices.ChoiceServerIP;
import myGame.choices.ChoiceServerPortTCP;
import myGame.choices.ChoiceServerPortUDP;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glViewport;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class MyMenu extends Menu {

    public MyMenu(Game game, int width, int height, int tileSize, Settings settings) {
        super(game, width, height, settings);
        generate();
    }

    @Override
    protected void generate() {
        delay = new Delay(25);
        delay.restart();
        menus = new MenuOpt[9];
        generateM0();
        generateM1();
        generateM2();
        generateM3();
        generateM4();
        generateM5();
        generateM6();
        generateM7();
        generateM8();
        this.r = 1f;
        this.g = 1f;
        this.b = 1f;
        fonts = new FontsHandler(20);
        fonts.add("Amble-Regular", (int) (settings.SCALE * 38));
        fonts.add("Amble-Regular", (int) (settings.SCALE * 64));
    }

    private void generateM0() {
        menus[0] = new MenuOpt(10, settings.language.Menu);
        menus[0].addChoice(new ChoiceStart(settings.language.Start, this, settings));
        menus[0].addChoice(new ChoiceSettings(settings.language.Options, this, settings));
        menus[0].addChoice(new ChoiceStop(settings.language.End, this, settings));
        menus[0].addChoice(new ChoiceExit(settings.language.Quit, this, settings));
    }

    private void generateM1() {
        menus[1] = new MenuOpt(12, settings.language.Options);
        menus[1].addChoice(new ChoicePlayers(settings.language.Number_Of_Players, this, settings));
        menus[1].addChoice(new ChoiceSplitScreen(settings.language.SplitScreen, this, settings));
        menus[1].addChoice(new ChoiceJoinSS(settings.language.JoinSS, this, settings));
        menus[1].addChoice(new ChoiceLanguage(settings.language.Language, this, settings));
        menus[1].addChoice(new ChoiceControls(settings.language.Controls, this, settings));
        menus[1].addChoice(new ChoiceBrightness(settings.language.Brigthness, this, settings));
        menus[1].addChoice(new ChoiceVolume(settings.language.Volume, this, settings));
        menus[1].addChoice(new ChoiceResolution(settings.language.Resolution, this, settings));
        menus[1].addChoice(new ChoiceFullScreen(settings.language.FullScreen, this, settings));
        menus[1].addChoice(new ChoiceVSync(settings.language.VSync, this, settings));
        menus[1].addChoice(new ChoiceSmoothShadows(settings.language.SmoothShadows, this, settings));
    }

    private void generateM2() {
        menus[2] = new MenuOpt(10, settings.language.Controls);
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player1, this, settings));
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player2, this, settings));
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player3, this, settings));
        menus[2].addChoice(new ChoicePlayerCtrl(settings.language.Player4, this, settings));
    }

    private void generateM3() {
        menus[3] = new MenuOpt(16, settings.language.Player1);
        int i;
        for (i = 0; i < 4; i++) {
            menus[3].addChoice(new ChoiceNMapButton(settings.language.Actions[i], this, settings, settings.players[0], i));
        }
        for (; i < settings.actionsNr; i++) {
            menus[3].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[0].ctrl, i));
        }
    }

    private void generateM4() {
        menus[4] = new MenuOpt(16, settings.language.Player2);
        for (int i = 0; i < settings.actionsNr; i++) {
            menus[4].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[1].ctrl, i));
        }
    }

    private void generateM5() {
        menus[5] = new MenuOpt(16, settings.language.Player3);
        for (int i = 0; i < settings.actionsNr; i++) {
            menus[5].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[2].ctrl, i));
        }
    }

    private void generateM6() {
        menus[6] = new MenuOpt(16, settings.language.Player4);
        for (int i = 0; i < settings.actionsNr; i++) {
            menus[6].addChoice(new ChoiceMapButton(settings.language.Actions[i], this, settings, settings.players[3].ctrl, i));
        }
    }

    private void generateM7() {
        menus[7] = new MenuOpt(4, settings.language.Start);
        menus[7].addChoice(new ChoiceStartLocalGame(settings.language.LocalGame, this, settings));
        menus[7].addChoice(new ChoiceOnlineGameSettings(settings.language.OnlineGame, this, settings));
        // menus[7].addChoice(new ChoiceStartOnlineGame(settings.language.OnlineGame, this, settings));
    }

    private void generateM8() {
        menus[8] = new MenuOpt(6, settings.language.OnlineGame);
        menus[8].addChoice(new ChoiceRunServer(settings.language.RunServer, this, settings));
        menus[8].addChoice(new ChoiceJoinServer(settings.language.JoinServer, this, settings));
        menus[8].addChoice(new ChoiceFindServer(settings.language.FindServer, this, settings));
        menus[8].addChoice(new ChoiceServerIP(settings.language.ServerIP, this, settings));
        menus[8].addChoice(new ChoiceServerPortTCP(settings.language.Port, this, settings));
        menus[8].addChoice(new ChoiceServerPortUDP(settings.language.Port, this, settings));
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glScissor(0, 0, Display.getWidth(), Display.getHeight());
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(r, g, b);
        renderText();
    }

    @Override
    protected void renderText() {
        int positions = menus[cur].getNr() + 1;
        renderMessage(1, dWidth / 2, dHeight / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1)) * fonts.write(0).getHeight() * 0.7), menus[cur].getLabel(), new Color(r, g, b));
        positions--;
        for (int i = 0; i < menus[cur].getNr(); i++) {
            renderMessage(0, dWidth / 2, dHeight / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1)) * fonts.write(0).getHeight() * 0.7), menus[cur].getChoice(i).getLabel(), getColor(menus[cur].getChoice(i)));
            positions--;
        }
    }

    public Color getColor(MenuChoice choice) {
        if (choice == menus[cur].getChoosen()) {
            return new Color(1f, 1f, 0.5f);
        } else {
            return new Color(1f, 1f, 1f);
        }
    }

    @Override
    public void back() {
        if (!isMapping && delay.isOver()) {
            if (cur > 2 && cur < 7) {
                cur = 2;
            } else if (cur == 2) {
                cur = 1;
            } else if (cur == 8) {
                cur = 7;
            } else if (cur != 0) {
                cur = 0;
            } else if (game.started) {
                game.resumeGame();
            }
        }
    }
}
