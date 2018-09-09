package com.l2g.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.l2g.editor.L2DEditor;

public class LeftPanel extends Table {
    public static final short
            LEFT_PANEL_WIDTH = 120;

    private static final short
            PAD_TOP = 10;

    private LeftPanelStyle style;
    private Array<Cell> cells;
    private Tree tree;

    public static class LeftPanelStyle {
        public Drawable background;
    }

    public LeftPanel(LeftPanelStyle style) {
        top(); left();

        add(new Tree(L2DEditor.SingletonHolder.getSkin())).padTop(PAD_TOP).width(70).top();

        cells = getCells();

        cells.get(0).height(L2DEditor.SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT - ToolBar.TOOL_BAR_HEIGHT - BottomPanel.BOTTOM_PANEL_HEIGHT).padLeft(41);

        tree = (Tree) cells.get(0).getActor();
        tree.add(new Tree.Node(new Label("Actors", L2DEditor.SingletonHolder.getSkin())));
        tree.expandAll();
        tree.pack();

        setStyle(style);
        initialize();
        setSize(getPrefWidth(), getPrefHeight());
    }

    private void setStyle(LeftPanelStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);

        invalidateHierarchy();
    }

    private void initialize() {
        cells.get(0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                StringBuilder selected = null;
                if (((Tree) actor).getSelection().first() != null)
                    selected = ((Label)((Tree) actor).getSelection().first().getActor()).getText();

                if (selected != null)
                    for (Actor actorStage: L2DEditor.getStage().getActors()
                    ) {
                        if (actorStage.getName().equals(selected.toString())) {
                            L2DEditor.getStage().setKeyboardFocus(actorStage);
                            break;
                        }
                    }
                else L2DEditor.getStage().setKeyboardFocus(null);
            }
        });
    }

    public void addNode(Label label) {
        tree.getNodes().get(0).add(new Tree.Node(label));
    }

    public void removeNode(Actor actor) {
        for (Tree.Node node: tree.getNodes().get(0).getChildren()) {
            if (((Label) node.getActor()).getText().toString().equals(actor.getName()))
                tree.remove(node);
        }
    }

    public void clearSelection() {
        tree.getSelection().clear();
    }

    /*public void setSelection(Actor actor) {
        Selection selection = tree.getSelection();
        for (Tree.Node node: tree.getNodes().get(0).getChildren()
             ) {
                if (((Label) node.getActor()).getText().toString().equals(actor.getName()))
                    ;;
                  //  selection.setActor(node.getActor());
                  //  Gdx.app.log("","" + actor.getName());

                //tree.getNodes().get(0).setObject(actor.getName());
        }
    }*/

    public void clear() {
        tree.getNodes().get(0).removeAll();
    }
}