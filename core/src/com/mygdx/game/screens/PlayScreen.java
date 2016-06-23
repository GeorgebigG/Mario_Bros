package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Sprites.Goomba;
import com.mygdx.game.Sprites.Items.Item;
import com.mygdx.game.Sprites.Items.ItemDef;
import com.mygdx.game.Sprites.Items.Mushroom;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Turtle;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gio on 07/04/16.
 */

public class PlayScreen implements Screen {
    public static boolean isPhone;

    public MarioBros game;
    public OrthographicCamera gamecam;
    public Viewport gamePort;
    public Hud hud;

    public TmxMapLoader mapLoader;
    public TiledMap map;
    public OrthogonalTiledMapRenderer renderer;

    public World world;
    public Box2DDebugRenderer b2dr;

    public Mario player;

    public B2WorldCreator creator;

    public Array<Goomba> goombas;

    public Music music;

    public Array<Item> items;
    public LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public TextureAtlas atlas;

    public boolean hasAlreadyJumped = false;

    public Array<Turtle> turtles;

    public PlayScreen(MarioBros game)
    {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;

        //create game camera
        gamecam = new OrthographicCamera();

        //create StretchViewport to have a perfect size in all screen size
        gamePort = new StretchViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);

        //create our game hud for scores, labels, etc.
        hud = new Hud(this, game.batch);

        //load our map in the program;
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create world with gravitation -10.
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        //make our programm know where is bricks, pipes, coins and ground.
        creator = new B2WorldCreator(this);

        //create mario in our world
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);

        goombas = new Array<Goomba>();

        for (int i = 0; i < 3; i++)
            goombas.add(new Goomba(this, ((i * 110) + 564) / MarioBros.PPM, 25 / MarioBros.PPM));
        goombas.add(new Goomba(this, 2768 / MarioBros.PPM, 25 / MarioBros.PPM));

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        turtles = new Array<Turtle>();

        turtles.add(new Turtle(this, 50 / MarioBros.PPM, 40 / MarioBros.PPM));

        music.play();
    }

    public void spawnItem(ItemDef iDef) {
        itemsToSpawn.add(iDef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef iDef = itemsToSpawn.poll();

            if (iDef.type == Mushroom.class) {
                items.add(new Mushroom(this, iDef.position.x, iDef.position.y, player));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void handleInput(float dt) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
            if (player.previousState != Mario.State.JUMPING && player.currentState != Mario.State.FALLING && player.currentState != Mario.State.DIE)
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2 && player.currentState != Mario.State.DIE)
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2 && player.currentState != Mario.State.DIE)
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

        //if (isPhone) {}
    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        if (player.marioDie) {
            if (!hasAlreadyJumped) {
                marioDie();
                hasAlreadyJumped = true;
            }
            if (player.goToGameOverScreen) {
                music.stop();
                game.setScreen(new GameOverScreen(game, hud.score));
            }
        }

        player.update(dt);

        for (Goomba goomba: goombas) {
            goomba.update(dt);

            if (!goomba.b2body.isActive()) {
                if (goomba.getX() < player.getX() + 255 / MarioBros.PPM)
                    goomba.b2body.setActive(true);
            }
        }

        for (Item item : items)
            item.update(dt);

        hud.update(dt);

        gamecam.position.x = player.b2body.getPosition().x;

        gamecam.update();

        renderer.setView(gamecam);
    }

    @Override
    public void show() {
    }

    //davxatot marios msoflio
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        //b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        for (Goomba goomba: goombas)
            goomba.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        //for (Turtle turtle : turtles)
          //  turtle.draw(game.batch);
        player.draw(game.batch);

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public void marioDie() {
        player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
    }
}
