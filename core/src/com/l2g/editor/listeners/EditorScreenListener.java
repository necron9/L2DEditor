package com.l2g.editor.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.l2g.editor.EditorManager;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.actors.Bomb;
import com.l2g.editor.actors.Shooter;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.utils.CameraUtils;
import com.l2g.editor.utils.MoveUtils;

public class EditorScreenListener implements InputProcessor {
    private final EditorScreen editorScreen;

    // private static MouseJoint mouseJoint;

    private Actor movedActor;
    //private MouseJointDef mouseJointDef;
    private QueryCallback queryCallback;
    //private Vector2 endPoint, direction;
    private Vector3 position;
    //private Element.UserData userData;

    private boolean isMove;//, isDrag;

    public EditorScreenListener(final EditorScreen editorScreen) {
        this.editorScreen = editorScreen;

        //mouseJointDef = new MouseJointDef();
        //endPoint = new Vector2();
        //direction = new Vector2();

        //mouseJointDef.collideConnected = true;
        //mouseJointDef.maxForce = MyGdxGame.MAX_FORCE_MOUSE;
        //mouseJointDef.bodyA = MyGdxGame.world.createBody(new BodyDef());

        isMove = false;
        //isDrag = false;

        L2DEditor.setResizeBorder(L2DEditor.getViewport().getScreenWidth() / 12f);

        queryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if(!fixture.testPoint(L2DEditor.SingletonHolder.getTouch().x / L2DEditor.PPM, L2DEditor.SingletonHolder.getTouch().y / L2DEditor.PPM)) {
                    return false;
                }


                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                    CameraUtils.moveToTarget(L2DEditor.getCamera(), fixture.getBody().getPosition().scl(L2DEditor.PPM), CameraUtils.DEFAULT_CAMERA_SMOOTH);

                if (fixture.getUserData() != null) {
                    if (!fixture.getBody().getUserData().toString().contains("collision"))
                        if (L2DEditor.isDelete()) {
                            L2DEditor.SingletonHolder.getEditorManager().deleteBody(fixture.getBody());
                            return false;
                        } else
                            MoveUtils.takeObject(fixture.getBody());
                    //moveObject(fixture.getBody());
                } else
                    MoveUtils.takeObject(fixture.getBody());
                //moveObject(fixture.getBody());

                return false;
            }
        };
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                if (!EditorScreen.isVisible())
                    EditorScreen.setVisible(true);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (EditorScreen.getSelection() != null) {
            float step = L2DEditor.SingletonHolder.getGrid().getStep() / L2DEditor.PPM;
            Body body = EditorScreen.getSelection();

            switch (character) {
                case 'w':
                    MoveUtils.setPosition(body, body.getPosition().x, body.getPosition().y + step);
                    break;
                case 's':
                    MoveUtils.setPosition(body, body.getPosition().x, body.getPosition().y - step);
                    break;
                case 'a':
                    MoveUtils.setPosition(body, body.getPosition().x - step, body.getPosition().y);
                    break;
                case 'd':
                    MoveUtils.setPosition(body, body.getPosition().x + step, body.getPosition().y);
                    break;
                case 'q':
                    MoveUtils.setPosition(body, body.getPosition().x - step, body.getPosition().y + step);
                    break;
                case 'e':
                    MoveUtils.setPosition(body, body.getPosition().x + step, body.getPosition().y + step);
                    break;
                case 'z':
                    MoveUtils.setPosition(body, body.getPosition().x - step, body.getPosition().y - step);
                    break;
                case 'c':
                    MoveUtils.setPosition(body, body.getPosition().x + step, body.getPosition().y - step);
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touch = L2DEditor.SingletonHolder.getTouch();
        EditorManager manager = L2DEditor.SingletonHolder.getEditorManager();
        Stage stage = L2DEditor.getStage();

        L2DEditor.getCamera().unproject(touch.set(screenX, screenY, 0));

        //getPosition();
        position = L2DEditor.SingletonHolder.getGrid().getPosition(touch);

        if (L2DEditor.SingletonHolder.getWorkArea().contains(screenX, screenY))
            CameraUtils.setStartPoint(screenX, screenY);

        switch (button) {
            case 0:
                if (stage.hit(touch.x, touch.y, false) == null && L2DEditor.SingletonHolder.getWorkArea().contains(screenX, screenY)) {
                    stage.setKeyboardFocus(null);
                }

                if (L2DEditor.SingletonHolder.getWorkArea().contains(screenX, screenY))
                    PhysicalElementsFactory.setSelection(null);
                manager.queryAABB(queryCallback, touch.x / L2DEditor.PPM, touch.y / L2DEditor.PPM,
                        touch.x / L2DEditor.PPM, touch.y / L2DEditor.PPM);
                break;
            case 1:
                if (stage.hit(touch.x, touch.y, false) != null) {
                    movedActor = stage.hit(touch.x, touch.y, false);
                    isMove = !isMove;
                } else if (!L2DEditor.isDelete()) {
                    float size = EditorManager.SHAPE_SIZE * Grid.getStep();
                    switch (EditorManager.elementType.type) {
                        case SHOOTER:
                            stage.setKeyboardFocus(manager.createShooter(position.x, position.y, Shooter.SHOOTER_WIDTH, Shooter.SHOOTER_HEIGHT, 0, 0, Shooter.shooterType.type, ExplosiveElementsFactory.shellType.type));
                            break;
                        case BOMB:
                            stage.setKeyboardFocus(manager.createBomb(position.x, position.y, size, size, 0, Bomb.bombType.type));
                            break;
                        case LANDMINE:
                            PhysicalElementsFactory.setSelection(manager.createLandmine(position.x, position.y, 25, 0, PhysicalElementsFactory.shapeType.type, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, true));
                            break;
                        case RECTANGLE:
                            PhysicalElementsFactory.setSelection(manager.createRectangle(position.x + size, position.y + size, size, size, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.RECTANGLE.toString(), true));
                            break;
                        case RECTANGLE_ARRAY:
                            manager.createRectangleArray(position.x + size, position.y + size, size, size, EditorManager.ARRAY_SIZE_1, EditorManager.ARRAY_SIZE_2, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.RECTANGLE.toString(), true);
                            break;
                        case CIRCLE:
                            PhysicalElementsFactory.setSelection(manager.createCircle(position.x + size, position.y + size, size, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.CIRCLE.toString(), true));
                            break;
                        case CIRCLE_ARRAY:
                            manager.createCircleArray(position.x + size, position.y + size, size, EditorManager.ARRAY_SIZE_1, EditorManager.ARRAY_SIZE_2, 0, PhysicalElementsFactory.materialType.STONE, BodyDef.BodyType.StaticBody, EditorManager.elementType.CIRCLE.toString(), true);
                            break;
                        case TRIANGLE_LEFT:
                            PhysicalElementsFactory.setSelection(manager.createTriangle(position.x, position.y, size, true, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.TRIANGLE_LEFT.toString(), true));
                            break;
                        case TRIANGLE_LEFT_ARRAY:
                            manager.createTriangleArray(position.x, position.y, size, true, EditorManager.ARRAY_SIZE_1, EditorManager.ARRAY_SIZE_2, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.TRIANGLE_LEFT.toString(), true);
                            break;
                        case TRIANGLE_RIGHT:
                            PhysicalElementsFactory.setSelection(manager.createTriangle(position.x, position.y, size, false, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.TRIANGLE_RIGHT.toString(), true));
                            break;
                        case TRIANGLE_RIGHT_ARRAY:
                            manager.createTriangleArray(position.x, position.y, size, false, EditorManager.ARRAY_SIZE_1, EditorManager.ARRAY_SIZE_2, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.StaticBody, EditorManager.elementType.TRIANGLE_RIGHT.toString(), true);
                            break;
                        case DYNAMIC_BOXES:
                            manager.createRectangleArray(position.x, position.y, Grid.getStep(), Grid.getStep(), EditorManager.ARRAY_SIZE_1, EditorManager.ARRAY_SIZE_2, 0, PhysicalElementsFactory.materialType.type, BodyDef.BodyType.DynamicBody, EditorManager.elementType.RECTANGLE.toString(), true);
                            break;
                    }

                    L2DEditor.isChanged(true);
                }
                break;
            case 2:
                break;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case 0:
                MoveUtils.putObject(screenX, screenY);
                break;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (L2DEditor.SingletonHolder.getWorkArea().contains(screenX, screenY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                EditorScreen.getSelection() == null && L2DEditor.getStage().getKeyboardFocus() == null)
            CameraUtils.moveToTouch(L2DEditor.getCamera(), screenX, screenY);

        if (EditorScreen.getSelection() != null)
            MoveUtils.moveObject(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (movedActor != null && isMove) {
            L2DEditor.getCamera().unproject(L2DEditor.SingletonHolder.getTouch().set(screenX, screenY, 0));

            movedActor.setPosition(L2DEditor.SingletonHolder.getTouch().x - movedActor.getWidth() *.5f, L2DEditor.SingletonHolder.getTouch().y - movedActor.getHeight() *.5f);
            position = L2DEditor.SingletonHolder.getGrid().getPosition(L2DEditor.SingletonHolder.getTouch());
            movedActor.setPosition(position.x, position.y);
        }

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        switch (amount) {
            case 1:
                CameraUtils.zoomOut(L2DEditor.getCamera(), CameraUtils.DEFAULT_ZOOM_SMOOTH);
                break;
            case -1:
                CameraUtils.zoomIn(L2DEditor.getCamera(), CameraUtils.DEFAULT_ZOOM_SMOOTH);
                break;
        }
        return false;
    }
}