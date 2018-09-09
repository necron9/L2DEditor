package com.l2g.editor.elements.ui;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.l2g.editor.EditorManager;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;

public class ElementUI extends Table {
    public static final float
            SLIDER_SHAPE_MIN = 1,
            SLIDER_SHAPE_MAX = 15,
            SLIDER_SHAPE_STEP = 1,
            SLIDER_FRICTION_MIN = 0f,
            SLIDER_FRICTION_MAX = 1f,
            SLIDER_FRICTION_STEP = 0.05f,
            SLIDER_RESTITUTION_MIN = 0f,
            SLIDER_RESTITUTION_MAX = 1f,
            SLIDER_RESTITUTION_STEP = 0.05f,
            SLIDER_DENSITY_MIN = 50,
            SLIDER_DENSITY_MAX = 2000,
            SLIDER_DENSITY_STEP = 50,
            SELECT_BOX_WIDTH = 128,
            SELECT_BOX_HEIGHT = 24,
            SELECT_BOX_PADDING_BOT = 10,
            SELECT_BOX_PADDING_TOP = 10,
            SLIDER_PADDING = 3,
            LABEL_WIDTH = 90;

    protected ElementUIStyle style;

    protected Array<Cell> cellsShape, cellsMaterial;

    private static boolean isChangedMaterial, isActiveSliderShape;

    public static class ElementUIStyle {
        public Drawable background;
    }

