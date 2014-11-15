/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.gameobject.Player;
import game.place.cameras.PlayersCamera;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 *
 * @author przemek
 */
public class SplitScreen {

    private static final int width3o4 = (Display.getWidth() * 3) >> 2;
    private static final int heigth3o4 = (Display.getHeight() * 3) >> 2;
    private static final int width2o3 = (Display.getWidth() << 1) / 3;
    private static final int heigth2o3 = (Display.getHeight() << 1) / 3;
    private static final int width1o2 = Display.getWidth() >> 1;
    private static final int heigth1o2 = Display.getHeight() >> 1;

    public static void setSplitScreen(Place pl, int p) {
        pl.singleCam = false;
        if (pl.playersLength == 1) {
            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glScissor(0, 0, Display.getWidth(), Display.getHeight());
            pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
        } else if (pl.playersLength == 2 && (!pl.settings.joinSS || isFar(pl))) {
            if (pl.changeSSMode) {
                changeSSMode2(pl);
            }
            if (pl.settings.hSplitScreen) {
                pl.ssMode = 1;
                if (p == 0) {
                    glViewport(0, heigth1o2, Display.getWidth(), heigth1o2);
                    glScissor(0, heigth1o2, Display.getWidth(), heigth1o2);
                    glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    pl.camXStart = pl.camXTStart = 0f;
                    pl.camYStart = pl.camYTStart = -0.5f;
                    pl.camXEnd = pl.camXTEnd = 1f;
                    pl.camYEnd = pl.camYTEnd = 0.5f;
                } else {
                    glViewport(0, 0, Display.getWidth(), heigth1o2);
                    glScissor(0, 0, Display.getWidth(), heigth1o2);
                    pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
                    pl.camXEnd = pl.camXTEnd = 1f;
                    pl.camYEnd = pl.camYTEnd = 0.5f;
                }
            } else {
                pl.ssMode = 2;
                if (p == 0) {
                    glViewport(0, 0, width1o2, Display.getHeight());
                    glScissor(0, 0, width1o2, Display.getHeight());
                    glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    pl.camXStart = pl.camXTStart = pl.camYStart = pl.camYTStart = 0f;
                    pl.camXEnd = pl.camXTEnd = 0.5f;
                    pl.camYEnd = pl.camYTEnd = 1f;
                } else {
                    glViewport(width1o2, 0, width1o2, Display.getHeight());
                    glScissor(width1o2, 0, width1o2, Display.getHeight());
                    pl.camXTStart = pl.camXEnd = 0.5f;
                    pl.camXTEnd = pl.camYTEnd = pl.camYEnd = 1f;
                    pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
                }
            }
        } else if (pl.playersLength == 2 && pl.settings.joinSS) {
            glScissor(0, 0, Display.getWidth(), Display.getHeight());
            pl.cam = pl.cams[0];
            pl.ssMode = 0;
            pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
            pl.singleCam = true;
        } else if (pl.playersLength == 3 && (!pl.settings.joinSS || isFar(pl))) {
            if (pl.changeSSMode) {
                changeSSMode3(pl);
            }
            if (pl.settings.hSplitScreen) {
                pl.ssMode = 3;
                if (p == 0) {
                    glViewport(0, heigth1o2, Display.getWidth(), heigth1o2);
                    glScissor(0, heigth1o2, Display.getWidth(), heigth1o2);
                    glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    pl.camXStart = pl.camXTStart = 0f;
                    pl.camYStart = pl.camYTStart = -0.5f;
                    pl.camXEnd = pl.camXTEnd = 1f;
                    pl.camYEnd = pl.camYTEnd = 0.5f;
                } else if (p == 1) {
                    glViewport(0, 0, width1o2, heigth1o2);
                    glScissor(0, 0, width1o2, heigth1o2);
                    glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    pl.camYStart = pl.camYTStart = pl.camXStart = pl.camXTStart = 0f;
                    pl.camXEnd = pl.camXTEnd = pl.camYEnd = pl.camYTEnd = 0.5f;
                } else if (p == 2) {
                    glViewport(width1o2, 0, width1o2, heigth1o2);
                    glScissor(width1o2, 0, width1o2, heigth1o2);
                    pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
                    pl.camXTEnd = 1f;
                    pl.camYEnd = pl.camYTEnd = pl.camXTStart = pl.camXEnd = 0.5f;
                }
            } else {
                pl.ssMode = 4;
                if (p == 0) {
                    glViewport(0, 0, width1o2, Display.getHeight());
                    glScissor(0, 0, width1o2, Display.getHeight());
                    glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    pl.camXStart = pl.camXTStart = pl.camYStart = pl.camYTStart = 0f;
                    pl.camXEnd = pl.camXTEnd = 0.5f;
                    pl.camYEnd = pl.camYTEnd = 1f;
                } else if (p == 1) {
                    glViewport(width1o2, heigth1o2, width1o2, heigth1o2);
                    glScissor(width1o2, heigth1o2, width1o2, heigth1o2);
                    glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    pl.camXTStart = pl.camXEnd = pl.camYTStart = pl.camYEnd = 0.5f;
                    pl.camXTEnd = pl.camYTEnd = 1f;
                    pl.camXStart = pl.camYStart = 0f;
                } else if (p == 2) {
                    glViewport(width1o2, 0, width1o2, heigth1o2);
                    glScissor(width1o2, 0, width1o2, heigth1o2);
                    pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
                    pl.camXTEnd = 1f;
                    pl.camYEnd = pl.camYTEnd = pl.camXTStart = pl.camXEnd = 0.5f;
                }
            }
        } else if (pl.playersLength == 3 && pl.settings.joinSS) {
            glScissor(0, 0, Display.getWidth(), Display.getHeight());
            pl.cam = pl.cams[1];
            pl.ssMode = 0;
            pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
            pl.singleCam = true;
        } else if (pl.playersLength == 4 && (!pl.settings.joinSS || isFar(pl))) {
            pl.ssMode = 5;
            if (p == 0) {
                glViewport(0, heigth1o2, width1o2, heigth1o2);
                glScissor(0, heigth1o2, width1o2, heigth1o2);
                glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
                pl.camXStart = pl.camXTStart = 0f;
                pl.camYStart = pl.camYTStart = -0.5f;
                pl.camXEnd = pl.camXTEnd = pl.camYEnd = pl.camYTEnd = 0.5f;
            } else if (p == 1) {
                glViewport(width1o2, heigth1o2, width1o2, heigth1o2);
                glScissor(width1o2, heigth1o2, width1o2, heigth1o2);
                pl.camXTStart = pl.camXEnd = pl.camYTStart = pl.camYEnd = 0.5f;
                pl.camXTEnd = pl.camYTEnd = 1f;
                pl.camXStart = pl.camYStart = 0f;
            } else if (p == 2) {
                glViewport(0, 0, width1o2, heigth1o2);
                glScissor(0, 0, width1o2, heigth1o2);
                pl.camYStart = pl.camYTStart = pl.camXStart = pl.camXTStart = 0f;
                pl.camXEnd = pl.camXTEnd = pl.camYEnd = pl.camYTEnd = 0.5f;
            } else if (p == 3) {
                glViewport(width1o2, 0, width1o2, heigth1o2);
                glScissor(width1o2, 0, width1o2, heigth1o2);
                pl.camXStart = pl.camYStart = pl.camYTStart = 0f;
                pl.camXTEnd = 1f;
                pl.camYEnd = pl.camYTEnd = pl.camXTStart = pl.camXEnd = 0.5f;
            }
        } else if (pl.playersLength == 4 && pl.settings.joinSS) {
            glScissor(0, 0, Display.getWidth(), Display.getHeight());
            pl.cam = pl.cams[2];
            pl.ssMode = 0;
            pl.camXStart = pl.camYStart = pl.camXTStart = pl.camYTStart = 0f;
            pl.camXEnd = pl.camYEnd = pl.camXTEnd = pl.camYTEnd = 1f;
            pl.singleCam = true;
        }
    }

//    public static boolean isClose2(Place pl) {
//        if (pl.settings.joinSS) {
//            if (Math.abs(pl.players[0].getX() - pl.players[1].getX()) < width2o3 && Math.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth2o3) {
//                pl.isSplit = false;
//                return true;
//            } else if (!pl.isSplit) {
//                if (Math.abs(pl.players[0].getX() - pl.players[1].getX()) < Math.abs(pl.players[0].getY() - pl.players[1].getY())) {
//                    pl.settings.hSplitScreen = true;
//                    swampY(pl);
//                } else {
//                    pl.settings.hSplitScreen = false;
//                    swampX(pl);
//                }
//                pl.isSplit = true;
//            } else if (pl.changeSSMode) {
//                if (pl.settings.hSplitScreen) {
//                    pl.settings.hSplitScreen = false;
//                    swampX(pl);
//                } else {
//                    pl.settings.hSplitScreen = true;
//                    swampY(pl);
//                }
//                pl.changeSSMode = false;
//            }
//        }
//        return false;
//    }
    public static boolean isClose(Place pl) {
        if (pl.playersLength == 2) {
            return isClose2(pl);
        } else if (pl.playersLength == 3) {
            return isClose3(pl);
        } else if (pl.playersLength == 4) {
            return isClose4(pl);
        }
        return false;
    }

