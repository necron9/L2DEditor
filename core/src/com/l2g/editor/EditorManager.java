package com.l2g.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.l2g.editor.actors.Bomb;
import com.l2g.editor.actors.Shooter;
import com.l2g.editor.elements.BlastFactory;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.listeners.ActorListener;
import com.l2g.editor.utils.utils.Scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public final class EditorManager implements Disposable {
    private final ShapeRenderer shapeRenderer;

    public final static int
            ARRAY_SIZE_1 = 2,
            ARRAY_SIZE_2 = 8,
            SHAPE_SIZE = 2;

    private World world;

    private Box2DDebugRenderer box2DRender;
    private Array<Body> bodiesToDestroy, bodiesToDraw;
    private Array<Fixture> fixturesToDestroy;
    private Json json;
    private Timer deleteTimer;

    private float deleteTime = 1;
    private boolean isDebug, isWarmStarting, isContinuousPhysics;

    private short shooterCounter, bombCounter;

    public enum elementType {
        SHOOTER, BOMB, LANDMINE,
        RECTANGLE, RECTANGLE_ARRAY,
        CIRCLE, CIRCLE_ARRAY,
        TRIANGLE_LEFT, TRIANGLE_LEFT_ARRAY,
        TRIANGLE_RIGHT, TRIANGLE_RIGHT_ARRAY,
        POLYGON, DYNAMIC_BOXES;

        public static elementType type = SHOOTER;
    }

    public EditorManager(final ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;

        box2DRender = new Box2DDebugRenderer(true, true, false, true, false, false);

        bodiesToDestroy = new Array<Body>();
        fixturesToDestroy = new Array<Fixture>();
        bodiesToDraw = new Array<Body>();

        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);

        deleteTimer = new Timer();

        shooterCounter = bombCounter = 0;
    }

    public Body createPolygon(float worldX, float worldY, float vertexes[], float angle, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        return PhysicalElementsFactory.createPolygon(worldX, worldY, vertexes, angle, material, bodyType, userData, isUserData);
    }

    public Body createPolygon(float worldX, float worldY, float vertexes[], float angle, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        return PhysicalElementsFactory.createPolygon(worldX, worldY, vertexes, angle, friction, restitution, density, bodyType, userData, isUserData);
    }

    public Body createRectangle(float posX, float posY, float width, float height, float rotation, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        return PhysicalElementsFactory.createRectangle(posX, posY, width, height, rotation, material, bodyType, userData, isUserData);
    }

    public Body createRectangle(float posX, float posY, float width, float height, float rotation, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        return PhysicalElementsFactory.createRectangle(posX, posY, width, height, rotation, friction, restitution, density, bodyType, userData, isUserData);
    }

    public void createRectangleArray(float worldX, float worldY, float width, float height, int sizeX, int sizeY, float angle, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++) {
                Body body = PhysicalElementsFactory.createRectangle(worldX + (width * 2) * i, worldY + (height * 2) * j, width, height, angle, material, bodyType, userData, isUserData);
                body.setFixedRotation(false);
                body.setType(bodyType);
                PhysicalElementsFactory.setSelection(body);
            }
    }

    public void createRectangleArray(float worldX, float worldY, float width, float height, int sizeX, int sizeY, float angle, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData,  boolean isUserData) {
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++) {
                Body body = PhysicalElementsFactory.createRectangle(worldX + (width * 2) * i, worldY + (height * 2) * j, width, height, angle, friction, restitution, density, bodyType, userData, isUserData);
                body.setFixedRotation(false);
                body.setType(bodyType);
                PhysicalElementsFactory.setSelection(body);
            }
    }

    public Body createTriangle(float worldX, float worldY, float length, boolean position, float angle, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        if (position) return PhysicalElementsFactory.createTriangleLeft(worldX, worldY, length, angle, material, bodyType, userData, isUserData);
        else return PhysicalElementsFactory.createTriangleRight(worldX, worldY, length, angle, material, bodyType, userData, isUserData);
    }

    public Body createTriangle(float worldX, float worldY, float length, boolean position, float angle, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        if (position) return PhysicalElementsFactory.createTriangleLeft(worldX, worldY, length, angle, friction, restitution, density, bodyType, userData, isUserData);
        else return PhysicalElementsFactory.createTriangleRight(worldX, worldY, length, angle, friction, restitution, density, bodyType, userData, isUserData);
    }

    public void createTriangleArray(float worldX, float worldY, float length, boolean position, int sizeX, int sizeY, float angle, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++) {
                if (position) PhysicalElementsFactory.setSelection(PhysicalElementsFactory.createTriangleLeft(worldX + (length * 2) * i, worldY + (length * 2) * j, length, angle, material, bodyType, userData, isUserData));
                else PhysicalElementsFactory.setSelection(PhysicalElementsFactory.createTriangleRight(worldX + (length * 2) * i, worldY + (length * 2) * j, length, angle, material, bodyType, userData, isUserData));
            }
    }

    public void createTriangleArray(float worldX, float worldY, float length, boolean position, int sizeX, int sizeY, float angle, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++) {
                if (position) PhysicalElementsFactory.setSelection(PhysicalElementsFactory.createTriangleLeft(worldX + (length * 2) * i, worldY + (length * 2) * j, length, angle, friction, restitution, density, bodyType, userData, isUserData));
                else PhysicalElementsFactory.setSelection(PhysicalElementsFactory.createTriangleRight(worldX + (length * 2) * i, worldY + (length * 2) * j, length, angle, friction, restitution, density, bodyType, userData, isUserData));
            }
    }

    public Body createCircle(float worldX, float worldY, float radius, float angle, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        return PhysicalElementsFactory.createCircle(worldX, worldY, radius, angle, material, bodyType, userData, isUserData);
    }

    public Body createCircle(float worldX, float worldY, float radius, float angle, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        return PhysicalElementsFactory.createCircle(worldX, worldY, radius, angle, friction, restitution, density, bodyType, userData, isUserData);
    }

    public void createCircleArray(float worldX, float worldY, float radius, int sizeX, int sizeY, float angle, PhysicalElementsFactory.materialType material, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++)
                PhysicalElementsFactory.setSelection(PhysicalElementsFactory.createCircle(worldX + (radius * 2) * i, worldY + (radius * 2) * j, radius, angle, material, bodyType, userData, isUserData));
    }

    public void createCircleArray(float worldX, float worldY, float radius, int sizeX, int sizeY, float angle, float friction, float restitution, float density, BodyDef.BodyType bodyType, String userData, boolean isUserData) {
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++)
                PhysicalElementsFactory.setSelection(PhysicalElementsFactory.createCircle(worldX + (radius * 2) * i, worldY + (radius * 2) * j, radius, angle, friction, restitution, density, bodyType, userData, isUserData));
    }

    public Bomb createBomb(float posX, float posY, float width, float height, float rotation, Bomb.bombType type) {
        Bomb bomb = new Bomb(posX, posY, width, height, rotation, type);
        bomb.setName("col_B[" + shooterCounter++ + "]");
        bomb.setBodyName(bomb.getName());
        L2DEditor.getStage().addActor(bomb);
        return bomb;
    }

    public Body createCannonball(float posX, float posY, float size, float rotation, PhysicalElementsFactory.shapeType shapeType, PhysicalElementsFactory.materialType materialType, BodyDef.BodyType bodyType, boolean isUserData) {
        return ExplosiveElementsFactory.createCannonball(posX, posY, size, rotation, shapeType, materialType, bodyType, isUserData);
    }

    public Body createCannonball(float posX, float posY, float size, float rotation, PhysicalElementsFactory.shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, boolean isUserData) {
        return ExplosiveElementsFactory.createCannonball(posX, posY, size, rotation, shapeType, friction, restitution, density, bodyType, isUserData);
    }

    public Body createLandmine(float posX, float posY, float size, float rotation, PhysicalElementsFactory.shapeType shapeType, PhysicalElementsFactory.materialType materialType, BodyDef.BodyType bodyType, boolean isUserData) {
        return ExplosiveElementsFactory.createLandmine(posX, posY, size, rotation, shapeType, materialType, bodyType, isUserData);
    }

    public Body createLandmine(float posX, float posY, float size, float rotation, PhysicalElementsFactory.shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, boolean isUserData) {
        return ExplosiveElementsFactory.createLandmine(posX, posY, size, rotation, shapeType, friction, restitution, density, bodyType, isUserData);
    }

    public Body createEmptyShell(float posX, float posY, float size, float rotation, float linearDamping, float angularDamping, PhysicalElementsFactory.shapeType shapeType, PhysicalElementsFactory.materialType materialType, BodyDef.BodyType bodyType, boolean isUserData) {
        return ExplosiveElementsFactory.createEmpty(posX, posY, size, rotation, linearDamping, angularDamping, shapeType, materialType, bodyType, isUserData);
    }

    public Body createEmptyShell(float posX, float posY, float size, float rotation, float linearDamping, float angularDamping, PhysicalElementsFactory.shapeType shapeType, float friction, float restitution, float density, BodyDef.BodyType bodyType, boolean isUserData) {
        return ExplosiveElementsFactory.createEmpty(posX, posY, size, rotation, linearDamping, angularDamping, shapeType, friction, restitution, density, bodyType, isUserData);
    }

    public Shooter createShooter(float posX, float posY, float width, float height, float rotation, float angle, Shooter.shooterType shooterType, ExplosiveElementsFactory.shellType shellType) {
        Shooter shooter = new Shooter(posX, posY, width, height, rotation, angle, shooterType, shellType);
        shooter.setName("Shooter" + shooterCounter++);
        shooter.addListener(new ActorListener());
        L2DEditor.getStage().addActor(shooter);
        return shooter;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getBodyCount() {
        if (world == null)
            return -1;
        else
            return world.getBodyCount();
    }

    public int getFixtureCount() {
        if (world == null)
            return  -1;
        else
            return world.getFixtureCount();
    }

    public int getJointCount() {
        if (world == null)
            return  -1;
        else
            return world.getJointCount();
    }

    public int getContactCount() {
        if (world == null)
            return  -1;
        else
            return world.getContactCount();
    }

    public int getProxyCount() {
        if (world == null)
            return  -1;
        else
            return world.getProxyCount();
    }

    public Body createBody(BodyDef bodyDef) {
        if (world == null)
            return null;
        else
            return world.createBody(bodyDef);
    }

    public Joint createJoint(JointDef jointDef) {
        if (world == null)
            return null;
        else
            return world.createJoint(jointDef);
    }

    public void destroyBody(Body body) {
        if (world != null)
            world.destroyBody(body);
    }

    public float getGravityHorizontal() {
        if (world == null)
            return -1f;
        else
            return world.getGravity().x;
    }

    public float getGravityVertical() {
        if (world == null)
            return -1f;
        else
            return world.getGravity().y;
    }

    public void destroyJoint(Joint joint) {
        if (world != null)
            world.destroyJoint(joint);
    }

    public void setAutoClearForces(boolean autoClearForces) {
        if (world != null) {
            world.setAutoClearForces(autoClearForces);
        }
    }

    public void queryAABB(QueryCallback queryCallback, float lowerX, float lowerY, float upperX, float upperY) {
        if (world != null)
            world.QueryAABB(queryCallback, lowerX, lowerY, upperX, upperY);
    }

    public void rayCast(RayCastCallback callback, Vector2 point1, Vector2 point2) {
        if (world != null)
            world.rayCast(callback, point1, point2);
    }

    public void alignObjects(Stage stage) {
        Vector3 position;

        world.getBodies(bodiesToDraw);

        for (Actor actor : stage.getActors()
        ) {
            if (L2DEditor.SingletonHolder.getGrid().contains(actor.getX(), actor.getY())) {
                position = L2DEditor.SingletonHolder.getGrid().move(actor.getX(), actor.getY());
                actor.setPosition(position.x, position.y);
            }
        }

        if (world != null) {
            PhysicalElementsFactory.UserData userData;

            for (Body body: bodiesToDraw
            ) {
                if (body.getUserData() != null) {
                    if (body.getUserData() instanceof ExplosiveElementsFactory.UserData)
                        userData = (ExplosiveElementsFactory.UserData) body.getUserData();
                    else if (body.getUserData() instanceof PhysicalElementsFactory.UserData)
                        userData = (PhysicalElementsFactory.UserData) body.getUserData();
                    else continue;

                    float bodyX = userData.getPosX() * Grid.getStep() - userData.getWidth() * .5f * Grid.getStep();
                    float bodyY = userData.getPosY() * Grid.getStep() - userData.getHeight() * .5f * Grid.getStep();

                    if (L2DEditor.SingletonHolder.getGrid().contains(bodyX, bodyY)) {
                        position = L2DEditor.SingletonHolder.getGrid().move(bodyX, bodyY);
                        body.setTransform(position.x / L2DEditor.PPM, position.y / L2DEditor.PPM, body.getAngle());
                        userData.setPosition(body.getPosition().x * L2DEditor.PPM, body.getPosition().y * L2DEditor.PPM);
                        PhysicalElementsFactory.updateBody(body);
                    }
                }
            }
        }
    }

    public void update() {
        String temp;
        boolean isDeleted = false;

        if (world != null)
            world.step(L2DEditor.FRAME_DURATION, L2DEditor.VELOCITY_ITERATIONS, L2DEditor.POSITION_ITERATIONS);

        for (final Fixture fixture : fixturesToDestroy) {
            temp = fixture.getUserData().toString();
            // shell
            if (temp.contains(Names.fixtureNames[0])) {

                // cannonball
                if (temp.contains(ExplosiveElementsFactory.shellType.CANNONBALL.toString())) {
                    deleteTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            destroyBody(fixture.getBody());
                        }
                    }, deleteTime);
                    continue;
                }

                // landmine
                else if(temp.contains(ExplosiveElementsFactory.shellType.LANDMINE.toString())) {
                    destroyBody(fixture.getBody());
                    continue;
                }
            } else destroyBody(fixture.getBody());
        }

        for (final Body body: bodiesToDestroy
        ) {
            if (body.getUserData() != null && body.getUserData().toString().contains("col")) {
                String string = body.getUserData().toString();
                for (Actor actor: L2DEditor.getStage().getActors()
                ) {
                    if (actor != null && (actor.getName() != null) && actor.getName().equals(string)) {
                        actor.remove();
                        destroyBody(body);
                        isDeleted = true;
                    }
                }
            } if (!isDeleted)
                destroyBody(body);
        }

        fixturesToDestroy.clear();
        bodiesToDestroy.clear();
    }

    public void draw(SpriteBatch batch) {
        if (world != null) {
            batch.setProjectionMatrix(L2DEditor.getCamera().combined);
            batch.begin();

            world.getBodies(bodiesToDraw);

            for (Body body : bodiesToDraw)
                if (body.getUserData() != null && body.getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) body.getUserData();
                    if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody)) {
                        userData.setPosition(body.getPosition().x * L2DEditor.PPM, body.getPosition().y * L2DEditor.PPM);
                        userData.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                    }
                    if (userData.getSprite() != null) {
                        Sprite sprite = userData.getSprite();
                        if (userData.isPolygon())
                            sprite.setPosition(body.getPosition().x * L2DEditor.PPM, body.getPosition().y * L2DEditor.PPM);
                        else
                            sprite.setPosition(body.getPosition().x * L2DEditor.PPM - sprite.getWidth() * .5f, body.getPosition().y * L2DEditor.PPM - sprite.getHeight() * .5f);
                        sprite.setRotation(userData.getRotation());
                        sprite.draw(batch);
                    }
                }

            batch.end();

            box2DRender.render(world, L2DEditor.getCamera().combined.scl(L2DEditor.PPM));
        }

        if (isDebug)
            debug(shapeRenderer);
    }

    private void debug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(L2DEditor.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (BlastFactory.getCounterDebug() < BlastFactory.getAmountRays() && BlastFactory.isDebugRays()) {
            for (int i = 0; i < BlastFactory.getAmountRays(); i++)
                shapeRenderer.line(BlastFactory.getOrigin().x * L2DEditor.PPM, BlastFactory.getOrigin().y * L2DEditor.PPM,
                        BlastFactory.getDirection()[i].x * L2DEditor.PPM, BlastFactory.getDirection()[i].y * L2DEditor.PPM);
            BlastFactory.counerDebugInc();
        }

        shapeRenderer.end();
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public void setStart(boolean start) {
        L2DEditor.isStart(start);
    }

    public void clear() {
        world.getBodies(bodiesToDestroy);
        L2DEditor.getStage().clear();
    }

    public void deleteBody(Body body) {
        bodiesToDestroy.add(body);
    }

    public void deleteFixture(Fixture fixture) {
        fixturesToDestroy.add(fixture);
    }

    public void saveScene(File file) {
        try {
            Scene scene = new Scene(3);
            Array<Body> bodies = new Array<Body>();
            world.getBodies(bodies);

            for (Body body: bodies
            ) {
                if (body.getUserData() != null ) {
                    if (body.getUserData() instanceof PhysicalElementsFactory.UserData)
                        scene.add(body.getUserData());
                }
            }
            for (Actor actor: L2DEditor.getStage().getActors()
            ) {
                if (actor.getUserObject() != null) {
                    if (actor.getUserObject() instanceof Shooter.UserData)
                        scene.add(actor.getUserObject());
                    else if (actor.getUserObject() instanceof Bomb.UserData)
                        scene.add(actor.getUserObject());
                }
            }

            if (!scene.isEmpty()) {
                if(!file.exists()){
                    file.createNewFile();
                }

                PrintWriter printWriter = new PrintWriter(file.getAbsoluteFile());

                try {
                    printWriter.print(json.prettyPrint(scene));
                } finally {
                    printWriter.close();
                }
            }

            L2DEditor.isChanged(false);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadScene(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String string;
                Body body = null;
                float posX, posY;
                while ((string = bufferedReader.readLine()) != null)
                    stringBuilder.append(string);

                Scene scene = json.fromJson(Scene.class, stringBuilder.toString());

                Array<Object> objects = scene.getObjects();
                for (short i = 0; i < objects.size; i++) {
                    if (objects.get(i) instanceof ExplosiveElementsFactory.UserData) {
                        ExplosiveElementsFactory.UserData userData = (ExplosiveElementsFactory.UserData) objects.get(i);

                        switch (userData.getShellType()) {
                            case CANNONBALL:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createCannonball(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getShapeType(), userData.getFriction(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), true);
                                else
                                    body = createCannonball(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getShapeType(), userData.getMaterialType(), userData.getBodyType(), true);
                                break;
                            case LANDMINE:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createLandmine(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getShapeType(), userData.getFriction(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), true);
                                else
                                    body = createLandmine(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getShapeType(), userData.getMaterialType(), userData.getBodyType(), true);
                                break;
                            case EMPTY:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createEmptyShell(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getLinearDamping(), userData.getAngularDamping(), userData.getShapeType(), userData.getFriction(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), true);
                                else
                                    body = createEmptyShell(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getLinearDamping(), userData.getAngularDamping(), userData.getShapeType(), userData.getMaterialType(), userData.getBodyType(), true);
                                break;
                        }
                        if (body != null)
                            PhysicalElementsFactory.setSelection(body);
                        return;
                    }
                    if (objects.get(i) instanceof PhysicalElementsFactory.UserData) {
                        PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) objects.get(i);

                        if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody)) {
                            posX = userData.getPosX();
                            posY = userData.getPosY();
                        } else {
                            posX = userData.getPosX() * Grid.getStep();
                            posY = userData.getPosY() * Grid.getStep();
                        }
                        switch (userData.getShapeType()) {
                            case CIRCLE:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createCircle(posX, posY, userData.getWidth() * Grid.getStep(), userData.getFriction(), userData.getRotation(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), userData.getFixtureName(), true);
                                else
                                    body = createCircle(posX, posY, userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), userData.getFixtureName(), true);
                                break;
                            case RECTANGLE:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createRectangle(posX, posY, userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep(), userData.getRotation(), userData.getFriction(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), userData.getFixtureName(), true);
                                else
                                    body = createRectangle(posX, posY, userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), userData.getFixtureName(), true);
                                break;
                            case TRIANGLE_LEFT:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createTriangle(posX, posY, userData.getWidth() * Grid.getStep(), true, userData.getRotation(), userData.getFriction(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), userData.getFixtureName(), true);
                                else
                                    body = createTriangle(posX, posY, userData.getWidth() * Grid.getStep(), true, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), userData.getFixtureName(), true);
                                break;
                            case TRIANGLE_RIGHT:
                                if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY))
                                    body = createTriangle(posX, posY, userData.getWidth() * Grid.getStep(), false, userData.getRotation(), userData.getFriction(), userData.getRestitution(), userData.getDensity(), userData.getBodyType(), userData.getFixtureName(), true);
                                else
                                    body = createTriangle(posX, posY, userData.getWidth() * Grid.getStep(), false, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), userData.getFixtureName(), true);
                                break;
                        }
                        if (body != null)
                            PhysicalElementsFactory.setSelection(body);
                    } else if (objects.get(i) instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) objects.get(i);
                        //   MyGdxGame.stage.setKeyboardFocus(MyGdxGame.manager.createShooter(userData.getWorldX(), userData.getWorldY(), userData.getShooterType(), userData.getShellType()));
                    } else if (objects.get(i) instanceof Bomb.UserData) {
                        Bomb.UserData userData = (Bomb.UserData) objects.get(i);
                        L2DEditor.getStage().setKeyboardFocus(createBomb(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth(), userData.getHeight(), userData.getRotation(), userData.getBombType()));
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException f) {
            throw new RuntimeException(f);
        }
    }

    @Override
    public void dispose() {
        box2DRender.dispose();
    }
}