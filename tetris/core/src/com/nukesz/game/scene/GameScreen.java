package com.nukesz.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nukesz.game.items.Item;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;
    private static final int GRID_CELL = 16;

    private static final float MOVE_TIME = 0.01F;
    private float timer = MOVE_TIME;

    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Array<Item> items = new Array<Item>();

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }



    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void render(float delta) {
        updateItems(delta);
        clearScreen();
        drawGrid();
        drawOneElement();
        draw();
    }

    private void updateItems(float delta) {
        timer -= delta;
        if (timer <= 0) {
            timer = MOVE_TIME;
            if (!hasMovingItem()) {
                createItem();
            }
            moveItems();
        }
    }

    private boolean hasMovingItem() {
        for (Item item: items) {
            if (item.isMoving()) {
                return true;
            }
        }
        return false;
    }

    private void createItem() {
        items.add(new Item((int) (viewport.getWorldWidth() / 2), (int) (viewport.getWorldHeight() - GRID_CELL),
                GRID_CELL * 5, GRID_CELL));
    }

    // TODO One item only
    private void moveItems() {
        items.peek().move(items);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.end();
    }

    private void drawGrid() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < viewport.getWorldWidth(); x += GRID_CELL) {
            for (int y = 0; y < viewport.getWorldHeight(); y += GRID_CELL) {
                shapeRenderer.setColor(Color.DARK_GRAY);
                shapeRenderer.rect(x, y, GRID_CELL, GRID_CELL);
            }
        }
        shapeRenderer.end();
    }

    private void drawOneElement() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        for (Item item: items) {
            item.draw(shapeRenderer);
        }
    }



    @Override
    public void dispose() {
        batch.dispose();
    }
}
