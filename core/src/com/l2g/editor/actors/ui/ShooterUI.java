package com.l2g.editor.actors.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.l2g.editor.Grid;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.Names;
import com.l2g.editor.actors.Shooter;
import com.l2g.editor.elements.ExplosiveElementsFactory;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.screens.LoadScreen;

public class ShooterUI extends Table {
    private static final float
            SLIDER_WIDTH_MIN = 50,
            SLIDER_WIDTH_MAX = 220,
            SLIDER_WIDTH_STEP = 5,
            SLIDER_HEIGHT_MIN = 50,
            SLIDER_HEIGHT_MAX = 220,
            SLIDER_HEIGHT_STEP = 5,
            SLIDER_ANGLE_MIN = 0,
            SLIDER_ANGLE_MAX = 360,
            SLIDER_ANGLE_STEP = 15,
            SLIDER_SIZE_MIN = 5,
            SLIDER_SIZE_MAX = 50,
            SLIDER_SIZE_STEP = 5,
            SLIDER_FREQUENCY_MIN = 0.3f,
            SLIDER_FREQUENCY_MAX = 25f,
            SLIDER_FREQUENCY_STEP = 0.3f,
            SLIDER_LIN_VELOCITY_MIN = 2,
            SLIDER_LIN_VELOCITY_MAX = 30,
            SLIDER_LIN_VELOCITY_STEP = 2,
            SLIDER_ANG_VELOCITY_MIN = 2,
            SLIDER_ANG_VELOCITY_MAX = 30,
            SLIDER_ANG_VELOCITY_STEP = 2,
            SLIDER_FRICTION_MIN = 0f,
            SLIDER_FRICTION_MAX = 1f,
            SLIDER_FRICTION_STEP = 0.05f,
            SLIDER_RESTITUTION_MIN = 0f,
            SLIDER_RESTITUTION_MAX = 1f,
            SLIDER_RESTITUTION_STEP = 0.05f,
            SLIDER_DENSITY_MIN = 50,
            SLIDER_DENSITY_MAX = 2000,
            SLIDER_DENSITY_STEP = 50,
            SLIDER_LIN_DAMPING_MIN = 0,
            SLIDER_LIN_DAMPING_MAX = 10,
            SLIDER_LIN_DAMPING_STEP = 0.5f,
            SLIDER_ANG_DAMPING_MIN = 0,
            SLIDER_ANG_DAMPING_MAX = 10,
            SLIDER_ANG_DAMPING_STEP = 0.5f,
            SELECT_BOX_WIDTH = 128,
            SELECT_BOX_HEIGHT = 24,
            SELECT_BOX_PADDING_BOT = 10,
            SELECT_BOX_PADDING_TOP = 10,
            SLIDER_PADDING = 3,
            LABEL_WIDTH = 90;

    private ShooterUIStyle style;
    private static Array<Cell> cellsShooter, cellsShell, cellsShape, cellsMaterial;

    private boolean isChanged;

    public static class ShooterUIStyle {
        public Drawable background;
    }

