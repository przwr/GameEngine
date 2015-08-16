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
import game.menu.MenuChoice;
import game.menu.Menu;
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

	private static int position;
	private final FontHandler smallFont, bigFont;
	private final Color color;

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
		StartChoice start = new StartChoice(Settings.language.menu.Start, this);
		StartLocalGameChoice startLocalGame = new StartLocalGameChoice(Settings.language.menu.LocalGame, this);
		MenuChoice onlineGameSettings = new MenuChoice(Settings.language.menu.OnlineGame, this);
		start.addChoice(startLocalGame);
		start.addChoice(onlineGameSettings);
		MenuChoice options = new MenuChoice(Settings.language.menu.Options, this);
		MenuChoice exit = new ExitChoice(Settings.language.menu.Quit, this);
		root.addChoice(start);
		root.addChoice(exit);
	}

//    private void generate() {		
//        menus = new MenuOptions[9];
//        generateM0();
//        generateM1();
//        generateM2();
//        generateM3();
//        generateM4();
//        generateM5();
//        generateM6();
//        generateM7();
//        generateM8();
//        this.color = new Color(Color.white);
//        fonts = new FontBase(20);
//        smallFont = fonts.add("Amble-Regular", Methods.roundDouble(Settings.nativeScale * 38));
//        bigFont = fonts.add("Amble-Regular", Methods.roundDouble(Settings.nativeScale * 64));
//        delay = new Delay(25);
//        delay.start();
//    }
//
//    private void generateM0() {
//        menus[0] = new MenuOptions(10, Settings.language.menu.Menu);
//        menus[0].addChoice(new StartChoice(Settings.language.menu.Start, this));
//        menus[0].addChoice(new SettingsChoice(Settings.language.menu.Options, this));
//        menus[0].addChoice(new StopChoice(Settings.language.menu.End, this));
//        menus[0].addChoice(new ExitChoice(Settings.language.menu.Quit, this));
//    }
//	private void generateM1() {
//		menus[1] = new MenuOptions(12, Settings.language.menu.Options);
//		menus[1].addChoice(new PlayersNumberChoice(Settings.language.menu.Number_Of_Players, this));
//		menus[1].addChoice(new SplitScreenChoice(Settings.language.menu.SplitScreen, this));
//		menus[1].addChoice(new JoinSplitScreenChoice(Settings.language.menu.JoinSS, this));
//		menus[1].addChoice(new LanguageChoice(Settings.language.menu.Language, this));
//		menus[1].addChoice(new ControlsChoice(Settings.language.menu.Controls, this));
//		menus[1].addChoice(new VolumeChoice(Settings.language.menu.Volume, this));
//		menus[1].addChoice(new ResolutionChoice(Settings.language.menu.Resolution, this));
//		menus[1].addChoice(new FullScreenChoice(Settings.language.menu.FullScreen, this));
//		menus[1].addChoice(new VerticalSynchronizationChoice(Settings.language.menu.VSync, this));
//		menus[1].addChoice(new ShadowsOffChoice(Settings.language.menu.ShadowOff, this));
//		menus[1].addChoice(new SmoothShadowsChoice(Settings.language.menu.SmoothShadows, this));
//	}
//
//	private void generateM2() {
//		menus[2] = new MenuOptions(10, Settings.language.menu.Controls);
//		menus[2].addChoice(new PlayerControllerChoice(Settings.language.menu.Player1, this));
//		menus[2].addChoice(new PlayerControllerChoice(Settings.language.menu.Player2, this));
//		menus[2].addChoice(new PlayerControllerChoice(Settings.language.menu.Player3, this));
//		menus[2].addChoice(new PlayerControllerChoice(Settings.language.menu.Player4, this));
//	}
//
//	private void generateM3() {
//		menus[3] = new MenuOptions(16, Settings.language.menu.Player1);
//		int i;
//		for (i = 0; i < 4; i++) {
//			menus[3].addChoice(new NotMapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[0], i));
//		}
//		for (; i < Settings.actionsCount; i++) {
//			menus[3].addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[0].playerController, i));
//		}
//	}
//
//	private void generateM4() {
//		menus[4] = new MenuOptions(16, Settings.language.menu.Player2);
//		for (int i = 3; i < Settings.actionsCount; i++) {
//			menus[4].addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[1].playerController, i));
//		}
//	}
//
//	private void generateM5() {
//		menus[5] = new MenuOptions(16, Settings.language.menu.Player3);
//		for (int i = 3; i < Settings.actionsCount; i++) {
//			menus[5].addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[2].playerController, i));
//		}
//	}
//
//	private void generateM6() {
//		menus[6] = new MenuOptions(16, Settings.language.menu.Player4);
//		for (int i = 3; i < Settings.actionsCount; i++) {
//			menus[6].addChoice(new MapButtonChoice(Settings.language.menu.Actions[i], this, Settings.players[3].playerController, i));
//		}
//	}
//
//	private void generateM7() {
//		menus[7] = new MenuOptions(4, Settings.language.menu.Start);
//		menus[7].addChoice(new StartLocalGameChoice(Settings.language.menu.LocalGame, this));
//		menus[7].addChoice(new OnlineGameSettingsChoice(Settings.language.menu.OnlineGame, this));
//	}
//
//	private void generateM8() {
//		menus[8] = new MenuOptions(6, Settings.language.menu.OnlineGame);
//		menus[8].addChoice(new RunServerChoice(Settings.language.menu.RunServer, this));
//		menus[8].addChoice(new JoinServerChoice(Settings.language.menu.JoinServer, this));
//		menus[8].addChoice(new FindServerChoice(Settings.language.menu.FindServer, this));
//		menus[8].addChoice(new ServerIPChoice(Settings.language.menu.ServerIP, this));
//		menus[8].addChoice(new ServerTCPPortChoice(Settings.language.menu.Port, this));
//		menus[8].addChoice(new ServerUDPPortChoice(Settings.language.menu.Port, this));
//	}

	@Override
	public void render() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glScissor(0, 0, Display.getWidth(), Display.getHeight());
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor3f(color.r, color.g, color.b);
		renderText();
	}

	protected void renderText() {
		position = root.getSize() + 1;
		Drawer.renderStringCentered(root.getLabel(), widthHalf / 2, heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1))
				* fonts.getFont(0).getHeight() * 0.7),
				bigFont, new Color(color.r, color.g, color.b));
		position--;
		for (int i = 0; i < root.getSize(); i++) {
			Drawer.renderStringCentered(root.getChoice(i).getLabel(), widthHalf / 2,
					heightHalf / 2 - (int) ((1.5 * position - (root.getSize() + 1)) * fonts.getFont(0).getHeight() * 0.7),
					smallFont, getColor(i));
			position--;
		}
	}

	private Color getColor(int choice) {
		if (choice == root.getCurrent()) {
			return new Color(1f, 1f, 0.5f);
		} else {
			return new Color(1f, 1f, 1f);
		}
	}

}
