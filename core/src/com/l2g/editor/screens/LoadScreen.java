package com.l2g.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.actors.ui.ShooterUI;
import com.l2g.editor.elements.ui.ElementUI;
import com.l2g.editor.ui.*;

public class LoadScreen implements Screen {
    private static final float
            LOAD_WIDTH = L2DEditor.SCREEN_WIDTH * 0.7f,             // Download indicator height
            LOAD_HEIGHT = L2DEditor.SCREEN_HEIGHT * 0.1f;           // Download indicator width

    private float progress;

    private static TextureAtlas editorAtlas, spriteAtlas;

    private final ShapeRenderer shapeRenderer;

    public LoadScreen() {
        shapeRenderer = L2DEditor.SingletonHolder.getShapeRenderer();

        AssetManager assetManager = L2DEditor.SingletonHolder.getAssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());

        assetManager.load("atlases/editor_ui.atlas", TextureAtlas.class);
        assetManager.load("atlases/sprites.atlas", TextureAtlas.class);
        assetManager.load("maps/test.tmx", TiledMap.class);
    }

    private void update() {
        AssetManager assetManager = L2DEditor.SingletonHolder.getAssetManager();

        progress = assetManager.getProgress();

        if (assetManager.update()) {
            if (assetManager.isLoaded("atlases/editor_ui.atlas", TextureAtlas.class))
                editorAtlas = assetManager.get("atlases/editor_ui.atlas", TextureAtlas.class);
            if (assetManager.isLoaded("atlases/sprites.atlas", TextureAtlas.class))
                spriteAtlas = assetManager.get("atlases/sprites.atlas", TextureAtlas.class);

            Skin skin = L2DEditor.SingletonHolder.getSkin();

            skin.addRegions(editorAtlas);
            skin.addRegions(spriteAtlas);

            FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Tahoma.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            ImageButton.ImageButtonStyle
                buttonStyleOpen         = new ImageButton.ImageButtonStyle(),
                buttonStyleSave         = new ImageButton.ImageButtonStyle(),
                buttonStyleZoom         = new ImageButton.ImageButtonStyle(),
                buttonStylePlay         = new ImageButton.ImageButtonStyle(),
                buttonStyleStop         = new ImageButton.ImageButtonStyle(),
                buttonStyleClear        = new ImageButton.ImageButtonStyle(),
                buttonStyleHide         = new ImageButton.ImageButtonStyle(),
                buttonStyleSettings     = new ImageButton.ImageButtonStyle();

            TextButton.TextButtonStyle
                    textButtonStyleVertical     = new TextButton.TextButtonStyle(),
                    menuButtonStyle             = new TextButton.TextButtonStyle(),
                    textButtonStyleHorizontal   = new TextButton.TextButtonStyle();

            Label.LabelStyle labelStyle = new Label.LabelStyle();

            Slider.SliderStyle sliderStyleHorizontal   = new Slider.SliderStyle();

            MainMenu.MainMenuStyle mainMenuStyle = new MainMenu.MainMenuStyle();
            ToolBar.ToolBarStyle toolBarStyle = new ToolBar.ToolBarStyle();
            LeftPanel.LeftPanelStyle leftPanelStyle = new LeftPanel.LeftPanelStyle();
            RightPanel.RightPanelStyle rightPanelStyle = new RightPanel.RightPanelStyle();
            BottomPanel.BottomPanelStyle bottomPanelStyle = new BottomPanel.BottomPanelStyle();

            Tree.TreeStyle treeStyle = new Tree.TreeStyle();
            ShooterUI.ShooterUIStyle shooterUIStyle = new ShooterUI.ShooterUIStyle();
            ElementUI.ElementUIStyle elementUIStyle = new ElementUI.ElementUIStyle();
            TextButton.TextButtonStyle textFieldStyle = new TextButton.TextButtonStyle();
            CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
            List.ListStyle listStyle = new List.ListStyle();
            ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
            SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();

            parameter.size = 14;
            parameter.spaceX = 1;
            parameter.color = Color.WHITE;
            skin.add("main_font", freeTypeFontGenerator.generateFont(parameter));
            parameter.size = 13;
            parameter.spaceX = 2;
            parameter.color = Color.LIGHT_GRAY;
            skin.add("select_font", freeTypeFontGenerator.generateFont(parameter));
            parameter.size = 15;
            parameter.spaceX = 2;
            parameter.color = Color.WHITE;
            skin.add("console_font", freeTypeFontGenerator.generateFont(parameter));

            labelStyle.font = skin.getFont("main_font");

            sliderStyleHorizontal.knob = skin.getDrawable("slider_knob_horizontal");
            sliderStyleHorizontal.background = skin.getDrawable("slider_horizontal");

            textButtonStyleHorizontal.font = skin.getFont("main_font");
            textButtonStyleHorizontal.up = skin.getDrawable("button_vertical_up");
            textButtonStyleHorizontal.down = skin.getDrawable("horizontal_button_down");
            textButtonStyleHorizontal.over = skin.getDrawable("horizontal_button_over");

            mainMenuStyle.background = skin.getDrawable("horizontal_background_line");
            menuButtonStyle.font = skin.getFont("main_font");
            menuButtonStyle.up = skin.getDrawable("menu_button_up");
            menuButtonStyle.down = skin.getDrawable("menu_button_down");
            menuButtonStyle.over = skin.getDrawable("menu_button_over");
            toolBarStyle.background = skin.getDrawable("horizontal_background_line");
            bottomPanelStyle.background = skin.getDrawable("horizontal_background");
            leftPanelStyle.background = skin.getDrawable("vertical_background");
            rightPanelStyle.background = skin.getDrawable("vertical_background");
            rightPanelStyle.backgroundTable = skin.getDrawable("horizontal_background");
            rightPanelStyle.titleFont = skin.getFont("main_font");

            textButtonStyleVertical.font = skin.getFont("main_font");
            textButtonStyleVertical.up = skin.getDrawable("button_vertical_up");
            textButtonStyleVertical.down = skin.getDrawable("button_vertical_down");
            textButtonStyleVertical.over = skin.getDrawable("button_vertical_over");

            shooterUIStyle.background = skin.getDrawable("horizontal_background");
            elementUIStyle.background = skin.getDrawable("horizontal_background");

            treeStyle.selection = skin.getDrawable("horizontal_button_up");
            treeStyle.over = skin.getDrawable("vertical_background");
            treeStyle.minus = skin.getDrawable("tree_minus");
            treeStyle.plus = skin.getDrawable("tree_plus");

            buttonStyleOpen.up = skin.getDrawable("open_up");
            buttonStyleOpen.down = skin.getDrawable("open_down");
            buttonStyleOpen.over = skin.getDrawable("open_over");
            buttonStyleSave.up = skin.getDrawable("save_up");
            buttonStyleSave.down = skin.getDrawable("save_down");
            buttonStyleSave.over = skin.getDrawable("save_over");
            buttonStyleSave.disabled = skin.getDrawable("save_disabled");
            buttonStyleZoom.up = skin.getDrawable("zoom_up");
            buttonStyleZoom.down = skin.getDrawable("zoom_down");
            buttonStyleZoom.over = skin.getDrawable("zoom_over");
            buttonStyleZoom.checked = skin.getDrawable("zoom_checked");
            buttonStylePlay.up = skin.getDrawable("play_up");
            buttonStylePlay.down = skin.getDrawable("play_down");
            buttonStylePlay.over = skin.getDrawable("play_over");
            buttonStyleStop.up = skin.getDrawable("stop_up");
            buttonStyleStop.down = skin.getDrawable("stop_down");
            buttonStyleStop.over = skin.getDrawable("stop_over");
            buttonStyleClear.up = skin.getDrawable("clear_up");
            buttonStyleClear.down = skin.getDrawable("clear_down");
            buttonStyleClear.over = skin.getDrawable("clear_over");
            buttonStyleClear.disabled = skin.getDrawable("clear_disabled");
            buttonStyleHide.up = skin.getDrawable("hide_up");
            buttonStyleHide.down = skin.getDrawable("hide_down");
            buttonStyleHide.over = skin.getDrawable("hide_over");
            buttonStyleHide.checked = skin.getDrawable("hide_checked");
            buttonStyleSettings.up = skin.getDrawable("settings_up");
            buttonStyleSettings.down = skin.getDrawable("settings_down");
            buttonStyleSettings.over = skin.getDrawable("settings_over");
            buttonStyleSettings.checked = skin.getDrawable("settings_checked");

            checkBoxStyle.checkboxOn = skin.getDrawable("checkbox_checked");
            checkBoxStyle.checkboxOff = skin.getDrawable("checkbox_unchecked");
            checkBoxStyle.disabled = skin.getDrawable("checkbox_disabled");
            checkBoxStyle.font = skin.getFont("main_font");

            textFieldStyle.font = skin.getFont("select_font");
            textFieldStyle.fontColor = Color.LIGHT_GRAY;

            listStyle.background = skin.getDrawable("menu_button_up");
            listStyle.font = skin.getFont("main_font");
            listStyle.selection = skin.getDrawable("menu_button_down");
            selectBoxStyle.background = skin.getDrawable("select_box_icon");
            selectBoxStyle.font = skin.getFont("select_font");
            selectBoxStyle.listStyle = listStyle;
            selectBoxStyle.scrollStyle = scrollPaneStyle;

            skin.add("default", labelStyle);
            skin.add("default", checkBoxStyle);
            skin.add("default", textFieldStyle);
            skin.add("default", treeStyle);
            skin.add("default", selectBoxStyle);
            skin.add("default", scrollPaneStyle);
            skin.add("main_menu_style", mainMenuStyle);
            skin.add("toolbar_style", toolBarStyle);
            skin.add("bottom_panel_style", bottomPanelStyle);
            skin.add("left_panel_style", leftPanelStyle);
            skin.add("right_panel_style", rightPanelStyle);
            skin.add("slider_horizontal", sliderStyleHorizontal);
            skin.add("text_button_vertical_style", textButtonStyleVertical);
            skin.add("horizontal_button_style", textButtonStyleHorizontal);
            skin.add("menu_button_style", menuButtonStyle);
            skin.add("button_open_style", buttonStyleOpen);
            skin.add("button_save_style", buttonStyleSave);
            skin.add("button_zoom_style", buttonStyleZoom);
            skin.add("button_play_style", buttonStylePlay);
            skin.add("button_stop_style", buttonStyleStop);
            skin.add("button_clear_style", buttonStyleClear);
            skin.add("button_style_hide", buttonStyleHide);
            skin.add("button_settings_style", buttonStyleSettings);
            skin.add("shooterUI_style", shooterUIStyle);
            skin.add("elementUI_style", elementUIStyle);

            ((L2DEditor) Gdx.app.getApplicationListener()).setScreen(new EditorScreen());

            freeTypeFontGenerator.dispose();

            dispose();
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void render(float delta) {
        update();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(L2DEditor.SCREEN_WIDTH * .5f - LOAD_WIDTH * .5f, L2DEditor.SCREEN_HEIGHT * .5f - LOAD_HEIGHT * .5f, LOAD_WIDTH, LOAD_HEIGHT);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(L2DEditor.SCREEN_WIDTH * .5f - LOAD_WIDTH * .5f, L2DEditor.SCREEN_HEIGHT * .5f - LOAD_HEIGHT * .5f, LOAD_WIDTH * progress, LOAD_HEIGHT);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }

    public static TextureAtlas getEditorAtlas() {
        return editorAtlas;
    }

    public static TextureAtlas getSpriteAtlas() {
        return spriteAtlas;
    }
}
