package game.place;

import engine.Delay;
import engine.Drawer;
import game.Settings;
import game.place.cameras.Camera;
import game.text.FontHandler;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 21.08.15.
 */
public class LoadingMap extends Map {

    Delay delay = new Delay(500);
    int dotsCount = -1;

    {
        delay.start();
    }

    public LoadingMap(Place place) {
        super((short) -1, "Empty", place, Place.tileSize, Place.tileSize, Place.tileSize);
    }

    @Override
    public void renderObjects(Camera camera) {
        String loading = Settings.language.menu.Loading;
        String dots = "";
        if (delay.isOver()) {
            delay.start();
            dotsCount++;
            if (dotsCount > 3) dotsCount = 0;
        }
        for (int i = 0; i < dotsCount; i++) {
            if (i == 0) {
                dots = " .";
            } else {
                dots = " " + dots + ".";
            }
        }
        FontHandler font = Drawer.getFont("Amble-Regular", (int) (camera.getScale() * 48));
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
