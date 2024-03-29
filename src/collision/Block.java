/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.map.ForegroundTile;
import net.jodk.lang.FastMath;

import java.util.ArrayList;
import java.util.List;

import static collision.OpticProperties.*;

/**
 * @author Wojtek
 */
public class Block extends GameObject {

    private final ArrayList<Figure> top = new ArrayList<>(1);
    private final ArrayList<ForegroundTile> topForegroundTiles = new ArrayList<>();
    private final ArrayList<ForegroundTile> wallForegroundTiles = new ArrayList<>();
    private boolean forNavigationMesh;
    private Figure topSimple;

    private Block(int x, int y, int width, int height, int shadowHeight, boolean round, boolean invisible) {  //Point (x, y) should be
        // in left top
        // corner of
        // Block
        this.x = x;
        this.y = y;
        name = "area";
        setVisible(true);
        setSolid(true);
        if (round) {
            setCollision(RoundRectangle.createShadowHeight(0, 0, width, height, FULL_SHADOW, shadowHeight, this));
        } else if (invisible) {
            setCollision(Rectangle.createShadowHeight(0, 0, width, height, NO_SHADOW, shadowHeight, this));
        } else {
            setCollision(Rectangle.createShadowHeight(0, 0, width, height, FULL_SHADOW, shadowHeight, this));
            setSimpleLighting(true);
        }
    }

    public static Block create(int x, int y, int width, int height, int shadowHeight) {
        return new Block(x, y, width, height, shadowHeight, false, false);
    }

    public static Block createInvisible(int x, int y, int width, int height) {
        return new Block(x, y, width, height, 0, false, true);
    }

    public static Block createRound(int x, int y, int width, int height, int shadowHeight) {
        return new Block(x, y, width, height, shadowHeight, true, false);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        topForegroundTiles.stream().forEach((fgt) -> fgt.setPosition(fgt.getX() + dx, fgt.getY() + dy));
        wallForegroundTiles.stream().forEach((fgt) -> fgt.setPosition(fgt.getX() + dx, fgt.getY() + dy));
        if (!collision.isMobile()) {
            collision.updatePoints();
        }
        // map.sortForegroundTiles();
    }

