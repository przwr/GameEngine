/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Delay;
import engine.Drawer;
import engine.Methods;
import game.Game;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
import game.text.FontBase;
import game.text.FontHandler;
import gamecontent.choices.*;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class MyMenu extends Menu {

    private final FontHandler smallFont, bigFont;
    private final Color color;
    private final Color normalColor = new Color(1f, 1f, 1f);
    private final Color chosenColor = new Color(1f, 1f, 0.5f);
    private final Color gammaColor1 = new Color(0.32f, 0.32f, 0.32f);
    private final Color gammaColor2 = new Color(0.16f, 0.16f, 0.16f);
    private final Color gammaColor3 = new Color(0.08f, 0.08f, 0.08f);

    public MyMenu(Game game) {
        super(game);
        setFirstRoot(new MenuChoice(Settings.language.menu.Menu, this));
        this.color = new Color(Color.white);
        fonts = new FontBase(20);
        smallFont = fonts.add("Amble-Regular", Methods.roundDouble(Settings.nativeScale * 38));
        bigFont = fonts.add("Amble-Regular", Methods.roundDouble(Settings.nativeScale * 64));
        delay = new Delay(25);
        delay.start();
        generate();
    }

    private void generate() {
        MenuChoice start = new StartChoice(Settings.language.menu.Start, this);
        MenuChoice onlineGameSettings = new MenuChoice(Settings.language.menu.OnlineGame, this);
        onlineGameSettings.addChoice(new RunServerChoice(Settings.language.menu.RunServer, this));
        onlineGameSettings.addChoice(new JoinServerChoice(Settings.language.menu.JoinServer, this));
        onlineGameSettings.addChoice(new FindServerChoice(Settings.language.menu.FindServer, this));
        onlineGameSettings.addChoice(new ServerIPChoice(Settings.language.menu.ServerIP, this));
        onlineGameSettings.addChoice(new ServerTCPPortChoice(Settings.language.menu.Port, this));
        onlineGameSettings.addChoice(new ServerUDPPortChoice(Settings.language.menu.Port, this));
        start.addChoice(new StartLocalGameChoice(Settings.language.menu.LocalGame, this));
//        start.addChoice(onlineGameSettings);
        root.addChoice(start);

        MenuChoice options = new MenuChoice(Settings.language.menu.Options, this);
        options.addChoice(new PlayersNumberChoice(Settings.language.menu.Number_Of_Players, this));
        options.addChoice(new SplitScreenChoice(Settings.language.menu.SplitScreen, this));
        options.addChoice(new JoinSplitScreenChoice(Settings.language.menu.JoinSS, this));
        options.addChoice(new LanguageChoice(Settings.language.menu.Language, this));
        MenuChoice controls = new MenuChoice(Settings.language.menu.Controls, this);
        addControlsChoices(controls);
        options.addChoice(controls);
        options.addChoice(new VolumeChoice(Settings.language.menu.Volume, this));
        options.addChoice(new GammaChoice(Settings.language.menu.Gamma, this));
        options.addChoice(new ResolutionChoice(Settings.language.menu.Resolution, this));
        options.addChoice(new FullScreenChoice(Settings.language.menu.FullScreen, this));
        options.addChoice(new VerticalSynchronizationChoice(Settings.language.menu.VSync, this));
        options.addChoice(new ShadowsOffChoice(Settings.language.menu.ShadowOff, this));
        options.addChoice(new SmoothShadowsChoice(Settings.language.menu.SmoothShadows, this));
        root.addChoice(options);

        root.addChoice(new StopChoice(Settings.language.menu.End, this));
        root.addChoice(new ExitChoice(Settings.language.menu.Quit, this));
    }

    private void addControlsChoices(MenuChoice controls) {
        MenuChoice player1 = new MenuChoice(Settings.language.menu.Player1, this);
        MenuChoice player2 = new MenuChoice(Settings.language.menu.Player2, this);
        MenuChoice player3 = new MenuChoice(Settings.language.menu.Player3, this);
        MenuChoice player4 = new MenuChoice(Settings.language.menu.Player4, this);
        int i;
        for (i = 0; i < 4; i++) {
            player1.addChoice(new NotMapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[0], i));
        }
        for (; i < Settings.actionsCount; i++) {
            player1.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[0].playerController, i));
        }
        for (i = 3; i < Settings.actionsCount; i++) {
            player2.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[1].playerController, i));
            player3.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[2].playerController, i));
            player4.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[3].playerController, i));
        }
        controls.addChoice(player1);
        controls.addChoice(player2);
        controls.addChoice(player3);
        controls.addChoice(player4);
    }

    @Override
    public void render() {
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glScissor(0, 0, Display.getWidth(), Display.getHeight());
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(color.r, color.g, color.b);
        renderText();
    }

    private void renderText() {
        int position = root.getSize() + 1;
        Drawer.renderStringCentered(root.getLabel(), widthHalf / 2, heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1))
                        * fonts.getFont(0).getHeight() * 0.7),
                bigFont, new Color(color.r, color.g, color.b));
        position--;
        for (int i = 0; i < root.getSize(); i++) {
            if (root.getChoice(i) instanceof GammaChoice) {
                renderGammaHelper(position, i);
            }
            Drawer.renderStringCentered(root.getChoice(i).getLabel(), widthHalf / 2,
                    heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * fonts.getFont(0).getHeight() * 0.7),
                    smallFont, getColor(i));


            position--;
        }
    }

    private void renderGammaHelper(int position, int i) {
        Drawer.renderStringCentered("#", (widthHalf + fonts.getFont(0).getWidth(root.getChoice(i).getLabel() + "##")) / 2,
                heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * fonts.getFont(0).getHeight() * 0.7),
                smallFont, gammaColor1);
        Drawer.renderStringCentered("#", (widthHalf + fonts.getFont(0).getWidth(root.getChoice(i).getLabel() + "####")) / 2,
                heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * fonts.getFont(0).getHeight() * 0.7),
                smallFont, gammaColor2);
        Drawer.renderStringCentered("#", (widthHalf + fonts.getFont(0).getWidth(root.getChoice(i).getLabel() + "######")) / 2,
                heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * fonts.getFont(0).getHeight() * 0.7),
                smallFont, gammaColor3);
    }

    private Color getColor(int choice) {
        if (choice == root.getCurrent()) {
            return chosenColor;
        } else {
            return normalColor;
        }
    }
}
