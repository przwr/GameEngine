/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.gameobject.Player;
import game.place.cameras.PlayersCamera;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 *
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
    private static final int heigth3o4 = (Display.getHeight() * 3) >> 2;
    private static final int width2o3 = (Display.getWidth() << 1) / 3;
    private static final int heigth2o3 = (Display.getHeight() << 1) / 3;
    private static final int width1o2 = Display.getWidth() / 2;
    private static final int heigth1o2 = Display.getHeight() / 2;

    public static void setSplitScreen(Place pl, int playersLength, int p) {
        pl.singleCam = false;
        splits[playersLength - 1].setSplit(pl, p);
    }

    public static boolean isClose(Place pl) {
        switch (pl.getPlayersLenght()) {
            case 2:
                return isClose2(pl);
            case 3:
                return isClose3(pl);
            case 4:
                return isClose4(pl);
            default:
                return false;
        }
    }

    public static boolean isClose2(Place pl) {
        return pl.players[0].getMap() == pl.players[1].getMap() && FastMath.abs(pl.players[0].getX() - pl.players[1].getX()) < width3o4 && FastMath.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth3o4;
    }

    public static boolean isClose3(Place pl) {
        return pl.players[0].getMap() == pl.players[1].getMap() && pl.players[0].getMap() == pl.players[2].getMap() && FastMath.abs(pl.players[0].getX() - pl.players[1].getX()) < width2o3 && FastMath.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth2o3 && FastMath.abs(pl.players[0].getX() - pl.players[2].getX()) < width2o3 && FastMath.abs(pl.players[0].getY() - pl.players[2].getY()) < heigth2o3 && FastMath.abs(pl.players[1].getX() - pl.players[2].getX()) < width2o3 && FastMath.abs(pl.players[1].getY() - pl.players[2].getY()) < heigth2o3;
    }

    public static boolean isClose4(Place pl) {
        return pl.players[0].getMap() == pl.players[1].getMap() && pl.players[0].getMap() == pl.players[2].getMap() && pl.players[0].getMap() == pl.players[3].getMap() && FastMath.abs(pl.players[0].getX() - pl.players[1].getX()) < width1o2 && FastMath.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth1o2 && FastMath.abs(pl.players[0].getX() - pl.players[2].getX()) < width1o2 && FastMath.abs(pl.players[0].getY() - pl.players[2].getY()) < heigth1o2 && FastMath.abs(pl.players[1].getX() - pl.players[2].getX()) < width1o2 && FastMath.abs(pl.players[1].getY() - pl.players[2].getY()) < heigth1o2 && FastMath.abs(pl.players[0].getX() - pl.players[3].getX()) < width1o2 && FastMath.abs(pl.players[0].getY() - pl.players[3].getY()) < heigth1o2 && FastMath.abs(pl.players[1].getX() - pl.players[3].getX()) < width1o2 && FastMath.abs(pl.players[1].getY() - pl.players[3].getY()) < heigth1o2 && FastMath.abs(pl.players[2].getX() - pl.players[3].getX()) < width1o2 && FastMath.abs(pl.players[2].getY() - pl.players[3].getY()) < heigth1o2;
    }

    public static boolean isFar(Place pl) {
        if (!pl.singleCam) {
            for (int p = 0; p < pl.playersLength; p++) {
                if (pl.players[0].getMap() != pl.players[p].getMap() || pl.players[p].getX() > pl.cams[pl.playersLength - 2].getEX() || pl.players[p].getX() < pl.cams[pl.playersLength - 2].getSX() || pl.players[p].getY() > pl.cams[pl.playersLength - 2].getEY() || pl.players[p].getY() < pl.cams[pl.playersLength - 2].getSY()) {
                    pl.settings.joinSS = false;
                    return true;
                }
            }
        }
        return false;
    }

    public static void changeSSMode2(Place pl) {
        if (pl.changeSSMode) {
            if (pl.settings.hSplitScreen) {
                pl.settings.hSplitScreen = false;
                ((PlayersCamera) ((Player) pl.players[0]).getCam()).reInitialize(4, 2);
                ((PlayersCamera) ((Player) pl.players[1]).getCam()).reInitialize(4, 2);
            } else {
                pl.settings.hSplitScreen = true;
                ((PlayersCamera) ((Player) pl.players[0]).getCam()).reInitialize(2, 4);
                ((PlayersCamera) ((Player) pl.players[1]).getCam()).reInitialize(2, 4);
            }
            pl.changeSSMode = false;
        }
    }

    public static void changeSSMode3(Place pl) {
        if (pl.changeSSMode) {
            if (pl.settings.hSplitScreen) {
                pl.settings.hSplitScreen = false;
                ((PlayersCamera) ((Player) pl.players[0]).getCam()).reInitialize(4, 2);
                ((PlayersCamera) ((Player) pl.players[1]).getCam()).reInitialize(4, 4);
                ((PlayersCamera) ((Player) pl.players[2]).getCam()).reInitialize(4, 4);
            } else {
                pl.settings.hSplitScreen = true;
                ((PlayersCamera) ((Player) pl.players[0]).getCam()).reInitialize(2, 4);
                ((PlayersCamera) ((Player) pl.players[1]).getCam()).reInitialize(4, 4);
                ((PlayersCamera) ((Player) pl.players[2]).getCam()).reInitialize(4, 4);
            }
            pl.changeSSMode = false;
        }
    }

    public static void initialzie() {
        players2h[0] = (Place pl) -> {
            glViewport(0, heigth1o2, Display.getWidth(), heigth1o2);
            glScissor(0, heigth1o2, Display.getWidth(), heigth1o2);
            glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
            pl.camXStart = pl.camXTStart = 0f;
            pl.camYStart = pl.camYTStart = -0.5f;
            pl.camXEnd = pl.camXTEnd = 1f;
            pl.camYEnd = pl.camYTEnd = 0.5f;
        };
        players2h[1] = (Place pl) -> {
            glViewport(0, 0, Display.getWidth(), heigth1o2);
            glScissor(0, 0, Display.getWidth(), heigth1o2);
            pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camXTEnd = 1f;
            pl.camYEnd = pl.camYTEnd = 0.5f;
        };
        players2v[0] = (Place pl) -> {
            glViewport(0, 0, width1o2, Display.getHeight());
            glScissor(0, 0, width1o2, Display.getHeight());
            glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
            pl.camXStart = pl.camXTStart = pl.camYStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camXTEnd = 0.5f;
            pl.camYEnd = pl.camYTEnd = 1f;
        };
        players2v[1] = (Place pl) -> {
            glViewport(width1o2, 0, width1o2, Display.getHeight());
            glScissor(width1o2, 0, width1o2, Display.getHeight());
            pl.camXTStart = pl.camXEnd = 0.5f;
            pl.camXTEnd = pl.camYTEnd = pl.camYEnd = 1f;
            pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
        };
        orients2[0] = (Place pl, int p) -> {
            pl.ssMode = 1;
            players2h[p].setPlayer(pl);
        };
        orients2[1] = (Place pl, int p) -> {
            pl.ssMode = 2;
            players2v[p].setPlayer(pl);
        };
        splits[0] = (Place pl, int p) -> {
            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glScissor(0, 0, Display.getWidth(), Display.getHeight());
            pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
        };
        splits[1] = (Place pl, int p) -> {
            if (!pl.settings.joinSS || isFar(pl)) {
                changeSSMode2(pl);
                orients2[pl.settings.hSplitScreen ? 0 : 1].setOrient(pl, p);
            } else {
                glScissor(0, 0, Display.getWidth(), Display.getHeight());
                pl.cam = pl.cams[0];
                pl.ssMode = 0;
                pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
                pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
                pl.singleCam = true;
            }
        };
        initialzie2();
        initialize3();
    }

    private static void initialzie2() {
        players3h[0] = (Place pl) -> {
            glViewport(0, heigth1o2, Display.getWidth(), heigth1o2);
            glScissor(0, heigth1o2, Display.getWidth(), heigth1o2);
            glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
            pl.camXStart = pl.camXTStart = 0f;
            pl.camYStart = pl.camYTStart = -0.5f;
            pl.camXEnd = pl.camXTEnd = 1f;
            pl.camYEnd = pl.camYTEnd = 0.5f;
        };
        players3h[1] = (Place pl) -> {
            glViewport(0, 0, width1o2, heigth1o2);
            glScissor(0, 0, width1o2, heigth1o2);
            glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
            pl.camYStart = pl.camYTStart = pl.camXStart = pl.camXTStart = 0f;
            pl.camXEnd = pl.camXTEnd = pl.camYEnd = pl.camYTEnd = 0.5f;
        };
        players3h[2] = (Place pl) -> {
            glViewport(width1o2, 0, width1o2, heigth1o2);
            glScissor(width1o2, 0, width1o2, heigth1o2);
            pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
            pl.camXTEnd = 1f;
            pl.camYEnd = pl.camYTEnd = pl.camXTStart = pl.camXEnd = 0.5f;
        };
        players3v[0] = (Place pl) -> {
            glViewport(0, 0, width1o2, Display.getHeight());
            glScissor(0, 0, width1o2, Display.getHeight());
            glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
            pl.camXStart = pl.camXTStart = pl.camYStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camXTEnd = 0.5f;
            pl.camYEnd = pl.camYTEnd = 1f;
        };
        players3v[1] = (Place pl) -> {
            glViewport(width1o2, heigth1o2, width1o2, heigth1o2);
            glScissor(width1o2, heigth1o2, width1o2, heigth1o2);
            glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
            pl.camXTStart = pl.camXEnd = pl.camYTStart = pl.camYEnd = 0.5f;
            pl.camXTEnd = pl.camYTEnd = 1f;
            pl.camXStart = pl.camYStart = 0f;
        };
        players3v[2] = (Place pl) -> {
            glViewport(width1o2, 0, width1o2, heigth1o2);
            glScissor(width1o2, 0, width1o2, heigth1o2);
            pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
            pl.camXTEnd = 1f;
            pl.camYEnd = pl.camYTEnd = pl.camXTStart = pl.camXEnd = 0.5f;
        };
        orients3[0] = (Place pl, int p) -> {
            pl.ssMode = 3;
            players3h[p].setPlayer(pl);
        };
        orients3[1] = (Place pl, int p) -> {
            pl.ssMode = 4;
            players3v[p].setPlayer(pl);
        };
        splits[2] = (Place pl, int p) -> {
            if (!pl.settings.joinSS || isFar(pl)) {
                changeSSMode3(pl);
                orients3[pl.settings.hSplitScreen ? 0 : 1].setOrient(pl, p);
            } else {
                glScissor(0, 0, Display.getWidth(), Display.getHeight());
                pl.cam = pl.cams[1];
                pl.ssMode = 0;
                pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
                pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
                pl.singleCam = true;
            }
        };
    }

    private static void initialize3() {
        players4[0] = (Place pl) -> {
            glViewport(0, heigth1o2, width1o2, heigth1o2);
            glScissor(0, heigth1o2, width1o2, heigth1o2);
            glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
            pl.camXStart = pl.camXTStart = 0f;
            pl.camYStart = pl.camYTStart = -0.5f;
            pl.camXEnd = pl.camXTEnd = pl.camYEnd = pl.camYTEnd = 0.5f;
        };
        players4[1] = (Place pl) -> {
            glViewport(width1o2, heigth1o2, width1o2, heigth1o2);
            glScissor(width1o2, heigth1o2, width1o2, heigth1o2);
            pl.camXTStart = pl.camXEnd = pl.camYTStart = pl.camYEnd = 0.5f;
            pl.camXTEnd = pl.camYTEnd = 1f;
            pl.camXStart = pl.camYStart = 0f;
        };
        players4[2] = (Place pl) -> {
            glViewport(0, 0, width1o2, heigth1o2);
            glScissor(0, 0, width1o2, heigth1o2);
            pl.camYStart = pl.camYTStart = pl.camXStart = pl.camXTStart = 0f;
            pl.camXEnd = pl.camXTEnd = pl.camYEnd = pl.camYTEnd = 0.5f;
        };
        players4[3] = (Place pl) -> {
            glViewport(width1o2, 0, width1o2, heigth1o2);
            glScissor(width1o2, 0, width1o2, heigth1o2);
            pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
            pl.camXTEnd = 1f;
            pl.camYEnd = pl.camYTEnd = pl.camXTStart = pl.camXEnd = 0.5f;
        };
        splits[3] = (Place pl, int p) -> {
            if (!pl.settings.joinSS || isFar(pl)) {
                pl.ssMode = 5;
                players4[p].setPlayer(pl);
            } else {
                glScissor(0, 0, Display.getWidth(), Display.getHeight());
                pl.cam = pl.cams[2];
                pl.ssMode = 0;
                pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
                pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
                pl.singleCam = true;
            }
        };
    }

    private interface setSplitType {

        void setSplit(Place pl, int p);
    }

    private interface setOrientType {

        void setOrient(Place pl, int p);
    }

    private interface setPlayerNumber {

        void setPlayer(Place pl);
    }

    private SplitScreen() {
    }
}
