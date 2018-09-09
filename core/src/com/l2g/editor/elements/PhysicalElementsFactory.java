package com.l2g.editor.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.l2g.editor.EditorManager;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.screens.LoadScreen;
import com.l2g.editor.utils.utils.math.GeometryUtils;

public abstract class PhysicalElementsFactory {
    private static BodyDef bodyDef = new BodyDef();
    private static FixtureDef fixtureDef = new FixtureDef();

    protected static Body body;

    public enum materialType {
        STONE, WOOD, EMPTY;

        public static materialType type = STONE;
    }

    public enum shapeType {
        CIRCLE, RECTANGLE, TRIANGLE_LEFT, TRIANGLE_RIGHT, POLYGON;

        public static shapeType type = CIRCLE;
    }

    public static class UserData {
        private transient Sprite sprite;
        private shapeType shapeType;
        private materialType materialType;

        private float posX, posY, vertexes[], rotation, friction, restitution, density, width, height;
        private boolean isPolygon;
        private String fixtureName;
        private BodyDef.BodyType bodyType;

        public UserData() { }

        public UserData(float posX, float posY, float size, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, String fixtureName) {
            if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                this.posX = posX;
                this.posY = posY;
            } else {
                this.posX = (short) (posX / Grid.getStep());
                this.posY = (short) (posY / Grid.getStep());
            }

            this.rotation = rotation;
            this.shapeType = shapeType;
            this.materialType = materialType;
            this.bodyType = bodyType;
            this.fixtureName = Names.noDelete + fixtureName;

            this.friction = fixtureDef.friction;
            this.restitution = fixtureDef.restitution;
            this.density = fixtureDef.density;

            createCircleUserData(UserData.this, size);
        }

        public UserData(float posX, float posY, float vertexes[], float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, String fixtureName) {
            if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                this.posX = posX;
                this.posY = posY;
            } else {
                this.posX = (short) (posX / Grid.getStep());
                this.posY = (short) (posY / Grid.getStep());
            }
            this.rotation = rotation;
            this.shapeType = shapeType;
            this.materialType = materialType;
            this.bodyType = bodyType;
            this.fixtureName = Names.noDelete + fixtureName;

            this.friction = fixtureDef.friction;
            this.restitution = fixtureDef.restitution;
            this.density = fixtureDef.density;

