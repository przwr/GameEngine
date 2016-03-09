/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Game;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
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
    private final Color darkChosenColor = new Color(0.75f, 0.75f, 0.375f);
    //    private final Color gammaColor1 = new Color(0.32f, 0.32f, 0.32f);
//    private final Color gammaColor2 = new Color(0.16f, 0.16f, 0.16f);
//    private final Color gammaColor3 = new Color(0.08f, 0.08f, 0.08f);
    private final Color gammaColor1 = new Color(0.32f, 0.32f, 0.32f);
    private final Color gammaColor2 = new Color(0.16f, 0.16f, 0.16f);
    private final Color gammaColor3 = new Color(0.1f, 0.1f, 0.1f);


    public MyMenu(Game game) {
        super(game);
        setFirstRoot(new MenuChoice(Settings.language.menu.Menu, this));
        int normalFontSize = Methods.roundDouble(Settings.nativeScale * 38);
        int bigFontSize = Methods.roundDouble(Settings.nativeScale * 64);
        smallFont = Settings.fonts.getFont("Amble-Regular", normalFontSize);
        bigFont = Settings.fonts.getFont("Amble-Regular", bigFontSize);
        delay = Delay.createInMilliseconds(25, true);
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
//        options.addChoice(new PlayersNumberChoice(Settings.language.menu.Number_Of_Players, this));
        MenuChoice gameplay = new MenuChoice(Settings.language.menu.Gameplay, this);
        gameplay.addChoice(new SplitScreenChoice(Settings.language.menu.SplitScreen, this));
        gameplay.addChoice(new JoinSplitScreenChoice(Settings.language.menu.JoinSS, this));
//        options.addChoice(gameplay);


        MenuChoice controls = new MenuChoice(Settings.language.menu.Controls, this);
        addControlsChoices(controls);
        options.addChoice(controls);
        MenuChoice sound = new MenuChoice(Settings.language.menu.Sound, this);
        sound.addChoice(new VolumeChoice(Settings.language.menu.Volume, this));
//        options.addChoice(sound);
        MenuChoice graphic = new MenuChoice(Settings.language.menu.Video, this);
        graphic.addChoice(new BrightnessChoice(Settings.language.menu.Brightness, this));
        graphic.addChoice(new GammaChoice(Settings.language.menu.Gamma, this));
        graphic.addChoice(new FramesNumberChoice(Settings.language.menu.FramesLimit, this));
        graphic.addChoice(new ResolutionChoice(Settings.language.menu.Resolution, this).setBlockOnRun(true));
        graphic.addChoice(new FullScreenChoice(Settings.language.menu.FullScreen, this).setBlockOnRun(true));
        graphic.addChoice(new VerticalSynchronizationChoice(Settings.language.menu.VSync, this).setBlockOnRun(true));
//        graphic.addChoice(new ShadowsOffChoice(Settings.language.menu.ShadowOff, this));
        graphic.addChoice(new SmoothShadowsChoice(Settings.language.menu.SmoothShadows, this).setBlockOnRun(true));
        graphic.addChoice(new ApplyChoice(Settings.language.menu.Apply, this).setBlockOnRun(true));
        options.addChoice(graphic);

        MenuChoice language = new MenuChoice(Settings.language.menu.Language, this);
        language.setBlockOnRun(true);
        language.addChoice(new LanguageChoice(Settings.language.menu.Text, this));
        language.addChoice(new ApplyChoice(Settings.language.menu.Apply, this));
//        options.addChoice(language);

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
        for (i = 0; i < MyController.MENU_ACTIONS_COUNT; i++) {
            player1.addChoice(new NotMapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[0], i));
        }
        for (; i < Settings.actionsCount; i++) {
            player1.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 0, i));
        }
        player2.addChoice(new MapButtonChoice(Settings.language.menu.Actions[3], this, 1, 3));
        player3.addChoice(new MapButtonChoice(Settings.language.menu.Actions[3], this, 2, 3));
        player4.addChoice(new MapButtonChoice(Settings.language.menu.Actions[3], this, 3, 3));
        for (i = MyController.MENU_ACTIONS_COUNT; i < Settings.actionsCount; i++) {
            player2.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 1, i));
            player3.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 2, i));
            player4.addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, 3, i));
        }
        controls.addChoice(player1);
        controls.addChoice(player2);
//        controls.addChoice(player3);
//        controls.addChoice(player4);
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
        if (root.getCurrent() >= positions / 2 + 1 && root.getSize() > maxPositions) {
            shift = root.getCurrent() - positions / 2 + 1;
            if (positions + shift > root.getSize() - 1) {
                shift = root.getSize() - positions;
            }
        }
        Drawer.renderStringCentered(root.getLabel(), widthHalf / 2, heightHalf / 2 - (int) ((1.5 * line - (positions + 1))
                        * smallFont.getHeight() * 0.7),
                bigFont, normalColor);
        line--;
        if (shift > 0) {
            Drawer.renderStringCentered("/|\\", widthHalf / 2, heightHalf / 2 - (int) ((1.5 * line - (positions + 1)) * smallFont.getHeight() * 0.7),
                    smallFont, darkColor);
        }
        line--;
        for (int i = 0; i < positions; i++) {
            if (root.getChoice(i + shift) instanceof GammaChoice || root.getChoice(i + shift) instanceof BrightnessChoice) {
                renderGammaHelper(line, i + shift);
            }
            Drawer.renderStringCentered(root.getChoice(i + shift).getLabel(), widthHalf / 2,
                    heightHalf / 2 - (int) ((1.5 * line - (positions + 1)) * smallFont.getHeight() * 0.7),
                    smallFont, getColor(i + shift));
            line--;
        }
        if (root.getSize() > maxPositions && positions + shift <= root.getSize() - 1) {
            Drawer.renderStringCentered("\\|/", widthHalf / 2, heightHalf / 2 - (int) ((1.5 * line - (positions + 1)) * smallFont.getHeight() * 0.7),
                    smallFont, darkColor);
        }
    }

    private void renderGammaHelper(int position, int i) {
        Drawer.renderStringCentered("#", (widthHalf + smallFont.getWidth(root.getChoice(i).getLabel() + "##")) / 2,
                heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * smallFont.getHeight() * 0.7),
                smallFont, gammaColor1);
        Drawer.renderStringCentered("#", (widthHalf + smallFont.getWidth(root.getChoice(i).getLabel() + "####")) / 2,
                heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * smallFont.getHeight() * 0.7),
                smallFont, gammaColor2);
        Drawer.renderStringCentered("#", (widthHalf + smallFont.getWidth(root.getChoice(i).getLabel() + "######")) / 2,
                heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * smallFont.getHeight() * 0.7),
                smallFont, gammaColor3);
    }

    private Color getColor(int choice) {
        if (choice == root.getCurrent()) {
            if (root.getChoice(choice).isBlocked()) {
                return darkChosenColor;
            } else {
                return chosenColor;
            }
        } else if (root.getChoice(choice).isBlocked()) {
            return darkColor;
        } else {
            return normalColor;
        }
    }
}