    @Override
    public void setSimpleLighting(boolean simpleLighting) {
        super.setSimpleLighting(simpleLighting);
        if (simpleLighting) {
            if (topSimple == null) {
                topSimple = Rectangle.createShadowHeight(0, 0, collision.getWidth(), collision.getHeight(), TRANSPARENT, collision.getShadowHeight() + collision
                        .getHeight(), this);
            }
            if (!top.contains(topSimple)) {
                top.add(topSimple);
            }
        } else {
            if (topSimple != null) {
                top.remove(topSimple);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        topForegroundTiles.stream().forEach((fgt) -> fgt.setVisible(visible));
        wallForegroundTiles.stream().forEach((fgt) -> fgt.setVisible(visible));
    }

    public void setDarkValue(float darkValue) {
        topForegroundTiles.stream().forEach((fgt) -> fgt.getCollision().setDarkValue(0f));
        wallForegroundTiles.stream().forEach((fgt) -> fgt.getCollision().setDarkValue(darkValue));
    }

    public void addForegroundTile(ForegroundTile foregroundTile) {
        if (foregroundTile.isWall()) {
            wallForegroundTiles.add(foregroundTile);
        } else {
            topForegroundTiles.add(foregroundTile);
            if (collision instanceof RoundRectangle) {
                top.add(foregroundTile.getCollision());
            }
        }
        foregroundTile.setBlockPart(true);
        foregroundTile.setInCollidingPosition(inCollision(foregroundTile));
        if (!foregroundTile.isSimpleLighting()) {
            setSimpleLighting(false);
        }
    }

    private boolean inCollision(ForegroundTile foregroundTile) {
//        if (collision instanceof RoundRectangle) {
//            RoundRectangle round = (RoundRectangle) collision;
//            if ((round.isCornerTriangular(LEFT_TOP) || round.isCornerConcave(LEFT_TOP))
//                    && foregroundTile.getX() == collision.getX() && foregroundTile.getY() == collision.getY()) {
//                return false;
//            }
//            if ((round.isCornerTriangular(RIGHT_TOP) || round.isCornerConcave(RIGHT_TOP))
//                    && foregroundTile.getX() + Place.tileSize == collision.getXEnd() && foregroundTile.getY() == collision.getY()) {
//                return false;
//            }
//            return foregroundTile.getX() >= collision.getX() && foregroundTile.getX() < collision.getXEnd() && foregroundTile.getY() >= collision.getY() &&
//                    foregroundTile.getY() < collision.getYEnd();
//        } else {
        return foregroundTile.getX() >= collision.getX() && foregroundTile.getX() < collision.getXEnd() && foregroundTile.getY() >= collision.getY()
                && foregroundTile.getY() < collision.getYEnd();
//        }
    }

    public void removeForegroundTile(ForegroundTile foregroundTile) {
        wallForegroundTiles.remove(foregroundTile);
        topForegroundTiles.remove(foregroundTile);
        top.remove(foregroundTile.getCollision());
    }

    public void pushCorner(int corner, int xChange, int yChange) {
        if (collision instanceof RoundRectangle) {
            ((RoundRectangle) collision).pushCorner(corner, xChange, yChange);
        }
    }

    public boolean isCollide(int x, int y, Figure figure) {
        return figure.isCollideSingle(x, y, collision);
    }

    public Figure whatCollide(int x, int y, Figure figure) {
        if (figure.isCollideSingle(x, y, collision)) {
            return collision;
        }
        return null;
    }

    private Point getPushValueOfCorner(int corner) {
        if (collision instanceof RoundRectangle) {
            return collision.getPushValueOfCorner(corner);
        }
        return null;
    }

    @Override
    public void renderShadowLit(Figure figure) {
        if (isSimpleLighting()) {
            Drawer.drawRectangleLit(figure.getX(), figure.getY() - figure.getShadowHeight(),
                    figure.width, figure.height + figure.getShadowHeight());
        } else {
            wallForegroundTiles.stream().forEach((wall) -> {
                Figure col = wall.getCollision();
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleLit(col.getX(), col.getY() - col.getShadowHeight(), col.width, col.height + col.getShadowHeight());
                } else {
                    Drawer.drawShapeLit(wall, col.getX(), col.getY() - col.getShadowHeight());
                }
            });
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (isSimpleLighting()) {
            Drawer.drawRectangleBlack(figure.getX(), figure.getY() - figure.getShadowHeight(),
                    figure.width, figure.height + (top.contains(figure) ? 0 : figure.getShadowHeight()),
                    top.contains(figure) ? 0f : (float) FastMath.sqrt(collision.getDarkValue()));
        } else {
            for (ForegroundTile wall : wallForegroundTiles) {
                Figure tempCollision = wall.getCollision();
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleBlack(tempCollision.getX(), tempCollision.getY() - tempCollision.getShadowHeight(), tempCollision.width,
                            tempCollision.height + tempCollision.getShadowHeight(), (float) FastMath.sqrt(collision.getDarkValue()));
                } else {
                    Drawer.drawShapeBlack(wall, collision.getDarkValue(), tempCollision.getX(), tempCollision.getY() - tempCollision.getShadowHeight());
                }
            }
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (isSimpleLighting() || !collision.isBottomRounded()) {
            if (Main.DEBUG) {
                System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
            }
        } else {
            for (ForegroundTile wall : wallForegroundTiles) {
                Figure tempCollision = wall.getCollision();
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleLit(tempCollision.getX() + xStart, tempCollision.getY() - tempCollision.getShadowHeight(), xEnd - xStart,
                            tempCollision.height + tempCollision.getShadowHeight());
                } else {
                    Drawer.drawShapePartLit(wall, tempCollision.getX(), tempCollision.getY() - tempCollision.getShadowHeight(), xStart, xEnd);
                }
            }
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (isSimpleLighting() || !collision.isBottomRounded()) {
            if (Main.DEBUG) {
                System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
            }
        } else {
            for (ForegroundTile wall : wallForegroundTiles) {
                Figure tempCollision = wall.getCollision();
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleBlack(tempCollision.getX() + xStart, tempCollision.getY() - tempCollision.getShadowHeight(), xEnd - xStart,
                            tempCollision.height + tempCollision.getShadowHeight(), (float) FastMath.sqrt(collision.getDarkValue()));
                } else {
                    Drawer.drawShapePartBlack(wall, collision.getDarkValue(), tempCollision.getX(), tempCollision.getY() - tempCollision.getShadowHeight(),
                            xStart, xEnd);
                }
            }
        }

    }

    @Override
    public void render() {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public int getActualHeight() {
//        System.out.println(collision.getHeight() + collision.getShadowHeight());
        return collision.getHeight() + collision.getShadowHeight();
    }

    //b:x:y:width:height:shadowHeight:round
    public String saveToString(int xBegin, int yBegin, int tile) {
        String string = ((collision instanceof RoundRectangle) ? "rb:" : "b:") + ((int) (x - xBegin) / tile) + ":" + ((int) (y - yBegin) / tile) + ":"
                + (collision.width / tile) + ":" + (collision.height / tile) + ":" + (collision.getShadowHeight() / tile) + ":"
                + (isSimpleLighting() ? "0" : "1");
        if (collision instanceof RoundRectangle) {
            for (int i = 0; i < 4; i++) {
                Point temp = getPushValueOfCorner(i);
                string += ":" + (temp.getX() != 0 ? temp.getX() : "") + ":" + (temp.getY() != 0 ? temp.getY() : "");
            }
            string += ":0";
        }
        return string;
    }

    public List<ForegroundTile> getAllForegroundTiles() {
        List<ForegroundTile> all = new ArrayList<>();
        all.addAll(topForegroundTiles);
        all.addAll(wallForegroundTiles);
        return all;
    }

    public List<ForegroundTile> getTopForegroundTiles() {
        return topForegroundTiles;
    }

    public List<ForegroundTile> getWallForegroundTiles() {
        return wallForegroundTiles;
    }

    public List<Figure> getTop() {
        return top;
    }

    public void setTop(Figure top) {
        this.top.clear();
        this.top.add(top);
        this.top.trimToSize();
    }

    public List<Point> getPoints() {
        return collision.getPoints();
    }

    public boolean isForNavigationMesh() {
        return forNavigationMesh;
    }

    public void setForNavigationMesh(boolean forNavigationMesh) {
        this.forNavigationMesh = forNavigationMesh;
    }
}