    public static boolean isClose2(Place pl) {
        if (Math.abs(pl.players[0].getX() - pl.players[1].getX()) < width3o4 && Math.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth3o4) {
            return true;
        }
        return false;
    }

    public static boolean isClose3(Place pl) {
        if (Math.abs(pl.players[0].getX() - pl.players[1].getX()) < width2o3 && Math.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth2o3 && Math.abs(pl.players[0].getX() - pl.players[2].getX()) < width2o3 && Math.abs(pl.players[0].getY() - pl.players[2].getY()) < heigth2o3 && Math.abs(pl.players[1].getX() - pl.players[2].getX()) < width2o3 && Math.abs(pl.players[1].getY() - pl.players[2].getY()) < heigth2o3) {
            return true;
        }
        return false;
    }

    public static boolean isClose4(Place pl) {
        if (Math.abs(pl.players[0].getX() - pl.players[1].getX()) < width1o2 && Math.abs(pl.players[0].getY() - pl.players[1].getY()) < heigth1o2 && Math.abs(pl.players[0].getX() - pl.players[2].getX()) < width1o2 && Math.abs(pl.players[0].getY() - pl.players[2].getY()) < heigth1o2 && Math.abs(pl.players[1].getX() - pl.players[2].getX()) < width1o2 && Math.abs(pl.players[1].getY() - pl.players[2].getY()) < heigth1o2 && Math.abs(pl.players[0].getX() - pl.players[3].getX()) < width1o2 && Math.abs(pl.players[0].getY() - pl.players[3].getY()) < heigth1o2 && Math.abs(pl.players[1].getX() - pl.players[3].getX()) < width1o2 && Math.abs(pl.players[1].getY() - pl.players[3].getY()) < heigth1o2 && Math.abs(pl.players[2].getX() - pl.players[3].getX()) < width1o2 && Math.abs(pl.players[2].getY() - pl.players[3].getY()) < heigth1o2) {
            return true;
        }
        return false;
    }

