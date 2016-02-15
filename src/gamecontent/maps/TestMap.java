package gamecontent.maps;

import static collision.OpticProperties.IN_SHADE_NO_SHADOW;
import collision.Rectangle;
import engine.utilities.RandomGenerator;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;
import game.place.map.WarpPoint;
import gamecontent.Bush;
import gamecontent.GrassClump;
import gamecontent.Tree;
import gamecontent.mobs.Blazag;
import gamecontent.mobs.Dummy;
import gamecontent.mobs.Rock;
import gamecontent.mobs.Shen;
import gamecontent.npcs.Magician;
import gamecontent.npcs.Melodia;
import gamecontent.npcs.Nutka;

/**
 * Created by przemek on 21.11.15.
 */
public class TestMap extends Map {

    public TestMap(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "Test", place, width, height, tileSize);
        Tile GRASS = new Tile(place.getSpriteSheet("grassland", "backgrounds"), 1, 1);
        Tile[] flowers = new Tile[7];
        for (int i = 0; i < flowers.length; i++) {
            flowers[i] = new Tile(place.getSpriteSheet("grassland", "backgrounds"), i, 10);
        }
        RandomGenerator rand = RandomGenerator.create();

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                if (!rand.chance(5)) {
                    setTile(x, y, GRASS);
                } else {
                    setTile(x, y, flowers[rand.random(flowers.length - 1)]);
                }
            }
        }
        placePuzzle(0, 0, new PuzzleObject("demo/testMap", place));
        
        WarpPoint warp = new WarpPoint("toCaveLeft", 35 * tileSize, 67 * tileSize, "CaveTest");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toTestLeft", 36 * tileSize + tileSize / 2, 68 * tileSize));
        generateNavigationMeshes();
    }

    @Override
    public void populate() {
        Shen shen = new Shen(3010, 1226, place, mobID++);
        addObject(shen);

        Rock rock;
        addObject(new Dummy(4643, 1793, place, mobID++));
        rock = new Rock(4060, 2976, place, mobID++);
        addObject(rock);
        addObject(new Melodia(4034, 1811, place, mobID++, shen, rock));
        addObject(new Blazag(3808, 4010, place, mobID++));
        addObject(new Blazag(4522, 4122, place, mobID++));
        addObject(new Blazag(3352, 4337, place, mobID++));
        rock = new Rock(2770, 4361, place, mobID++);
        addObject(rock);
        addObject(new Nutka(3663, 3084, place, mobID++, rock));
        addObject(new Magician(2906, 763, place, mobID++));        
    }
}
