package com.l2g.editor.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;

public abstract class BlastFactory {
    private static float
            blastDuration,
            blastPower,
            blastRadius;

    private static short
            counterDebug,
            amountRays;

    private static boolean
            isDebugRays = false;

    private static RayCastCallback
            callback;

    private static Vector2
            origin,
            direction[];

    public enum blastType {
        EXPLOSIVE
    }

    public static void createBlast(final Body body, blastType type) {
        switch (type) {
            case EXPLOSIVE:
                amountRays = (short) Names.blastParameters[0][0];
                blastDuration = Names.blastParameters[0][1];
                blastPower = Names.blastParameters[0][2];
                blastRadius = Names.blastParameters[0][3];
                createRaycastBlast(body);

                break;
        }
    }

    private static void createRaycastBlast(final Body body) {
        float step = 360 / amountRays;
        origin = new Vector2();
        direction = new Vector2[amountRays];

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                applyBlastImpulse(fixture.getBody(), origin, point, blastPower);
                return 0;
            }
        };

        origin.set(body.getPosition());

        for (int i = 0; i < amountRays; i++) {
            direction[i] = new Vector2(1, 1);
            direction[i].setAngle(step * i);
            direction[i].setLength(blastRadius);
            direction[i].set(direction[i].x + origin.x, direction[i].y + origin.y);
            L2DEditor.SingletonHolder.getEditorManager().rayCast(callback, origin, direction[i]);
        }

        isDebugRays = true;
        counterDebug = 0;
    }

    private static void applyBlastImpulse(final Body body, final Vector2 blastCenter, final Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = new Vector2(applyPoint.x - blastCenter.x, applyPoint.y - blastCenter.y);
        Vector2 distance2 = blastDir.nor();
        float distance = distance2.len();
        if(distance == 0) return;
        float invDistance = 1 / distance;
        float impulseMag = blastPower * invDistance * invDistance;
        body.applyLinearImpulse(blastDir.x * impulseMag, blastDir.y * impulseMag, applyPoint.x, applyPoint.y, true);
    }

    public static short getCounterDebug() {
        return counterDebug;
    }

    public static void counerDebugInc() {
        counterDebug++;
    }

    public static short getAmountRays() {
        return amountRays;
    }

    public static boolean isDebugRays() {
        return isDebugRays;
    }

    public static Vector2 getOrigin() {
        return origin;
    }

    public static Vector2[] getDirection() {
        return direction;
    }
}