    public ElementUI(ElementUIStyle style) {
        Table tableShape = new Table(), tableMaterial = new Table();

        tableShape.setSkin(L2DEditor.SingletonHolder.getSkin());
        tableMaterial.setSkin(L2DEditor.SingletonHolder.getSkin());

        tableShape.left();
        tableMaterial.left();

        add(new SelectBox<String>(L2DEditor.SingletonHolder.getSkin())).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP).row();
        tableShape.add("Width: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING);
        tableShape.add(new Slider(SLIDER_SHAPE_MIN, SLIDER_SHAPE_MAX, SLIDER_SHAPE_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShape.add("Height: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING);
        tableShape.add(new Slider(SLIDER_SHAPE_MIN, SLIDER_SHAPE_MAX, SLIDER_SHAPE_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class)));
        add(tableShape).row();

        add(new SelectBox<String>(L2DEditor.SingletonHolder.getSkin())).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP).row();
        tableMaterial.add("Friction: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING);
        tableMaterial.add(new Slider(SLIDER_FRICTION_MIN, SLIDER_FRICTION_MAX, SLIDER_FRICTION_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableMaterial.add("Restitution: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING);
        tableMaterial.add(new Slider(SLIDER_RESTITUTION_MIN, SLIDER_RESTITUTION_MAX, SLIDER_RESTITUTION_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableMaterial.add("Density: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING);
        tableMaterial.add(new Slider(SLIDER_DENSITY_MIN, SLIDER_DENSITY_MAX, SLIDER_DENSITY_STEP, false, L2DEditor.SingletonHolder.getSkin().get("slider_horizontal", Slider.SliderStyle.class)));
        add(tableMaterial).row();

        add(new SelectBox<String>(L2DEditor.SingletonHolder.getSkin())).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP);

        cellsShape = tableShape.getCells();
        cellsMaterial = tableMaterial.getCells();

        isChangedMaterial = false;
        isActiveSliderShape = true;

        setStyle(style);
        initialize();
        setSize(getPrefWidth(), getPrefHeight());
    }

    private void setStyle(ElementUIStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;
        setBackground(style.background);
        invalidateHierarchy();
    }

    protected void initialize() {
        ((SelectBox) getCells().get(0).getActor()).setItems(PhysicalElementsFactory.shapeType.values());
        ((SelectBox) getCells().get(2).getActor()).setItems(PhysicalElementsFactory.materialType.values());
        ((SelectBox) getCells().get(4).getActor()).setItems(BodyDef.BodyType.values());

        // select shape
        getCells().get(0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                PhysicalElementsFactory.shapeType selected = (PhysicalElementsFactory.shapeType) selectBox.getSelected();

                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setShapeType(selected);

                    switch (selected) {
                        case RECTANGLE:
                        case CIRCLE:
                            userData.setPolygon(false);
                            break;
                        case TRIANGLE_LEFT:
                        case TRIANGLE_RIGHT:
                        case POLYGON:
                            userData.setPolygon(true);
                            break;
                    }

                    ((Slider)cellsShape.get(1).getActor()).setValue(userData.getWidth());
                    ((Slider)cellsShape.get(3).getActor()).setValue(userData.getHeight());

                    cellsShape.get(1).getActor().fire(new ChangeEvent());
                    cellsShape.get(3).getActor().fire(new ChangeEvent());
                }
            }
        });

        // select material
        getCells().get(2).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                PhysicalElementsFactory.materialType selected = (PhysicalElementsFactory.materialType) selectBox.getSelected();

                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setMaterialType(selected);
                }

                if (!selected.equals(PhysicalElementsFactory.materialType.EMPTY)) {
                    isChangedMaterial = false;

                    if (selected.equals(PhysicalElementsFactory.materialType.WOOD)) {
                        ((Slider) cellsMaterial.get(1).getActor()).setValue(Names.materialParameters[0][0]);
                        ((Slider) cellsMaterial.get(3).getActor()).setValue(Names.materialParameters[0][1]);
                        ((Slider) cellsMaterial.get(5).getActor()).setValue(Names.materialParameters[0][2]);

                    } else if (selected.equals(PhysicalElementsFactory.materialType.STONE)) {
                        ((Slider) cellsMaterial.get(1).getActor()).setValue(Names.materialParameters[1][0]);
                        ((Slider) cellsMaterial.get(3).getActor()).setValue(Names.materialParameters[1][1]);
                        ((Slider) cellsMaterial.get(5).getActor()).setValue(Names.materialParameters[1][2]);
                    }

                    cellsMaterial.get(1).getActor().fire(new ChangeEvent());
                    cellsMaterial.get(3).getActor().fire(new ChangeEvent());
                    cellsMaterial.get(5).getActor().fire(new ChangeEvent());
                }
            }
        });

        // select body type
        getCells().get(4).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                BodyDef.BodyType selected = (BodyDef.BodyType) selectBox.getSelected();

                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setBodyType(selected);
                    EditorScreen.getSelection().setType(selected);
                }
            }
        });

        // width
        cellsShape.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData && L2DEditor.isFrameStep()) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();

                    if (isActiveSliderShape) {
                        L2DEditor.SingletonHolder.getEditorManager().deleteBody(EditorScreen.getSelection());

                        switch (userData.getShapeType()) {
                            case CIRCLE:
                                userData.setWidth((short) ((Slider) actor).getValue());
                                userData.setHeight((short) ((Slider) actor).getValue());

                                isActiveSliderShape = false;
                                ((Slider)cellsShape.get(1).getActor()).setValue(((Slider) actor).getValue());
                                ((Slider)cellsShape.get(3).getActor()).setValue(((Slider) actor).getValue());
                                isActiveSliderShape  = true;

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createCircle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.CIRCLE.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createCircle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.CIRCLE.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                            case RECTANGLE:
                                userData.setWidth((short) ((Slider) actor).getValue());

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createRectangle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.RECTANGLE.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createRectangle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.RECTANGLE.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                            case TRIANGLE_LEFT:
                                userData.setWidth((short) ((Slider) actor).getValue());
                                userData.setHeight((short) ((Slider) actor).getValue());

                                isActiveSliderShape = false;
                                ((Slider)cellsShape.get(1).getActor()).setValue(((Slider) actor).getValue());
                                ((Slider)cellsShape.get(3).getActor()).setValue(((Slider) actor).getValue());
                                isActiveSliderShape  = true;

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), true, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_LEFT.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), true, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_LEFT.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                            case TRIANGLE_RIGHT:
                                userData.setWidth((short) ((Slider) actor).getValue());
                                userData.setHeight((short) ((Slider) actor).getValue());

                                isActiveSliderShape = false;
                                ((Slider)cellsShape.get(1).getActor()).setValue(((Slider) actor).getValue());
                                ((Slider)cellsShape.get(3).getActor()).setValue(((Slider) actor).getValue());
                                isActiveSliderShape  = true;

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), false, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_RIGHT.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), false, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_RIGHT.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                        }

                        userData.updateSprite();
                    } else
                        userData.setWidth((short) ((Slider) actor).getValue());

                    L2DEditor.isFrameStep(false);
                }
            }
        });

        // height
        cellsShape.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData && L2DEditor.isFrameStep()) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();

                    if (isActiveSliderShape) {
                        L2DEditor.SingletonHolder.getEditorManager().deleteBody(EditorScreen.getSelection());

                        switch (userData.getShapeType()) {
                            case CIRCLE:
                                userData.setWidth((short) ((Slider) actor).getValue());
                                userData.setHeight((short) ((Slider) actor).getValue());

                                isActiveSliderShape = false;
                                ((Slider) cellsShape.get(1).getActor()).setValue(((Slider) actor).getValue());
                                ((Slider) cellsShape.get(3).getActor()).setValue(((Slider) actor).getValue());
                                isActiveSliderShape = true;

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createCircle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.CIRCLE.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createCircle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.CIRCLE.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                            case RECTANGLE:
                                userData.setHeight((short) ((Slider) actor).getValue());

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createRectangle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.RECTANGLE.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createRectangle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep(), userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.RECTANGLE.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                            case TRIANGLE_LEFT:
                                userData.setWidth((short) ((Slider) actor).getValue());
                                userData.setHeight((short) ((Slider) actor).getValue());

                                isActiveSliderShape = false;
                                ((Slider) cellsShape.get(1).getActor()).setValue(((Slider) actor).getValue());
                                ((Slider) cellsShape.get(3).getActor()).setValue(((Slider) actor).getValue());
                                isActiveSliderShape = true;

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), true, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_LEFT.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), true, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_LEFT.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                            case TRIANGLE_RIGHT:
                                userData.setWidth((short) ((Slider) actor).getValue());
                                userData.setHeight((short) ((Slider) actor).getValue());

                                isActiveSliderShape = false;
                                ((Slider) cellsShape.get(1).getActor()).setValue(((Slider) actor).getValue());
                                ((Slider) cellsShape.get(3).getActor()).setValue(((Slider) actor).getValue());
                                isActiveSliderShape = true;

                                if (userData.getBodyType().equals(BodyDef.BodyType.DynamicBody))
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX(), userData.getPosY(), userData.getWidth() * Grid.getStep(), false, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_RIGHT.toString(), false));
                                else
                                    EditorScreen.setSelection(L2DEditor.SingletonHolder.getEditorManager().createTriangle(userData.getPosX() * Grid.getStep(), userData.getPosY() * Grid.getStep(), userData.getWidth() * Grid.getStep(), false, userData.getRotation(), userData.getMaterialType(), userData.getBodyType(), EditorManager.elementType.TRIANGLE_RIGHT.toString(), false));
                                EditorScreen.getSelection().setUserData(userData);
                                break;
                        }

                        userData.updateSprite();
                    } else
                        userData.setHeight((short) ((Slider) actor).getValue());

                    L2DEditor.isFrameStep(false);
                }
            }
        });

        // friction
        cellsMaterial.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setFriction(((Slider) actor).getValue());
                    if (isChangedMaterial) {
                        userData.setMaterialType(PhysicalElementsFactory.materialType.EMPTY);
                        ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.EMPTY);
                        ((SelectBox) EditorScreen.getShellUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.EMPTY);
                    }
                    userData.updateSprite();
                }
            }
        });

        cellsMaterial.get(1).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChangedMaterial = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // restitution
        cellsMaterial.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setRestitution(((Slider) actor).getValue());
                    if (isChangedMaterial) {
                        userData.setMaterialType(PhysicalElementsFactory.materialType.EMPTY);
                        ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.EMPTY);
                        ((SelectBox) EditorScreen.getShellUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.EMPTY);
                    }
                    userData.updateSprite();
                }
            }
        });

        cellsMaterial.get(3).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChangedMaterial = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // density
        cellsMaterial.get(5).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (EditorScreen.getSelection() != null && EditorScreen.getSelection().getUserData() instanceof PhysicalElementsFactory.UserData) {
                    PhysicalElementsFactory.UserData userData = (PhysicalElementsFactory.UserData) EditorScreen.getSelection().getUserData();
                    userData.setDensity(((Slider) actor).getValue());
                    if (isChangedMaterial) {
                        userData.setMaterialType(PhysicalElementsFactory.materialType.EMPTY);
                        ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.EMPTY);
                        ((SelectBox) EditorScreen.getShellUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.EMPTY);
                    }
                    userData.updateSprite();
                }
            }
        });

        cellsMaterial.get(5).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChangedMaterial = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public static void setValues(PhysicalElementsFactory.UserData userData) {
        if (userData != null) {
            ElementUI.isActiveSliderShape = false;
            ((SelectBox) EditorScreen.getElementUI().getCells().get(0).getActor()).setSelected(userData.getShapeType());
            EditorScreen.getElementUI().getCells().get(0).getActor().fire(new ChangeListener.ChangeEvent());
            ElementUI.isActiveSliderShape = true;

            if (userData.getMaterialType().equals(PhysicalElementsFactory.materialType.EMPTY)) {
                ((Slider) EditorScreen.getElementUI().cellsMaterial.get(1).getActor()).setValue(userData.getFriction());
                ((Slider) EditorScreen.getElementUI().cellsMaterial.get(3).getActor()).setValue(userData.getRestitution());
                ((Slider) EditorScreen.getElementUI().cellsMaterial.get(5).getActor()).setValue(userData.getDensity());
            }

            ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(userData.getMaterialType());
            ((SelectBox) EditorScreen.getElementUI().getCells().get(4).getActor()).setSelected(userData.getBodyType());

            EditorScreen.getElementUI().getCells().get(2).getActor().fire(new ChangeListener.ChangeEvent());
            EditorScreen.getElementUI().getCells().get(4).getActor().fire(new ChangeListener.ChangeEvent());
        }
    }

    public static void isChangedMaterial(boolean changedMaterial1) {
        isChangedMaterial = changedMaterial1;
    }

    public static boolean isChangedMaterial() {
        return isChangedMaterial;
    }

    public static void isActiveSliderShape(boolean activeSliderShape1) {
        isActiveSliderShape = activeSliderShape1;
    }

    public static boolean isActiveSliderShape() {
        return isActiveSliderShape;
    }
}