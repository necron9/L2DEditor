package com.l2g.editor.actors.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.l2g.editor.L2DEditor;

public abstract class CollisionActor extends Actor {
    protected Body body;

    @Override
    protected void positionChanged() {
        body.setTransform(getX() / L2DEditor.PPM, getY() / L2DEditor.PPM, body.getAngle());
        super.positionChanged();
    }

    @Override
    protected void rotationChanged() {
        body.setTransform(body.getPosition(), CollisionActor.this.getRotation() * MathUtils.radiansToDegrees);
        super.rotationChanged();
    }

    @Override
    public boolean remove() {
        L2DEditor.SingletonHolder.getEditorManager().deleteBody(body);
        return super.remove();
    }
}