    public static boolean isFar(Place pl) {
        if (!pl.singleCam) {
            for (int p = 0; p < pl.playersLength; p++) {
                if (pl.players[p].getX() > pl.cams[pl.playersLength - 2].getEX() || pl.players[p].getX() < pl.cams[pl.playersLength - 2].getSX() || pl.players[p].getY() > pl.cams[pl.playersLength - 2].getEY() || pl.players[p].getY() < pl.cams[pl.playersLength - 2].getSY()) {
                    pl.settings.joinSS = false;
                    return true;
                }
            }
        }
        return false;
    }

    public static void changeSSMode2(Place pl) {
        if (pl.settings.hSplitScreen) {
            pl.settings.hSplitScreen = false;
            ((PlayersCamera) ((Player) pl.players[0]).getCam()).init(4, 2, 0);
            ((PlayersCamera) ((Player) pl.players[1]).getCam()).init(4, 2, 1);
        } else {
            pl.settings.hSplitScreen = true;
            ((PlayersCamera) ((Player) pl.players[0]).getCam()).init(2, 4, 0);
            ((PlayersCamera) ((Player) pl.players[1]).getCam()).init(2, 4, 1);
        }
        pl.changeSSMode = false;
    }

    public static void changeSSMode3(Place pl) {
        if (pl.settings.hSplitScreen) {
            pl.settings.hSplitScreen = false;
            ((PlayersCamera) ((Player) pl.players[0]).getCam()).init(4, 2, 0);
            ((PlayersCamera) ((Player) pl.players[1]).getCam()).init(4, 4, 1);
            ((PlayersCamera) ((Player) pl.players[2]).getCam()).init(4, 4, 2);
        } else {
            pl.settings.hSplitScreen = true;
            ((PlayersCamera) ((Player) pl.players[0]).getCam()).init(2, 4, 0);
            ((PlayersCamera) ((Player) pl.players[1]).getCam()).init(4, 4, 1);
            ((PlayersCamera) ((Player) pl.players[2]).getCam()).init(4, 4, 2);
        }
        pl.changeSSMode = false;
    }

//    private static void swampY(Place pl) {
//        if (pl.players[0].getY() > pl.players[1].getY()) {
//            swampFirstWithSecond(pl);
//        }
//        ((PlayersCamera) ((Player) pl.players[0]).getCam()).init(2, 4, 0);
//        ((PlayersCamera) ((Player) pl.players[1]).getCam()).init(2, 4, 1);
//    }
//
//    private static void swampX(Place pl) {
//        if (pl.players[0].getX() > pl.players[1].getX()) {
//            swampFirstWithSecond(pl);
//        }
//        ((PlayersCamera) ((Player) pl.players[0]).getCam()).init(4, 2, 0);
//        ((PlayersCamera) ((Player) pl.players[1]).getCam()).init(4, 2, 1);
//    }
//    public static void swampFirstWithSecond(Place pl) {
//        GameObject temp = pl.players[0];
//        Player tempG = pl.game.players[0];
//        pl.players[0] = pl.players[1];
//        pl.game.players[0] = pl.game.players[1];
//        pl.players[1] = temp;
//        pl.game.players[1] = tempG;
//    }
    private SplitScreen() {
    }
}
