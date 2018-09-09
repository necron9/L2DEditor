package com.l2g.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.l2g.editor.L2DEditor;
import com.l2g.editor.actors.ui.ShooterUI;
import com.l2g.editor.elements.PhysicalElementsFactory;
import com.l2g.editor.listeners.EditorScreenListener;
import com.l2g.editor.listeners.WorldContactListener;
import com.l2g.editor.utils.CameraUtils;
import com.l2g.editor.elements.ui.ElementUI;
import com.l2g.editor.elements.ui.ShellUI;
import com.l2g.editor.ui.*;
import com.l2g.editor.utils.physics.Box2DMapObjectParser;

import java.io.File;

public final class EditorScreen implements Screen {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private World world;

    private static Body border, selection;
    private static File currentFile;

    private static boolean
            isVisible, isResizeBorder;

    private static MainMenu mainMenu;
    private static ToolBar toolBar;
    private static BottomPanel bottomPanel;
    private static LeftPanel leftPanel;
    private static RightPanel rightPanel;
    private static ShooterUI shooterUI;
    private static ElementUI elementUI;
    private static ShellUI shellUI;

    public EditorScreen() {
        if (L2DEditor.SingletonHolder.getAssetManager().isLoaded("maps/test.tmx", TiledMap.class)) {
            tiledMap = L2DEditor.SingletonHolder.getAssetManager().get("maps/test.tmx", TiledMap.class);

            CameraUtils.setCameraZoomMax(L2DEditor.getCamera(), tiledMap);
            CameraUtils.setCameraMax(tiledMap);

            L2DEditor.SingletonHolder.getGrid().setSize(Integer.valueOf(tiledMap.getProperties().get("gridSize").toString()));

            orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

            world = new World(new Vector2(L2DEditor.GRAVITY_HORIZONTAL, L2DEditor.GRAVITY_VERTICAL), true);
            world.setContactListener(new WorldContactListener());

            L2DEditor.SingletonHolder.getEditorManager().setWorld(world);
            L2DEditor.SingletonHolder.getEditorManager().setAutoClearForces(true);

            new Box2DMapObjectParser(1f / L2DEditor.PPM).load(world, tiledMap);

            //Body body = MyGdxGame.world.createBody(bodyDef);
            //bodyEditorLoader.attachFixture(body, "body", fixtureDef, 12, 1, 1);

            float mas[] = {0, 0, L2DEditor.SCREEN_WIDTH / L2DEditor.PPM, 0};
            L2DEditor.SingletonHolder.getEditorManager().createPolygon(0, 0, mas, 0, PhysicalElementsFactory.materialType.STONE, BodyDef.BodyType.StaticBody,"ground", true);
        }

        float mas[] = {0, 0, 0, L2DEditor.DELETE_BORDER_HEIGHT / L2DEditor.PPM, L2DEditor.DELETE_BORDER_WIDTH / L2DEditor.PPM,
                L2DEditor.DELETE_BORDER_HEIGHT / L2DEditor.PPM, L2DEditor.DELETE_BORDER_WIDTH  / L2DEditor.PPM, 0, 0, 0};

        border = L2DEditor.SingletonHolder.getEditorManager().createPolygon(-(BottomPanel.DELETE_BORDER_WIDTH - L2DEditor.SCREEN_WIDTH) * .5f, -(BottomPanel.DELETE_BORDER_HIEGHT - L2DEditor.SCREEN_HEIGHT) * .5f, mas, 0, PhysicalElementsFactory.materialType.STONE, BodyDef.BodyType.StaticBody, "border", false);

        MainMenu.MainMenuStyle mainMenuStyle = new MainMenu.MainMenuStyle();
        mainMenuStyle.background = L2DEditor.SingletonHolder.getSkin().getDrawable("horizontal_background_line");
        L2DEditor.SingletonHolder.getSkin().add("main_menu_style", mainMenuStyle);

        mainMenu = new MainMenu(L2DEditor.SingletonHolder.getSkin().get("main_menu_style", MainMenu.MainMenuStyle.class), MainMenu.MenuStates.DEFAULT);
        toolBar = new ToolBar(L2DEditor.SingletonHolder.getSkin().get("toolbar_style",ToolBar.ToolBarStyle.class), ToolBar.ToolBarStates.DEFAULT);
        bottomPanel = new BottomPanel(L2DEditor.SingletonHolder.getSkin().get("bottom_panel_style",BottomPanel.BottomPanelStyle.class));
        leftPanel = new LeftPanel(L2DEditor.SingletonHolder.getSkin().get("left_panel_style", LeftPanel.LeftPanelStyle.class));
        rightPanel = new RightPanel("", L2DEditor.SingletonHolder.getSkin().get("right_panel_style", RightPanel.RightPanelStyle.class));
        shooterUI = new ShooterUI(L2DEditor.SingletonHolder.getSkin().get("shooterUI_style",ShooterUI.ShooterUIStyle.class));
        elementUI = new ElementUI(L2DEditor.SingletonHolder.getSkin().get("elementUI_style", ElementUI.ElementUIStyle.class));
        shellUI = new ShellUI(L2DEditor.SingletonHolder.getSkin().get("elementUI_style", ElementUI.ElementUIStyle.class));

        mainMenu.setBounds(0, L2DEditor.SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT, L2DEditor.SCREEN_WIDTH, MainMenu.MAIN_MENU_HEIGHT);
        toolBar.setBounds(0, L2DEditor.SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT - ToolBar.TOOL_BAR_HEIGHT, L2DEditor.SCREEN_WIDTH, ToolBar.TOOL_BAR_HEIGHT);
        bottomPanel.setBounds(0, 0, L2DEditor.SCREEN_WIDTH, BottomPanel.BOTTOM_PANEL_HEIGHT);
        leftPanel.setBounds(0, BottomPanel.BOTTOM_PANEL_HEIGHT, LeftPanel.LEFT_PANEL_WIDTH, L2DEditor.SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT - ToolBar.TOOL_BAR_HEIGHT - BottomPanel.BOTTOM_PANEL_HEIGHT);
        rightPanel.setBounds(L2DEditor.SCREEN_WIDTH - RightPanel.RIGHT_PANEL_WIDTH, BottomPanel.BOTTOM_PANEL_HEIGHT, RightPanel.RIGHT_PANEL_WIDTH, L2DEditor.SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT - ToolBar.TOOL_BAR_HEIGHT - BottomPanel.BOTTOM_PANEL_HEIGHT);

        L2DEditor.getStageHUD().addActor(mainMenu);
        L2DEditor.getStageHUD().addActor(toolBar);
        L2DEditor.getStageHUD().addActor(bottomPanel);
        L2DEditor.getStageHUD().addActor(leftPanel);
        L2DEditor.getStageHUD().addActor(rightPanel);

        L2DEditor.SingletonHolder.getGrid().setVisible(true);
        L2DEditor.SingletonHolder.getInputMultiplexer().addProcessor(new EditorScreenListener(this));

        currentFile = null;
        isVisible = true;
        isResizeBorder = true;
    }

