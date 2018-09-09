package com.l2g.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.l2g.editor.EditorManager;
import com.l2g.editor.L2DEditor;

public class RightPanel extends Window {
    public static final short
            RIGHT_PANEL_WIDTH = 255;

    private static final short
            TABLE_PADDING = 15,
            TABLE_WIDTH = 240,
            WINDOW_RESIZE_BORDER = 40;

    private RightPanelStyle style;
    private Array<Cell> cells, tableCells;
    private Table table;

    public static class RightPanelStyle extends Window.WindowStyle {
        public Drawable background, backgroundTable;
    }

    public RightPanel(String title, RightPanelStyle style) {
        super(title, style);

        table = new Table();
        table.setSkin(L2DEditor.SingletonHolder.getSkin());

        left(); top();

        table.add("").row();
        table.add("").row();
        table.add("").row();
        table.add("").row();
        table.add("").row();
        table.add("").row();
        table.add("").row();
        table.add("").row();
        table.add("");

        add(table).width(TABLE_WIDTH).pad(TABLE_PADDING, TABLE_PADDING, TABLE_PADDING, 0).left().row();
        add(new Actor()).width(TABLE_WIDTH).padLeft(TABLE_PADDING).left();

        cells = getCells();

        tableCells = table.getCells();
        for (Cell cell: tableCells
        ) {
            cell.left();
        }

        setResizeBorder(WINDOW_RESIZE_BORDER);
        setKeepWithinStage(true);
        setModal(false);
        setResizable(true);
        setMovable(false);

        setStyle(style);
        setSize(getPrefWidth(), getPrefHeight());
    }

    private void setStyle(RightPanelStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);
        table.setBackground(style.backgroundTable);

        invalidateHierarchy();
    }

    @Override
    protected void sizeChanged() {
        L2DEditor.SingletonHolder.getWorkArea().setWidth(L2DEditor.SCREEN_WIDTH - LeftPanel.LEFT_PANEL_WIDTH - getWidth());
        super.sizeChanged();
    }

    @Override
    public void act(float delta) {
        EditorManager editorManager = L2DEditor.SingletonHolder.getEditorManager();

        ((Label) tableCells.get(0).getActor()).setText("Bodies: " + editorManager.getBodyCount());
        ((Label) tableCells.get(1).getActor()).setText("Fixtures: " + editorManager.getFixtureCount());
        ((Label) tableCells.get(2).getActor()).setText("Joints: " + editorManager.getJointCount());
        ((Label) tableCells.get(3).getActor()).setText("Contacts: " + editorManager.getContactCount());
        ((Label) tableCells.get(4).getActor()).setText("Proxies: " + editorManager.getProxyCount());
        ((Label) tableCells.get(5).getActor()).setText("Gravity: " + editorManager.getGravityHorizontal() + "  " + editorManager.getGravityVertical());
        ((Label) tableCells.get(6).getActor()).setText("Time step: " + String.format("%2.4f", L2DEditor.FRAME_DURATION));
        ((Label) tableCells.get(7).getActor()).setText("Velocity iteration: " + L2DEditor.VELOCITY_ITERATIONS);
        ((Label) tableCells.get(8).getActor()).setText("Position iteration: " + L2DEditor.POSITION_ITERATIONS);
        super.act(delta);
    }

    public void setCurrentUI(Table table) {
        getCells().get(1).setActor(table);
    }

    public void clearCurrentUI() {
        getCells().get(1).clearActor();
    }
}