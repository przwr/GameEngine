package gamecontent.choices;

import engine.systemcommunication.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * Created by przemek on 11.04.16.
 */
public class AutoFramesChoice extends MenuChoice {
    public AutoFramesChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        Settings.autoFrames = !Settings.autoFrames;
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        if (Settings.autoFrames) {
            return label + Settings.language.menu.On;
        } else {
            return label + Settings.language.menu.Off;
        }
    }

}
