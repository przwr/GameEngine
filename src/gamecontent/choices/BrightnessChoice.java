package gamecontent.choices;

import engine.Main;
import engine.systemcommunication.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * Created by przemek on 17.08.15.
 */
public class BrightnessChoice extends MenuChoice {

    public BrightnessChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION || button == RIGHT) {
            Settings.gameBrightness += 0.01f;
            if (Settings.gameBrightness > 0.25f) {
                Settings.gameBrightness = 0.25f;
            }
        } else {
            Settings.gameBrightness -= 0.01f;
            if (Settings.gameBrightness < -0.25f) {
                Settings.gameBrightness = -0.25f;
            }
        }
        Settings.gameBrightness = (Math.round(Settings.gameBrightness * 100)) / 100f;
        AnalyzerSettings.update();
        Main.refreshGammaAndBrightness();
    }

    @Override
    public String getLabel() {
        return label + Settings.gameBrightness;
    }
}