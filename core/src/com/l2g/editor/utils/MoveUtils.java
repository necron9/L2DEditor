package com.l2g.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;

public abstract class MoveUtils {
    private final static int MAX_FORCE_MOUSE = 5000000;

    private static MouseJointDef mouseJointDef;
    private static MouseJoint mouseJoint;

    private static PhysicalElementsFactory.UserData userData;
    private static Vector3 position;
    private static Vector2 endPoint;

    private static float gravityScale;
    private static boolean fixedRotation;
    private static BodyDef.BodyType bodyType;

    public static void takeObject(Body body) {
        EditorScreen.setSelection(body);

        if (EditorScreen.getSelection().getUserData() != null) {
            if (EditorScreen.getSelection().getUserData() instanceof ExplosiveElementsFactory.UserData) {
                userData = (ExplosiveElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                Gdx.app.log("UserData instanceof ","Shell.UserData");
            }
            else if (EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                Gdx.app.log("UserData instanceof ","Element.UserData");
            }
        }

        if (endPoint == null)
            endPoint = new Vector2();

        bodyType = EditorScreen.getSelection().getType();
        fixedRotation = EditorScreen.getSelection().isFixedRotation();
        EditorScreen.getSelection().setFixedRotation(true);
        gravityScale = EditorScreen.getSelection().getGravityScale();
        EditorScreen.getSelection().setGravityScale(0);

        if (mouseJointDef == null) {
            mouseJointDef = new MouseJointDef();
            mouseJointDef.collideConnected = true;
            mouseJointDef.maxForce = MAX_FORCE_MOUSE;
            mouseJointDef.bodyA = L2DEditor.SingletonHolder.getEditorManager().createBody(new BodyDef());
        }

        mouseJointDef.bodyB = EditorScreen.getSelection();
        if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
            mouseJointDef.target.set(L2DEditor.SingletonHolder.getTouch().x / L2DEditor.PPM, L2DEditor.SingletonHolder.getTouch().y / L2DEditor.PPM);
        } else {
            EditorScreen.getSelection().setType(BodyDef.BodyType.DynamicBody);
            position = L2DEditor.SingletonHolder.getGrid().getPosition(L2DEditor.SingletonHolder.getTouch());
            mouseJointDef.target.set(position.x / L2DEditor.PPM, position.y / L2DEditor.PPM);
        }

        mouseJoint = (MouseJoint) L2DEditor.SingletonHolder.getEditorManager().createJoint(mouseJointDef);
        if (mouseJoint != null)
        mouseJoint.setUserData(true);
    }

    public static void putObject(float screenX, float screenY) {
        if (mouseJoint == null) return;

        if (mouseJoint.getUserData() != null && (Boolean) mouseJoint.getUserData()) {
            EditorScreen.getSelection().setType(bodyType);
            EditorScreen.getSelection().setFixedRotation(fixedRotation);
            EditorScreen.getSelection().setGravityScale(gravityScale);

            L2DEditor.SingletonHolder.getEditorManager().destroyJoint(mouseJoint);

            L2DEditor.getCamera().unproject(L2DEditor.SingletonHolder.getTouch().set(screenX, screenY, 0));

            if (!bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                endPoint = EditorScreen.getSelection().getPosition();
                endPoint.scl(L2DEditor.PPM);

                if (L2DEditor.SingletonHolder.getGrid().contains(endPoint.x, endPoint.y))
                    EditorScreen.getSelection().setTransform(endPoint.x / L2DEditor.PPM, endPoint.y / L2DEditor.PPM, userData.getRotation() * MathUtils.degreesToRadians);
                else {
                    position = L2DEditor.SingletonHolder.getGrid().move(endPoint.x + 1, endPoint.y + 1);
                    EditorScreen.getSelection().setTransform(position.x / L2DEditor.PPM, position.y / L2DEditor.PPM, userData.getRotation() * MathUtils.degreesToRadians);
                }
            }

            if (userData != null) {
                if (userData.isPolygon()) {
                    userData.setPosition(EditorScreen.getSelection().getPosition().x * L2DEditor.PPM + userData.getWidth(),
                            EditorScreen.getSelection().getPosition().y * L2DEditor.PPM + userData.getHeight());
                } else
                    userData.setPosition(EditorScreen.getSelection().getPosition().scl(L2DEditor.PPM));
                userData = null;
            }
        }
    }

    public static void moveObject(float screenX, float screenY) {
        if (mouseJoint == null) return;

        if (mouseJoint.getUserData() != null && (Boolean) mouseJoint.getUserData()) {
            L2DEditor.getCamera().unproject(L2DEditor.SingletonHolder.getTouch().set(screenX, screenY, 0));

            if (bodyType.equals(BodyDef.BodyType.DynamicBody)) {
                if ((L2DEditor.SingletonHolder.getTouch().x / L2DEditor.PPM < 10 / L2DEditor.PPM) && (L2DEditor.SingletonHolder.getTouch().x / L2DEditor.PPM > 2 / L2DEditor.PPM))
                    EditorScreen.getSelection().setTransform(L2DEditor.SingletonHolder.getTouch().x / L2DEditor.PPM, L2DEditor.SingletonHolder.getTouch().y / L2DEditor.PPM, userData.getRotation());

                mouseJoint.setTarget(endPoint.set(L2DEditor.SingletonHolder.getTouch().x / L2DEditor.PPM, L2DEditor.SingletonHolder.getTouch().y / L2DEditor.PPM));
            } else {
                position = L2DEditor.SingletonHolder.getGrid().getPosition(L2DEditor.SingletonHolder.getTouch());

                if ((position.x / L2DEditor.PPM < 10 / L2DEditor.PPM) && (position.x / L2DEditor.PPM > 2 / L2DEditor.PPM))
                    EditorScreen.getSelection().setTransform(position.x / L2DEditor.PPM, position.y / L2DEditor.PPM, 0);

                mouseJoint.setTarget(endPoint.set(position.x / L2DEditor.PPM, position.y / L2DEditor.PPM));
            }
        }
    }

    public static void setPosition(Body body, float x, float y) {
        body.setTransform(x, y, body.getAngle());

        if (body.getUserData() != null && body.getUserData() instanceof PhysicalElementsFactory.UserData) {
            PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) body.getUserData();
            userData.setPosition(body.getPosition().scl(L2DEditor.PPM));
        }
    }

    public static MouseJoint getMouseJoint() {
        return mouseJoint;
    }
}

