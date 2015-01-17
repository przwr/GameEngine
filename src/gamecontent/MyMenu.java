/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Delay;
import engine.FontsHandler;
import engine.Methods;
import game.Game;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MenuOpt;
import game.place.Menu;
import gamecontent.choices.BrightnessChoice;
import gamecontent.choices.ControlsChoice;
import gamecontent.choices.ExitChoice;
import gamecontent.choices.FindServerChoice;
import gamecontent.choices.FullScreenChoice;
import gamecontent.choices.JoinServerChoice;
import gamecontent.choices.JoinSplitScreenChoice;
import gamecontent.choices.LanguageChoice;
import gamecontent.choices.MapButtonChoice;
import gamecontent.choices.NotMapButtonChoice;
import gamecontent.choices.OnlineGameSettingsChoice;
import gamecontent.choices.PlayerControllerChoice;
import gamecontent.choices.PlayersNumberChoice;
import gamecontent.choices.ResolutionChoice;
import gamecontent.choices.RunServerChoice;
import gamecontent.choices.ServerIPChoice;
import gamecontent.choices.ServerTCPPortChoice;
import gamecontent.choices.ServerUDPPortChoice;
import gamecontent.choices.SettingsChoice;
import gamecontent.choices.ShadowsOffChoice;
import gamecontent.choices.SmoothShadowsChoice;
import gamecontent.choices.SplitScreenChoice;
import gamecontent.choices.StartChoice;
import gamecontent.choices.StartLocalGameChoice;
import gamecontent.choices.StopChoice;
import gamecontent.choices.VerticalSynchronizationChoice;
import gamecontent.choices.VolumeChoice;
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

    private void generate() {
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
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
        fonts = new FontsHandler(20);
        fonts.add("Amble-Regular", Methods.RoundHU(settings.SCALE * 38));
        fonts.add("Amble-Regular", Methods.RoundHU(settings.SCALE * 64));
    }

    private void generateM0() {
        menus[0] = new MenuOpt(10, settings.language.m.Menu);
        menus[0].addChoice(new StartChoice(settings.language.m.Start, this, settings));
        menus[0].addChoice(new SettingsChoice(settings.language.m.Options, this, settings));
        menus[0].addChoice(new StopChoice(settings.language.m.End, this, settings));
        menus[0].addChoice(new ExitChoice(settings.language.m.Quit, this, settings));
    }

    private void generateM1() {
        menus[1] = new MenuOpt(12, settings.language.m.Options);
        menus[1].addChoice(new PlayersNumberChoice(settings.language.m.Number_Of_Players, this, settings));
        menus[1].addChoice(new SplitScreenChoice(settings.language.m.SplitScreen, this, settings));
        menus[1].addChoice(new JoinSplitScreenChoice(settings.language.m.JoinSS, this, settings));
        menus[1].addChoice(new LanguageChoice(settings.language.m.Language, this, settings));
        menus[1].addChoice(new ControlsChoice(settings.language.m.Controls, this, settings));
        menus[1].addChoice(new BrightnessChoice(settings.language.m.Brigthness, this, settings));
        menus[1].addChoice(new VolumeChoice(settings.language.m.Volume, this, settings));
        menus[1].addChoice(new ResolutionChoice(settings.language.m.Resolution, this, settings));
        menus[1].addChoice(new FullScreenChoice(settings.language.m.FullScreen, this, settings));
        menus[1].addChoice(new VerticalSynchronizationChoice(settings.language.m.VSync, this, settings));
        menus[1].addChoice(new ShadowsOffChoice(settings.language.m.ShadowOff, this, settings));
        menus[1].addChoice(new SmoothShadowsChoice(settings.language.m.SmoothShadows, this, settings));
    }

    private void generateM2() {
        menus[2] = new MenuOpt(10, settings.language.m.Controls);
        menus[2].addChoice(new PlayerControllerChoice(settings.language.m.Player1, this, settings));
        menus[2].addChoice(new PlayerControllerChoice(settings.language.m.Player2, this, settings));
        menus[2].addChoice(new PlayerControllerChoice(settings.language.m.Player3, this, settings));
        menus[2].addChoice(new PlayerControllerChoice(settings.language.m.Player4, this, settings));
    }

    private void generateM3() {
        menus[3] = new MenuOpt(16, settings.language.m.Player1);
        int i;
        for (i = 0; i < 4; i++) {
            menus[3].addChoice(new NotMapButtonChoice(settings.language.m.Actions[i], this, settings, settings.players[0], i));
        }
        for (; i < settings.actionsNr; i++) {
            menus[3].addChoice(new MapButtonChoice(settings.language.m.Actions[i], this, settings, settings.players[0].ctrl, i));
        }
    }

    private void generateM4() {
        menus[4] = new MenuOpt(16, settings.language.m.Player2);
        for (int i = 3; i < settings.actionsNr; i++) {
            menus[4].addChoice(new MapButtonChoice(settings.language.m.Actions[i], this, settings, settings.players[1].ctrl, i));
        }
    }

    private void generateM5() {
        menus[5] = new MenuOpt(16, settings.language.m.Player3);
        for (int i = 3; i < settings.actionsNr; i++) {
            menus[5].addChoice(new MapButtonChoice(settings.language.m.Actions[i], this, settings, settings.players[2].ctrl, i));
        }
    }

    private void generateM6() {
        menus[6] = new MenuOpt(16, settings.language.m.Player4);
        for (int i = 3; i < settings.actionsNr; i++) {
            menus[6].addChoice(new MapButtonChoice(settings.language.m.Actions[i], this, settings, settings.players[3].ctrl, i));
        }
    }

    private void generateM7() {
        menus[7] = new MenuOpt(4, settings.language.m.Start);
        menus[7].addChoice(new StartLocalGameChoice(settings.language.m.LocalGame, this, settings));
        menus[7].addChoice(new OnlineGameSettingsChoice(settings.language.m.OnlineGame, this, settings));
    }

    private void generateM8() {
        menus[8] = new MenuOpt(6, settings.language.m.OnlineGame);
        menus[8].addChoice(new RunServerChoice(settings.language.m.RunServer, this, settings));
        menus[8].addChoice(new JoinServerChoice(settings.language.m.JoinServer, this, settings));
        menus[8].addChoice(new FindServerChoice(settings.language.m.FindServer, this, settings));
        menus[8].addChoice(new ServerIPChoice(settings.language.m.ServerIP, this, settings));
        menus[8].addChoice(new ServerTCPPortChoice(settings.language.m.Port, this, settings));
        menus[8].addChoice(new ServerUDPPortChoice(settings.language.m.Port, this, settings));
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glScissor(0, 0, Display.getWidth(), Display.getHeight());
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(red, green, blue);
        renderText();
    }

    @Override
    protected void renderText() {
        int positions = menus[cur].getNr() + 1;
        renderMessage(1, dWidth / 2, dHeight / 2 - (int) ((1.5 * positions - (menus[cur].getNr() + 1))
                * fonts.write(0).getHeight() * 0.7),
                menus[cur].getLabel(), new Color(red, green, blue));
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
