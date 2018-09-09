package com.l2g.editor.elements.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;

public class ShellUI extends ElementUI {
    private final static float
            SLIDER_LIN_DAMPING_MIN = 0,
            SLIDER_LIN_DAMPING_MAX = 30,
            SLIDER_LIN_DAMPING_STEP = 1,
            SLIDER_ANG_DAMPING_MIN = 0,
            SLIDER_ANG_DAMPING_MAX = 1,
            SLIDER_ANG_DAMPING_STEP = 0.1f;

    private Array<Cell> cellsShell;

    private static boolean isChangedShell;

    public ShellUI(ElementUIStyle style) {
        super(style);
        super.initialize();

        Table shellTable = new Table();

        shellTable.setSkin(L2DEditor.SingletonHolder.getSkin());

        SelectBox<String> selectBox = new SelectBox<String>(L2DEditor.SingletonHolder.getSkin());
        selectBox.setSize(SELECT_BOX_WIDTH, SELECT_BOX_HEIGHT);

        add(selectBox).padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP).left().row();
        shellTable.add("Lin. damping: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        shellTable.add(new Slider(SLIDER_LIN_DAMPING_MIN, SLIDER_LIN_DAMPING_MAX, SLIDER_LIN_DAMPING_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class))).row();
        shellTable.add("Ang. damping: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        shellTable.add(new Slider(SLIDER_ANG_DAMPING_MIN, SLIDER_ANG_DAMPING_MAX, SLIDER_ANG_DAMPING_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class))).row();
        add(shellTable);

        cellsShell = shellTable.getCells();

        isChangedShell = false;

        setStyled(style);
        initialized();
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyled(ElementUIStyle style) {

        Gdx.app.log("SIZE", "");
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);

        invalidateHierarchy();
    }

    public void initialized() {
        ((SelectBox) getCells().get(5).getActor()).setItems(ExplosiveElementsFactory.shellType.values());

        // select shell
        getCells().get(5).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                ExplosiveElementsFactory.shellType selected = (ExplosiveElementsFactory.shellType) selectBox.getSelected();

                if (!selected.equals(ExplosiveElementsFactory.shellType.EMPTY)) {
                    isChangedShell = false;

                    if (selected.equals(ExplosiveElementsFactory.shellType.CANNONBALL)) {
                        ((Slider) cellsShell.get(1).getActor()).setValue(Names.shellParameters[0][0]);
                        ((Slider) cellsShell.get(3).getActor()).setValue(Names.shellParameters[0][1]);
                    } else if (selected.equals(ExplosiveElementsFactory.shellType.LANDMINE)) {
                        ((Slider) cellsShell.get(1).getActor()).setValue(Names.shellParameters[1][0]);
                        ((Slider) cellsShell.get(3).getActor()).setValue(Names.shellParameters[1][1]);
                    }

                    cellsShell.get(1).getActor().fire(new ChangeEvent());
                    cellsShell.get(3).getActor().fire(new ChangeEvent());
                }

                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof ExplosiveElementsFactory.UserData) {
                    ExplosiveElementsFactory.UserData userData = (ExplosiveElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setShellType(selected);
                }
            }
        });

        // linear damping
        cellsShell.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof ExplosiveElementsFactory.UserData) {
                    ExplosiveElementsFactory.UserData userData = (ExplosiveElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setLinearDamping(((Slider) actor).getValue());
                    if (isChangedShell) {
                        userData.setShellType(ExplosiveElementsFactory.shellType.EMPTY);
                        ((SelectBox) EditorScreen.getShellUI().getCells().get(4).getActor()).setSelected(ExplosiveElementsFactory.shellType.EMPTY);
                    }
                }
            }
        });

        cellsShell.get(1).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChangedShell = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // angular damping
        cellsShell.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof ExplosiveElementsFactory.UserData) {
                    ExplosiveElementsFactory.UserData userData = (ExplosiveElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setAngularDamping(((Slider) actor).getValue());
                    if (isChangedShell) {
                        userData.setShellType(ExplosiveElementsFactory.shellType.EMPTY);
                        ((SelectBox) EditorScreen.getShellUI().getCells().get(4).getActor()).setSelected(ExplosiveElementsFactory.shellType.EMPTY);
                    }
                }
            }
        });

        cellsShell.get(3).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChangedShell = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public static void setValues(ExplosiveElementsFactory.UserData userData) {
        if (userData != null) {
            ElementUI.isActiveSliderShape(false);
            ((SelectBox) EditorScreen.getElementUI().getCells().get(0).getActor()).setSelected(userData.getShapeType());
            EditorScreen.getElementUI().getCells().get(0).getActor().fire(new ChangeListener.ChangeEvent());
            ElementUI.isActiveSliderShape(true);

            if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY)) {
                ((Slider) EditorScreen.getElementUI().cellsMaterial.get(1).getActor()).setValue(userData.getFriction());
                ((Slider) EditorScreen.getElementUI().cellsMaterial.get(3).getActor()).setValue(userData.getRestitution());
                ((Slider) EditorScreen.getElementUI().cellsMaterial.get(5).getActor()).setValue(userData.getDensity());
            }

            ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(userData.getMaterialType());

            EditorScreen.getElementUI().getCells().get(2).getActor().fire(new ChangeListener.ChangeEvent());

            if (userData.getShapeType().equals(ExplosiveElementsFactory.shellType.EMPTY)) {
                ((Slider) EditorScreen.getShellUI().cellsShell.get(1).getActor()).setValue(userData.getLinearDamping());
                ((Slider) EditorScreen.getShellUI().cellsShell.get(3).getActor()).setValue(userData.getAngularDamping());
            }

            ((SelectBox) EditorScreen.getShellUI().getCells().get(4).getActor()).setSelected(userData.getShellType());

            EditorScreen.getShellUI().getCells().get(4).getActor().fire(new ChangeListener.ChangeEvent());
        }
    }
}