            createPolygonUserData(UserData.this, vertexes);
        }

        public UserData(float posX, float posY, float width, float height, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, String fixtureName) {
            if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                this.posX = posX;
                this.posY = posY;
            } else {
                this.posX = (short) (posX / Grid.getStep());
                this.posY = (short) (posY / Grid.getStep());
            }
            this.rotation = rotation;
            this.shapeType = shapeType;
            this.materialType = materialType;
            this.bodyType = bodyType;
            this.fixtureName = Names.noDelete + fixtureName;

            this.friction = fixtureDef.friction;
            this.restitution = fixtureDef.restitution;
            this.density = fixtureDef.density;

            createRectangleUserData(UserData.this, width, height);

            updateSprite();
        }

        public void setShapeType(shapeType shapeType) {
            this.shapeType = shapeType;
        }

        public void setMaterialType(materialType materialType) {
            this.materialType = materialType;
        }

        public void setPolygon(boolean polygon) {
            isPolygon = polygon;
        }

        public void setPosition(Vector2 position) {
            if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                this.posX = position.x;
                this.posY = position.y;
            } else {
                this.posX = (short) (position.x / Grid.getStep());
                this.posY = (short) (position.y / Grid.getStep());
            }
        }

        public void setPosition(float posX, float posY) {
            if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                this.posX = posX;
                this.posY = posY;
            } else {
                this.posX = (short) (posX / Grid.getStep());
                this.posY = (short) (posY / Grid.getStep());
            }
        }

        public void setBodyType(BodyDef.BodyType bodyType) {
            this.bodyType = bodyType;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void setSprite(final Sprite sprite, float width, float height) {
            this.sprite = sprite;
            this.width = (short) (width / Grid.getStep());
            this.height = (short) (height / Grid.getStep());

            sprite.setSize(width * 2, height * 2);
            sprite.setOrigin(sprite.getWidth() *.5f, sprite.getHeight() * .5f);
        }

        public boolean isPolygon() {
            return isPolygon;
        }

        public BodyDef.BodyType getBodyType() {
            return bodyType;
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        public float getPosY() {
            return posY;
        }

        public float getPosX() {
            return posX;
        }

        public shapeType getShapeType() {
            return shapeType;
        }

        public materialType getMaterialType() {
            return materialType;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public float getDensity() {
            return density;
        }

        public void setDensity(float density) {
            this.density = density;
        }

        public String getFixtureName() {
            return fixtureName;
        }

        public float getFriction() {
            return friction;
        }

        public void setFriction(float friction) {
            this.friction = friction;
        }

        public float getRestitution() {
            return restitution;
        }

        public void setRestitution(float restitution) {
            this.restitution = restitution;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public void updateSprite() {
            switch (materialType) {
                case STONE:
                    switch (shapeType) {
                        case RECTANGLE:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("rec");
                            break;
                        case CIRCLE:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("cir");
                            break;
                        case TRIANGLE_LEFT:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("trig");
                            break;
                        case TRIANGLE_RIGHT:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("trig3");
                            break;
                    }
                    break;
                case WOOD:
                    switch (shapeType) {
                        case RECTANGLE:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("rec2");
                            break;
                        case CIRCLE:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("cir2");
                            break;
                        case TRIANGLE_LEFT:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("trig2");
                            break;
                        case TRIANGLE_RIGHT:
                            sprite = LoadScreen.getSpriteAtlas().createSprite("trig4");
                            break;
                    }
                    break;
            }

            if (sprite != null) {
                if (shapeType.equals(shapeType.TRIANGLE_LEFT) || shapeType.equals(shapeType.TRIANGLE_RIGHT))
                    sprite.setSize(width * Grid.getStep(), height * Grid.getStep());
                else
                    sprite.setSize(width * 2 * Grid.getStep(), height * 2 * Grid.getStep());

                sprite.setOrigin(sprite.getWidth() * .5f, sprite.getHeight() * .5f);
            }
        }
    }

    public static Body createElement(float posX, float posY, float size, float rotation, shapeType shapeType, materialType materialType, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        switch (shapeType) {
            case CIRCLE:
                body = createCircle(posX, posY, size, rotation, materialType, bodyType, fixtureName, isUserData);
                break;
            case RECTANGLE:
                body = createRectangle(posX, posY, size, size, rotation, materialType, bodyType, fixtureName, isUserData);
                break;
            case TRIANGLE_LEFT:
                body = createTriangleLeft(posX, posY, size, rotation, materialType, bodyType, fixtureName, isUserData);
                break;
            case TRIANGLE_RIGHT:
                body = createTriangleRight(posX, posY, size, rotation, materialType, bodyType, fixtureName, isUserData);
                break;
        }

        return body;
    }

    public static Body createElement(float posX, float posY, float size, float rotation, shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        switch (shapeType) {
            case CIRCLE:
                body = createCircle(posX, posY, size, rotation, friction, restitution, density, bodyType, fixtureName, isUserData);
                break;
            case RECTANGLE:
                body = createRectangle(posX, posY, size, size, rotation, friction, restitution, density, bodyType, fixtureName, isUserData);
                break;
            case TRIANGLE_LEFT:
                body = createTriangleLeft(posX, posY, size, rotation, friction, restitution, density, bodyType, fixtureName, isUserData);
                break;
            case TRIANGLE_RIGHT:
                body = createTriangleRight(posX, posY, size, rotation, friction, restitution, density, bodyType, fixtureName, isUserData);
                break;
        }

        return body;
    }

    public static Body createCircle(float posX, float posY, float radius, float rotation, materialType materialType, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius / L2DEditor.PPM);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(materialType);

        if (isUserData) body.setUserData(new UserData(posX, posY, radius, rotation, shapeType.CIRCLE, materialType, bodyType, fixtureName));

        fixtureDef.shape = circleShape;
        //fixtureDef.filter.categoryBits = ElementManager.BIT_SHELL;
        //fixtureDef.filter.maskBits

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        circleShape.dispose();

        return body;
    }

    public static Body createCircle(float posX, float posY, float radius, float rotation, float friction, float restitution, float density, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius / L2DEditor.PPM);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(friction, restitution, density);

        if (isUserData) body.setUserData(new UserData(posX, posY, radius, rotation, shapeType.CIRCLE, materialType.EMPTY, bodyType, fixtureName));

        fixtureDef.shape = circleShape;
        //fixtureDef.filter.categoryBits = ElementManager.BIT_SHELL;
        //fixtureDef.filter.maskBits

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        circleShape.dispose();

        return body;
    }

    public static Body createPolygon(float posX, float posY, float vertexes[], float rotation, materialType materialType, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(vertexes);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(materialType);

        if (isUserData) body.setUserData(new UserData(posX, posY, vertexes, rotation, shapeType.POLYGON, materialType, bodyType, fixtureName));

        fixtureDef.shape = chainShape;
        //fixtureDef.filter.categoryBits = ElementManager.BIT_GROUND;
        //fixtureDef.filter.maskBits

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        chainShape.dispose();

        return body;
    }

    public static Body createPolygon(float posX, float posY, float vertexes[], float rotation, float friction, float restitution, float density, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(vertexes);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(friction, restitution, density);

        if (isUserData) body.setUserData(new UserData(posX, posY, vertexes, rotation, shapeType.POLYGON, materialType.EMPTY, bodyType, fixtureName));

        fixtureDef.shape = chainShape;
        //fixtureDef.filter.categoryBits = ElementManager.BIT_GROUND;
        //fixtureDef.filter.maskBits

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        chainShape.dispose();

        return body;
    }

    public static Body createRectangle(float posX, float posY, float width, float height, float rotation, materialType materialType, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / L2DEditor.PPM, height / L2DEditor.PPM);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(materialType);

        if (isUserData) body.setUserData(new UserData(posX, posY, width, height, rotation, shapeType.RECTANGLE, materialType, bodyType, fixtureName));

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        polygonShape.dispose();

        return body;
    }

    public static Body createRectangle(float posX, float posY, float width, float height, float rotation, float friction, float restitution, float density, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / L2DEditor.PPM, height / L2DEditor.PPM);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(friction, restitution, density);

        if (isUserData) body.setUserData(new UserData(posX, posY, width, height, rotation, shapeType.RECTANGLE, materialType.EMPTY, bodyType, fixtureName));

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        polygonShape.dispose();

        return body;
    }

    public static Body createTriangleLeft(float posX, float posY, float length, float rotation, materialType materialType, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        float vertex[] = {0, 0, length / L2DEditor.PPM, 0, 0, length / L2DEditor.PPM};

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertex);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(materialType);

        if (isUserData) body.setUserData(new UserData(posX, posY, vertex, rotation, shapeType.TRIANGLE_LEFT, materialType, bodyType, fixtureName));

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        polygonShape.dispose();

        return body;
    }

    public static Body createTriangleLeft(float posX, float posY, float length, float rotation, float friction, float restitution, float density, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        float vertex[] = {0, 0, length / L2DEditor.PPM, 0, 0, length / L2DEditor.PPM};

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertex);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(friction, restitution, density);

        if (isUserData) body.setUserData(new UserData(posX, posY, vertex, rotation, shapeType.TRIANGLE_LEFT, materialType.EMPTY, bodyType, fixtureName));

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        polygonShape.dispose();

        return body;
    }

    public static Body createTriangleRight(float posX, float posY, float length, float rotation, materialType materialType, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        float vertex[] = {0, 0, length / L2DEditor.PPM, 0, length / L2DEditor.PPM, length / L2DEditor.PPM};

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertex);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(materialType);

        if (isUserData) body.setUserData(new UserData(posX, posY, vertex, rotation, shapeType.TRIANGLE_RIGHT, materialType, bodyType, fixtureName));

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        polygonShape.dispose();

        return body;
    }

    public static Body createTriangleRight(float posX, float posY, float length, float rotation, float friction, float restitution, float density, BodyDef.BodyType bodyType, String fixtureName, boolean isUserData) {
        float vertex[] = {0, 0, length / L2DEditor.PPM, 0, length / L2DEditor.PPM, length / L2DEditor.PPM};

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertex);

        createBody(posX, posY, rotation, bodyType);
        setMaterial(friction, restitution, density);

        if (isUserData) body.setUserData(new UserData(posX, posY, vertex, rotation, shapeType.TRIANGLE_RIGHT, materialType.EMPTY, bodyType, fixtureName));

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef).setUserData(Names.noDelete + fixtureName);

        polygonShape.dispose();

        return body;
    }

    public static void updateBody(final Body body) {
        UserData userData;

        if (body.getUserData() != null) {
            if (body.getUserData() instanceof ExplosiveElementsFactory.UserData)
                userData = (ExplosiveElementsFactory.UserData) body.getUserData();
            else if (body.getUserData() instanceof UserData)
                userData = (UserData) body.getUserData();
            else return;

            L2DEditor.SingletonHolder.getEditorManager().deleteBody(body);

            switch (userData.shapeType) {
                case CIRCLE:
                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createCircle(userData.posX * Grid.getStep(), userData.posY * Grid.getStep(),  userData.width * Grid.getStep(), userData.rotation, userData.materialType, userData.bodyType, EditorManager.elementType.CIRCLE.toString(), false));
                    EditorScreen.getSelection().setUserData(userData);
                    break;
                case RECTANGLE:
                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createRectangle(userData.posX * Grid.getStep(), userData.posY * Grid.getStep(), userData.width * Grid.getStep(), userData.height * Grid.getStep(), userData.rotation, userData.materialType, userData.bodyType, EditorManager.elementType.RECTANGLE.toString(), false));
                    EditorScreen.getSelection().setUserData(userData);
                    break;
                case TRIANGLE_LEFT:
                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.posX * Grid.getStep(), userData.posY * Grid.getStep(), userData.width * Grid.getStep(), true, userData.rotation, userData.materialType, userData.bodyType, EditorManager.elementType.TRIANGLE_LEFT.toString(), false));
                    EditorScreen.getSelection().setUserData(userData);
                    break;
                case TRIANGLE_RIGHT:
                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.posX * Grid.getStep(), userData.posY * Grid.getStep(), userData.width * Grid.getStep(), false, userData.rotation, userData.materialType, userData.bodyType, EditorManager.elementType.TRIANGLE_RIGHT.toString(), false));
                    EditorScreen.getSelection().setUserData(userData);
                    break;
            }

            userData.updateSprite();
        }
    }

    private static void createCircleUserData(UserData userData, float radius) {
        userData.width = userData.height = (short) (radius / Grid.getStep());

        userData.isPolygon = false;
    }

    private static void createRectangleUserData(UserData userData, float width, float height) {
        userData.width = (short) (width / Grid.getStep());
        userData.height = (short) (height / Grid.getStep());

        userData.isPolygon = false;
    }

    /*
    private static void createTriangleUserData(UserData userData, final float length) {
        userData.width = userData.height = (short) (length / Grid.getStep());

        userData.isPolygon = true;
    }
    */

    private static void createPolygonUserData(UserData userData, float vertexes[]) {
        userData.vertexes = vertexes;
        userData.width = GeometryUtils.width(vertexes);
        userData.height = GeometryUtils.height(vertexes);
        userData.isPolygon = true;
    }

    private static void createBody(float posX, float posY, float rotation, BodyDef.BodyType bodyType) {
        bodyDef.type = bodyType;
        bodyDef.position.set(posX / L2DEditor.PPM, posY / L2DEditor.PPM);
        bodyDef.angle = rotation * MathUtils.degreesToRadians;

        body = L2DEditor.SingletonHolder.getEditorManager().createBody(bodyDef);
    }

    private static void setMaterial(final materialType type) {
        if (type != null) {
            switch (type) {
                case STONE:
                    fixtureDef.friction = Names.materialParameters[0][0];
                    fixtureDef.restitution = Names.materialParameters[0][1];
                    fixtureDef.density = Names.materialParameters[0][2];
                    break;
                case WOOD:
                    fixtureDef.friction = Names.materialParameters[1][0];
                    fixtureDef.restitution = Names.materialParameters[1][1];
                    fixtureDef.density = Names.materialParameters[1][2];
                    break;
            }
        }
    }

    private static void setMaterial(float friction, float restitution, float density) {
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
    }

    public static void setSelection(Body body) {
        if (body != null) {
            EditorScreen.setSelection(body);
            if (body.getUserData() != null) {
                if (body.getUserData() instanceof ExplosiveElementsFactory.UserData) {
                    EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getShellUI());
                    EditorScreen.getShellUI().setValues((ExplosiveElementsFactory.UserData) body.getUserData());
                    return;
                } else if (body.getUserData() instanceof UserData) {
                    EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getElementUI());
                    EditorScreen.getElementUI().setValues((UserData) body.getUserData());
                }
            }
        } else {
            EditorScreen.setSelection(null);
            if (EditorScreen.getRightPanel() != null)
                EditorScreen.getRightPanel().clearCurrentUI();
        }
    }
}