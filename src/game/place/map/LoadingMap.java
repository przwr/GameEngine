package game.place.map;

import engine.utilities.Delay;
import engine.utilities.Drawer;
import game.Settings;
import game.place.Place;
import game.place.cameras.Camera;
import game.text.FontHandler;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 21.08.15.
 */
public class LoadingMap extends Map {

    Delay delay = Delay.createInMilliseconds(200);
    int progress = -1;

    public LoadingMap(Place place) {
        super((short) -1, "Empty", place, Place.tileSize, Place.tileSize, Place.tileSize);
        setColor(new Color(1f, 1f, 1f));
        delay.terminate();
    }

    @Override
    public void renderObjects(Camera camera) {
        String loading = Settings.language.menu.Loading;
        String dots = "";
        if (delay.isOver()) {
            delay.start();
            progress++;
            if (progress > 3) progress = 0;
        }
        for (int i = 0; i < progress; i++) {
            if (i == 0) {
                dots = " .";
            } else {
                dots = " " + dots + ".";
            }
        }
        FontHandler font = place.fonts.getFont("Amble-Regular", 0, (int) (camera.getScale() * 48));
        Drawer.renderStringCentered(loading, camera.getWidth() / 2, camera.getHeight() / 2, font, Color.white);
        Drawer.renderStringCentered(dots, (camera.getWidth() + font.getWidth(loading)) / 2, camera.getHeight() / 2, font, Color.white);
    }

    @Override
    public void populate() {
    }


    @Override
    public int getAreaIndex(int x, int y) {
        return 0;
    }

    public Area getArea(int i) {
        return areas[0];
    }
}