    public ShooterUI(ShooterUIStyle style) {
        Table tableShooter = new Table(),
                tableShell = new Table(),
                tableShape = new Table(),
                tableMaterial = new Table();

        Skin skin = L2DEditor.SingletonHolder.getSkin();

        tableShooter.setSkin(skin);
        tableShell.setSkin(skin);
        tableShape.setSkin(skin);
        tableMaterial.setSkin(skin);

        add(new SelectBox<String>(skin)).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).padBottom(SELECT_BOX_PADDING_BOT).left().row();
        tableShooter.add("Width: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShooter.add(new Slider(SLIDER_WIDTH_MIN, SLIDER_WIDTH_MAX, SLIDER_WIDTH_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShooter.add("Height: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShooter.add(new Slider(SLIDER_HEIGHT_MIN, SLIDER_HEIGHT_MAX, SLIDER_HEIGHT_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShooter.add("Fire angle: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShooter.add(new Slider(SLIDER_ANGLE_MIN, SLIDER_ANGLE_MAX, SLIDER_ANGLE_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShooter.add("Frequency: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShooter.add(new Slider(SLIDER_FREQUENCY_MIN, SLIDER_FREQUENCY_MAX, SLIDER_FREQUENCY_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShooter.add("Lin. velocity: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShooter.add(new Slider(SLIDER_LIN_VELOCITY_MIN, SLIDER_LIN_VELOCITY_MAX, SLIDER_LIN_VELOCITY_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShooter.add("Ang. velocity: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShooter.add(new Slider(SLIDER_ANG_VELOCITY_MIN, SLIDER_ANG_VELOCITY_MAX, SLIDER_ANG_VELOCITY_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class)));
        add(tableShooter).row();

        add(new SelectBox<String>(skin)).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).left().padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP).row();
        tableShell.add("Lin. damping: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShell.add(new Slider(SLIDER_LIN_DAMPING_MIN, SLIDER_LIN_DAMPING_MAX, SLIDER_LIN_DAMPING_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableShell.add("Ang. damping: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShell.add(new Slider(SLIDER_ANG_DAMPING_MIN, SLIDER_ANG_DAMPING_MAX, SLIDER_ANG_DAMPING_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class)));
        add(tableShell).row();

        add(new SelectBox<String>(skin)).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).left().padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP).row();
        tableShape.add("Size: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableShape.add(new Slider(SLIDER_SIZE_MIN, SLIDER_SIZE_MAX, SLIDER_SIZE_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class)));
        add(tableShape).row();

        add(new SelectBox<String>(skin)).width(SELECT_BOX_WIDTH).height(SELECT_BOX_HEIGHT).left().padBottom(SELECT_BOX_PADDING_BOT).padTop(SELECT_BOX_PADDING_TOP).row();
        tableMaterial.add("Friction: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableMaterial.add(new Slider(SLIDER_FRICTION_MIN, SLIDER_FRICTION_MAX, SLIDER_FRICTION_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableMaterial.add("Restitution: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableMaterial.add(new Slider(SLIDER_RESTITUTION_MIN, SLIDER_RESTITUTION_MAX, SLIDER_RESTITUTION_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class))).row();
        tableMaterial.add("Density: ").width(LABEL_WIDTH).padBottom(SLIDER_PADDING).left();
        tableMaterial.add(new Slider(SLIDER_DENSITY_MIN, SLIDER_DENSITY_MAX, SLIDER_DENSITY_STEP, false, skin.get("slider_horizontal", Slider.SliderStyle.class)));
        add(tableMaterial);

        cellsShooter = tableShooter.getCells();
        cellsShell = tableShell.getCells();
        cellsShape = tableShape.getCells();
        cellsMaterial = tableMaterial.getCells();

        setStyle(style);
        initialize();
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyle(ShooterUIStyle style) {
        if (style == null) throw new
                IllegalArgumentException("Style cannot be null.");

        this.style = style;

        setBackground(style.background);

        invalidateHierarchy();
    }

    public void initialize() {
        ((SelectBox) getCells().get(0).getActor()).setItems(Shooter.shooterType.values());
        ((SelectBox) getCells().get(2).getActor()).setItems(ExplosiveElementsFactory.shellType.values());
        ((SelectBox) getCells().get(4).getActor()).setItems(PhysicalElementsFactory.shapeType.values());
        ((SelectBox) getCells().get(6).getActor()).setItems(PhysicalElementsFactory.materialType.values());

        // select shooter
        getCells().get(0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                Shooter.shooterType selected = (Shooter.shooterType) selectBox.getSelected();
                if (selected.equals(Shooter.shooterType.SIMPLE)) {
                    ((Slider)cellsShooter.get(1).getActor()).setValue(Names.shooterParameters[0][0]);
                    ((Slider)cellsShooter.get(3).getActor()).setValue(Names.shooterParameters[0][1]);
                    ((Slider)cellsShooter.get(5).getActor()).setValue(Names.shooterParameters[0][2]);
                    ((Slider)cellsShooter.get(7).getActor()).setValue(Names.shooterParameters[0][3]);
                    ((Slider)cellsShooter.get(9).getActor()).setValue(Names.shooterParameters[0][4]);
                    ((Slider)cellsShooter.get(11).getActor()).setValue(Names.shooterParameters[0][5]);
                    cellsShooter.get(1).getActor().fire(new ChangeEvent());
                    cellsShooter.get(3).getActor().fire(new ChangeEvent());
                    cellsShooter.get(5).getActor().fire(new ChangeEvent());
                    cellsShooter.get(7).getActor().fire(new ChangeEvent());
                    cellsShooter.get(9).getActor().fire(new ChangeEvent());
                    cellsShooter.get(11).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShooterType(Shooter.shooterType.SIMPLE);

                        userData.setSprite(LoadScreen.getSpriteAtlas().createSprite("simple_cannon"), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                    }
                } else if (selected.equals(Shooter.shooterType.DOUBLE)) {
                    ((Slider)cellsShooter.get(1).getActor()).setValue(Names.shooterParameters[1][0]);
                    ((Slider)cellsShooter.get(3).getActor()).setValue(Names.shooterParameters[1][1]);
                    ((Slider)cellsShooter.get(5).getActor()).setValue(Names.shooterParameters[1][2]);
                    ((Slider)cellsShooter.get(7).getActor()).setValue(Names.shooterParameters[1][3]);
                    ((Slider)cellsShooter.get(9).getActor()).setValue(Names.shooterParameters[1][4]);
                    ((Slider)cellsShooter.get(11).getActor()).setValue(Names.shooterParameters[1][5]);
                    cellsShooter.get(1).getActor().fire(new ChangeEvent());
                    cellsShooter.get(3).getActor().fire(new ChangeEvent());
                    cellsShooter.get(5).getActor().fire(new ChangeEvent());
                    cellsShooter.get(7).getActor().fire(new ChangeEvent());
                    cellsShooter.get(9).getActor().fire(new ChangeEvent());
                    cellsShooter.get(11).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShooterType(Shooter.shooterType.DOUBLE);
                        userData.setSprite(LoadScreen.getSpriteAtlas().createSprite("double_cannon"), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                    }
                }

                isChanged = false;
            }
        });

        // select shell
        getCells().get(2).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                ExplosiveElementsFactory.shellType selected = (ExplosiveElementsFactory.shellType) selectBox.getSelected();

                if (selected.equals(ExplosiveElementsFactory.shellType.CANNONBALL)) {
                    ((Slider)cellsShell.get(1).getActor()).setValue(Names.shellParameters[0][0]);
                    ((Slider)cellsShell.get(3).getActor()).setValue(Names.shellParameters[0][1]);
                    cellsShell.get(1).getActor().fire(new ChangeEvent());
                    cellsShell.get(3).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShellType(ExplosiveElementsFactory.shellType.CANNONBALL);
                    }
                } else if (selected.equals(ExplosiveElementsFactory.shellType.LANDMINE)) {
                    ((Slider)cellsShell.get(1).getActor()).setValue(Names.shellParameters[1][0]);
                    ((Slider)cellsShell.get(3).getActor()).setValue(Names.shellParameters[1][1]);
                    cellsShell.get(1).getActor().fire(new ChangeEvent());
                    cellsShell.get(3).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShellType(ExplosiveElementsFactory.shellType.LANDMINE);
                    }
                }
            }
        });

        // select shape
        getCells().get(4).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                PhysicalElementsFactory.shapeType selected = (PhysicalElementsFactory.shapeType) selectBox.getSelected();

                if (selected.equals(PhysicalElementsFactory.shapeType.CIRCLE)) {
                    ((Slider)cellsShape.get(1).getActor()).setValue(Names.shapeParameters[0][0]);
                    cellsShell.get(1).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShellType(null);
                    }
                } else if (selected.equals(PhysicalElementsFactory.shapeType.RECTANGLE)) {
                    if (Names.shapeParameters[1][0] >= Names.shapeParameters[1][1])
                        ((Slider)cellsShape.get(1).getActor()).setValue(Names.shapeParameters[0][1]);
                    else ((Slider)cellsShape.get(1).getActor()).setValue(Names.shapeParameters[1][1]);
                    cellsShell.get(1).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShellType(null);
                    }
                }
            }
        });

        // select material
        getCells().get(6).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox selectBox = (SelectBox) actor;
                PhysicalElementsFactory.materialType selected = (PhysicalElementsFactory.materialType) selectBox.getSelected();

                if (selected.equals(PhysicalElementsFactory.materialType.STONE)) {
                    ((Slider)cellsMaterial.get(1).getActor()).setValue(Names.materialParameters[0][0]);
                    ((Slider)cellsMaterial.get(3).getActor()).setValue(Names.materialParameters[0][1]);
                    ((Slider)cellsMaterial.get(5).getActor()).setValue(Names.materialParameters[0][2]);
                    cellsMaterial.get(1).getActor().fire(new ChangeEvent());
                    cellsMaterial.get(3).getActor().fire(new ChangeEvent());
                    cellsMaterial.get(5).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShellType(null);
                    }
                } else if (selected.equals(PhysicalElementsFactory.materialType.WOOD)) {
                    ((Slider)cellsMaterial.get(1).getActor()).setValue(Names.materialParameters[1][0]);
                    ((Slider)cellsMaterial.get(3).getActor()).setValue(Names.materialParameters[1][1]);
                    ((Slider)cellsMaterial.get(5).getActor()).setValue(Names.materialParameters[1][2]);
                    cellsMaterial.get(1).getActor().fire(new ChangeEvent());
                    cellsMaterial.get(3).getActor().fire(new ChangeEvent());
                    cellsMaterial.get(5).getActor().fire(new ChangeEvent());

                    if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                        Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                        userData.setShellType(null);
                    }
                }
            }
        });

        // width
        cellsShooter.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setWidth((short) ((Slider) actor).getValue());
                    if (isChanged) {
                        userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("empty_cannon")), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                        userData.setShooterType(null);
                    }
                }
            }
        });

        cellsShooter.get(1).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // height
        cellsShooter.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setHeight((short) ((Slider) actor).getValue());
                    if (isChanged) {
                        userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("empty_cannon")), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                        userData.setShooterType(null);
                    }
                }
            }
        });

        cellsShooter.get(3).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // angle
        cellsShooter.get(5).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    //  userData.angle = ((Slider) actor).getValue();
                    if (isChanged) {
                        userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("empty_cannon")), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                        userData.setShooterType(null);
                    }
                }
            }
        });

        cellsShooter.get(5).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // frequency
        cellsShooter.get(7).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    // userData.frequency = (short) ((Slider) actor).getValue();
                    if (isChanged) {
                        userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("empty_cannon")), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                        userData.setShooterType(null);
                    }
                }
            }
        });

        cellsShooter.get(7).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // linear velocity
        cellsShooter.get(9).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setLinearVelocity(((Slider) actor).getValue());
                    if (isChanged) {
                        userData.setShooterType(null);
                        userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("empty_cannon")), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                    }
                }
            }
        });

        cellsShooter.get(9).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // angular velocity
        cellsShooter.get(11).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setAngularVelocity(((Slider) actor).getValue());
                    if (isChanged) {
                        userData.setShooterType(null);
                        userData.setSprite(new Sprite(LoadScreen.getSpriteAtlas().createSprite("empty_cannon")), userData.getWidth() * Grid.getStep(), userData.getHeight() * Grid.getStep());
                    }
                }
            }
        });

        cellsShooter.get(11).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // linear damping
        cellsShell.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setLinearDamping(((Slider) actor).getValue());

                    if (isChanged) userData.setShellType(null);
                }
            }
        });

        cellsShell.get(1).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // angular damping
        cellsShell.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setAngularDamping(((Slider) actor).getValue());

                    if (isChanged) userData.setShellType(null);
                }
            }
        });

        cellsShell.get(3).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // size
        cellsShape.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setSize(((Slider) actor).getValue());

                    if (isChanged) userData.setShellType(null);
                }
            }
        });

        cellsShape.get(1).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // friction
        cellsMaterial.get(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setFriction(((Slider) actor).getValue());

                    if (isChanged) userData.setShellType(null);
                }
            }
        });

        cellsMaterial.get(1).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // restitution
        cellsMaterial.get(3).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setRestitution(((Slider) actor).getValue());

                    if (isChanged) userData.setShellType(null);
                }
            }
        });

        cellsMaterial.get(3).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // density
        cellsMaterial.get(5).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (L2DEditor.getStage().getKeyboardFocus() != null && L2DEditor.getStage().getKeyboardFocus().getUserObject() instanceof Shooter.UserData) {
                    Shooter.UserData userData = (Shooter.UserData) L2DEditor.getStage().getKeyboardFocus().getUserObject();
                    userData.setDensity(((Slider) actor).getValue());

                    if (isChanged) userData.setShellType(null);
                }
            }
        });

        cellsMaterial.get(5).getActor().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isChanged = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public void setValues(Shooter.UserData userData) {
        if (userData.getShooterType() == null) {
            ((Slider)ShooterUI.cellsShooter.get(1).getActor()).setValue(userData.getWidth());
            ((Slider)ShooterUI.cellsShooter.get(3).getActor()).setValue(userData.getHeight());
            // ((Slider)ShooterUI.cellsShooter.get(5).getActor()).setValue(userData.angle);
            // ((Slider)ShooterUI.cellsShooter.get(7).getActor()).setValue(userData.frequency);
            ((Slider)ShooterUI.cellsShooter.get(9).getActor()).setValue(userData.getLinearDamping());
            ((Slider)ShooterUI.cellsShooter.get(11).getActor()).setValue(userData.getAngularDamping());
        } else {
            switch (userData.getShooterType()) {
                case SIMPLE:
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(0).getActor()).setSelected(Shooter.shooterType.SIMPLE);
                    break;
                case DOUBLE:
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(0).getActor()).setSelected(Shooter.shooterType.DOUBLE);
                    break;
            }
            EditorScreen.getShooterUI().getCells().get(0).getActor().fire(new ChangeListener.ChangeEvent());
        }
        if (userData.getShellType() == null) {
            ((Slider)ShooterUI.cellsShell.get(1).getActor()).setValue(userData.getLinearDamping());
            ((Slider)ShooterUI.cellsShell.get(3).getActor()).setValue(userData.getAngularDamping());
            ((Slider)ShooterUI.cellsShape.get(1).getActor()).setValue(userData.getSize());
            ((Slider)ShooterUI.cellsMaterial.get(1).getActor()).setValue(userData.getFriction());
            ((Slider)ShooterUI.cellsMaterial.get(3).getActor()).setValue(userData.getRestitution());
            ((Slider)ShooterUI.cellsMaterial.get(5).getActor()).setValue(userData.getDensity());
        } else {
            switch (userData.getShellType()) {
                case CANNONBALL:
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(2).getActor()).setSelected(ExplosiveElementsFactory.shellType.CANNONBALL);
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(4).getActor()).setSelected(PhysicalElementsFactory.shapeType.CIRCLE);
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(6).getActor()).setSelected(PhysicalElementsFactory.materialType.STONE);
                    break;
                case LANDMINE:
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(2).getActor()).setSelected(ExplosiveElementsFactory.shellType.LANDMINE);
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(4).getActor()).setSelected(PhysicalElementsFactory.shapeType.CIRCLE);
                    ((SelectBox) EditorScreen.getShooterUI().getCells().get(6).getActor()).setSelected(PhysicalElementsFactory.materialType.STONE);
                    break;
            }
            EditorScreen.getShooterUI().getCells().get(2).getActor().fire(new ChangeListener.ChangeEvent());
            EditorScreen.getShooterUI().getCells().get(4).getActor().fire(new ChangeListener.ChangeEvent());
            EditorScreen.getShooterUI().getCells().get(6).getActor().fire(new ChangeListener.ChangeEvent());
        }
    }
}