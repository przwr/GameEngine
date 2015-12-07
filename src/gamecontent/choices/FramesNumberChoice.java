
package gamecontent.choices;

import engine.systemcommunication.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class FramesNumberChoice extends MenuChoice {

    public FramesNumberChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION || button == RIGHT) {
            Settings.framesLimit++;
            if (Settings.framesLimit > 120) {
                Settings.framesLimit = 24;
            }
        } else {
            Settings.framesLimit--;
            if (Settings.framesLimit < 24) {
                Settings.framesLimit = 120;
            }
        }
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        return label + Settings.framesLimit + " fps";
    }
}
