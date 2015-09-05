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
    private final int maxPositions;
    private final Color normalColor = new Color(1f, 1f, 1f);
    private final Color darkColor = new Color(0.5f, 0.5f, 0.5f);
    private final Color chosenColor = new Color(1f, 1f, 0.5f);
    private final Color gammaColor1 = new Color(0.32f, 0.32f, 0.32f);
    private final Color gammaColor2 = new Color(0.16f, 0.16f, 0.16f);
    private final Color gammaColor3 = new Color(0.08f, 0.08f, 0.08f);

    public MyMenu(Game game) {
        super(game);
        setFirstRoot(new MenuChoice(Settings.language.menu.Menu, this));
        fonts = new FontBase(20);
        int normalFontSize = Methods.roundDouble(Settings.nativeScale * 38);
        int bigFontSize = Methods.roundDouble(Settings.nativeScale * 64);
        smallFont = fonts.add("Amble-Regular", normalFontSize);
        bigFont = fonts.add("Amble-Regular", bigFontSize);
        delay = new Delay(25);
        delay.start();
        maxPositions = calculateMaxPositions(normalFontSize, bigFontSize);
        generate();
    }

    private int calculateMaxPositions(int normal, int big) {
        return (int) ((Display.getHeight() - big * 2) / (1.5 * normal)) - 2;
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
            player1.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 0, i));
        }
        for (i = 3; i < Settings.actionsCount; i++) {
            player2.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 1, i));
            player3.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 2, i));
            player4.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 3, i));
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
        renderText();
    }

    private void renderText() {
        int shift = 0;
        int positions = root.getSize();
        if (positions > maxPositions) {
            positions = maxPositions;
        }
        int line = positions + 2;
        if (root.getCurrent() >= positions) {
            shift = root.getCurrent() - positions + 1;
        }
        Drawer.renderStringCentered(root.getLabel(), widthHalf / 2, heightHalf / 2 - (int) ((1.5 * line - (positions + 1))
                        * fonts.getFont(0).getHeight() * 0.7),
                bigFont, normalColor);
        line--;
        if (shift > 0) {
            Drawer.renderStringCentered("/|\\", widthHalf / 2, heightHalf / 2 - (int) ((1.5 * line - (positions + 1)) * fonts.getFont(0).getHeight() * 0.7),
                    smallFont, darkColor);
        }
        line--;
        for (int i = 0; i < positions; i++) {
            if (root.getChoice(i + shift) instanceof GammaChoice) {
                renderGammaHelper(line, i + shift);
            }
            Drawer.renderStringCentered(root.getChoice(i + shift).getLabel(), widthHalf / 2,
                    heightHalf / 2 - (int) ((1.5 * line - (positions + 1)) * fonts.getFont(0).getHeight() * 0.7),
                    smallFont, getColor(i + shift));
            line--;
        }
        if (root.getSize() > maxPositions && root.getCurrent() != root.getSize() - 1) {
            Drawer.renderStringCentered("\\|/", widthHalf / 2, heightHalf / 2 - (int) ((1.5 * line - (positions + 1)) * fonts.getFont(0).getHeight() * 0.7),
                    smallFont, darkColor);
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
