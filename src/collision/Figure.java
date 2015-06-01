/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.BlueArray;
import engine.Methods;
import engine.Point;
import engine.ShadowContener;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import engine.Shadow;
import java.util.List;

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
    private boolean mobile = false;

    public abstract boolean isCollideSingle(int x, int y, Figure figure);

    public abstract List<Point> getPoints();

    public abstract void updatePoints();

    public Figure(int xStart, int yStart, GameObject owner, OpticProperties opticProperties) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.owner = owner;
        this.opticProperties = opticProperties;
        this.points = new BlueArray<>(4);
    }

    public boolean isCollideSolid(int x, int y, Map map) {
        if (map.getBlocks(getOwner().getArea()).stream().anyMatch((object) -> (object.isSolid() && object.isCollide(x, y, this)))) {
            return true;
        }
        if (map.getSolidMobs(getOwner().getArea()).stream().anyMatch((object) -> (checkCollison(x, y, object)))) {
            return true;
        }
        if (map.getSolidObjects(getOwner().getArea()).stream().anyMatch((object) -> (checkCollison(x, y, object)))) {
            return true;
        }
        tiles = map.getNearNullTiles(this);
        for (int i = 0; i < tiles.size(); i++) {
            setTile(tiles.get(i));
            if (isCollideSingle(x, y, tempTile)) {
                return true;
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
        for (Block object : map.getBlocks(x, y)) {
            if (object.isSolid() && object.isCollide(x, y, this)) {
                return object.getCollision();
            }
        }
        for (GameObject object : map.getSolidMobs(x, y)) {
            if (checkCollison(x, y, object)) {
                return object.getCollision();
            }
        }
        for (GameObject object : map.getSolidObjects(x, y)) {
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
        return null;
    }

    public List<Figure> whatClose(GameObject owner, Map map, Point destination, double range) {
        int x = owner.getX(), y = owner.getY();
        List<Figure> colided = new BlueArray<>();

        setScope(owner, destination, range / 4);
        for (Block object : map.getBlocks(x, y)) {
            if (object.isSolid() && scope.isCollideSingle(x, y, object.getCollision())) {
                Figure figure = object.getCollision();
                figure.setLightDistance(Methods.pointDistance(figure.getXCentral(), figure.getYCentral(), destination.getX(), destination.getY()));
                colided.add(figure);
            }
        }
        for (GameObject object : map.getSolidMobs(getX(), getY())) {
            if (object != owner && scope.isCollideSingle(x, y, object.getCollision())) {
                Figure figure = object.getCollision();
                figure.setLightDistance(Methods.pointDistance(figure.getXCentral(), figure.getYCentral(), destination.getX(), destination.getY()));
                colided.add(figure);
            }
        }
        for (GameObject object : map.getSolidObjects(getX(), getY())) {
            if (object != owner && scope.isCollideSingle(x, y, object.getCollision())) {
                Figure figure = object.getCollision();
                figure.setLightDistance(Methods.pointDistance(figure.getXCentral(), figure.getYCentral(), destination.getX(), destination.getY()));
                colided.add(figure);
            }
        }
        return colided;
    }

    public void setScope(GameObject owner, Point destination, double range) {
//        int distance = Methods.pointDistance(owner.getX(), owner.getY(), destination.getX(), destination.getY());
//        if (distance > range) {
//            distance = (int) range;
//        }
        int distance = (int) range;

        //To DO make right points - 4 cases
        int xSign = destination.getX() >= owner.getX() ? -1 : 1;
        int ySign = destination.getY() >= owner.getY() ? -1 : 1;

        scope.setXStart(owner.getX() - distance);
        scope.setYStart(owner.getY() - distance);
        scope.width = 2 * distance;
        scope.height = 2 * distance;

        scope.updateTilePoints();
    }

//    public ColidedResponse whatCloseToSolid(int x, int y, Map map) {
//        circle.radius = (width + height);
//        circle.owner = this.getOwner();
//        ColidedResponse response = new ColidedResponse();
//        for (Block object : map.getBlocks(x, y)) {
//            if (object.isSolid() && checkCollison(x, y, object, circle)) {
//                response.colided.add(object.getCollision());
//                if (checkCollison(x, y, object)) {
//                    response.collison = true;
//                }
//            }
//        }
//        for (GameObject object : map.getSolidMobs(x, y)) {
//            if (checkCollison(x, y, object, circle)) {
//                response.colided.add(object.getCollision());
//                if (checkCollison(x, y, object)) {
//                    response.collison = true;
//                }
//            }
//        }
//        for (GameObject object : map.getSolidObjects(x, y)) {
//            if (checkCollison(x, y, object, circle)) {
//                response.colided.add(object.getCollision());
//                if (checkCollison(x, y, object)) {
//                    response.collison = true;
//                }
//            }
//        }
//        tiles = map.getNearNullTiles(this);
//        for (int i = 0; i < tiles.size(); i++) {
//            setTile(tiles.get(i));
//            if (isCollideSingle(x, y, tempTile)) {
//                response.collison = true;
//                break;
//            }
//        }
//        return response;
//    }
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
        } else {
            return getY();
        }
        //return getOwner().getDepth();
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

    public int getYStart() { // Czy aby potrzebne? <(^=^<)
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
}
