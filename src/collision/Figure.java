/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.BlueArray;
import engine.Methods;
import engine.Point;
import engine.PointedValue;
import engine.PointContener;
import engine.Shadow;
import engine.ShadowContener;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.Area;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import java.util.List;
import net.jodk.lang.FastMath;

/**
 *
 * @author Wojtek
 */
public abstract class Figure implements Comparable<Figure> {

    private static Figure figure;
    private static Rectangle tempTile = Rectangle.createTileRectangle();
    private static Rectangle scope = Rectangle.createTileRectangle();

    protected GameObject owner;
    private final OpticProperties opticProperties;
    protected int xStart, yStart, width, height, xCenter, yCenter;
    protected final BlueArray<Point> points;
    private static PointContener tiles;
    private boolean mobile = false, small = false;
    private final DoublePoint slideSpeed;

    public abstract boolean isCollideSingle(int x, int y, Figure figure);

    public abstract List<Point> getPoints();

    public abstract void updatePoints();

    public Figure(int xStart, int yStart, GameObject owner, OpticProperties opticProperties) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.owner = owner;
        this.opticProperties = opticProperties;
        this.points = new BlueArray<>(4);
        slideSpeed = new DoublePoint();
    }

    public boolean isCollideSolid(int x, int y, Map map) {
        if (getOwner().getArea() != -1) {
            Area area = map.getArea(getOwner().getArea());
            if (area.getNearBlocks().stream().anyMatch((block) -> (block.isSolid() && block.isCollide(x, y, this)))) {
                return true;
            }
            if (area.getNearSolidMobs().stream().anyMatch((object) -> (checkCollison(x, y, object)))) {
                return true;
            }
            if (area.getNearSolidObjects().stream().anyMatch((object) -> (checkCollison(x, y, object)))) {
                return true;
            }
            tiles = map.getNearNullTiles(this);
            for (int i = 0; i < tiles.size(); i++) {
                setTile(tiles.get(i));
                if (isCollideSingle(x, y, tempTile)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setTile(Point point) {
        tempTile.setXStart(point.getX() * Place.tileSize);
        tempTile.setYStart(point.getY() * Place.tileSize);
        tempTile.updateTilePoints();
    }

    public Figure whatCollideSolid(int x, int y, Map map) {
        if (getOwner().getArea() != -1) {
            Area area = map.getArea(getOwner().getArea());
            for (Block block : area.getNearBlocks()) {
                if (block.isSolid() && block.isCollide(x, y, this)) {
                    return block.getCollision();
                }
            }
            for (GameObject object : area.getNearSolidMobs()) {
                if (checkCollison(x, y, object)) {
                    return object.getCollision();
                }
            }
            for (GameObject object : area.getNearSolidObjects()) {
                if (checkCollison(x, y, object)) {
                    return object.getCollision();
                }
            }
            tiles = map.getNearNullTiles(this);
            for (int i = 0; i < tiles.size(); i++) {
                setTile(tiles.get(i));
                if (isCollideSingle(x, y, tempTile)) {
                    return tempTile;
                }
            }
        }
        return null;
    }

    public static void updateWhatClose(GameObject owner, int x, int y, int range, int xDest, int yDest, Map map, List<Figure> close) {
        close.clear();
        setScope(x, y, range);
        Area area = map.getArea(x, y);
        area.getNearBlocks().stream().filter((block) -> (block.isSolid() && scope.isCollideSingle(x, y, block.getCollision()))).forEach((block) -> {
            Figure figure = block.getCollision();
            figure.setLightDistance(Methods.pointDistance(figure.getXCentral(), figure.getYCentral(), xDest, yDest));
            close.add(figure);
        });
        area.getNearSolidMobs().stream().filter((object) -> (object != owner && scope.isCollideSingle(x, y, object.getCollision()))).forEach((object) -> {
            Figure figure = object.getCollision();
            figure.setLightDistance(Methods.pointDistance(figure.getXCentral(), figure.getYCentral(), xDest, yDest));
            close.add(figure);
        });
        area.getNearSolidObjects().stream().filter((object) -> (object != owner && scope.isCollideSingle(x, y, object.getCollision()))).forEach((object) -> {
            Figure figure = object.getCollision();
            figure.setLightDistance(Methods.pointDistance(figure.getXCentral(), figure.getYCentral(), xDest, yDest));
            close.add(figure);
        });
    }

    public static void setScope(int x, int y, int range) {
        scope.setXStart(x - range);
        scope.setYStart(y - range);
        scope.width = 2 * range;
        scope.height = scope.width;
        scope.updateTilePoints();
    }

    public boolean isCollide(int x, int y, List<GameObject> objects) {
        return objects.stream().anyMatch((object) -> (checkCollison(x, y, object)));
    }

    public GameObject whatCollide(int x, int y, List<GameObject> objects) {
        for (GameObject object : objects) {
            if (checkCollison(x, y, object)) {
                return object;
            }
        }
        return null;
    }

    public boolean isCollidePlayer(int x, int y, Place place) {
        for (int i = 0; i < place.playersCount; i++) {
            if (checkCollison(x, y, place.players[i])) {
                return true;
            }
        }
        return false;
    }

    public Player firstPlayerCollide(int x, int y, Place place) {
        if (place.players[0].getMap() == owner.getMap() && checkCollison(x, y, place.players[0])) {
            return (Player) place.players[0];
        }
        return null;
    }

    private boolean checkCollison(int x, int y, GameObject object) {
        figure = object.getCollision();
        return checkCollison(x, y, object, figure);
    }

    private boolean checkCollison(int x, int y, GameObject object, Figure figure) {
        if (object == owner || figure == null) {
            return false;
        }
        return isCollideSingle(x, y, figure);
    }

    public void addAllShadows(ShadowContener shadows) {
        opticProperties.addAllShadows(shadows);
    }

    public void addShadow(Shadow shadow) {
        opticProperties.addShadow(shadow);
    }

    public void addShadow(int type, int x, int y) {
        opticProperties.addShadow(type, x, y);
    }

    public void addShadowType(int type) {
        opticProperties.addShadowType(type);
    }

    public void addShadowWithCaster(int type, int x, int y, Figure caster) {
        opticProperties.addShadowWithCaster(type, x, y, caster);
    }

    public void clearShadows() {
        opticProperties.clearShadows();
    }

    public void removeShadow(Shadow shadow) {
        opticProperties.removeShadow(shadow);
    }

    @Override
    public int compareTo(Figure Figure) { // Check this out
        return ((getDepth() - ((Figure) Figure).getDepth()) << 13) - (getLightDistance() - ((Figure) Figure).getLightDistance());
    }

    private int getDepth() {
        if (getOwner() instanceof Block || getOwner() instanceof ForegroundTile) {
            return getYEnd() - Place.tileSize;
        }
        return getY();
    }

    public void prepareSlideSpeed(double xSpeed, double ySpeed) {   //YOUR SPPED WITHOUT SLIDING
        slideSpeed.setStart(xSpeed, ySpeed);
        slideSpeed.changed = false;
    }

    public void setSlideSpeed(double xSpeed, double ySpeed) {
        slideSpeed.changeSlide(xSpeed, ySpeed);
        slideSpeed.changed = true;
    }

    public void resetSlideSpeed() {
        if (!slideSpeed.changed) {
            slideSpeed.changeSlide(0, 0);
            slideSpeed.changed = false;
        }
    }

    public double getXStartSlideSpeed() {
        return slideSpeed.getStartX();
    }

    public double getYStartSlideSpeed() {
        return slideSpeed.getStartY();
    }
    
    public double getXSlideSpeed() {
        return slideSpeed.getAllX();
    }

    public double getYSlideSpeed() {
        return slideSpeed.getAllY();
    }

    public boolean isLittable() {
        return opticProperties.isLitable();
    }

    public boolean isGiveShadow() {
        return opticProperties.isGiveShadow();
    }

    public boolean isMobile() {
        return mobile;
    }

    public boolean isSmall() {
        return small;
    }

    public boolean isConcave() {
        return false;
    }

    public boolean isTriangular() {
        return false;
    }

    public boolean isBottomRounded() {
        return false;
    }

    public int getX() {
        return owner.getX() + xStart;
    }

    public int getY() {
        return owner.getY() + yStart;
    }

    public int getXEnd() {
        return owner.getX() + xStart + width;
    }

    public int getYEnd() {
        return owner.getY() + yStart + height;
    }

    public int getYSpriteBegin() {
        return owner.getYSpriteBegin();
    }

    public int getYSpriteEnd() {
        return owner.getYSpriteEnd();
    }

    public int getXSpriteBegin() {
        return owner.getXSpriteBegin();
    }

    public int getXSpriteEnd() {
        return owner.getXSpriteEnd();
    }

    public int getX(int x) {
        return x + xStart;
    }

    public int getY(int y) {
        return y + yStart;
    }

    public int getXStart() {
        return xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public int getXSpriteOffset() {
        return owner.getXSpriteOffset();
    }

    public int getXSpriteOffsetWidth() {
        return owner.getXSpriteOffsetWidth();
    }

    public int getYSpriteOffset() {
        return owner.getYSpriteOffset();
    }

    public int getXCentral() {
        return owner.getX() + xStart + xCenter;
    }

    public int getYCentral() {
        return owner.getY() + yStart + yCenter;
    }

    public int getXCentral(int x) {
        return x + yStart + xCenter;
    }

    public int getYCentral(int y) {
        return y + yStart + yCenter;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getActualWidth() {
        return owner.getSprite().getActualWidth();
    }

    public int getActualHeight() {
        return owner.getSprite().getActualHeight();
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public GameObject getOwner() {
        return owner;
    }

    public int getShadowHeight() {
        return opticProperties.getShadowHeight();
    }

    public int getLightDistance() {
        return opticProperties.getLightDistance();
    }

    public int getShadowCount() {
        return opticProperties.getShadowCount();
    }

    public Shadow getShadow(int i) {
        return opticProperties.getShadow(i);
    }

    public int getType() {
        return opticProperties.getType();
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public void setXStart(int xStart) {
        this.xStart = xStart;
    }

    public void setYStart(int yStart) {
        this.yStart = yStart;
    }

    public void setLightDistance(int lightDistance) {
        opticProperties.setLightDistance(lightDistance);
    }

    public void setOpticProperties(int type) {
        opticProperties.setType(type);
    }

    public void setShadowHeight(int shadowHeight) {
        opticProperties.setType(shadowHeight);
    }

    private class DoublePoint {

        private double x, y;
        private double startX, startY;
        private boolean changed;
        private double lastX, lastY;
        
        public double getAllX() {
            return x != 0 ? x : lastX;
        }

        public double getAllY() {
            return y != 0 ? y : lastY;
        }
        
        public double getStartX() {
            return startX;
        }

        public double getStartY() {
            return startY;
        }

        public void changeSlide(double x, double y) {
            lastX = this.x;
            lastY = this.y;
            this.x = x;
            this.y = y;
        }

        public void setStart(double x, double y) {
            startX = x;
            startY = y;
        }
    }
}
