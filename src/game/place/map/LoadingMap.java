package game.place.map;

import engine.utilities.Delay;
import game.Settings;
import game.place.Place;
import game.place.cameras.Camera;
import game.text.fonts.FontType;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 21.08.15.
 */
public class LoadingMap extends Map {

    Delay delay = Delay.createInMilliseconds(200);
    int progress = -1;
    FontType font;
    TextPiece loading;

    public LoadingMap(Place place) {
        super((short) -1, "Empty", place, Place.tileSize, Place.tileSize, Place.tileSize);
        setColor(new Color(1f, 1f, 1f));
        delay.terminate();
        font = TextMaster.getFont("Lato-Regular");
        loading = new TextPiece(Settings.language.menu.Loading + " . . .", 38, font, Display.getWidth(), true);
    }

    @Override
    public void renderObjects(Camera camera) {
        if (delay.isOver()) {
            delay.start();
            progress++;
            if (progress > 3) progress = 0;
        }
        TextMaster.renderFirstCharactersOnce(loading, 0, camera.getHeight() / 2, loading.getTextString().length() - 6 + progress);
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
