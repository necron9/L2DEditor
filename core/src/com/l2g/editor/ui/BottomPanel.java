package com.l2g.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.utils.CameraUtils;

public class BottomPanel extends Table {
    public static final short
            BOTTOM_PANEL_HEIGHT = 30;

    public static final float
            DELETE_BORDER_WIDTH = L2DEditor.SCREEN_WIDTH * 1.3f,
            DELETE_BORDER_HIEGHT = L2DEditor.SCREEN_HEIGHT * 1.3f;

    private static final short
            CHECK_BOX_PADDING = 10,
            SLIDER_GRID_MIN = 40,
            SLIDER_GRID_MAX = 80,
            SLIDER_GRID_STEP = 1;

    private BottomPanelStyle style;
    public static Array<Cell> cells;

    public static class BottomPanelStyle {
        public Drawable background;
    }

    public BottomPanel(BottomPanelStyle style) {
        left();
        setSkin(L2DEditor.SingletonHolder.getSkin());

        add(new CheckBox("Border", L2DEditor.SingletonHolder.getSkin()).pad(CHECK_BOX_PADDING));
        add(new Image(L2DEditor.SingletonHolder.getSkin().getDrawable("separator_icon"))).pad(CHECK_BOX_PADDING).width(2);
        add(new CheckBox("  Grid", L2DEditor.SingletonHolder.getSkin())).pad(CHECK_BOX_PADDING);
        add(new Slider(SLIDER_GRID_MIN, SLIDER_GRID_MAX, SLIDER_GRID_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class)));
        add("").pad(CHECK_BOX_PADDING).width(15);
        add(new CheckBox("  Resize border", L2DEditor.SingletonHolder.getSkin())).pad(CHECK_BOX_PADDING);
        add(new CheckBox("  Camera border", L2DEditor.SingletonHolder.getSkin())).pad(CHECK_BOX_PADDING);
        add(new CheckBox("  Show map", L2DEditor.SingletonHolder.getSkin())).pad(CHECK_BOX_PADDING);
        add(new TextButton("Align objects", L2DEditor.SingletonHolder.getSkin().get("horizontal_button_style", TextButton.TextButtonStyle.class))).left();
        add("FPS: ").pad(CHECK_BOX_PADDING).padLeft(300);

        cells = getCells();

        ((CheckBox) cells.get(0).getActor()).setChecked(true);
        ((CheckBox) cells.get(2).getActor()).setChecked(true);
        ((CheckBox) cells.get(5).getActor()).setChecked(true);
        ((CheckBox) cells.get(6).getActor()).setChecked(L2DEditor.isCameraBorder());
        ((CheckBox) cells.get(7).getActor()).setChecked(L2DEditor.isMap());

        setStyle(style);
        initialize();
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyle(BottomPanelStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);

        invalidateHierarchy();
    }

    private void initialize() {
        // Border
        cells.get(0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.setBorderVisible(((CheckBox) actor).isChecked());
            }
        });

        // Grid
        cells.get(2).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                L2DEditor.SingletonHolder.getGrid().setVisible(((CheckBox) actor).isChecked());
                cells.get(3).getActor().setVisible(L2DEditor.SingletonHolder.getGrid().isVisible());
                cells.get(4).getActor().setVisible(L2DEditor.SingletonHolder.getGrid().isVisible());
            }
        });

        // Grid slider
        cells.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.isFrameStep()) {
                    Slider slider = (Slider) actor;
                    L2DEditor.SingletonHolder.getGrid().setSize((int) slider.getValue());
                    ((Label) cells.get(4).getActor()).setText("" + (short)slider.getValue());
                    if (L2DEditor.SingletonHolder.getGrid().isVisible()) L2DEditor.SingletonHolder.getEditorManager().alignObjects(L2DEditor.getStage());

                    L2DEditor.isFrameStep(false);
                }
            }
        });

        // Resize border
        cells.get(4).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.isResizeBorder(((CheckBox) actor).isChecked());
            }
        });

        // Camera border
        cells.get(6).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CameraUtils.setCameraBorder(((CheckBox) actor).isChecked());
            }
        });

        // Show map
        cells.get(7).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                L2DEditor.isMap(((CheckBox) actor).isChecked());
            }
        });

        // align objects
        cells.get(8).getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                L2DEditor.SingletonHolder.getEditorManager().alignObjects(L2DEditor.getStage());
                super.clicked(event, x, y);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ((Label) cells.get(9).getActor()).setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        super.draw(batch, parentAlpha);
    }
}