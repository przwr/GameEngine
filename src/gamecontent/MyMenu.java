/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import game.Game;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import gamecontent.choices.*;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class MyMenu extends Menu {

    private final int maxPositions;
    private final Color normalColor = new Color(1f, 1f, 1f);
    private final Color darkColor = new Color(0.5f, 0.5f, 0.5f);
    private final Color chosenColor = new Color(1f, 1f, 0.5f);
    private final Color darkChosenColor = new Color(0.75f, 0.75f, 0.375f);
    private final Color gammaColor1 = new Color(0.32f, 0.32f, 0.32f);
    private final Color gammaColor2 = new Color(0.16f, 0.16f, 0.16f);
    private final Color gammaColor3 = new Color(0.1f, 0.1f, 0.1f);
    private int bigFontSize;
    private TextPiece arrowUp, arrowDown, title;

    public MyMenu(Game game) {
        super(game);
        bigFontSize = 64;
        fontSize = 36;
        font = TextMaster.getFont("Lato-Regular");
        delay = Delay.createInMilliseconds(25, true);
        delay.terminate();
        maxPositions = calculateMaxPositions(fontSize, bigFontSize);
        arrowUp = new TextPiece("/|\\", fontSize, font, Display.getWidth(), true);
        arrowDown = new TextPiece("\\|/", fontSize, font, Display.getWidth(), true);
        title = new TextPiece("Menu", bigFontSize, font, Display.getWidth(), true);
        setFirstRoot(new MenuChoice(Settings.language.menu.Menu, this));
        generate();
    }

    private int calculateMaxPositions(int normal, int big) {
        return (int) ((Display.getHeight() - 2 * big) / (1.5 * normal * Settings.nativeScale)) - 2;
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
        sound.addChoice(new SoundVolumeChoice(Settings.language.menu.SoundVolume, this));
        sound.addChoice(new MusicVolumeChoice(Settings.language.menu.MusicVolume, this));
        options.addChoice(sound);
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
//        for (i = 0; i < MyController.MENU_ACTIONS_COUNT; i++) {
//            player1.addChoice(new NotMapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[0], i));
//        }
        MenuChoice player1Default = new MenuChoice(Settings.language.menu.Default, this);
        player1Default.addChoice(new DefaultInputChoice(Settings.language.menu.WSAD, 0, 0, this));
        player1Default.addChoice(new DefaultInputChoice(Settings.language.menu.Arrows, 0, 1, this));
        player1Default.addChoice(new DefaultInputChoice(Settings.language.menu.Gamepad, 0, 2, this));
        player1.addChoice(player1Default);

        MenuChoice player2Default = new MenuChoice(Settings.language.menu.Default, this);
        player2Default.addChoice(new DefaultInputChoice(Settings.language.menu.WSAD, 1, 0, this));
        player2Default.addChoice(new DefaultInputChoice(Settings.language.menu.Arrows, 1, 1, this));
        player2Default.addChoice(new DefaultInputChoice(Settings.language.menu.Gamepad, 1, 2, this));
        player2.addChoice(player2Default);

        MenuChoice player3Default = new MenuChoice(Settings.language.menu.Default, this);
        player3Default.addChoice(new DefaultInputChoice(Settings.language.menu.WSAD, 2, 0, this));
        player3Default.addChoice(new DefaultInputChoice(Settings.language.menu.Arrows, 2, 1, this));
        player3Default.addChoice(new DefaultInputChoice(Settings.language.menu.Gamepad, 2, 2, this));
        player3.addChoice(player3Default);

        MenuChoice player4Default = new MenuChoice(Settings.language.menu.Default, this);
        player4Default.addChoice(new DefaultInputChoice(Settings.language.menu.WSAD, 3, 0, this));
        player4Default.addChoice(new DefaultInputChoice(Settings.language.menu.Arrows, 3, 1, this));
        player4Default.addChoice(new DefaultInputChoice(Settings.language.menu.Gamepad, 3, 2, this));
        player4.addChoice(player4Default);

//        player2.addChoice(new MapButtonChoice(Settings.language.menu.Actions[3], this, 1, 3));
//        player3.addChoice(new MapButtonChoice(Settings.language.menu.Actions[3], this, 2, 3));
//        player4.addChoice(new MapButtonChoice(Settings.language.menu.Actions[3], this, 3, 3));
        for (i = MyController.MENU_ACTIONS_COUNT; i < Settings.actionsCount; i++) {
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
        if (root.getCurrent() >= positions / 2 + 1 && root.getSize() > maxPositions) {
            shift = root.getCurrent() - positions / 2 + 1;
            if (positions + shift > root.getSize() - 1) {
                shift = root.getSize() - positions;
            }
        }
        TextMaster.startRenderText();
        title.setText(root.getLabel());
        TextMaster.render(title, 0, calculatePosition(positions, line) - (int) ((bigFontSize - fontSize / 2f) * Settings.nativeScale));
        line--;
        if (shift > 0) {
            TextMaster.render(arrowUp, 0, calculatePosition(positions, line));
        }
        line--;
        for (int i = 0; i < positions; i++) {
            root.getChoice(i + shift).getText().setColor(getColor(i + shift));
            if (root.getChoice(i + shift) instanceof GammaChoice || root.getChoice(i + shift) instanceof BrightnessChoice) {
                renderGammaHelper(root.getChoice(i + shift).getText(), calculatePosition(positions, line));
            } else {
                TextMaster.render(root.getChoice(i + shift).getText(), 0, calculatePosition(positions, line));
            }
            line--;
        }
        if (root.getSize() > maxPositions && positions + shift <= root.getSize() - 1) {
            TextMaster.render(arrowDown, 0, calculatePosition(positions, line));
        }
        TextMaster.endRenderText();
    }

    private int calculatePosition(int positions, int line) {
        return (int) (Display.getHeight() - ((1.5 * (2 * line - positions - 1.5)) * fontSize * Settings.nativeScale)) / 2;
    }

    private void renderGammaHelper(TextPiece text, int positions) {
        String temp = text.getTextString();
        TextMaster.renderFirstCharacters(text, 0, positions, temp.length() - 8);
        text.setColor(gammaColor1);
        TextMaster.renderCharacters(text, 0, positions, temp.length() - 8, 1);
        text.setColor(gammaColor2);
        TextMaster.renderCharacters(text, 0, positions, temp.length() - 7, 1);
        text.setColor(gammaColor3);
        TextMaster.renderCharacters(text, 0, positions, temp.length() - 6, 1);
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
