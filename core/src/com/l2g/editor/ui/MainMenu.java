package com.l2g.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.l2g.editor.L2DEditor;

public class MainMenu extends HorizontalGroup {
    public static final short
            MAIN_MENU_HEIGHT = 25;

    private static final short
            PAD_LEFT = 4,
            PAD_RIGHT = 10,
            PAD_TOP = 4,
            PAD_BOTTOM = 3;

    private MainMenuStyle style;
    private MenuStates state;
    private Image background;

    public enum MenuStates {
        DEFAULT
    }

    public static class MainMenuStyle {
        public Drawable background;
    }

    public MainMenu(MainMenuStyle style, MenuStates state) {
        setState(state);
        setStyle(style);
    }

    public MainMenu(MainMenuStyle style) {
        setState(MenuStates.DEFAULT);
        setStyle(style);
    }

    private void setBackground(Drawable background) {
        if (this.background == null) {
            this.background = new Image();
            L2DEditor.getStageHUD().addActor(this.background);
        }

        this.background.setDrawable(background);
        this.background.setPosition(0, L2DEditor.SCREEN_HEIGHT - getHeight() - PAD_BOTTOM - PAD_TOP);
        this.background.setSize(L2DEditor.SCREEN_WIDTH, getHeight() + PAD_BOTTOM + PAD_TOP);
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

    private void setStyle(MainMenuStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);

        invalidateHierarchy();
    }

    MainMenuStyle getStyle() {
        return style;
    }

    private void setState(MenuStates state) {
        this.state = state;

        clear();

        switch (state) {
            case DEFAULT:
                final TextButton textButtonFile = new TextButton("File", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                final TextButton textButtonEdit = new TextButton("Edit", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                final TextButton textButtonView = new TextButton("View", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                final TextButton textButtonTools = new TextButton("Tools", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));
                final TextButton textButtonHelp = new TextButton("Help", L2DEditor.SingletonHolder.getSkin().get("menu_button_style", TextButton.TextButtonStyle.class));

                final MenuElementList commandListFile = new MenuElementList(MenuElementList.MenuElementListTypes.FILE);
                final MenuElementList commandListEdit = new MenuElementList(MenuElementList.MenuElementListTypes.EDIT);
                final MenuElementList commandListView = new MenuElementList(MenuElementList.MenuElementListTypes.VIEW);
                final MenuElementList commandListTools = new MenuElementList(MenuElementList.MenuElementListTypes.TOOLS);
                final MenuElementList commandListHelp = new MenuElementList(MenuElementList.MenuElementListTypes.HELP);

                commandListFile.setVisible(false);
                commandListEdit.setVisible(false);
                commandListView.setVisible(false);
                commandListTools.setVisible(false);
                commandListHelp.setVisible(false);

                L2DEditor.getStageHUD().addActor(commandListFile);
                L2DEditor.getStageHUD().addActor(commandListEdit);
                L2DEditor.getStageHUD().addActor(commandListView);
                L2DEditor.getStageHUD().addActor(commandListTools);
                L2DEditor.getStageHUD().addActor(commandListHelp);

                textButtonFile.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        commandListEdit.setVisible(false);
                        commandListView.setVisible(false);
                        commandListTools.setVisible(false);
                        commandListHelp.setVisible(false);

                        if (textButtonFile.isPressed()) {
                            commandListFile.setPosition(getX(), getY() - commandListFile.getHeight());
                            commandListFile.setZIndex(L2DEditor.getStageHUD().getActors().size);
                            commandListFile.setVisible(!commandListFile.isVisible());
                        }
                    }
                });

                textButtonEdit.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        commandListFile.setVisible(false);
                        commandListView.setVisible(false);
                        commandListTools.setVisible(false);
                        commandListHelp.setVisible(false);

                        if (textButtonEdit.isPressed()) {
                            commandListEdit.setPosition(getX() + textButtonFile.getWidth(), getY() - commandListEdit.getHeight());
                            commandListEdit.setZIndex(L2DEditor.getStageHUD().getActors().size);
                            commandListEdit.setVisible(!commandListEdit.isVisible());
                        }
                    }
                });

                textButtonView.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        commandListFile.setVisible(false);
                        commandListEdit.setVisible(false);
                        commandListTools.setVisible(false);
                        commandListHelp.setVisible(false);

                        if (textButtonView.isPressed()) {
                            commandListView.setPosition(getX() + textButtonFile.getWidth() + textButtonEdit.getWidth(), getY() - commandListView.getHeight());
                            commandListView.setZIndex(L2DEditor.getStageHUD().getActors().size);
                            commandListView.setVisible(!commandListView.isVisible());
                        }
                    }
                });

                textButtonTools.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        commandListFile.setVisible(false);
                        commandListEdit.setVisible(false);
                        commandListView.setVisible(false);
                        commandListHelp.setVisible(false);

                        if (textButtonTools.isPressed()) {
                            commandListTools.setPosition(getX() + textButtonFile.getWidth() + textButtonEdit.getWidth() + textButtonView.getWidth(), getY() - commandListTools.getHeight());
                            commandListTools.setZIndex(L2DEditor.getStageHUD().getActors().size);
                            commandListTools.setVisible(!commandListTools.isVisible());
                        }
                    }
                });

                textButtonHelp.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        commandListFile.setVisible(false);
                        commandListEdit.setVisible(false);
                        commandListView.setVisible(false);
                        commandListTools.setVisible(false);

                        if (textButtonHelp.isPressed()) {
                            commandListHelp.setPosition(getX() + textButtonFile.getWidth() + textButtonEdit.getWidth() + textButtonView.getWidth() + textButtonTools.getWidth(), getY() - commandListHelp.getHeight());
                            commandListHelp.setZIndex(L2DEditor.getStageHUD().getActors().size);
                            commandListHelp.setVisible(!commandListHelp.isVisible());
                        }
                    }
                });

                addComponent(textButtonFile);
                addComponent(textButtonEdit);
                addComponent(textButtonView);
                addComponent(textButtonTools);
                addComponent(textButtonHelp);

                setPadding(PAD_TOP, PAD_BOTTOM, PAD_LEFT, PAD_RIGHT);
        }

        setSize(getPrefWidth(), getPrefHeight());
    }

    MenuStates getState() {
        return state;
    }

    private void setPadding(float padTop, float padBottom, float padLeft, float padRight) {
        for (Actor actor : getChildren()
        ) {
            if (actor instanceof TextButton) {
                ((TextButton) actor).padTop(padTop);
                ((TextButton) actor).padBottom(padBottom);
                ((TextButton) actor).padLeft(padLeft);
                ((TextButton) actor).padRight(padRight);
            }
        }
    }
}