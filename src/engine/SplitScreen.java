/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.Settings;
import game.gameobject.Player;
import game.place.Place;
import game.place.cameras.PlayersCamera;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class SplitScreen {

    private static final setSplitType[] splits = new setSplitType[4];
    private static final setOrientType[] orients2 = new setOrientType[2];
    private static final setOrientType[] orients3 = new setOrientType[2];
    private static final setPlayerNumber[] players2h = new setPlayerNumber[2];
    private static final setPlayerNumber[] players2v = new setPlayerNumber[2];
    private static final setPlayerNumber[] players3h = new setPlayerNumber[3];
    private static final setPlayerNumber[] players3v = new setPlayerNumber[3];
    private static final setPlayerNumber[] players4 = new setPlayerNumber[4];
    private static final int width3o4 = (Display.getWidth() * 3) >> 2;
    private static final int height3o4 = (Display.getHeight() * 3) >> 2;
    private static final int width2o3 = (Display.getWidth() << 1) / 3;
    private static final int height2o3 = (Display.getHeight() << 1) / 3;
    private static final int width1o2 = Display.getWidth() / 2;
    private static final int height1o2 = Display.getHeight() / 2;

    private SplitScreen() {
    }

    public static void setSplitScreen(Place place, int playersLength, int player) {
        place.singleCamera = false;
        splits[playersLength - 1].setSplit(place, player);
    }

    public static boolean isClose(Place place) {
        switch (place.getPlayersCount()) {
            case 2:
                return isClose2(place);
            case 3:
                return isClose3(place);
            case 4:
                return isClose4(place);
            default:
                return false;
        }
    }

    private static boolean isClose2(Place place) {
        return place.players[0].getMap() == place.players[1].getMap() && FastMath.abs(place.players[0].getX() - place.players[1].getX()) < width3o4 && FastMath.abs(place.players[0].getY() - place.players[1].getY()) < height3o4;
    }

    private static boolean isClose3(Place place) {
        return place.players[0].getMap() == place.players[1].getMap() && place.players[0].getMap() == place.players[2].getMap() && FastMath.abs(place.players[0].getX() - place.players[1].getX()) < width2o3 && FastMath.abs(place.players[0].getY() - place.players[1].getY()) < height2o3 && FastMath.abs(place.players[0].getX() - place.players[2].getX()) < width2o3 && FastMath.abs(place.players[0].getY() - place.players[2].getY()) < height2o3 && FastMath.abs(place.players[1].getX() - place.players[2].getX()) < width2o3 && FastMath.abs(place.players[1].getY() - place.players[2].getY()) < height2o3;
    }

    private static boolean isClose4(Place place) {
        return place.players[0].getMap() == place.players[1].getMap() && place.players[0].getMap() == place.players[2].getMap() && place.players[0].getMap() == place.players[3].getMap() && FastMath.abs(place.players[0].getX() - place.players[1].getX()) < width1o2 && FastMath.abs(place.players[0].getY() - place.players[1].getY()) < height1o2 && FastMath.abs(place.players[0].getX() - place.players[2].getX()) < width1o2 && FastMath.abs(place.players[0].getY() - place.players[2].getY()) < height1o2 && FastMath.abs(place.players[1].getX() - place.players[2].getX()) < width1o2 && FastMath.abs(place.players[1].getY() - place.players[2].getY()) < height1o2 && FastMath.abs(place.players[0].getX() - place.players[3].getX()) < width1o2 && FastMath.abs(place.players[0].getY() - place.players[3].getY()) < height1o2 && FastMath.abs(place.players[1].getX() - place.players[3].getX()) < width1o2 && FastMath.abs(place.players[1].getY() - place.players[3].getY()) < height1o2 && FastMath.abs(place.players[2].getX() - place.players[3].getX()) < width1o2 && FastMath.abs(place.players[2].getY() - place.players[3].getY()) < height1o2;
    }

    private static boolean isFar(Place place) {
        if (!place.singleCamera) {
            for (int p = 0; p < place.playersCount; p++) {
                if (place.players[0].getMap() != place.players[p].getMap() || place.players[p].getX() > place.cameras[place.playersCount - 2].getXEnd() || place.players[p].getX() < place.cameras[place.playersCount - 2].getXStart() || place.players[p].getY() > place.cameras[place.playersCount - 2].getYEnd() || place.players[p].getY() < place.cameras[place.playersCount - 2].getYStart()) {
                    Settings.joinSplitScreen = false;
                    return true;
                }
            }
        }
        return false;
    }

    public static void changeSplitScreenMode2(Place place) {
        if (place.changeSSMode) {
            if (Settings.horizontalSplitScreen) {
                Settings.horizontalSplitScreen = false;
                ((PlayersCamera) ((Player) place.players[0]).getCamera()).reInitialize(4, 2, 1);
                ((PlayersCamera) ((Player) place.players[1]).getCamera()).reInitialize(4, 2, 1);
            } else {
                Settings.horizontalSplitScreen = true;
                ((PlayersCamera) ((Player) place.players[0]).getCamera()).reInitialize(2, 4, 1);
                ((PlayersCamera) ((Player) place.players[1]).getCamera()).reInitialize(2, 4, 1);
            }
            place.changeSSMode = false;
        }
    }

    public static void changeSplitScreenMode3(Place place) {
        if (place.changeSSMode) {
            if (Settings.horizontalSplitScreen) {
                Settings.horizontalSplitScreen = false;
                ((PlayersCamera) ((Player) place.players[0]).getCamera()).reInitialize(4, 2, 2);
                ((PlayersCamera) ((Player) place.players[1]).getCamera()).reInitialize(4, 4, 2);
                ((PlayersCamera) ((Player) place.players[2]).getCamera()).reInitialize(4, 4, 2);
            } else {
                Settings.horizontalSplitScreen = true;
                ((PlayersCamera) ((Player) place.players[0]).getCamera()).reInitialize(2, 4, 2);
                ((PlayersCamera) ((Player) place.players[1]).getCamera()).reInitialize(4, 4, 2);
                ((PlayersCamera) ((Player) place.players[2]).getCamera()).reInitialize(4, 4, 2);
            }
            place.changeSSMode = false;
        }
    }

    public static void initialise() {
        players2h[0] = (Place place) -> {
            glViewport(0, height1o2, Display.getWidth(), height1o2);
            glScissor(0, height1o2, Display.getWidth(), height1o2);
            glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
            place.camXStart = place.camXTStart = 0f;
            place.camYStart = place.camYTStart = -0.5f;
            place.camXEnd = place.camXTEnd = 1f;
            place.camYEnd = place.camYTEnd = 0.5f;
        };
        players2h[1] = (Place place) -> {
            glViewport(0, 0, Display.getWidth(), height1o2);
            glScissor(0, 0, Display.getWidth(), height1o2);
            place.camXStart = place.camYStart = place.camXTStart = place.camYTStart = 0f;
            place.camXEnd = place.camXTEnd = 1f;
            place.camYEnd = place.camYTEnd = 0.5f;
        };
        players2v[0] = (Place place) -> {
            glViewport(0, 0, width1o2, Display.getHeight());
            glScissor(0, 0, width1o2, Display.getHeight());
            glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
            place.camXStart = place.camXTStart = place.camYStart = place.camYTStart = 0f;
            place.camXEnd = place.camXTEnd = 0.5f;
            place.camYEnd = place.camYTEnd = 1f;
        };
        players2v[1] = (Place place) -> {
            glViewport(width1o2, 0, width1o2, Display.getHeight());
            glScissor(width1o2, 0, width1o2, Display.getHeight());
            place.camXTStart = place.camXEnd = 0.5f;
            place.camXTEnd = place.camYTEnd = place.camYEnd = 1f;
            place.camXStart = place.camYStart = place.camYTStart = 0f;
        };
        orients2[0] = (Place place, int player) -> {
            place.splitScreenMode = 1;
            players2h[player].setPlayer(place);
        };
        orients2[1] = (Place place, int player) -> {
            place.splitScreenMode = 2;
            players2v[player].setPlayer(place);
        };
        splits[0] = (Place place, int player) -> {
            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glScissor(0, 0, Display.getWidth(), Display.getHeight());
            place.camXStart = place.camYStart = place.camXTStart = place.camYTStart = 0f;
            place.camXEnd = place.camYEnd = place.camXTEnd = place.camYTEnd = 1f;
        };
        splits[1] = (Place place, int player) -> {
            if (!Settings.joinSplitScreen || isFar(place)) {
                changeSplitScreenMode2(place);
                orients2[Settings.horizontalSplitScreen ? 0 : 1].setOrientation(place, player);
            } else {
                glScissor(0, 0, Display.getWidth(), Display.getHeight());
                Place.currentCamera = place.cameras[0];
                place.splitScreenMode = 0;
                place.camXStart = place.camYStart = place.camXTStart = place.camYTStart = 0f;
                place.camXEnd = place.camYEnd = place.camXTEnd = place.camYTEnd = 1f;
                place.singleCamera = true;
            }
        };
        initialize2();
        initialize3();
    }

    private static void initialize2() {
        players3h[0] = (Place place) -> {
            glViewport(0, height1o2, Display.getWidth(), height1o2);
            glScissor(0, height1o2, Display.getWidth(), height1o2);
            glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
            place.camXStart = place.camXTStart = 0f;
            place.camYStart = place.camYTStart = -0.5f;
            place.camXEnd = place.camXTEnd = 1f;
            place.camYEnd = place.camYTEnd = 0.5f;
        };
        players3h[1] = (Place place) -> {
            glViewport(0, 0, width1o2, height1o2);
            glScissor(0, 0, width1o2, height1o2);
            glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
            place.camYStart = place.camYTStart = place.camXStart = place.camXTStart = 0f;
            place.camXEnd = place.camXTEnd = place.camYEnd = place.camYTEnd = 0.5f;
        };
        players3h[2] = (Place place) -> {
            glViewport(width1o2, 0, width1o2, height1o2);
            glScissor(width1o2, 0, width1o2, height1o2);
            place.camXStart = place.camYStart = place.camYTStart = 0f;
            place.camXTEnd = 1f;
            place.camYEnd = place.camYTEnd = place.camXTStart = place.camXEnd = 0.5f;
        };
        players3v[0] = (Place place) -> {
            glViewport(0, 0, width1o2, Display.getHeight());
            glScissor(0, 0, width1o2, Display.getHeight());
            glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
            place.camXStart = place.camXTStart = place.camYStart = place.camYTStart = 0f;
            place.camXEnd = place.camXTEnd = 0.5f;
            place.camYEnd = place.camYTEnd = 1f;
        };
        players3v[1] = (Place place) -> {
            glViewport(width1o2, height1o2, width1o2, height1o2);
            glScissor(width1o2, height1o2, width1o2, height1o2);
            glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
            place.camXTStart = place.camXEnd = place.camYTStart = place.camYEnd = 0.5f;
            place.camXTEnd = place.camYTEnd = 1f;
            place.camXStart = place.camYStart = 0f;
        };
        players3v[2] = (Place place) -> {
            glViewport(width1o2, 0, width1o2, height1o2);
            glScissor(width1o2, 0, width1o2, height1o2);
            place.camXStart = place.camYStart = place.camYTStart = 0f;
            place.camXTEnd = 1f;
            place.camYEnd = place.camYTEnd = place.camXTStart = place.camXEnd = 0.5f;
        };
        orients3[0] = (Place place, int player) -> {
            place.splitScreenMode = 3;
            players3h[player].setPlayer(place);
        };
        orients3[1] = (Place place, int player) -> {
            place.splitScreenMode = 4;
            players3v[player].setPlayer(place);
        };
        splits[2] = (Place place, int p) -> {
            if (!Settings.joinSplitScreen || isFar(place)) {
                changeSplitScreenMode3(place);
                orients3[Settings.horizontalSplitScreen ? 0 : 1].setOrientation(place, p);
            } else {
                glScissor(0, 0, Display.getWidth(), Display.getHeight());
                Place.currentCamera = place.cameras[1];
                place.splitScreenMode = 0;
                place.camXStart = place.camYStart = place.camXTStart = place.camYTStart = 0f;
                place.camXEnd = place.camYEnd = place.camXTEnd = place.camYTEnd = 1f;
                place.singleCamera = true;
            }
        };
    }

    private static void initialize3() {
        players4[0] = (Place place) -> {
            glViewport(0, height1o2, width1o2, height1o2);
            glScissor(0, height1o2, width1o2, height1o2);
            glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
            place.camXStart = place.camXTStart = 0f;
            place.camYStart = place.camYTStart = -0.5f;
            place.camXEnd = place.camXTEnd = place.camYEnd = place.camYTEnd = 0.5f;
        };
        players4[1] = (Place place) -> {
            glViewport(width1o2, height1o2, width1o2, height1o2);
            glScissor(width1o2, height1o2, width1o2, height1o2);
            place.camXTStart = place.camXEnd = place.camYTStart = place.camYEnd = 0.5f;
            place.camXTEnd = place.camYTEnd = 1f;
            place.camXStart = place.camYStart = 0f;
        };
        players4[2] = (Place place) -> {
            glViewport(0, 0, width1o2, height1o2);
            glScissor(0, 0, width1o2, height1o2);
            place.camYStart = place.camYTStart = place.camXStart = place.camXTStart = 0f;
            place.camXEnd = place.camXTEnd = place.camYEnd = place.camYTEnd = 0.5f;
        };
        players4[3] = (Place place) -> {
            glViewport(width1o2, 0, width1o2, height1o2);
            glScissor(width1o2, 0, width1o2, height1o2);
            place.camXStart = place.camYStart = place.camYTStart = 0f;
            place.camXTEnd = 1f;
            place.camYEnd = place.camYTEnd = place.camXTStart = place.camXEnd = 0.5f;
        };
        splits[3] = (Place place, int player) -> {
            if (!Settings.joinSplitScreen || isFar(place)) {
                place.splitScreenMode = 5;
                players4[player].setPlayer(place);
            } else {
                glScissor(0, 0, Display.getWidth(), Display.getHeight());
                Place.currentCamera = place.cameras[2];
                place.splitScreenMode = 0;
                place.camXStart = place.camYStart = place.camXTStart = place.camYTStart = 0f;
                place.camXEnd = place.camYEnd = place.camXTEnd = place.camYTEnd = 1f;
                place.singleCamera = true;
            }
        };
    }

    private interface setSplitType {

        void setSplit(Place place, int player);
    }

    private interface setOrientType {

        void setOrientation(Place place, int player);
    }

    private interface setPlayerNumber {

        void setPlayer(Place player);
    }
}
