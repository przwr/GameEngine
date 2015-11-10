package game.gameobject.stats;

import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;
import game.place.cameras.Camera;
import gamecontent.MyPlayer;

/**
 * Created by przemek on 10.08.15.
 */
public class PlayerStats extends Stats {

    public PlayerStats(Player owner) {
        super(owner);
        strength = 20;
        sideDefenceModifier = 0.8f;
        backDefenceModifier = 0.5f;
        protection = 10;
        protectionSideModifier = 0.8f;
        protectionBackModifier = 0.1f;
    }

    @Override
    public void died() {
        health = maxHealth / 2;
        owner.setPosition(128, 128);
        Camera camera = ((Player) owner).getCamera();
        if (camera != null) {
            camera.updateStatic();
        }
        ((MyPlayer) owner).getGUI().deactivate();
        System.out.println(owner.getName() + " zginał.");
    }

    @Override
    public void hurtReaction(InteractiveResponse response) {
        super.hurtReaction(response);
        ((MyPlayer) owner).getGUI().activateLifeIndicator();
    }

}
