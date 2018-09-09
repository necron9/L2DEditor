package com.l2g.editor.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.actors.Bomb;
import com.l2g.editor.actors.Shooter;

public final class ActorListener extends InputListener {
    private static float MOVE_DURATION = 0f;

    private Shooter shooter;
    private Bomb bomb;

    public ActorListener() {
    }

    @Override
    public boolean keyTyped(InputEvent event, char character) {
        return super.keyTyped(event, character);
    }

    @Override
    public boolean handle(Event e) {
        return super.handle(e);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        switch (button) {
            case 0:
                if (event.getTarget() instanceof Shooter) {
                    shooter = (Shooter) event.getListenerActor();
                } else if (event.getTarget() instanceof Bomb) {
                    bomb = (Bomb) event.getListenerActor();
                }

                if (shooter != null) {
                    if (L2DEditor.isDelete())
                        shooter.remove();
                    else {
                        L2DEditor.getStage().setKeyboardFocus(shooter);
                        shooter = null;
                    }
                } else if (bomb != null) {
                    if (L2DEditor.isDelete())
                        bomb.remove();
                    else {
                        L2DEditor.getStage().setKeyboardFocus(bomb);
                        bomb = null;
                    }
                }
                break;
        }

        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        super.touchDragged(event, x, y, pointer);
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        return super.mouseMoved(event, x, y);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        event.getListenerActor().setDebug(true);
        super.enter(event, x, y, pointer, fromActor);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (L2DEditor.getStage().getKeyboardFocus() != null)
            if (!L2DEditor.getStage().getKeyboardFocus().equals(event.getListenerActor())) event.getListenerActor().setDebug(false);
        super.exit(event, x, y, pointer, toActor);
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        return super.scrolled(event, x, y, amount);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        MoveByAction mba;
        switch (keycode) {
            case Input.Keys.D:
                mba = new MoveByAction();
                mba.setAmount(Grid.getStep(), 0);
                mba.setDuration(MOVE_DURATION);
                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.A:
                mba = new MoveByAction();
                mba.setAmount(-Grid.getStep(), 0);
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.W:
                mba = new MoveByAction();
                mba.setAmount(0, Grid.getStep());
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.S:
                mba = new MoveByAction();
                mba.setAmount(0, -Grid.getStep());
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.Q:
                mba = new MoveByAction();
                mba.setAmount(-Grid.getStep(), Grid.getStep());
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.E:
                mba = new MoveByAction();
                mba.setAmount(Grid.getStep(), Grid.getStep());
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.Z:
                mba = new MoveByAction();
                mba.setAmount(-Grid.getStep(), -Grid.getStep());
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.C:
                mba = new MoveByAction();
                mba.setAmount(Grid.getStep(), -Grid.getStep());
                mba.setDuration(MOVE_DURATION);

                event.getListenerActor().addAction(mba);
                break;
            case Input.Keys.SPACE:
                L2DEditor.getStage().setKeyboardFocus(null);
                break;
        }

        return super.keyDown(event, keycode);
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        return super.keyUp(event, keycode);
    }
}