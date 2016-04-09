/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.utilities.Drawer;
import engine.utilities.Point;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.map.Map;
import game.place.map.MapObjectContainer;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import gamecontent.environment.GrassClump;
import org.lwjgl.opengl.Display;
import sprites.SpriteSheet;

/**
 * @author Wojtek
 */
public class ObjectUI extends GUIObject {

    private static TextPiece text;
    private final int tile;
    private final Point coordinates = new Point(0, 0);
    private final Point playerPosition = new Point(0, 0);
    private final Point selection = new Point(1, 1);
    private final String[] mapObjectsNames;
    private int choosenObject = 0;
    private SpriteSheet texture;
    private boolean change;
    private int mode;
    private GameObject[] mapObjects;
    private Map map;
    private int lastX, lastY;
    private String[] data;

    public ObjectUI(int tile, SpriteSheet tex, Place p, Map m) {
        super("OUI", p);
        this.tile = tile;
        this.texture = tex;
        mode = 0;
        mapObjectsNames = MapObjectContainer.getNames();
        mapObjects = new GameObject[mapObjectsNames.length];
        for (int i = 0; i < mapObjectsNames.length; i++) {
            mapObjects[i] = MapObjectContainer.generate(0, 0, null, (byte) i);
            mapObjects[i].setMapNotChange(m);
            mapObjects[i].update();
        }
        map = m;
        text = new TextPiece("", 24, TextMaster.getFont("Lato-Regular"), Display.getWidth(), false);
    }

    public void changeCoordinates(int x, int y) {
        if (mode != ObjectPlace.MODE_OBJECT) {
            int xLim = coordinates.getX() + x;
            int yLim = coordinates.getY() + y;
            if (xLim < 0) {
                xLim = texture.getXLimit() - 1;
            }
            if (xLim > texture.getXLimit() - 1) {
                xLim = 0;
            }
            if (yLim < 0) {
                yLim = texture.getYLimit() - 1;
            }
            if (yLim > texture.getYLimit() - 1) {
                yLim = 0;
            }
            coordinates.set(xLim, yLim);
        } else {
            int lim = choosenObject + y;
            if (lim < 0) {
                lim = mapObjectsNames.length - 1;
            }
            if (lim > mapObjectsNames.length - 1) {
                lim = 0;
            }
            choosenObject = lim;
        }
    }

    public void setCursorStatus(int x, int y, int xs, int ys) {
        playerPosition.set(x, y);
        selection.set(xs, ys);
    }

    public SpriteSheet getSpriteSheet() {
        return texture;
    }

    public void setSpriteSheet(SpriteSheet tex) {
        this.texture = tex;
        coordinates.set(0, 0);
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public int getChosenObject() {
        return choosenObject;
    }

    public void renderChosenObject(int x, int y) {
        mapObjects[choosenObject].setPositionWithoutAreaUpdate(x, y);
        mapObjects[choosenObject].render();
    }

    public String[] getData() {
        return data;
    }

    public void changeChosenObject(int x, int y) {
        if (lastX != x || lastY != y) {
            switch (choosenObject) {
                case MapObjectContainer.GRASS:
                    String ret = "";
                    if (x != 0) {
                        if (y != 0) {
                            ret = "4";
                        } else {
                            ret = "" + (x < 0 ? GrassClump.CORNER_DOWN_LEFT : GrassClump.CORNER_UP_RIGHT);
                        }
                    } else if (y != 0) {
                        ret = "" + (y > 0 ? GrassClump.CORNER_UP_LEFT : GrassClump.CORNER_DOWN_RIGHT);
                    }
                    if (!ret.isEmpty()) {
                        mapObjects[choosenObject] = MapObjectContainer.generate(0, 0, null, (byte) choosenObject, ret);
                        data = new String[]{ret};
                    } else {
                        mapObjects[choosenObject] = MapObjectContainer.generate(0, 0, null, (byte) choosenObject);
                        data = null;
                    }
                    mapObjects[choosenObject].setMapNotChange(map);
                    mapObjects[choosenObject].update();
                    break;
            }
        }
        lastX = x;
        lastY = y;
    }

    public void setChange(boolean ch) {
        change = ch;
    }

    public boolean isChanged() {
        return change;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void render() {
        if (player != null) {
            int d = 2;
            int xStart = texture.getXStart();
            int yStart = texture.getYStart();
            int wTex = texture.getWidth();
            int hTex = texture.getHeight();

            Drawer.regularShader.scaleTranslate(tile / 2, tile / 2, (float) Settings.nativeScale, (float) Settings.nativeScale);
            if (mode == ObjectPlace.MODE_TILE) {
                if (change) {
                    Drawer.regularShader.translateNoReset(player.getCamera().getWidthHalf() / 2, player.getCamera().getHeightHalf() / 2);
                    Drawer.setColorStatic(1f, 1f, 1f, 1f);
                    Drawer.regularShader.translateNoReset(-xStart - coordinates.getX() * wTex, -yStart - coordinates.getY() * hTex);
                    texture.render();
                    Drawer.regularShader.translateNoReset(coordinates.getX() * wTex, coordinates.getY() * hTex);
                }

                Drawer.setColorStatic(1f, 1f, 1f, 1f);
                Drawer.drawRectangle(-1, -1, wTex + 2, hTex + 2);
//
                Drawer.regularShader.translateNoReset(-xStart + 1, -yStart + 1);
                texture.renderPiece(coordinates.getX(), coordinates.getY());

                Drawer.setColorStatic(0f, 0f, 0f, 1f);
                Drawer.drawRectangle(-d, -d, wTex + 2 * d, d - 1);
                Drawer.drawRectangle(-d, hTex + 1, wTex + 2 * d, d - 1);
                Drawer.drawRectangle(-d, -1, d - 1, hTex + 2);
                Drawer.drawRectangle(wTex + 1, -1, d - 1, hTex + 2);
            } else if (mode == ObjectPlace.MODE_OBJECT) {
                TextMaster.startRenderText();
                int h = (int) (text.getFontSize() * Settings.nativeScale);
                if (change) {
                    for (int i = 0; i < mapObjectsNames.length; i++) {
                        text.setText(mapObjectsNames[i]);
                        TextMaster.render(text, h + (int) (tile * 0.1 * Settings.nativeScale), (i + 2) * h);
                    }
                    text.setText(">");
                    TextMaster.render(text, h / 3, (choosenObject + 2) * h);
                } else {
                    text.setText(mapObjectsNames[choosenObject]);
                    TextMaster.render(text, h + (int) (tile * 0.1 * Settings.nativeScale), 2 * h);
                }
                TextMaster.endRenderText();
            }
            if (mode != ObjectPlace.MODE_VIEWING) {
                text.setText(playerPosition.getX() + ":" + playerPosition.getY() + " - " + selection.getX() + ":" + selection.getY());
                TextMaster.renderOnce(text, (int) (tile * 0.1 * Settings.nativeScale), 0);
            }
            Drawer.refreshForRegularDrawing();
        }
    }
}
