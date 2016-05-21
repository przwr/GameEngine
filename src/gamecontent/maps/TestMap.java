package gamecontent.maps;

import collision.Rectangle;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;
import game.place.map.WarpPoint;
import gamecontent.SpawnPoint;
import gamecontent.environment.Fire;
import gamecontent.environment.MoneyBag;
import gamecontent.environment.Rock;
import gamecontent.mobs.Blazag;
import gamecontent.mobs.Dummy;
import gamecontent.mobs.Plurret;
import gamecontent.mobs.Shen;
import gamecontent.npcs.Melodia;
import gamecontent.npcs.Nutka;
import gamecontent.npcs.Tercja;
import sounds.Sound;

import static collision.OpticProperties.TRANSPARENT;

/**
 * Created by przemek on 21.11.15.
 */
public class TestMap extends Map {

    private Sound envSounds;

    public TestMap(short ID, Place place, int tileSize) {
        super(ID, "Test", place, tileSize);

        PuzzleObject puzzle = new PuzzleObject("demo/testMap", place);
        initializeAreas((puzzle.getWidth() + 20) * tileSize, (puzzle.getHeight() + 20) * tileSize);

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
        puzzle.addTileChanger("grassland", 10, 1, 4,
                7, 10, 8, 10, 9, 10, 10, 10, 11, 10, 12, 10, 13, 10);
        placePuzzle(0, 0, puzzle);

        WarpPoint warp = new WarpPoint("toCaveLeft", 35 * tileSize, 67 * tileSize, "CaveTest");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toTestLeft", 36 * tileSize + tileSize / 2, 68 * tileSize));

        warp = new WarpPoint("toCaveRight", 89 * tileSize, 64 * tileSize, "CaveTest");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toTestRight", 90 * tileSize + tileSize / 2, 65 * tileSize));
        generateNavigationMeshes();
    }

    @Override
    public void populate() {
        MoneyBag mb = new MoneyBag(3010, 1400, place);
        addObject(mb);

        Shen shen = new Shen(3010, 1226, place);
        addObject(shen);

        addObject(new Fire(2620, 1240, place));

        Rock rock;
        addObject(new Dummy(4643, 1793, place));
        rock = new Rock(4060, 2976, place);
        addObject(rock);
        rock = new Rock(4060, 6976, place);
        addObject(rock);
        addObject(new Melodia(4034, 1811, place, shen, rock));
        addObject(new Blazag(5473, 3783, place));
        addObject(new Blazag(5722, 3797, place));
        addObject(new Blazag(5311, 3761, place));
        addObject(new Nutka(3663, 3084, place));
        //addObject(new Magician(2906, 763, place));
        Plurret[] plurrets = new Plurret[]{
                new Plurret(7043, 4034, place),
                new Plurret(8618, 3609, place),
                new Plurret(8281, 4084, place)
        };
        addObject(plurrets[0]);
        addObject(plurrets[1]);
        addObject(plurrets[2]);
        addObject(new Tercja(7104, 3667, place, plurrets));

        addObject(SpawnPoint.createInVisible(8780, 3871, 54, 38, "Shen spawn", Shen.class, 7, 3));
    }

    //TYMCZASOWE
    @Override
    public void updateEntitesFromAreasToUpdate() {
        super.updateEntitesFromAreasToUpdate();
        if (envSounds != null) {
            if (place.players[0].getMap() == this) {
                envSounds.playSmooth(1000);
            } else {
                envSounds.stopSmooth(1000);
            }
        } else {
            envSounds = Settings.sounds.getBGSound("env_forest.ogg");
        }
    }
}
