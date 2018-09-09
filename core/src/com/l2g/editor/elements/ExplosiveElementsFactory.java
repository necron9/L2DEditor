package com.l2g.editor.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.l2g.editor.Names;
import com.l2g.editor.screens.LoadScreen;

public abstract class ExplosiveElementsFactory extends PhysicalElementsFactory {
    public enum shellType {
        CANNONBALL, LANDMINE, EMPTY;

        public static shellType type = CANNONBALL;
    }

    public static class UserData extends PhysicalElementsFactory.UserData {
        private shellType shellType;
        private float linearDamping, angularDamping;

        public UserData() { super(); }

        public UserData(float posX, float posY, float size, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, shellType shellType, String fixtureName) {
            super(posX, posY, size, rotation, shapeType, materialType, bodyType, fixtureName);

            setShellType(shellType);
        }

        public UserData(float posX, float posY, float vertexes[], float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, shellType shellType, String fixtureName) {
            super(posX, posY, vertexes, rotation, shapeType, materialType, bodyType, fixtureName);

            setShellType(shellType);
        }

        public UserData(float posX, float posY, float width, float height, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, shellType shellType, String fixtureName) {
            super(posX, posY, width, height, rotation, shapeType, materialType, bodyType, fixtureName);

            setShellType(shellType);
        }

        public float getLinearDamping() {
            return linearDamping;
        }

        public float getAngularDamping() {
            return angularDamping;
        }

        public shellType getShellType() {
            return shellType;
        }

        public void setLinearDamping(float linearDamping) {
            this.linearDamping = linearDamping;
        }

        public void setAngularDamping(float angularDamping) {
            this.angularDamping = angularDamping;
        }

        public void setShellType(shellType shellType) {
            this.shellType = shellType;

            switch (shellType) {
                case CANNONBALL:
                    this.linearDamping = Names.shellParameters[0][0];
                    this.angularDamping = Names.shellParameters[0][1];
                    break;
                case LANDMINE:
                    this.linearDamping = Names.shellParameters[1][0];
                    this.angularDamping = Names.shellParameters[1][1];
                    break;
            }
        }
    }

    public static Body createCannonball(float posX, float posY, float size, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, boolean isUserData) {
        body = createElement(posX, posY, size, rotation, shapeType, materialType, bodyType, Names.fixtureNames[0] + shellType.CANNONBALL.toString(), false);
        body.setLinearDamping(Names.shellParameters[0][0]);
        body.setAngularDamping(Names.shellParameters[0][1]);

        if (isUserData) {
            UserData userData = new UserData(posX, posY, size, rotation, shapeType, materialType, bodyType, shellType.CANNONBALL, Names.fixtureNames[0] + shellType.CANNONBALL.toString());
            userData.linearDamping = body.getLinearDamping();
            userData.angularDamping = body.getAngularDamping();
            userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("shell_cannonball_sprite")), userData.getWidth(), userData.getHeight());
            body.setUserData(userData);
        }

        return body;
    }

    public static Body createCannonball(float posX, float posY, float size, float rotation, shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, boolean isUserData) {
        body = createElement(posX, posY, size, rotation, shapeType, friction, restitution, density, bodyType, Names.fixtureNames[0] + shellType.CANNONBALL.toString(), false);
        body.setLinearDamping(Names.shellParameters[0][0]);
        body.setAngularDamping(Names.shellParameters[0][1]);

        if (isUserData) {
            UserData userData = new UserData(posX, posY, size, rotation, shapeType, materialType.EMPTY, bodyType, shellType.CANNONBALL, Names.fixtureNames[0] + shellType.CANNONBALL.toString());userData.linearDamping = body.getLinearDamping();
            userData.angularDamping = body.getAngularDamping();
            userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("shell_cannonball_sprite")), userData.getWidth(), userData.getHeight());
            body.setUserData(userData);
        }

        return body;
    }

    public static Body createLandmine(float posX, float posY, float size, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, boolean isUserData) {
        body = createElement(posX, posY, size, rotation, shapeType, materialType, bodyType, Names.fixtureNames[0] + shellType.LANDMINE.toString(), false);
        body.setLinearDamping(Names.shellParameters[1][0]);
        body.setAngularDamping(Names.shellParameters[1][1]);

        if (isUserData) {
            UserData userData = new UserData(posX, posY, size, rotation, shapeType, materialType, bodyType, shellType.LANDMINE, Names.fixtureNames[0] + shellType.LANDMINE.toString());
            userData.linearDamping = body.getLinearDamping();
            userData.angularDamping = body.getAngularDamping();
            userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("shell_landmine")), userData.getWidth(), userData.getHeight());
            body.setUserData(userData);
        }

        return body;
    }

    public static Body createLandmine(float posX, float posY, float size, float rotation, shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, boolean isUserData) {
        body = createElement(posX, posY, size, rotation, shapeType, friction, restitution, density, bodyType, Names.fixtureNames[0] + shellType.LANDMINE.toString(), false);
        body.setLinearDamping(Names.shellParameters[1][0]);
        body.setAngularDamping(Names.shellParameters[1][1]);

        if (isUserData) {
            UserData userData = new UserData(posX, posY, size, rotation, shapeType, materialType.EMPTY, bodyType, shellType.CANNONBALL, Names.fixtureNames[0] + shellType.CANNONBALL.toString());
            userData.linearDamping = body.getLinearDamping();
            userData.angularDamping = body.getAngularDamping();
            userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("shell_landmine_sprite")), userData.getWidth(), userData.getHeight());
            body.setUserData(userData);
        }

        return body;
    }

    public static Body createEmpty(float posX, float posY, float size, float rotation, float linearDamping, float angularDamping, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, boolean isUserData) {
        body = createElement(posX, posY, size, rotation, shapeType, materialType, bodyType, Names.fixtureNames[0] + shellType.EMPTY.toString(), false);
        body.setLinearDamping(linearDamping);
        body.setAngularDamping(angularDamping);

        if (isUserData) {
            UserData userData = new UserData(posX, posY, size, rotation, shapeType, materialType, bodyType, shellType.EMPTY, Names.fixtureNames[0] + shellType.EMPTY.toString());
            userData.linearDamping = body.getLinearDamping();
            userData.angularDamping = body.getAngularDamping();
            userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("shell_landmine_sprite")), userData.getWidth(), userData.getHeight());
            body.setUserData(userData);
        }

        return body;
    }

    public static Body createEmpty(float posX, float posY, float size, float rotation, float linearDamping, float angularDamping, shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, boolean isUserData) {
        body = createElement(posX, posY, size, rotation, shapeType, friction, restitution, density, bodyType, Names.fixtureNames[0] + shellType.EMPTY.toString(), false);
        body.setLinearDamping(linearDamping);
        body.setAngularDamping(angularDamping);

        if (isUserData) {
            UserData userData = new UserData(posX, posY, size, rotation, shapeType, materialType.EMPTY, bodyType, shellType.EMPTY, Names.fixtureNames[0] + shellType.EMPTY.toString());
            userData.linearDamping = body.getLinearDamping();
            userData.angularDamping = body.getAngularDamping();
            userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("shell_landmine_sprite")), userData.getWidth(), userData.getHeight());
            body.setUserData(userData);
        }

        return body;
    }
}