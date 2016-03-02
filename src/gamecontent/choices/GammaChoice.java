package gamecontent.choices;

import engine.Main;
import engine.systemcommunication.AnalyzerSettings;
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
            Settings.gameGamma += 0.05f;
            if (Settings.gameGamma > 3.05f) {
                Settings.gameGamma = 3f;
            }
        } else {
            Settings.gameGamma -= 0.05f;
            if (Settings.gameGamma < 1f) {
                Settings.gameGamma = 1f;
            }
        }
        Settings.gameGamma = (Math.round(Settings.gameGamma * 20)) / 20f;
        AnalyzerSettings.update();
        Main.refreshGammaAndBrightness();
    }

    @Override
    public String getLabel() {
        return label + Settings.gameGamma + (((int) (Settings.gameGamma * 100) % 10 == 0) ? "0" : "");
    }
}