    public static void setVisible(boolean visible) {
        bottomPanel.setVisible(visible);
        leftPanel.setVisible(visible);
        rightPanel.setVisible(visible);
        toolBar.setVisibleButtonChecked(visible);

        setBorderActive(visible);
        setGridActive(visible);

        isVisible = visible;
    }

    public static boolean isVisible() {
        return isVisible;
    }

    public static void setBorderActive(boolean active) {
        CheckBox checkBox = (CheckBox) BottomPanel.cells.get(0).getActor();
        if (checkBox.isChecked())
            checkBox.setChecked(!active);
        if (!isVisible)
            checkBox.setChecked(active);
    }

    public static void setGridActive(boolean active) {
        CheckBox checkBox = (CheckBox) BottomPanel.cells.get(2).getActor();
        if (checkBox.isChecked())
            checkBox.setChecked(!active);
        if (!isVisible)
            checkBox.setChecked(active);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(L2DEditor.SingletonHolder.getInputMultiplexer());
    }


    public static void setCurrentFile(File file) {
        currentFile = file;
    }

    public static File getCurrentFile() {
        return currentFile;
    }

    public static void setBorderVisible(boolean visible) {
        border.setActive(visible);
    }

    public static boolean isBorderVisible() {
        return border.isActive();
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            CameraUtils.moveToLeft(L2DEditor.getCamera(), CameraUtils.DEFAULT_CAMERA_SMOOTH);
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            CameraUtils.moveToRight(L2DEditor.getCamera(), CameraUtils.DEFAULT_CAMERA_SMOOTH);

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            CameraUtils.moveToUp(L2DEditor.getCamera(), CameraUtils.DEFAULT_CAMERA_SMOOTH);
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            CameraUtils.moveToDown(L2DEditor.getCamera(), CameraUtils.DEFAULT_CAMERA_SMOOTH);

        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP))
            CameraUtils.zoomIn(L2DEditor.getCamera(), CameraUtils.DEFAULT_ZOOM_SMOOTH);
        else if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN))
            CameraUtils.zoomOut(L2DEditor.getCamera(), CameraUtils.DEFAULT_ZOOM_SMOOTH);

        L2DEditor.getCamera().update();
    }

    @Override
    public void render(float delta) {
        update();

        if (L2DEditor.isMap()) {
            orthogonalTiledMapRenderer.setView(L2DEditor.getCamera());
            orthogonalTiledMapRenderer.render();
        }

        L2DEditor.SingletonHolder.getShapeRenderer().setProjectionMatrix(L2DEditor.getCamera().combined);
        L2DEditor.SingletonHolder.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);

        if (border.isActive()) {
            L2DEditor.SingletonHolder.getShapeRenderer().rect(-(BottomPanel.DELETE_BORDER_WIDTH - L2DEditor.SCREEN_WIDTH) * .5f, -(BottomPanel.DELETE_BORDER_HIEGHT - L2DEditor.SCREEN_HEIGHT) * .5f, BottomPanel.DELETE_BORDER_WIDTH, BottomPanel.DELETE_BORDER_HIEGHT, Color.RED, Color.RED, Color.RED, Color.RED);
            //MyGdxGame.shapeRenderer.rect(workArea.getX(), workArea.getY(), workArea.getWidth(), workArea.getHeight());
        }
        if (isResizeBorder) {
            L2DEditor.SingletonHolder.getShapeRenderer().rect(0, 0, L2DEditor.getResizeBorder(), L2DEditor.SCREEN_HEIGHT, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
            L2DEditor.SingletonHolder.getShapeRenderer().rect(CameraUtils.getCameraMaxX() + L2DEditor.getViewport().getWorldWidth() * .5f - L2DEditor.getResizeBorder(), 0, L2DEditor.getResizeBorder(), L2DEditor.SCREEN_HEIGHT, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
        }

        L2DEditor.SingletonHolder.getShapeRenderer().end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }

    public static Body getSelection() {
        return selection;
    }

    public static void setSelection(Body selection1) {
        selection = selection1;
    }

    public static boolean isResizeBorder() {
        return isResizeBorder;
    }

    public static void isResizeBorder(boolean resizeBorder1) {
        isResizeBorder = resizeBorder1;
    }

    public static MainMenu getMainMenu() {
       return mainMenu;
    }

    public static ToolBar getToolBar() {
        return toolBar;
    }

    public static BottomPanel getBottomPanel() {
        return bottomPanel;
    }

    public static LeftPanel getLeftPanel() {
        return leftPanel;
    }

    public static RightPanel getRightPanel() {
        return rightPanel;
    }

    public static ShellUI getShellUI() {
        return shellUI;
    }

    public static ShooterUI getShooterUI() {
        return shooterUI;
    }

    public static ElementUI getElementUI() {
        return elementUI;
    }
}