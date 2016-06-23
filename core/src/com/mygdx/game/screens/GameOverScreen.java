package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;

/**
 * Created by gio on 12/04/16.
 */
public class GameOverScreen implements Screen {

    Viewport gameOverViewport;
    Stage gameOverStage;
    Label gameOverLabel;
    Label touchToScreenToPlayAgain;
    Label scoreLabel;
    MarioBros game;

    public GameOverScreen(MarioBros game, Integer score) {
        gameOverViewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());

        gameOverStage = new Stage(gameOverViewport);
        this.game = game;

        Table GameOverTable = new Table();
        GameOverTable.center();
        GameOverTable.setFillParent(true);

        gameOverLabel = new Label("Game over!!!",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        touchToScreenToPlayAgain = new Label("Press the screen to play again!", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label("Your score is: " + score, new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        GameOverTable.add(gameOverLabel).expandX().pad(10);
        GameOverTable.row();
        GameOverTable.add(touchToScreenToPlayAgain).expandX().pad(10);
        GameOverTable.row();
        GameOverTable.add(scoreLabel).expandX().pad(10);

        gameOverStage.addActor(GameOverTable);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (Gdx.input.justTouched()) {
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).stop();
            game.setScreen(new PlayScreen(game));
        }
    }

    public void update(float dt) {
        handleInput(dt);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        gameOverStage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
}
