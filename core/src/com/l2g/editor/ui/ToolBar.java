package com.l2g.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.l2g.editor.EditorManager;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.utils.CameraUtils;

import javax.swing.*;

public class ToolBar extends HorizontalGroup {
    public static final short
            TOOL_BAR_HEIGHT = 30;

    private static final short
            PAD_LEFT = 10,
            PAD_RIGHT = 15,
            PAD_TOP = 2,
            PAD_BOTTOM = 2,
            SPACE = 17;

    private ToolBarStyle style;
    private ToolBarStates state;
    private Image background;

    private ImageButton imageButtonHide;

    public enum ToolBarStates {
        DEFAULT
    }

    public static class ToolBarStyle {
        public Drawable background;
    }

    public ToolBar(ToolBarStyle style, ToolBarStates state) {
        setState(state);
        setStyle(style);
    }

    public ToolBar(ToolBarStyle style) {
        setState(ToolBarStates.DEFAULT);
        setStyle(style);
    }

    private void setState(ToolBarStates state) {
        this.state = state;

        clear();

        switch (state) {
            case DEFAULT:
                final String
                        selectBoxElements[] = {
                        "  " + EditorManager.elementType.SHOOTER.toString(),
                        "  " + EditorManager.elementType.BOMB.toString(),
                        "  " + EditorManager.elementType.LANDMINE.toString(),
                        "  " + EditorManager.elementType.RECTANGLE.toString(),
                        "  " + EditorManager.elementType.RECTANGLE_ARRAY.toString(),
                        "  " + EditorManager.elementType.CIRCLE.toString(),
                        "  " + EditorManager.elementType.CIRCLE_ARRAY.toString(),
                        "  " + EditorManager.elementType.TRIANGLE_LEFT.toString(),
                        "  " + EditorManager.elementType.TRIANGLE_LEFT_ARRAY.toString(),
                        "  " + EditorManager.elementType.TRIANGLE_RIGHT.toString(),
                        "  " + EditorManager.elementType.TRIANGLE_RIGHT_ARRAY.toString(),
                        "  " + EditorManager.elementType.DYNAMIC_BOXES.toString()};

                final ImageButton imageButtonOpen = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_open_style", ImageButton.ImageButtonStyle.class));
                final ImageButton imageButtonSave = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_save_style", ImageButton.ImageButtonStyle.class));
                final Image imageSeparator0 = new Image(L2DEditor.SingletonHolder.getSkin().getDrawable("separator_icon"));
                final ImageButton imageButtonZoom = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_zoom_style", ImageButton.ImageButtonStyle.class));
                final Image imageSeparator1 = new Image(L2DEditor.SingletonHolder.getSkin().getDrawable("separator_icon"));
                final SelectBox<String> selectBoxElement = new SelectBox<String>(L2DEditor.SingletonHolder.getSkin());
                final ImageButton imageButtonStart = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_play_style", ImageButton.ImageButtonStyle.class));
                final ImageButton imageButtonStop = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_stop_style", ImageButton.ImageButtonStyle.class));
                final Image imageSeparator2 = new Image(L2DEditor.SingletonHolder.getSkin().getDrawable("separator_icon"));
                final ImageButton imageButtonClear = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_clear_style", ImageButton.ImageButtonStyle.class));
                final CheckBox checkBoxDelete = new CheckBox("", L2DEditor.SingletonHolder.getSkin());
                final Image imageSeparator3 = new Image(L2DEditor.SingletonHolder.getSkin().getDrawable("separator_icon"));
                final ImageButton imageButtonHide = new ImageButton(L2DEditor.SingletonHolder.getSkin().get("button_style_hide", ImageButton.ImageButtonStyle.class));

                this.imageButtonHide = imageButtonHide;

                imageButtonSave.setDisabled(true);
                imageButtonStop.setDisabled(true);

                selectBoxElement.setItems(selectBoxElements);

                imageButtonOpen.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        JFileChooser jFileChooser = new JFileChooser() {
                            @Override
                            public void approveSelection() {
                                EditorScreen.setCurrentFile(null);
                                L2DEditor.SingletonHolder.getEditorManager().loadScene(getSelectedFile());
                                super.approveSelection();
                            }
                        };
                        jFileChooser.showDialog(null, "Open file");
                    }
                });

                imageButtonSave.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (EditorScreen.getCurrentFile() == null) {
                            JFileChooser jFileChooser = new JFileChooser() {
                                @Override
                                public void approveSelection() {
                                    EditorScreen.setCurrentFile(getSelectedFile());
                                    L2DEditor.SingletonHolder.getEditorManager().saveScene(EditorScreen.getCurrentFile());
                                    super.approveSelection();
                                }
                            };
                            jFileChooser.showSaveDialog(null);
                        } else
                            L2DEditor.SingletonHolder.getEditorManager().saveScene(EditorScreen.getCurrentFile());
                    }
                });

                imageButtonZoom.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        CameraUtils.zoomIn(L2DEditor.getCamera(), 0.1f);
                        setVisible(!isVisible());
                    }
                });

                selectBoxElement.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        String selected = ((SelectBox) actor).getSelected().toString();
                        PhysicalElementsFactory.setSelection(null);

                        if (selected.equals(selectBoxElements[0])) {
                            EditorManager.elementType.type = EditorManager.elementType.SHOOTER;
                            EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getShooterUI());
                        } else if (selected.equals(selectBoxElements[1])) {
                            EditorManager.elementType.type = EditorManager.elementType.BOMB;
                        } else if (selected.equals(selectBoxElements[2])) {
                            EditorManager.elementType.type = EditorManager.elementType.LANDMINE;
                        } else if (selected.equals(selectBoxElements[3])) {
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(0).getActor()).setSelected(PhysicalElementsFactory.shapeType.RECTANGLE);
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.STONE);
                            EditorScreen.getElementUI().getCells().get(0).getActor().fire(new ChangeEvent());
                            EditorScreen.getElementUI().getCells().get(2).getActor().fire(new ChangeEvent());
                            EditorManager.elementType.type = EditorManager.elementType.RECTANGLE;
                            EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getElementUI());
                        } else if (selected.equals(selectBoxElements[4])) {
                            EditorManager.elementType.type = EditorManager.elementType.RECTANGLE_ARRAY;
                        } else if (selected.equals(selectBoxElements[5])) {
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(0).getActor()).setSelected(PhysicalElementsFactory.shapeType.CIRCLE);
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.STONE);
                            EditorScreen.getElementUI().getCells().get(0).getActor().fire(new ChangeEvent());
                            EditorScreen.getElementUI().getCells().get(2).getActor().fire(new ChangeEvent());
                            EditorManager.elementType.type = EditorManager.elementType.CIRCLE;
                            EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getElementUI());
                        } else if (selected.equals(selectBoxElements[6])) {
                            EditorManager.elementType.type = EditorManager.elementType.CIRCLE_ARRAY;
                        } else if (selected.equals(selectBoxElements[7])) {
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(0).getActor()).setSelected(PhysicalElementsFactory.shapeType.TRIANGLE_LEFT);
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.STONE);
                            EditorScreen.getElementUI().getCells().get(0).getActor().fire(new ChangeEvent());
                            EditorScreen.getElementUI().getCells().get(2).getActor().fire(new ChangeEvent());
                            EditorManager.elementType.type = EditorManager.elementType.TRIANGLE_LEFT;
                            EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getElementUI());
                        } else if (selected.equals(selectBoxElements[8])) {
                            EditorManager.elementType.type = EditorManager.elementType.TRIANGLE_LEFT_ARRAY;
                        } else if (selected.equals(selectBoxElements[9])) {
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(0).getActor()).setSelected(PhysicalElementsFactory.shapeType.TRIANGLE_RIGHT);
                            ((SelectBox) EditorScreen.getElementUI().getCells().get(2).getActor()).setSelected(PhysicalElementsFactory.materialType.STONE);
                            EditorScreen.getElementUI().getCells().get(0).getActor().fire(new ChangeEvent());
                            EditorScreen.getElementUI().getCells().get(2).getActor().fire(new ChangeEvent());
                            EditorManager.elementType.type = EditorManager.elementType.TRIANGLE_RIGHT;
                            EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getElementUI());
                        } else if (selected.equals(selectBoxElements[10])) {
                            EditorManager.elementType.type = EditorManager.elementType.TRIANGLE_RIGHT_ARRAY;
                        } else if (selected.equals(selectBoxElements[11])) {
                            EditorManager.elementType.type = EditorManager.elementType.DYNAMIC_BOXES;
                        }

                        L2DEditor.isDelete(false);
                    }
                });

                imageButtonStart.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        L2DEditor.SingletonHolder.getEditorManager().setStart(true);
                    }
                });

                imageButtonStop.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        L2DEditor.SingletonHolder.getEditorManager().setStart(false);
                    }
                });

                imageButtonClear.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        L2DEditor.SingletonHolder.getEditorManager().clear();
                    }
                });

                checkBoxDelete.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        L2DEditor.isDelete(!L2DEditor.isDelete());
                        EditorScreen.getRightPanel().clearCurrentUI();
                    }
                });

                imageButtonHide.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        EditorScreen.setVisible(!EditorScreen.isVisible());
                        setVisible(!isVisible());
                    }
                });

                addComponent(imageButtonOpen);
                addComponent(imageButtonSave);
                addComponent(imageSeparator0);
                addComponent(imageButtonZoom);
                addComponent(imageSeparator1);
                addComponent(selectBoxElement);
                addComponent(imageButtonStart);
                addComponent(imageButtonStop);
                addComponent(imageSeparator2);
                addComponent(imageButtonClear);
                addComponent(checkBoxDelete);
                addComponent(imageSeparator3);
                addComponent(imageButtonHide);

                space(SPACE);
                pad(PAD_TOP, PAD_LEFT, PAD_BOTTOM, PAD_RIGHT);

                break;
        }

        setSize(getPrefWidth(), getPrefHeight());
    }

    private void addComponent(Actor component) {
        if (component == null) throw new
                IllegalArgumentException("Component cannot be null.");

        addActor(component);
    }

    private void removeComponent(Actor component) {
        if (component == null) throw new
                IllegalArgumentException("Component cannot be null.");

        removeActor(component);
    }

    private void setBackground(Drawable background) {
        if (this.background == null) {
            this.background = new Image();
            L2DEditor.getStageHUD().addActor(this.background);
        }

        this.background.setDrawable(background);
        this.background.setPosition(0, L2DEditor.SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT - getHeight() - PAD_TOP - PAD_BOTTOM);
        this.background.setSize(L2DEditor.SCREEN_WIDTH, getHeight() + PAD_TOP + PAD_BOTTOM);
    }

    private void setStyle(ToolBarStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);

        invalidateHierarchy();
    }

    ToolBarStyle getStyle() {
        return style;
    }

    ToolBarStates getState() {
        return state;
    }

    public void setVisibleButtonChecked(boolean checked) {
        if (imageButtonHide != null)
            imageButtonHide.setChecked(!checked);
    }
}