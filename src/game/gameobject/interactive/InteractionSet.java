package game.gameobject.interactive;

import java.util.HashSet;
import java.util.Set;

import static game.gameobject.items.Weapon.UNIVERSAL;

/**
 * Created by przemek on 18.09.15.
 */
public class InteractionSet {

    private final byte weaponType;
    private final InteractionPair[] pairs = new InteractionPair[4];
    private int activePair;


    public InteractionSet(byte weaponType) {
        this.weaponType = weaponType;
        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = new InteractionPair();
        }
    }

    public boolean setInteraction(int pair, int slot, Interactive interaction) {
        if (interaction.getWeaponType() == weaponType || interaction.getWeaponType() == UNIVERSAL) {
            if (slot == 0) {
                pairs[pair].firstAction = interaction;
            } else {
                pairs[pair].secondAction = interaction;

            }
            return true;
        } else {
            System.out.println("Bad WeaponType");
            return false;
        }
    }

    public boolean addInteractionToNextFree(Interactive interaction) {
        if (interaction.getWeaponType() == weaponType || interaction.getWeaponType() == UNIVERSAL) {
            for (InteractionPair pair : pairs) {
                if (pair.firstAction == null) {
                    pair.firstAction = interaction;
                    return true;
                } else if (pair.secondAction == null) {
                    pair.secondAction = interaction;
                    return true;
                }
            }
            return false;
        } else {
            System.out.println("Bad WeaponType");
            return false;
        }
    }

    public void removeInteraction(int pair, int slot) {
        if (slot == 0) {
            pairs[pair].firstAction = null;
        } else {
            pairs[pair].secondAction = null;
        }
    }

    public Set<Interactive> getAllInteractives() {
        Set<Interactive> interactives = new HashSet<>();
        for (InteractionPair pair : pairs) {
            if (pair.firstAction != null) {
                interactives.add(pair.firstAction);
            }
            if (pair.secondAction != null) {
                interactives.add(pair.secondAction);
            }
        }
        return interactives;
    }

    public Interactive getFirstInteractive() {
        return pairs[activePair].firstAction;
    }

    public Interactive getSecondInteractive() {
        return pairs[activePair].secondAction;
    }

    public int getActivePair() {
        return activePair;
    }

    public void setActivePair(int activePair) {
        this.activePair = activePair;
    }

    public byte getWeaponType() {
        return weaponType;
    }


}



