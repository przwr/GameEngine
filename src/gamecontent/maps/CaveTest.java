package gamecontent.maps;

import collision.Rectangle;
import game.logic.DayCycle;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.WarpPoint;
import gamecontent.SpawnPoint;
import gamecontent.mobs.Blazag;
import gamecontent.mobs.Tongub;
import org.newdawn.slick.Color;

import static collision.OpticProperties.TRANSPARENT;
import gamecontent.environment.MoneyBag;
import gamecontent.npcs.Sonata;
import gamecontent.npcs.Zuocieyka;
import sounds.Sound;

/**
 * Created by przemek on 21.11.15.
 */
public class CaveTest extends Map {

    private Sound envSounds;

    public CaveTest(short ID, Place place, int tileSize) {
        super(ID, "CaveTest", place, tileSize);
        
        PuzzleObject puzzle = new PuzzleObject("demo/cave", place);
        initializeAreas((puzzle.getWidth() + 20) * tileSize, (puzzle.getHeight() + 20) * tileSize);
        
        setColor(new Color(DayCycle.NIGHT, DayCycle.NIGHT, DayCycle.NIGHT));    
        puzzle.addTileChanger("grassland", 10, 1, 4,
                7, 10,   8, 10,    9, 10,  10, 10,    11, 10,    12, 10,    13, 10);
        placePuzzle(0, 0, puzzle);

        WarpPoint warp = new WarpPoint("toTestLeft", 27 * tileSize, 17 * tileSize, "Test");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 2 * tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toCaveLeft", 26 * tileSize + tileSize / 2, 18 * tileSize + tileSize / 2));

        warp = new WarpPoint("toTestRight", 82 * tileSize, 23 * tileSize, "Test");
        warp.setCollision(Rectangle.create(0, 0, tileSize, 3 * tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toCaveRight", 81 * tileSize + tileSize / 2, 24 * tileSize + tileSize / 2));
        long start = System.nanoTime();
        generateNavigationMeshes();
        long end = System.nanoTime();
        System.out.println("Navigation mesh for " + GladeMap.class.getSimpleName() + " generated in " + (((end - start)) / 1000000) + " ms");
    }

    @Override
    public void populate() {
        MoneyBag mb = new MoneyBag(4350, 7461, place, mobID++);
        addObject(mb);
        Zuocieyka zuo = new Zuocieyka(2216, 3117, place, mobID++, mb);
        addObject(zuo);
        addObject(new Sonata(1165, 1297, place, mobID++, zuo, mb));
        addObject(SpawnPoint.createInVisible(2176, 5120, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(2880, 4096, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4352, 4864, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(3072, 5568, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4288, 5568, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(3584, 6272, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4544, 7168, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(5056, 7040, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(3648, 6912, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(4096, 8128, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(5504, 7936, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(5568, 6336, 54, 38, "Tongub spawn", Tongub.class, 2, 10));
        addObject(SpawnPoint.createInVisible(6818, 2662, 54, 38, "Blazag spawn", Blazag.class, 5, 10));
        addObject(SpawnPoint.createInVisible(7271, 2771, 54, 38, "Blazag spawn", Blazag.class, 5, 10));
        addObject(SpawnPoint.createInVisible(7126, 2445, 54, 38, "Blazag spawn", Blazag.class, 5, 10));
    }

    //TYMCZASOWE
    @Override
    public void updateEntitesFromAreasToUpdate() {
        super.updateEntitesFromAreasToUpdate();
        if (envSounds != null) {
            if (place.players[0].getMap() == this) {
                envSounds.resume();
            } else {
                envSounds.fade(1000, false);
            }
        } else {
            envSounds = place.getSounds().getSound("env_cave");
        }
    }
}
