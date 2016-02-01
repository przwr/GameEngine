package gamecontent.choices;

import engine.Launcher;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * Created by przemek on 01.02.16.
 */
public class ApplyChoice extends MenuChoice {

    public ApplyChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION) {
            Launcher.restart = true;
            menu.game.exit();
        }
    }
}
