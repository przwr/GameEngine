/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Block;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class PuzzleObject {

    protected ArrayList<TileContainer> bgTiles;
    protected ArrayList<GameObject> objects;
    protected ArrayList<FGTileContainer> fgTiles;
    protected ArrayList<BlockContainer> blocks;

    protected int xDelta, yDelta;
    protected int xBegin, yBegin;
    protected int xEnd, yEnd;
    protected int width, height;

    protected Place place;

    public PuzzleObject(String file, Place place) {
        this.place = place;
        try (BufferedReader input = new BufferedReader(new FileReader("res/objects/" + file + ".puz"))) {
            bgTiles = new ArrayList<>();
            objects = new ArrayList<>();
            fgTiles = new ArrayList<>();
            blocks = new ArrayList<>();

            String line = input.readLine();
            String[] t = line.split(":");
            xDelta = Integer.parseInt(t[0]);
            yDelta = Integer.parseInt(t[1]);

            xBegin = yBegin = Integer.MAX_VALUE;
            xEnd = yEnd = Integer.MIN_VALUE;

            Tile tmpTile;
            FGTileContainer tmpFgt;
            BlockContainer tmpBlock = null;
            int i;
            int tile = place.getTileSize();
            SpriteSheet tmpSS = null;
            while ((line = input.readLine()) != null) {
                t = line.split(":");
                switch (t[0]) {
                    case "t":
                        if (!t[3].equals("")) {
                            tmpSS = place.getSpriteSheet(t[3]);
                        }
                        tmpTile = new Tile(tmpSS, tile, Integer.parseInt(t[4]), Integer.parseInt(t[5]));
                        i = 6;
                        while (i + 1 < t.length) {
                            tmpTile.addTileToStack(Integer.parseInt(t[i]), Integer.parseInt(t[i + 1]));
                            i += 2;
                        }
                        addTile(tmpTile, Integer.parseInt(t[1]), Integer.parseInt(t[2]));
                        checkBoundaries(Integer.parseInt(t[1]), Integer.parseInt(t[2]), 1, 1);
                        break;

                    case "ft":
                        if (!t[4].equals("")) {
                            tmpSS = place.getSpriteSheet(t[4]);
                        }
                        tmpFgt = new FGTileContainer(tmpSS, tile, Integer.parseInt(t[8]), Integer.parseInt(t[9]),
                                t[5].equals("1"), Integer.parseInt(t[6]) * tile, t[7].equals("1"), Integer.parseInt(t[3]) * tile);
                        tmpFgt.xBegin = Integer.parseInt(t[1]) * tile;
                        tmpFgt.yBegin = Integer.parseInt(t[2]) * tile;
                        i = 10;
                        while (i + 1 < t.length) {
                            tmpFgt.additionalPlaces.add(new Point(Integer.parseInt(t[i]), Integer.parseInt(t[i + 1])));
                            i += 2;
                        }
                        if (tmpBlock == null) {
                            fgTiles.add(tmpFgt);
                        } else {
                            tmpBlock.containedFGTs.add(tmpFgt);
                        }
                        checkBoundaries(Integer.parseInt(t[1]), Integer.parseInt(t[2]), 1, 1);
                        break;

                    case "b":
                        tmpBlock = new BlockContainer(Integer.parseInt(t[1]) * tile,
                                Integer.parseInt(t[2]) * tile,
                                Integer.parseInt(t[3]) * tile,
                                Integer.parseInt(t[4]) * tile,
                                Integer.parseInt(t[5]) * tile);
                        blocks.add(tmpBlock);
                        checkBoundaries(Integer.parseInt(t[1]), Integer.parseInt(t[2]), Integer.parseInt(t[3]), Integer.parseInt(t[4]));
                        break;

                    case "rb":
                        tmpBlock = new RoundBlockContainer(Integer.parseInt(t[1]) * tile,
                                Integer.parseInt(t[2]) * tile,
                                Integer.parseInt(t[3]) * tile,
                                Integer.parseInt(t[4]) * tile,
                                Integer.parseInt(t[5]) * tile);
                        ((RoundBlockContainer) tmpBlock).setCorners(new int[]{
                            (t[7].equals("") ? 0 : Integer.parseInt(t[7])),
                            (t[8].equals("") ? 0 : Integer.parseInt(t[8])),
                            (t[9].equals("") ? 0 : Integer.parseInt(t[9])),
                            (t[10].equals("") ? 0 : Integer.parseInt(t[10])),
                            (t[11].equals("") ? 0 : Integer.parseInt(t[11])),
                            (t[12].equals("") ? 0 : Integer.parseInt(t[12])),
                            (t[13].equals("") ? 0 : Integer.parseInt(t[13])),
                            (t[14].equals("") ? 0 : Integer.parseInt(t[14]))});
                        blocks.add(tmpBlock);
                        checkBoundaries(Integer.parseInt(t[1]), Integer.parseInt(t[2]), Integer.parseInt(t[3]), Integer.parseInt(t[4]));
                        break;

                    default:
                        Methods.error("The object \"" + t[0] + "\" is undefined");
                }
            }
            width = Math.abs(xEnd - xBegin) + 1;
            height = Math.abs(yEnd - yBegin) + 1;
            //System.out.println("Tiles: " + bgTiles.size());
            //System.out.println("FGTiles: " + fgTiles.size());
            //System.out.println("Areas: " + areas.size());
            input.close();
        } catch (IOException e) {
            Methods.error("File " + file + " not found!\n" + e.getMessage());
        }
    }

    protected void addTile(Tile tile, int x, int y) {
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

    protected void checkBoundaries(int x, int y, int width, int height) {
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
        int tileSize = map.getTileSize();
        bgTiles.stream().forEach((TileContainer tc) -> {
            tc.places.stream().forEach((p) -> {
                map.setTile(p.getX() + x, p.getY() + y, tc.tile);
            });
        });
        blocks.stream().forEach((block) -> {
            Block tmpBlock = block.generateBlock(x * tileSize, y * tileSize);
            map.addBlock(tmpBlock);
            for (FGTileContainer tile : block.containedFGTs) {
                ForegroundTile fgt = tile.generateFGT(x * tileSize, y * tileSize);
                if (!fgt.isSimpleLighting()) {
                    map.addForegroundTileAndReplace(fgt);
                } else {
                    map.addForegroundTile(fgt);
                }
                tmpBlock.addForegroundTile(fgt);
            }
        });
        objects.stream().forEach((obj) -> {
            map.addObject(obj);
        });
        fgTiles.stream().forEach((tile) -> {
            map.addForegroundTileAndReplace(tile.generateFGT(x * tileSize, y * tileSize));
        });
    }

    public int getxBegin() {
        return xBegin;
    }

    public int getyBegin() {
        return yBegin;
    }

    public int getxEnd() {
        return xEnd;
    }

    public int getyEnd() {
        return yEnd;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected class TileContainer {

        ArrayList<Point> places = new ArrayList<>();
        Tile tile;

        public Tile getTile() {
            return tile;
        }

        public ArrayList<Point> getPlaces() {
            return places;
        }
    }

    protected class FGTileContainer {

        ArrayList<Point> additionalPlaces = new ArrayList<>();
        SpriteSheet texture;
        int[] values;
        boolean wall, round;
        int xBegin, yBegin;

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

        //0  1 2 3       4    5      6          7
        //ft:x:y:texture:wall:yStart:TileXSheet:TileYSheet...
        public FGTileContainer(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean wall, int yStart, boolean round, int depth) {
            texture = spriteSheet;
            values = new int[]{size, xSheet, ySheet, yStart, depth};
            this.wall = wall;
            this.round = round;
        }

        public ForegroundTile generateFGT(int x, int y) {
            ForegroundTile fgt = new ForegroundTile(texture, values[0], values[1], values[2], wall, values[3], round);
            fgt.setX(xBegin + x);
            fgt.setY(yBegin + y);
            fgt.setDepth(values[4]);
            additionalPlaces.stream().forEach((p) -> {
                fgt.addTileToStack(p.getX(), p.getY());
            });
            return fgt;
        }
    }

    protected class BlockContainer {

        int[] values;
        ArrayList<FGTileContainer> containedFGTs = new ArrayList<>();

        public BlockContainer(int x, int y, int width, int height, int shadowHeight) {
            values = new int[]{x, y, width, height, shadowHeight};
        }

        public Block generateBlock(int x, int y) {
            Block a = Block.create(values[0] + x, values[1] + y, values[2], values[3], values[4]);
            return a;
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

        public void setCorners(int[] corners) {
            this.corners = corners;
        }

        public int[] getCorners() {
            return corners;
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
