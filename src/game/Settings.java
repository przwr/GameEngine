/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import engine.Methods;
import engine.SoundBase;
import game.gameobject.Player;
import static game.place.fbo.FrameBufferObject.ARB;
import static game.place.fbo.FrameBufferObject.EXT;
import static game.place.fbo.FrameBufferObject.NATIVE;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBTextureMultisample;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.glGetInteger;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLContext;

/**
 *
 * @author przemek
 */
public class Settings {

	public static final int MIN_WIDTH = 1024, MIN_HEIGHT = 768, MAX_WIDTH = 1920, MAX_HEIGHT = 1200;
	public static final DisplayMode display = Display.getDesktopDisplayMode();
	public static final int depth = display.getBitsPerPixel();
	public static DisplayMode[] modesTemp;
	public static DisplayMode[] modes;
	public static int modesCount;
	public static int currentMode;
	public static boolean fullScreen;
	public static boolean horizontalSplitScreen;
	public static boolean joinSplitScreen;
	public static int playersCount = 1;
	public static float volume = 0.5f;
	public static SoundBase sounds;
	public static int resolutionWidth;
	public static int resolutionHeight;
	public static int frequency;
	public static boolean verticalSynchronization;
	public static int samplesCount = 0;
	public static String languageName;
	public static Language language;
	public static ArrayList<Language> languages = new ArrayList<>();
	public static int actionsCount;
	public static Player[] players;
	public static Controller[] controllers;
	public static int maxSamples;
	public static int supportedFrameBufferObjectVersion;
	public static boolean multiSampleSupported;
	public static boolean shadowOff;
	public static boolean scaled;
	public static double scale;
	public static double nativeScale;
	public static String serverIP = "127.0.0.1";

	public static void initialize() {
		try {
			modesTemp = Display.getAvailableDisplayModes();
		} catch (LWJGLException ex) {
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
		}
		DisplayMode temp;
		if (modesTemp[0].getWidth() >= MIN_WIDTH && modesTemp[0].getWidth() <= MAX_WIDTH
				&& modesTemp[0].getHeight() >= MIN_HEIGHT && modesTemp[0].getHeight() <= MAX_HEIGHT && modesTemp[0].getBitsPerPixel() == depth) {
			modesCount++;
		}
		int i, j;
		for (i = 1; i < modesTemp.length; i++) {
			if (modesTemp[i].getWidth() >= MIN_WIDTH && modesTemp[i].getWidth() <= MAX_WIDTH && modesTemp[i].getHeight() >= MIN_HEIGHT && modesTemp[i].getHeight() <= MAX_HEIGHT && modesTemp[i].getBitsPerPixel() == depth) {
				modesCount++;
			}
			temp = modesTemp[i];
			for (j = i; j > 0 && isBigger(modesTemp[j - 1], temp); j--) {
				modesTemp[j] = modesTemp[j - 1];
			}
			modesTemp[j] = temp;
		}
		modes = new DisplayMode[modesCount];
		i = 0;
		for (DisplayMode mode : modesTemp) {
			if (mode.getWidth() >= MIN_WIDTH && mode.getWidth() <= MAX_WIDTH && mode.getHeight() >= MIN_HEIGHT && mode.getHeight() <= MAX_HEIGHT && mode.getBitsPerPixel() == depth) {
				modes[i++] = mode;
			}
		}
		resolutionWidth = modes[0].getWidth();
		resolutionHeight = modes[0].getHeight();
		frequency = modes[0].getFrequency();
		languages.add(new LangPL());
		languages.add(new LangENG());
		language = languages.get(0);
		languageName = language.lang;
	}

	private static boolean isBigger(DisplayMode checked, DisplayMode temp) {
		if (checked.getBitsPerPixel() > temp.getBitsPerPixel()) {
			return true;
		} else if (checked.getWidth() > temp.getWidth()) {
			return true;
		} else if (checked.getWidth() == temp.getWidth() && checked.getHeight() > temp.getHeight()) {
			return true;
		} else {
			return checked.getWidth() == temp.getWidth() && checked.getHeight() == temp.getHeight() && checked.getFrequency() > temp.getFrequency();
		}
	}

	public static void calculateScale() {
		scale = ((int) ((resolutionHeight / 1024d / 0.25d)) * 0.25d) >= 1 ? 1 : (int) ((resolutionHeight / 1024d / 0.25d)) * 0.25d;
		nativeScale = scale;
		if (scale != 1f) {
			scaled = true;
		}
	}

	public static void update(int actionsCount, Player[] players, Controller[] controllers) {
		Settings.actionsCount = actionsCount;
		Settings.players = players;
		Settings.controllers = controllers;
		try {
			GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, 10, 10, false);
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
			supportedFrameBufferObjectVersion = NATIVE;
			multiSampleSupported = true;
			maxSamples = glGetInteger(GL30.GL_MAX_SAMPLES) / 2;
			maxSamples = maxSamples > 8 ? 8 : maxSamples;
			samplesCount = (samplesCount > maxSamples) ? maxSamples : samplesCount;
		} catch (Exception exception) {
			if (GLContext.getCapabilities().GL_ARB_framebuffer_object) {
				supportedFrameBufferObjectVersion = ARB;
				try {
					ARBTextureMultisample.glTexImage2DMultisample(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, 10, 10, false);
					ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
					multiSampleSupported = true;
					maxSamples = glGetInteger(GL30.GL_MAX_SAMPLES) / 2;
					maxSamples = maxSamples > 8 ? 8 : maxSamples;
					samplesCount = (samplesCount > maxSamples) ? maxSamples : samplesCount;
				} catch (Exception exception2) {
					multiSampleSupported = false;
				}
			} else if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
				supportedFrameBufferObjectVersion = EXT;
				multiSampleSupported = false;
			} else {
				Methods.javaError(language.menu.FBOError);
			}
		}
	}
}
