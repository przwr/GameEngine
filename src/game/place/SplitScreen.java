/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.gameobject.GameObject;
import game.myGame.MyPlayer;
import game.place.cameras.PlayersCamera;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 *
 * @author przemek
 */
public class SplitScreen {

    public static void setSplitScreen(Place pl, GameObject player) {
        if ((pl.players.length == 2 && !SplitScreen.isClose2(pl))) {
            if (pl.changeSSMode) {
                changeSSMode2(pl);
            }
            pl.cam = (((MyPlayer) player).getCam());
            if (pl.settings.hSplitScreen) {
                pl.ssMode = 1;
                if (player == pl.players[0]) {
                    glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                    glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    pl.camYStart = pl.camXStart = 0f;
                } else {
                    glViewport(0, 0, Display.getWidth(), Display.getHeight() / 2);
                    pl.camYStart = 0f;
                    pl.camYStart = -0.5f;
                }
            } else {
                pl.ssMode = 2;
                if (player == pl.players[0]) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                    glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    pl.camXStart = pl.camYStart = 0f;
                } else {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight());
                    pl.camXStart = 0.5f;
                    pl.camYStart = 0f;
                }
            }
        } else if (pl.players.length == 2 && SplitScreen.isClose2(pl)) {
            pl.cam = pl.camfor2;
            pl.ssMode = 0;
            if (player == pl.players[0]) {
                pl.camYStart = pl.camXStart = 0f;
            }
        } else if (pl.players.length == 3 && !SplitScreen.isClose3(pl)) {
            if (pl.changeSSMode) {
                changeSSMode3(pl);
            }
            pl.cam = (((MyPlayer) player).getCam());
            if (pl.settings.hSplitScreen) {
                pl.ssMode = 3;
                if (player == pl.players[0]) {
                    glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                    glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    pl.camXStart = pl.camYStart = 0f;
                } else if (player == pl.players[1]) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    pl.camXStart = 0f;
                    pl.camYStart = -0.5f;
                } else if (player == pl.players[2]) {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    pl.camXStart = 0.5f;
                    pl.camYStart = -0.5f;
                }
            } else {
                pl.ssMode = 4;
                if (player == pl.players[0]) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                    glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    pl.camXStart = pl.camYStart = 0f;
                } else if (player == pl.players[1]) {
                    glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    pl.camXStart = 0.5f;
                    pl.camYStart = 0f;
                } else if (player == pl.players[2]) {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    pl.camXStart = 0.5f;
                    pl.camYStart = -0.5f;
                }
            }
        } else if (pl.players.length == 3 && SplitScreen.isClose3(pl)) {
            pl.cam = pl.camfor3;
            pl.ssMode = 0;
            if (player == pl.players[0]) {
                pl.camYStart = pl.camXStart = 0f;
            }
        } else if (pl.players.length == 4 && !SplitScreen.isClose4(pl)) {
            pl.ssMode = 5;
            if (player == pl.players[0]) {
                glViewport(0, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
                pl.camXStart = pl.camYStart = 0.0f;
            } else if (player == pl.players[1]) {
                glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                pl.camXStart = 0.5f;
                pl.camYStart = 0f;
            } else if (player == pl.players[2]) {
                glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                pl.camXStart = 0f;
                pl.camYStart = -0.5f;
            } else if (player == pl.players[3]) {
                glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                pl.camXStart = 0.5f;
                pl.camYStart = -0.5f;
            }
        } else if (pl.players.length == 4 && SplitScreen.isClose4(pl)) {
            pl.cam = pl.camfor4;
            pl.ssMode = 0;
            if (player == pl.players[0]) {
                pl.camYStart = pl.camXStart = 0f;
            }
        }
    }

    public static boolean isClose2(Place pl) {
        if (pl.settings.joinSS) {
            if (Math.abs(pl.players[0].getMidX() - pl.players[1].getMidX()) < Display.getWidth() / 2 && Math.abs(pl.players[0].getMidY() - pl.players[1].getMidY()) < Display.getHeight() / 2) {
                pl.isSplit = false;
                return true;
            } else if (!pl.isSplit) {
                if (Math.abs(pl.players[0].getMidX() - pl.players[1].getMidX()) < Math.abs(pl.players[0].getMidY() - pl.players[1].getMidY())) {
                    pl.settings.hSplitScreen = true;
                    swampY(pl);
                } else {
                    pl.settings.hSplitScreen = false;
                    swampX(pl);
                }
                pl.isSplit = true;
            } else if (pl.changeSSMode) {
                if (pl.settings.hSplitScreen) {
                    pl.settings.hSplitScreen = false;
                    swampX(pl);
                } else {
                    pl.settings.hSplitScreen = true;
                    swampY(pl);
                }
                pl.changeSSMode = false;
            }
        }
        return false;
    }

    public static boolean isClose3(Place pl) {
        if (pl.settings.joinSS) {
            int dW = 2 * Display.getWidth() / 3;
            int dH = 2 * Display.getHeight() / 3;
            if (Math.abs(pl.players[0].getMidX() - pl.players[1].getMidX()) < dW && Math.abs(pl.players[0].getMidY() - pl.players[1].getMidY()) < dH && Math.abs(pl.players[0].getMidX() - pl.players[2].getMidX()) < dW && Math.abs(pl.players[0].getMidY() - pl.players[2].getMidY()) < dH && Math.abs(pl.players[1].getMidX() - pl.players[2].getMidX()) < dW && Math.abs(pl.players[1].getMidY() - pl.players[2].getMidY()) < dH) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClose4(Place pl) {
        if (pl.settings.joinSS) {
            int dW = 2 * Display.getWidth() / 3;
            int dH = 2 * Display.getHeight() / 3;
            if (Math.abs(pl.players[0].getMidX() - pl.players[1].getMidX()) < dW && Math.abs(pl.players[0].getMidY() - pl.players[1].getMidY()) < dH && Math.abs(pl.players[0].getMidX() - pl.players[2].getMidX()) < dW && Math.abs(pl.players[0].getMidY() - pl.players[2].getMidY()) < dH && Math.abs(pl.players[1].getMidX() - pl.players[2].getMidX()) < dW && Math.abs(pl.players[1].getMidY() - pl.players[2].getMidY()) < dH && Math.abs(pl.players[0].getMidX() - pl.players[3].getMidX()) < dW && Math.abs(pl.players[0].getMidY() - pl.players[3].getMidY()) < dH && Math.abs(pl.players[1].getMidX() - pl.players[3].getMidX()) < dW && Math.abs(pl.players[1].getMidY() - pl.players[3].getMidY()) < dH && Math.abs(pl.players[2].getMidX() - pl.players[3].getMidX()) < dW && Math.abs(pl.players[2].getMidY() - pl.players[3].getMidY()) < dH) {
                return true;
            }
        }
        return false;
    }

    public static void changeSSMode2(Place pl) {
        if (pl.settings.hSplitScreen) {
            pl.settings.hSplitScreen = false;
            ((PlayersCamera) ((MyPlayer) pl.players[0]).getCam()).init(4, 2, 0);
            ((PlayersCamera) ((MyPlayer) pl.players[1]).getCam()).init(4, 2, 1);
        } else {
            pl.settings.hSplitScreen = true;
            ((PlayersCamera) ((MyPlayer) pl.players[0]).getCam()).init(2, 4, 0);
            ((PlayersCamera) ((MyPlayer) pl.players[1]).getCam()).init(2, 4, 1);
        }
        pl.changeSSMode = false;
    }

    public static void changeSSMode3(Place pl) {
        if (pl.settings.hSplitScreen) {
            pl.settings.hSplitScreen = false;
            ((PlayersCamera) ((MyPlayer) pl.players[0]).getCam()).init(4, 2, 0);
            ((PlayersCamera) ((MyPlayer) pl.players[1]).getCam()).init(4, 4, 1);
            ((PlayersCamera) ((MyPlayer) pl.players[2]).getCam()).init(4, 4, 2);
        } else {
            pl.settings.hSplitScreen = true;
            ((PlayersCamera) ((MyPlayer) pl.players[0]).getCam()).init(2, 4, 0);
            ((PlayersCamera) ((MyPlayer) pl.players[1]).getCam()).init(4, 4, 1);
            ((PlayersCamera) ((MyPlayer) pl.players[2]).getCam()).init(4, 4, 2);
        }
        pl.changeSSMode = false;
    }

    private static void swampY(Place pl) {
        if (pl.players[0].getMidY() > pl.players[1].getMidY()) {
            GameObject temp = pl.players[0];
            pl.players[0] = pl.players[1];
            pl.players[1] = temp;
        }
        ((PlayersCamera) ((MyPlayer) pl.players[0]).getCam()).init(2, 4, 0);
        ((PlayersCamera) ((MyPlayer) pl.players[1]).getCam()).init(2, 4, 1);
    }

    private static void swampX(Place pl) {
        if (pl.players[0].getMidX() > pl.players[1].getMidX()) {
            GameObject temp = pl.players[0];
            pl.players[0] = pl.players[1];
            pl.players[1] = temp;
        }
        ((PlayersCamera) ((MyPlayer) pl.players[0]).getCam()).init(4, 2, 0);
        ((PlayersCamera) ((MyPlayer) pl.players[1]).getCam()).init(4, 2, 1);
    }
}
