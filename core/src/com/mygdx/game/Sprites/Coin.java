package com.mygdx.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Items.ItemDef;
import com.mygdx.game.Sprites.Items.Mushroom;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 08/04/16.
 */
public class Coin extends InteractiveTileObject {
    private TiledMapTileSet tileSet;
    private final int BLANCK_COIN = 28;
    Sound pick, bump;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
        pick = MarioBros.manager.get("audio/sounds/coin.wav", Sound.class);
        bump = MarioBros.manager.get("audio/sounds/bump.wav", Sound.class);
    }

    @Override
    public void onHeadHit() {
        if (getCell().getTile() != tileSet.getTile(BLANCK_COIN)) {
            getCell().setTile(tileSet.getTile(BLANCK_COIN));
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
                Hud.addScore(200);
            }
        } else {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }
}
