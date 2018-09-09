package com.l2g.editor.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.actors.utils.CollisionActor;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.listeners.ActorListener;
import com.l2g.editor.screens.LoadScreen;

public class Bomb extends CollisionActor {
    private UserData userData;

    public enum bombType {
        MINE;

        public static bombType type = MINE;
    }

    public static class UserData {
        private transient Sprite sprite;

        private bombType bombType;
        private short posX, posY, width, height;
        private float rotation;
        private float vertexes[];
        private String bombName, fixtureName;
        private BodyDef.BodyType bodyType;

        public UserData() { }

        public UserData(float posX, float posY, float width, float height, float rotation, bombType bombType) {
            this.posX = (short) (posX / Grid.getStep());
            this.posY = (short) (posY / Grid.getStep());
            this.width = (short) (width / Grid.getStep());
            this.height = (short) (height / Grid.getStep());
            this.rotation = rotation;
            this.bombType = bombType;

            this.fixtureName = Names.fixtureNames[2];
            this.bodyType = BodyDef.BodyType.StaticBody;

            switch (bombType) {
                case MINE:
                    setSprite(LoadScreen.getSpriteAtlas().createSprite("potato_mine"), width * Grid.getStep(), height * Grid.getStep());
                    this.vertexes = new float [] {0, 0, width / L2DEditor.PPM , 0, width / L2DEditor.PPM , height / L2DEditor.PPM, 0, height / L2DEditor.PPM, 0, 0};
                    this.fixtureName += bombType.MINE.toString();
                    break;
            }
        }

        private void setSprite(Sprite sprite, float width, float height) {
            this.sprite = sprite;
            this.width = (short) (width / Grid.getStep());
            this.height = (short) (height / Grid.getStep());

            sprite.setSize(width, height);
            sprite.setOrigin(sprite.getWidth() *.5f, sprite.getHeight() * .5f);
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;
        }

        public short getWidth() {
            return width;
        }

        public short getHeight() {
            return height;
        }

        public float getRotation() {
            return rotation;
        }

        public Bomb.bombType getBombType() {
            return bombType;
        }
    }

    public Bomb(float posX, float posY, float width, float height, float rotation, bombType bombType) {
        userData = new UserData(posX, posY, width, height, rotation, bombType);

        body = PhysicalElementsFactory.createPolygon(userData.posX * Grid.getStep(), userData.posY * Grid.getStep(), userData.vertexes, userData.rotation, null, userData.bodyType, userData.fixtureName, false);

        setBounds(posX, posY, width, height);
        setTouchable(Touchable.enabled);
        addListener(new ActorListener());

        Bomb.this.setUserObject(userData);
    }

    public void setBodyName(String string) {
        if (body != null) {
            body.setUserData(string);
            userData.bombName = string;
        }
    }

    @Override
    protected void positionChanged() {
        userData.posX = (short) (getX() / Grid.getStep());
        userData.posY = (short) (getY() / Grid.getStep());

        super.positionChanged();
    }

    @Override
    protected void rotationChanged() {
        userData.rotation = getRotation();

        super.rotationChanged();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(userData.sprite, getX(), getY(), getOriginX(),
                getOriginY(), getWidth(), getHeight(), getScaleX(),
                getScaleY(), userData.rotation);
        super.draw(batch, parentAlpha);
    }
}