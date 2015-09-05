package gamecontent.choices;

import engine.Main;
import engine.inout.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * Created by przemek on 17.08.15.
 */
public class GammaChoice extends MenuChoice {

    public GammaChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION || button == RIGHT) {
            Settings.gameGamma += 0.1f;
            if (Settings.gameGamma > 3.05f) {
                Settings.gameGamma = 1f;
            }
        } else {
            Settings.gameGamma -= 0.1f;
            if (Settings.gameGamma < 1f) {
                Settings.gameGamma = 3f;
            }
        }
        Settings.gameGamma = (Math.round(Settings.gameGamma * 10)) / 10f;
        AnalyzerSettings.update();
        Main.refreshGamma();
    }

    @Override
    public String getLabel() {
        return label + Settings.gameGamma;
    }
}