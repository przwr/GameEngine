/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Block;
import engine.ErrorHandler;
import engine.Point;
import engine.PointedValue;
import game.gameobject.GameObject;
import sprites.SpriteSheet;

import java.io.BufferedReader;
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
    protected ArrayList<GameObject> objects;
    protected ArrayList<FGTileContainer> fgTiles;
    protected ArrayList<BlockContainer> blocks;
    protected ArrayList<PointedValue> links;
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
    /*----*/

    public PuzzleObject(String file, Place place) {
        this.place = place;
        try (BufferedReader input = new BufferedReader(new FileReader("res/objects/" + file + ".puz"))) {
            bgTiles = new ArrayList<>();
            objects = new ArrayList<>();
            fgTiles = new ArrayList<>();
            blocks = new ArrayList<>();
            links = new ArrayList<>();

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

    protected PuzzleObject(ArrayList<String> map, Place place) {
        this.place = place;
        bgTiles = new ArrayList<>();
        objects = new ArrayList<>();
        fgTiles = new ArrayList<>();
        blocks = new ArrayList<>();
        links = new ArrayList<>();

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
        lineTab = line.split(":");
        switch (lineTab[0]) {
            case "t":
                decodeTile();
                break;

            case "ft":
                decodeFGTile();
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

            default:
                ErrorHandler.error("The object \"" + lineTab[0] + "\" is undefined");
        }
    }

    private void decodeTile() {
        if (!lineTab[3].equals("")) {
            tmpSS = place.getSpriteSheet(lineTab[3], "");
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

    private void decodeFGTile() {
        if (!lineTab[4].equals("")) {
            tmpSS = place.getSpriteSheet(lineTab[4], "");
        }
        tmpFgt = new FGTileContainer(tmpSS, tileSize, Integer.parseInt(lineTab[8]), Integer.parseInt(lineTab[9]),
                lineTab[5].equals("1"), Integer.parseInt(lineTab[6]) * tileSize, lineTab[7].equals("1"), Integer.parseInt(lineTab[3]) * tileSize);
        tmpFgt.xBegin = Integer.parseInt(lineTab[1]) * tileSize;
        tmpFgt.yBegin = Integer.parseInt(lineTab[2]) * tileSize;
        index = 10;
        while (index + 1 < lineTab.length) {
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

    public boolean hasLinks() {
        return links.size() > 0;
    }

    public ArrayList<PointedValue> getLinks() {
        return links;
    }

    private void addTile(Tile tile, int x, int y) {
        for (TileContainer tc : bgTiles) {
            if (tc.tile.equals(tile)) {
                tc.places.add(new Point(x, y));
                return;
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
        bgTiles.stream().forEach((TileContainer tc) -> tc.places.stream().forEach((p) -> map.setTile(p.getX() + x, p.getY() + y, tc.tile)));
        blocks.stream().forEach((block) -> {
            Block tmpBlock = block.generateBlock(x * tileSize, y * tileSize);
            map.addBlock(tmpBlock);
            block.containedFGTs.stream().map((tile) -> tile.generateFGT(x * tileSize, y * tileSize)).map((fgt) -> {
                if (!fgt.isSimpleLighting()) {
                    map.addForegroundTileAndReplace(fgt);
                } else {
                    map.addForegroundTile(fgt);
                }
                return fgt;
            }).forEach(tmpBlock::addForegroundTile);
        });
        objects.stream().forEach(map::addObject);
        fgTiles.stream().forEach((tile) -> map.addForegroundTileAndReplace(tile.generateFGT(x * tileSize, y * tileSize)));
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
        int xBegin, yBegin;

        //0  1 2 3       4    5      6          7
        //ft:x:y:texture:wall:yStart:TileXSheet:TileYSheet...
        public FGTileContainer(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean wall, int yStart, boolean round, int depth) {
            texture = spriteSheet;
            values = new int[]{size, xSheet, ySheet, yStart, depth + (round ? 1 : 0)};
            this.wall = wall;
            this.round = round;
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

        public ForegroundTile generateFGT(int x, int y) {
            ForegroundTile fgt = new ForegroundTile(texture, values[0], values[1], values[2], wall, values[3], round);
            fgt.setPositionAreaUpdate(xBegin + x, yBegin + y);
            fgt.setDepth(values[4]);
            additionalPlaces.stream().forEach((p) -> fgt.addTileToStack(p.getX(), p.getY()));
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
            return Block.create(values[0] + x, values[1] + y, values[2], values[3], values[4]);
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
}
