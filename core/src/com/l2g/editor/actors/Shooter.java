package com.l2g.editor.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.actors.utils.CollisionActor;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.listeners.ActorListener;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.screens.LoadScreen;

public class Shooter extends CollisionActor {
    public static final short SHOOTER_WIDTH = 40, SHOOTER_HEIGHT = 50;

    private UserData userData;

    public enum shooterType {
        SIMPLE, DOUBLE;

        public static shooterType type = SIMPLE;
    }

    public static class UserData {
        private transient Sprite sprite;

        private Shooter.shooterType shooterType;
        private ExplosiveElementsFactory.shellType shellType;
        private short posX, posY, width, height;
        private float rotation, angle,
                linearVelocity, angularVelocity,
                linearDamping, angularDamping,
                size, friction, restitution, density;
        private float vertexes[];
        private String shooterName, fixtureName;
        private BodyDef.BodyType bodyType;

        public UserData() { }

        public UserData(float posX, float posY, float width, float height, float rotation, float angle, shooterType shooterType, ExplosiveElementsFactory.shellType shellType) {
            this.posX = (short) (posX / Grid.getStep());
            this.posY = (short) (posY / Grid.getStep());
            this.width = (short) (width / Grid.getStep());
            this.height = (short) (height / Grid.getStep());
            this.rotation = rotation;
            this.shooterType = shooterType;
            this.shellType = shellType;
            this.angle = angle;

            this.fixtureName = Names.fixtureNames[3];
            this.bodyType = BodyDef.BodyType.StaticBody;

            switch (shooterType) {
                case SIMPLE:
                    setSprite(LoadScreen.getSpriteAtlas().createSprite("simple_cannon"), width * Grid.getStep(), height * Grid.getStep());
                    this.vertexes = new float [] {0, 0, width / L2DEditor.PPM , 0, width / L2DEditor.PPM , height / L2DEditor.PPM, 0, height / L2DEditor.PPM, 0, 0};
                    this.fixtureName += shooterType.SIMPLE.toString();
                    break;
                case DOUBLE:
                    setSprite(LoadScreen.getSpriteAtlas().createSprite("double_cannon"), width * Grid.getStep(), height * Grid.getStep());
                    this.vertexes = new float [] {0, 0, width / L2DEditor.PPM , 0, width / L2DEditor.PPM , height / L2DEditor.PPM, 0, height / L2DEditor.PPM, 0, 0};
                    this.fixtureName += shooterType.DOUBLE.toString();
                    break;
            }

            /*
            switch (shellType) {
                case CANNONBALL:
                    break;
                case LANDMINE:
                    break;
            }
            */
        }

        public void setSprite(Sprite sprite, float width, float height) {
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

        public void setSize(float size) {
            this.size = size;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public float getRotation() {
            return rotation;
        }

        public Shooter.shooterType getShooterType() {
            return shooterType;
        }

        public ExplosiveElementsFactory.shellType getShellType() {
            return shellType;
        }

        public void setShooterType(Shooter.shooterType shooterType) {
            this.shooterType = shooterType;
        }

        public void setShellType(ExplosiveElementsFactory.shellType shellType) {
            this.shellType = shellType;
        }

        public void setWidth(short width) {
            this.width = width;
        }

        public void setHeight(short height) {
            this.height = height;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public float getLinearDamping() {
            return linearDamping;
        }

        public float getAngularDamping() {
            return angularDamping;
        }

        public float getSize() {
            return size;
        }

        public float getFriction() {
            return friction;
        }

        public float getRestitution() {
            return restitution;
        }

        public float getDensity() {
            return density;
        }

        public void setAngularVelocity(float angularVelocity) {
            this.angularVelocity = angularVelocity;
        }

        public void setLinearVelocity(float linearVelocity) {
            this.linearVelocity = linearVelocity;
        }

        public void setLinearDamping(float linearDamping) {
            this.linearDamping = linearDamping;
        }

        public void setAngularDamping(float angularDamping) {
            this.angularDamping = angularDamping;
        }

        public void setFriction(float friction) {
            this.friction = friction;
        }

        public void setRestitution(float restitution) {
            this.restitution = restitution;
        }

        public void setDensity(float density) {
            this.density = density;
        }
    }

    public Shooter(float posX, float posY, float width, float height, float rotation, float angle, shooterType shooterType, ExplosiveElementsFactory.shellType shellType) {
        userData = new UserData(posX, posY, width, height, rotation, angle, shooterType, shellType);

        body = PhysicalElementsFactory.createPolygon(userData.posX * Grid.getStep(), userData.posY * Grid.getStep(), userData.vertexes, userData.rotation, null, userData.bodyType, userData.fixtureName, false);

        setBounds(posX, posY, width, height);
        setTouchable(Touchable.enabled);
        addListener(new ActorListener());

        Shooter.this.setUserObject(userData);
    }

    /*
    @Override
    public void act(float delta) {
        if (L2DEditor.isStart() && isAct) {
            fireInterval.scheduleTask(new Timer.Task() {
                Body shell;

                @Override
                public void run() {
                    if (userData.shellType == null)
                        shell = ExplosiveElementsFactory.createShell(userData.worldX + userData.width, userData.worldY + userData.height * .5f,
                                userData.size, userData.friction, userData.restitution, userData.density, userData.linearDamping, userData.angularDamping);
                    else
                       shell = ExplosiveElementsFactory.createShell(userData.worldX + userData.width, userData.worldY + userData.height, userData.shellType);

                    velocity.set(userData.linearVelocity, userData.linearVelocity);
                    velocity.setAngle(userData.angle);

                    shell.setType(BodyDef.BodyType.DynamicBody);
                    shell.setLinearVelocity(velocity);
                    shell.setAngularVelocity(userData.angularVelocity);
                }
            }, 0, 0.3f);
            isAct = false;
        } else if (!L2DEditor.isStart() && !isAct) stop();
        super.act(delta);
    }
    */

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

    @Override
    public boolean remove() {
        if (EditorScreen.getLeftPanel() != null)
            EditorScreen.getLeftPanel().removeNode(Shooter.this);
        return super.remove();
    }
}