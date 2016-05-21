/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.environment;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Drawer;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.items.Item;
import game.place.Place;
import game.text.effects.TextController;


/**
 * @author Wojtek
 */
public class MoneyBag extends Item {

    public MoneyBag(int x, int y, Place place) {
        super(x, y, "moneyBag", place, 1, "money", place.getNextItemID());
        setCollision(Rectangle.create(appearance.getActualWidth() / 2, appearance.getActualWidth() / 2, OpticProperties.NO_SHADOW, this));
        setSolid(false);
        setHasStaticShadow(true);
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            TextController text = player.getTextController();
            text.lockEntity(player);
            text.startFromText(new String[]{
                    "Aria włożyła $cfFF0000$sakiewkę z pieniędzmi$CN do kieszeni."
            });
            text.addEventOnBranchEnd(() -> {
                this.delete();
                player.addItem(this);
            }, "0");
        }
    }


    @Override
    public void update() {
//        if (target != null && ((Player) getTarget()).isInGame()) {
//            MyPlayer player = (MyPlayer) target;
//            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
//            if (isPlayerTalkingToMe(player)) {
//                TextController text = player.getTextController();
//                text.lockEntity(player);
//                text.startFromText(new String[]{
//                        "Aria włożyła $cfFF0000$sakiewkę z pieniędzmi$CN do kieszeni."
//                });
//                text.addEventOnBranchEnd(this::delete, "0");
//            }
//            if (d > hearRange * 1.5 || getTarget().getMap() != map) {
//                target = null;
//            }
//        } else {
//            lookForPlayers(place.players);
//        }
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            appearance.render();
        }
    }
}
