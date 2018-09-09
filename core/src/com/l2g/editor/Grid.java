package com.l2g.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public final class Grid {
    private Array<Vector2>  bot, top, lef, rig;

    private static float step;

    private float
            width,
            height;

    private Rectangle border;
    private boolean isVisible;

    private static float
            sizeWidth = 40,
            sizeHeight;

    public Grid(float width, float height) {
        this.width = width;
        this.height = height;

        step    =   width / sizeWidth;
        sizeHeight = height / step;

        bot     =   new Array<Vector2>();
        top     =   new Array<Vector2>();
        lef     =   new Array<Vector2>();
        rig     =   new Array<Vector2>();

        border = new Rectangle(0, 0, width, height);
        update();
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.DARK_GRAY);

        shapeRenderer.setProjectionMatrix(L2DEditor.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (isVisible) {
            for (int i = 0; i < bot.size; i++)
                L2DEditor.SingletonHolder.getShapeRenderer().line(bot.get(i), top.get(i));

            for (int i = 0; i < lef.size; i++)
                L2DEditor.SingletonHolder.getShapeRenderer().line(lef.get(i), rig.get(i));

            L2DEditor.SingletonHolder.getShapeRenderer().rect(0, 0, L2DEditor.SCREEN_WIDTH, L2DEditor.SCREEN_HEIGHT, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        }

        shapeRenderer.end();
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setSize(int sizeWidth1) {
        sizeWidth = sizeWidth1;

        step = width / sizeWidth;
        sizeHeight = height / step;

        bot.clear();
        top.clear();
        lef.clear();
        rig.clear();
        update();
    }

    private void update() {
        float position = 0;
        for (short i = 0; i < sizeWidth; i++) {
            position += step;
            bot.add(new Vector2(position, 0));
            top.add(new Vector2(position, height));
        }

        position = 0;
        for(short i = 0; i < sizeHeight - 1; i++) {
            position += step;
            lef.add(new Vector2(0, position));
            rig.add(new Vector2(width, position));
        }
    }

    public Vector3 move(float x, float y) {
        float
                x1, y1,
                x2, y2;

        float
                positionX = 0, positionY = 0;

        for (int i = 0; i < bot.size; i++, positionY = 0) {
            x1           =   positionX;
            positionX    +=  step;
            x2           =   positionX;

            for (int j = 0; j < lef.size; j++) {
                y1          =   positionY;
                positionY   +=  step;
                y2          =   positionY;

                if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
                    if (x2 == x && y2 == y) return new Vector3(x, y, 0);
                    return new Vector3(x1, y1, 0);
                }
            }
        }
        return new Vector3(0, 0, 0);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector3 getPosition(Vector3 point) {
        if (!border.contains(point.x, point.y))
            return new Vector3(point);
        else
            return move(point.x, point.y);
    }

    public boolean contains(float x, float y) {
        return border.contains(x, y);
    }

    public static float getStep() {
        return step;
    }
}