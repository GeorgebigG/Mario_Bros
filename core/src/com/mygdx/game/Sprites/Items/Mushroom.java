package com.mygdx.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.screens.PlayScreen;

/**
 * Created by gio on 10/04/16.
 */
public class Mushroom extends Item {
    Mario mario;

    public Mushroom(PlayScreen screen, float x, float y, Mario mario) {
        super(screen, x, y);
        this.mario = mario;
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ITEM_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use() {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        //b2body.setLinearVelocity(velocity);
        velocity.y = b2body.getLinearVelocity().y;
        b2body.setLinearVelocity(velocity);
    }
}
