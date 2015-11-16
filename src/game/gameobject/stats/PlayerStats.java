package game.gameobject.stats;

import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;
import game.place.cameras.Camera;
import gamecontent.MyPlayer;

/**
 * Created by przemek on 10.08.15.
 */
public class PlayerStats extends Stats {

    private float energy = 100;
    private float maxEnergy = 100;
    private Player player;

    public PlayerStats(Player owner) {
        super(owner);
        this.player = owner;
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
        player.setPosition(128, 128);
        Camera camera = player.getCamera();
        if (camera != null) {
            camera.updateStatic();
        }
        ((MyPlayer) player).getGUI().deactivate();
        System.out.println(player.getName() + " zginał.");
    }

    @Override
    public void hurtReaction(InteractiveResponse response) {
        super.hurtReaction(response);
        if (health <= 900) {
            System.out.println("____________R.I.P.___________");
        }
        ((MyPlayer) player).getGUI().activateLifeIndicator();
    }

    @Override
    public void reactionWhileProtect() {
        decreaseEnergy(20);
    }

    public void decreaseEnergy(float amount) {
        energy -= amount;
        if (energy <= 0) {
            energy = 0;
        }
        ((MyPlayer) player).getGUI().activateEnergyIndicator();
    }

    public void increaseEnergy(float amount) {
        if (energy < maxEnergy) {
            energy += amount;
            if (energy >= maxEnergy) {
                energy = maxEnergy;
                ((MyPlayer) player).getGUI().activateEnergyIndicator();
            }
        }
    }

    public float getEnergy() {
        return energy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }
}
