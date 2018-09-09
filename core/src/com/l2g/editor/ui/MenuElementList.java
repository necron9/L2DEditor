package com.l2g.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.utils.CameraUtils;

import javax.swing.*;

public class MenuElementList extends VerticalGroup {
    private static final short
            PAD_TOP = 4,
            PAD_BOTTOM = 4,
            PAD_LEFT = 10,
            PAD_RIGHT = 200;

    private MenuElementListTypes type;

    enum MenuElementListTypes {
        FILE, EDIT, VIEW, TOOLS, HELP
    }

    MenuElementList(MenuElementListTypes type) {
        setMenuList(type);
    }

    private void setMenuList(MenuElementListTypes type) {
        this.type = type;

        clear();

        switch (type) {
            case FILE:
                TextButton textButtonOpen = new TextButton("Open", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonSave = new TextButton("Save", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonSaveAs = new TextButton("Save As", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonExit = new TextButton("Exit", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));

                textButtonOpen.addListener(new ChangeListener() {
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

                textButtonSave.addListener(new ChangeListener() {
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

                textButtonSaveAs.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        JFileChooser jFileChooser = new JFileChooser() {
                            @Override
                            public void approveSelection() {
                                EditorScreen.setCurrentFile(getSelectedFile());
                                L2DEditor.SingletonHolder.getEditorManager().saveScene(EditorScreen.getCurrentFile());
                                super.approveSelection();
                            }
                        };
                        jFileChooser.showSaveDialog(null);
                    }
                });

                textButtonExit.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Gdx.app.exit();
                    }
                });

                addComponent(textButtonOpen);
                addComponent(textButtonSave);
                addComponent(textButtonSaveAs);
                addComponent(textButtonExit);

                break;
            case EDIT:
                TextButton textButtonZoomIn = new TextButton("Zoom In", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonZoomOut = new TextButton("Zoom Out", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonDeleteBodies = new TextButton("Delete Bodies", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonDeleteActors = new TextButton("Delete Actors", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                TextButton textButtonDeleteAll = new TextButton("Delete All", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));

                textButtonZoomIn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                            CameraUtils.zoomIn(L2DEditor.getCamera(), 0.1f);
                            setVisible(!isVisible());
                    }
                });

                textButtonZoomOut.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                            CameraUtils.zoomOut(L2DEditor.getCamera(), 0.5f);
                            setVisible(!isVisible());
                    }
                });

                textButtonDeleteBodies.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setVisible(!isVisible());
                    }
                });

                textButtonDeleteActors.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setVisible(!isVisible());

                        L2DEditor.getStage().setKeyboardFocus(null);
                        PhysicalElementsFactory.setSelection(null);

                        for (Actor tmpActor: L2DEditor.getStage().getActors()) {
                            tmpActor.remove();
                        }
                    }
                });

                textButtonDeleteAll.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setVisible(!isVisible());
                    }
                });

                addComponent(textButtonZoomIn);
                addComponent(textButtonZoomOut);
                addComponent(textButtonDeleteBodies);
                addComponent(textButtonDeleteActors);
                addComponent(textButtonDeleteAll);

                break;
            case VIEW:
                final TextButton textButtonScreen = new TextButton("Enter Full Screen", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));

                textButtonScreen.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                            EditorScreen.setVisible(!EditorScreen.isVisible());
                            setVisible(!isVisible());
                    }
                });

                addComponent(textButtonScreen);

                break;
            case TOOLS:
                final TextButton textButtonSettings = new TextButton("Settings", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));

                textButtonSettings.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setVisible(!isVisible());
                    }
                });

                addComponent(textButtonSettings);
                break;
            case HELP:
                final TextButton textButtonAbout = new TextButton("About", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));

                textButtonAbout.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setVisible(!isVisible());
                    }
                });

                addComponent(textButtonAbout);
                break;
        }

        setPadding(PAD_TOP, PAD_BOTTOM, PAD_LEFT, PAD_RIGHT);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public MenuElementListTypes getType() {
        return type;
    }

    private void addComponent(final Actor component) {
        if (component == null) throw new
                IllegalArgumentException("Component cannot be null.");

        addActor(component);
    }

    public void removeComponent(final Actor component) {
        if (component == null) throw new
                IllegalArgumentException("Component cannot be null.");

        removeActor(component);
    }

    private void setPadding(float padTop, float padBottom, float padLeft, float padRight) {
        for (Actor actor : getChildren()) {
            if (actor instanceof TextButton) {
                ((TextButton) actor).padTop(padTop);
                ((TextButton) actor).padBottom(padBottom);
                ((TextButton) actor).padLeft(padLeft);
                ((TextButton) actor).padRight(padRight - actor.getWidth());
            }
        }

        left();
    }
}