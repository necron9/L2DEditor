package com.l2g.editor.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.actors.Bomb;
import com.l2g.editor.elements.BlastFactory;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.utils.MoveUtils;

public class WorldContactListener implements ContactListener {
    private boolean isDelete;

    public WorldContactListener() {
    }

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(final Contact contact, Manifold oldManifold) {
        if (contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().contains("border")) {
            if (setDeleteFixture(contact.getFixtureB())) {
                if (MoveUtils.getMouseJoint() != null && MoveUtils.getMouseJoint().getBodyB().equals(contact.getFixtureB().getBody())) {
                    MoveUtils.getMouseJoint().setUserData(false);
                }
                L2DEditor.SingletonHolder.getEditorManager().deleteBody(contact.getFixtureB().getBody());
            } else contact.setEnabled(false);
        } else {
            if (contact.getFixtureA().getUserData() != null)
                collision(contact.getFixtureA(), contact.getFixtureA().getUserData().toString(), true);
            if (contact.getFixtureB().getUserData() != null)
                collision(contact.getFixtureB(), contact.getFixtureB().getUserData().toString(), true);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (contact.getFixtureA().getUserData() != null)
            collision(contact.getFixtureA(), contact.getFixtureA().getUserData().toString(), false);
        if (contact.getFixtureB().getUserData() != null)
            collision(contact.getFixtureB(), contact.getFixtureB().getUserData().toString(), false);
    }

    private void collision(Fixture fixture, String string, boolean solve) {
        boolean isFind;

        isFind = isShellFixture(fixture, string, solve);

        if (!isFind)
            isBombFixture(fixture, string, solve);
    }

    private boolean isShellFixture(Fixture fixture, String data, boolean solve) {
        if (solve && data.contains(Names.fixtureNames[0])) {
            //landmine
            if (data.contains(ExplosiveElementsFactory.shellType.LANDMINE.toString())) {
                BlastFactory.createBlast(fixture.getBody(), BlastFactory.blastType.EXPLOSIVE);
            }

            // cannonball
            else if (data.contains(ExplosiveElementsFactory.shellType.CANNONBALL.toString())) {
                // Blast.createBlast(fixture.getBody(), Blast.blastType.EXPLOSIVE);
            }
            return true;
        } else if(!solve && data.contains(Names.fixtureNames[0])) {
            isDelete = setDeleteFixture(fixture);

            if (isDelete) {
                L2DEditor.SingletonHolder.getEditorManager().deleteFixture(fixture);
                isDelete = false;
            }
            return true;
        }
        return false;
    }

    private boolean isBombFixture(Fixture fixture, String data, boolean solve) {
        if (solve && data.contains(Names.fixtureNames[2])) {
            // mine
            if (data.contains(Bomb.bombType.MINE.toString())) {
                BlastFactory.createBlast(fixture.getBody(), BlastFactory.blastType.EXPLOSIVE);
            }
            return true;
        } else if (!solve && data.contains(Names.fixtureNames[2])) {
            isDelete = setDeleteFixture(fixture);
            if (isDelete) {
                L2DEditor.SingletonHolder.getEditorManager().deleteBody(fixture.getBody());
                isDelete = false;
            }
            return true;
        }
        return false;
    }

    private boolean setDeleteFixture(final Fixture fixture) {
        if (fixture.getUserData() != null) {
            String data = fixture.getUserData().toString();
            if (Names.noDelete == data.charAt(0)) {
                StringBuffer newUserData = new StringBuffer(data);
                newUserData.setCharAt(0, Names.yesDelete);
                fixture.setUserData(newUserData);
                return true;
            }
        }

        return false;
    }
}