package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MarioBros;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 13/04/16.
 */
public class Turtle extends Enemy {
    Vector2 direction;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 32);
        setBounds(x, y, 21 / MarioBros.PPM, 21 / MarioBros.PPM);
        direction = new Vector2(0, -1f);
    }

    @Override
    protected void defineEnemy(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        FixtureDef fdef = new FixtureDef();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.MARIO_BIT |
                MarioBros.COIN_BIT |
                MarioBros.OBJECT_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void update(float dt) {
        b2body.setLinearVelocity(direction);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        //setRegion(walkAnimation.getKeyFrame(stateTimer, true));
    }

    @Override
    public void hitOnHead() {
    }
}
