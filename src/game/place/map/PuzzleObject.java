/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Block;
import collision.OpticProperties;
import engine.utilities.ErrorHandler;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.PointedValue;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.place.Place;
import sprites.SpriteSheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Wojtek
 */
public class PuzzleObject {

    private final Place place;
    private final int tileSize = Place.tileSize;
    protected ArrayList<TileContainer> bgTiles;
    protected ArrayList<FGTileContainer> fgTiles;
    protected ArrayList<BlockContainer> blocks;
    protected ArrayList<PointedValue> links;
    protected ArrayList<MapObjectContainer> mapObjects;
    private ArrayList<TileChanger> changers;
    protected int xDelta, yDelta;
    private int xBegin;
    private int yBegin;
    private int xEnd;
    private int yEnd;
    private int width;
    private int height;
    /*----*/
    private Tile tmpTile;
    private FGTileContainer tmpFgt;
    private BlockContainer tempBlock = null;
    private int index;
    private SpriteSheet tmpSS = null;
    private String[] lineTab;
    private boolean repeatTiles;
    /*----*/
    private RandomGenerator rand;

    int lineNum = 2;

    public PuzzleObject(File file, Place place, boolean repeatTiles) {
        this.place = place;
        this.repeatTiles = repeatTiles;
        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            lineNum = 2;
            bgTiles = new ArrayList<>();
            fgTiles = new ArrayList<>();
            blocks = new ArrayList<>();
            links = new ArrayList<>();
            mapObjects = new ArrayList<>();
            changers = new ArrayList<>();

            String line = input.readLine();
            lineTab = line.split(":");
            xDelta = Integer.parseInt(lineTab[0]);
            yDelta = Integer.parseInt(lineTab[1]);

            xBegin = yBegin = Integer.MAX_VALUE;
            xEnd = yEnd = Integer.MIN_VALUE;

            while ((line = input.readLine()) != null) {
                readLine(line);
            }
            width = Math.abs(xEnd - xBegin) + 1;
            height = Math.abs(yEnd - yBegin) + 1;
            input.close();
        } catch (IOException e) {
            ErrorHandler.error("File " + file + " not found!\n" + e.getMessage());
        }
    }

    public PuzzleObject(String file, Place place) {
        this(file, place, false);
    }

    public PuzzleObject(String file, Place place, boolean repeatTiles) {
        this(new File("res/objects/" + file + ".puz"), place, repeatTiles);
    }

    protected PuzzleObject(ArrayList<String> map, Place place) {
        lineNum = 2;
        this.place = place;
        bgTiles = new ArrayList<>();
        fgTiles = new ArrayList<>();
        blocks = new ArrayList<>();
        links = new ArrayList<>();
        changers = new ArrayList<>();
        mapObjects = new ArrayList<>();

        lineTab = map.get(0).split(":");
        xDelta = Integer.parseInt(lineTab[0]);
        yDelta = Integer.parseInt(lineTab[1]);

        xBegin = yBegin = Integer.MAX_VALUE;
        xEnd = yEnd = Integer.MIN_VALUE;

        for (int i = 1; i < map.size(); i++) {
            readLine(map.get(i));
        }
        width = Math.abs(xEnd - xBegin) + 1;
        height = Math.abs(yEnd - yBegin) + 1;
    }

    private void readLine(String line) {
        try {
            lineTab = line.split(":");
            switch (lineTab[0]) {
                case "t":
                    decodeTile();
                    break;
                case "ft":
                    decodeFGTile(false);
                    break;
                case "sft":
                    decodeFGTile(true);
                    break;
                case "b":
                    decodeBlock();
                    break;
                case "rb":
                    decodeRoundedBlock();
                    break;
                case "pl":
                    decodePortLinks();
                    break;
                case "o":
                    mapObjects.add(new MapObjectContainer(lineTab));
                    break;
                default:
                    ErrorHandler.error("The object \"" + lineTab[0] + "\" is undefined");
            }
            lineNum++;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + " at line " + lineNum);
        }
    }

    private void decodeTile() {
        if (!lineTab[3].equals("")) {
            tmpSS = place.getSpriteSheet(lineTab[3], "backgrounds");
        }
        tmpTile = new Tile(tmpSS, Integer.parseInt(lineTab[4]), Integer.parseInt(lineTab[5]));
        index = 6;
        while (index + 1 < lineTab.length) {
            tmpTile.addTileToStack(Integer.parseInt(lineTab[index]), Integer.parseInt(lineTab[index + 1]));
            index += 2;
        }
        addTile(tmpTile, Integer.parseInt(lineTab[1]), Integer.parseInt(lineTab[2]));
        checkBoundaries(Integer.parseInt(lineTab[1]), Integer.parseInt(lineTab[2]), 1, 1);
    }

    private void decodeFGTile(boolean shadowLightBased) {
        if (!lineTab[4].equals("")) {
            tmpSS = place.getSpriteSheet(lineTab[4], "backgrounds");
        }
        tmpFgt = new FGTileContainer(tmpSS, tileSize, Integer.parseInt(lineTab[8]), Integer.parseInt(lineTab[9]),
                lineTab[5].equals("1"), Integer.parseInt(lineTab[6]) * tileSize, lineTab[7].equals("1"), Integer.parseInt(lineTab[3]) * (tileSize / 2));
        if (shadowLightBased) {
            tmpFgt.setShadowLightBased(lineTab[lineTab.length - 1].equals("1"));
        }
        tmpFgt.xBegin = Integer.parseInt(lineTab[1]) * tileSize;
        tmpFgt.yBegin = Integer.parseInt(lineTab[2]) * tileSize;
        index = 10;
        while (index + 1 < lineTab.length - (shadowLightBased ? 1 : 0)) {
            tmpFgt.additionalPlaces.add(new Point(Integer.parseInt(lineTab[index]), Integer.parseInt(lineTab[index + 1])));
            index += 2;
        }
        if (tempBlock == null) {
            fgTiles.add(tmpFgt);
        } else {
            tempBlock.containedFGTs.add(tmpFgt);
        }
        checkBoundaries(Integer.parseInt(lineTab[1]), Integer.parseInt(lineTab[2]), 1, 1);
    }

    private void decodeBlock() {
        tempBlock = new BlockContainer(Integer.parseInt(lineTab[1]) * tileSize,
                Integer.parseInt(lineTab[2]) * tileSize,
                Integer.parseInt(lineTab[3]) * tileSize,
                Integer.parseInt(lineTab[4]) * tileSize,
                Integer.parseInt(lineTab[5]) * tileSize);
        blocks.add(tempBlock);
        checkBoundaries(Integer.parseInt(lineTab[1]), Integer.parseInt(lineTab[2]), Integer.parseInt(lineTab[3]), Integer.parseInt(lineTab[4]));
    }

    private void decodeRoundedBlock() {
        tempBlock = new RoundBlockContainer(Integer.parseInt(lineTab[1]) * tileSize,
                Integer.parseInt(lineTab[2]) * tileSize,
                Integer.parseInt(lineTab[3]) * tileSize,
                Integer.parseInt(lineTab[4]) * tileSize,
                Integer.parseInt(lineTab[5]) * tileSize);
        ((RoundBlockContainer) tempBlock).setCorners(new int[]{
            (lineTab[7].equals("") ? 0 : Integer.parseInt(lineTab[7])),
            (lineTab[8].equals("") ? 0 : Integer.parseInt(lineTab[8])),
            (lineTab[9].equals("") ? 0 : Integer.parseInt(lineTab[9])),
            (lineTab[10].equals("") ? 0 : Integer.parseInt(lineTab[10])),
            (lineTab[11].equals("") ? 0 : Integer.parseInt(lineTab[11])),
            (lineTab[12].equals("") ? 0 : Integer.parseInt(lineTab[12])),
            (lineTab[13].equals("") ? 0 : Integer.parseInt(lineTab[13])),
            (lineTab[14].equals("") ? 0 : Integer.parseInt(lineTab[14]))});
        blocks.add(tempBlock);
        checkBoundaries(Integer.parseInt(lineTab[1]), Integer.parseInt(lineTab[2]), Integer.parseInt(lineTab[3]), Integer.parseInt(lineTab[4]));
    }

    private void decodePortLinks() {
        PointedValue pv;
        index = 1;
        while (index + 2 < lineTab.length) {
            pv = new PointedValue(Integer.parseInt(lineTab[index]), Integer.parseInt(lineTab[index + 1]), Integer.parseInt(lineTab[index + 2]));
            links.add(pv);
            index += 3;
        }
    }

    public void addTileChanger(String sprite, int chance, int fromX, int fromY, int... to) {
        if (to.length % 2 == 0) {
            Point from = new Point(fromX, fromY);
            Point[] tab = new Point[to.length / 2];
            for (int i = 0; i < tab.length; i++) {
                tab[i] = new Point(to[2 * i], to[2 * i + 1]);
            }
            SpriteSheet s = null;
            for (FGTileContainer fgt : fgTiles) {
                if (fgt.texture.getKey().equals(sprite)) {
                    s = fgt.texture;
                }
            }
            if (s == null) {
                throw new RuntimeException("This puzzle does not use texture '" + sprite + "'");
            }
            changers.add(new TileChanger(s, from, tab, chance));
        } else {
            throw new RuntimeException("Argument 'to' has to have an even number of integers!");
        }
    }

    private Point changeTile(SpriteSheet s, Point p) {
        for (TileChanger tc : changers) {
            return tc.change(s, p);
        }
        return p;
    }

    public boolean hasLinks() {
        return links.size() > 0;
    }

    public ArrayList<PointedValue> getLinks() {
        return links;
    }

    private void addTile(Tile tile, int x, int y) {
        if (!repeatTiles) {
            for (TileContainer tc : bgTiles) {
                if (tc.tile.isTheSame(tile)) {
                    tc.places.add(new Point(x, y));
                    return;
                }
            }
        }
        TileContainer tmpContainer = new TileContainer();
        tmpContainer.tile = tile;
        tmpContainer.places.add(new Point(x, y));
        bgTiles.add(tmpContainer);
    }

    private void checkBoundaries(int x, int y, int width, int height) {
        if (x < xBegin) {
            xBegin = x;
        }
        if (y < yBegin) {
            yBegin = y;
        }
        if (x + width > xEnd) {
            xEnd = x + width;
        }
        if (y + height > yEnd) {
            yEnd = y + height;
        }
    }

    public Point getStartingPoint() {
        return new Point(xDelta, yDelta);
    }

    public void placePuzzle(int x, int y, Map map) {
        rand = RandomGenerator.create();
        placePuzzle(x, y, map, rand);
    }

    public void placePuzzle(int x, int y, Map map, RandomGenerator rand) {
        this.rand = rand;
        bgTiles.stream().forEach((tc) -> {
            tc.places.stream().forEach((p) -> {
                Point point = changeTile(tc.tile.spriteSheet, tc.tile.getPointFormStack(0));
                Tile tmp = new Tile(tc.tile.spriteSheet, point.getX(), point.getY());
                int i = 1;
                while ((point = tc.tile.getPointFormStack(i)) != null) {
                    point = changeTile(tc.tile.spriteSheet, point);
                    tmp.addTileToStack(point.getX(), point.getY());
                    i++;
                }
                map.setTile(p.getX() + x, p.getY() + y, tmp);
            });
        });
        blocks.stream().forEach((block) -> {
            Block tmpBlock = block.generateBlock(x * tileSize, y * tileSize);
            map.addBlock(tmpBlock);
            block.containedFGTs.stream().map((tile) -> tile.generateFGT(x * tileSize, y * tileSize, false)).map((fgt) -> {
                if (!fgt.isSimpleLighting()) {
                    map.addForegroundTile(fgt);
                } else {
                    map.addForegroundTile(fgt);
                }
                return fgt;
            }).forEach(tmpBlock::addForegroundTile);
        });
        fgTiles.stream().forEach((tile) -> {
            ForegroundTile fgt = tile.generateFGT(x * tileSize, y * tileSize, false);
            /*fgt.getCollision().setOpticProperties(OpticProperties.FULL_SHADOW);
             fgt.setSimpleLighting(false);*/
            map.addForegroundTile(fgt);
        });
        mapObjects.stream().forEach((moc) -> {
            map.addObject(moc.generateObject(x * tileSize, y * tileSize, rand));
        });
    }

    public int getXBegin() {
        return xBegin;
    }

    public int getYBegin() {
        return yBegin;
    }

    public int getXEnd() {
        return xEnd;
    }

    public int getYEnd() {
        return yEnd;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected class TileContainer {

        final ArrayList<Point> places = new ArrayList<>();
        Tile tile;

        public Tile getTile() {
            return tile;
        }

        public ArrayList<Point> getPlaces() {
            return places;
        }
    }

    protected class FGTileContainer {

        final ArrayList<Point> additionalPlaces = new ArrayList<>();
        final SpriteSheet texture;
        final int[] values;
        final boolean wall;
        final boolean round;

        boolean shadowLightBased;
        boolean isShadow;
        int xBegin, yBegin;

        //0  1 2 3       4    5      6          7
        //ft:x:y:texture:wall:yStart:TileXSheet:TileYSheet...
        public FGTileContainer(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean wall, int yStart, boolean round, int depth) {
            texture = spriteSheet;
            values = new int[]{size, xSheet, ySheet, yStart, depth + (round ? 1 : 0)};
            this.wall = wall;
            this.round = round;
        }

        public void setShadowLightBased(boolean isShadow) {
            shadowLightBased = true;
            this.isShadow = isShadow;
        }

        public void setBeginning(int x, int y) {
            xBegin = x;
            yBegin = y;
        }

        public int getXBegin() {
            return xBegin;
        }

        public int getYBegin() {
            return yBegin;
        }

        public void addPlace(Point p) {
            additionalPlaces.add(p);
        }

        public int[] getValues() {
            return values;
        }

        public SpriteSheet getTexture() {
            return texture;
        }

        public boolean getRound() {
            return round;
        }

        public ForegroundTile generateFGT(int x, int y, boolean objectFGT) {
            ForegroundTile fgt;
            Point firstTile = changeTile(texture, new Point(values[1], values[2]));
            if (!shadowLightBased) {
                if (!objectFGT) {
                    fgt = new ForegroundTile(texture, values[0], firstTile.getX(), firstTile.getY(), wall, values[3], round);
                } else {
                    fgt = new ObjectFGTile(texture, values[0], firstTile.getX(), firstTile.getY(), wall, values[3], round);
                }
            } else {
                fgt = new ShadowLightTile(texture, values[0], firstTile.getX(), firstTile.getY(), wall, values[3], round, isShadow);
            }
            fgt.setPosition(xBegin + x, yBegin + y);
            fgt.setDepth(values[4]);
            additionalPlaces.stream().forEach((p) -> {
                p = changeTile(texture, p);
                fgt.addTileToStack(p.getX(), p.getY());
            });
            return fgt;
        }
    }

    protected class BlockContainer {

        final int[] values;
        final ArrayList<FGTileContainer> containedFGTs = new ArrayList<>();

        public BlockContainer(int x, int y, int width, int height, int shadowHeight) {
            values = new int[]{x, y, width, height, shadowHeight};
        }

        public Block generateBlock(int x, int y) {
            Block b = Block.create(values[0] + x, values[1] + y, values[2], values[3], values[4]);
            if ((values[4] + values[3]) / tileSize == 0) {
                b.getCollision().setOpticProperties(collision.OpticProperties.TRANSPARENT);
            }
            return b;
        }

        public ArrayList<FGTileContainer> getForegroundTiles() {
            return containedFGTs;
        }

        public int[] getValues() {
            return values;
        }

        public int getY() {
            return values[1];
        }
    }

    protected class RoundBlockContainer extends BlockContainer {

        int[] corners;

        public RoundBlockContainer(int x, int y, int width, int height, int shadowHeight) {
            super(x, y, width, height, shadowHeight);
        }

        public int[] getCorners() {
            return corners;
        }

        public void setCorners(int[] corners) {
            this.corners = corners;
        }

        @Override
        public Block generateBlock(int x, int y) {
            Block b = Block.createRound(values[0] + x, values[1] + y, values[2], values[3], values[4]);
            for (int i = 0; i < 4; i++) {
                if (corners[2 * i] + corners[2 * i + 1] != 0) {
                    b.pushCorner(i, corners[2 * i], corners[2 * i + 1]);
                }
            }
            return b;
        }
    }

    private class TileChanger {

        SpriteSheet texture;
        Point from;
        Point[] to;
        int chance;

        public TileChanger(SpriteSheet texture, Point from, Point[] to, int chance) {
            this.texture = texture;
            this.from = from;
            this.to = to;
            this.chance = chance;
        }

        public Point change(SpriteSheet tex, Point p) {
            if (tex == texture && p.equals(from) && rand.chance(chance)) {
                int i = rand.random(to.length - 1);
                return new Point(to[i].getX(), to[i].getY());
            }
            return p;
        }
    }
}
