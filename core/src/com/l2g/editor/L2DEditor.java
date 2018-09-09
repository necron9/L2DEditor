package com.l2g.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.l2g.editor.actors.Shooter;
import com.l2g.editor.screens.EditorScreen;
import com.l2g.editor.screens.LoadScreen;
import com.l2g.editor.ui.*;
import com.l2g.editor.utils.CameraUtils;

public final class L2DEditor extends Game {
	public static final short
			SCREEN_WIDTH 		=	1300,						// Editor screen height
			SCREEN_HEIGHT		=	700,						// Editor screen width
			PPM 				=	100,						// Number of pixels in one meter
			VELOCITY_ITERATIONS	=	6,							// Acceleration update frequency
			POSITION_ITERATIONS	=	2;							// Position update frequency

	public static float
			DELETE_BORDER_WIDTH = SCREEN_WIDTH * 1.3f,			// Width of the border for deleting objects
			DELETE_BORDER_HEIGHT = SCREEN_HEIGHT * 1.3f,		// Height of the border for deleting objects
			VOLUME_MUSIC 		= 	0.02f,
			FRAME_DURATION 		= 	1.0f / 60.0f,
			GRAVITY_HORIZONTAL 	= 	0,
			GRAVITY_VERTICAL 	= 	-9.8f;

	public static final String
			TITLE 				= 	"L2G";						// Name of the editor

	public static final Color
			BACKGROUND_COLOR 	= 	new Color(1,1,1,1);			// Background color of the editor

	private static boolean
			isDelete = false,					// Object deletion flag
			isStart = false,					// Play button flag
			isChanged = false,					// Object adding flag
			isMap = true,						// Map rendering flag
			isFrameStep = false,					// Frame drawing flag
			isCameraBorder = true;

	private static float
			resizeBorder;

	private SpriteBatch batch;

	private static Stage stage, stageHUD;
	private static OrthographicCamera  camera, cameraHUD;
	private static Viewport viewport, viewportHUD;

	public static final class SingletonHolder {
		private static final ShapeRenderer shapeRenderer = new ShapeRenderer();
		private static final Skin skin = new Skin();
		private static final EditorManager editorManager = new EditorManager(shapeRenderer);
		private static final Rectangle workArea = new Rectangle(LeftPanel.LEFT_PANEL_WIDTH,
				BottomPanel.BOTTOM_PANEL_HEIGHT,
				SCREEN_WIDTH - LeftPanel.LEFT_PANEL_WIDTH - RightPanel.RIGHT_PANEL_WIDTH,
				SCREEN_HEIGHT - MainMenu.MAIN_MENU_HEIGHT - ToolBar.TOOL_BAR_HEIGHT - BottomPanel.BOTTOM_PANEL_HEIGHT);
		private static final AssetManager assetManager = new AssetManager();
		private static Vector3 touch = new Vector3(), pointerScreen = new Vector3(), pointerWorld = new Vector3();
		private static Grid grid = new Grid(SCREEN_WIDTH, SCREEN_HEIGHT);
		private static InputMultiplexer inputMultiplexer = new InputMultiplexer();

		public static ShapeRenderer getShapeRenderer() {
			return shapeRenderer;
		}

		public static Skin getSkin() {
			return skin;
		}

		public static EditorManager getEditorManager() {
			return editorManager;
		}

		public static Rectangle getWorkArea() {
			return workArea;
		}

		public static AssetManager getAssetManager() {
			return assetManager;
		}

		public static Vector3 getTouch() {
			return touch;
		}

		public static Vector3 getPointerScreen() {
			return pointerScreen;
		}

		public static Vector3 getPointerWorld() {
			return pointerWorld;
		}

		public static Grid getGrid() {
			return grid;
		}

		public static InputMultiplexer getInputMultiplexer() {
			return inputMultiplexer;
		}
	}

	@Override
	public void create () {
		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		cameraHUD = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		cameraHUD.position.x = SCREEN_WIDTH * .5f;
		cameraHUD.position.y = SCREEN_HEIGHT * .5f;

		viewport = new StretchViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
		viewportHUD = new StretchViewport(SCREEN_WIDTH, SCREEN_HEIGHT, cameraHUD);

		batch = new SpriteBatch();

		SingletonHolder.getEditorManager().setDebug(true);

		CameraUtils.setCameraBorder(true);
		CameraUtils.setCameraSpeed(CameraUtils.DEFAULT_CAMERA_SPEED);
		CameraUtils.setCameraZoomSpeed(CameraUtils.DEFAULT_CAMERA_ZOOM_SPEED);
		CameraUtils.setCameraZoomMin(CameraUtils.DEFAULT_CAMERA_ZOOM_MIN);

		CameraUtils.setCameraMin(viewport);

		stage = new Stage(viewport, batch) {
			@Override
			public void clear() {
				if (EditorScreen.getLeftPanel() != null)
					EditorScreen.getLeftPanel().clear();
				super.clear();
			}

			@Override
			public void addActor(Actor actor) {
				if (EditorScreen.getLeftPanel() != null)
					EditorScreen.getLeftPanel().addNode(new Label(actor.getName(), SingletonHolder.getSkin()));
				super.addActor(actor);
			}

			@Override
			public boolean setKeyboardFocus(Actor actor) {
				super.setKeyboardFocus(actor);

				getRoot().setDebug(false, true);
				if (actor != null) {
					actor.setDebug(true);
					System.out.println("dsd");
					if (actor instanceof Shooter) {
						EditorScreen.getShooterUI().setValues((Shooter.UserData) actor.getUserObject());
						EditorScreen.getRightPanel().setCurrentUI(EditorScreen.getShooterUI());
					}
				} else {
					if (EditorScreen.getLeftPanel() != null)
						EditorScreen.getLeftPanel().clearSelection();
					if (EditorScreen.getRightPanel() != null)
						EditorScreen.getRightPanel().clearCurrentUI();
				}

				return true;
			}
		};

		stageHUD = new Stage(viewportHUD, batch) {
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				SingletonHolder.getPointerWorld().set(screenX, screenY, 0);
				SingletonHolder.getPointerScreen().set(screenX, screenY, 0);
				camera.unproject(SingletonHolder.getPointerWorld());
				return super.mouseMoved(screenX, screenY);
			}
		};

		SingletonHolder.getInputMultiplexer().addProcessor(stage);
		SingletonHolder.getInputMultiplexer().addProcessor(stageHUD);

		Gdx.input.setInputProcessor(SingletonHolder.getInputMultiplexer());

		setScreen(new LoadScreen());
	}

	void update() {
		SingletonHolder.getEditorManager().update();
		CameraUtils.update();

		isFrameStep = true;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
		update();

		SingletonHolder.getGrid().draw(SingletonHolder.getShapeRenderer());

		SingletonHolder.getEditorManager().draw(batch);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stageHUD.act(Gdx.graphics.getDeltaTime());
		stageHUD.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewportHUD.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		CameraUtils.setCameraMin(viewport.getWorldWidth() * .5f, viewport.getWorldHeight() * .5f);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		batch.dispose();
		SingletonHolder.getSkin().dispose();
		stage.dispose();
		stageHUD.dispose();
		SingletonHolder.getShapeRenderer().dispose();
		SingletonHolder.getEditorManager().dispose();
		SingletonHolder.getAssetManager().dispose();
	}

	public static Stage getStage() {
		return stage;
	}

	public static Stage getStageHUD() {
		return stageHUD;
	}

	public static OrthographicCamera getCamera() {
		return camera;
	}

	public static OrthographicCamera getCameraHUD() {
		return cameraHUD;
	}

	public static Viewport getViewport() {
		return viewport;
	}

	public static Viewport getViewportHUD() {
		return viewportHUD;
	}

	public static void isFrameStep(boolean frameStep1) {
		isFrameStep = frameStep1;
	}

	public static boolean isFrameStep() {
		return isFrameStep;
	}

	public static void isMap(boolean map1) {
		isMap = map1;
	}

	public static boolean isMap() {
		return isMap;
	}

	public static void isDelete(boolean delete1) {
		isDelete = delete1;
	}

	public static boolean isDelete() {
		return isDelete;
	}

	public static void isStart(boolean start1) {
		isStart = start1;
	}

	public static boolean isStart() {
		return isStart;
	}

	public static void isChanged(boolean changed1) {
		isChanged = changed1;
	}

	public static boolean isChanged() {
		return isChanged;
	}

	public static void isCameraBorder(boolean cameraBorder1) {
		isCameraBorder = cameraBorder1;
	}

	public static boolean isCameraBorder() {
		return isCameraBorder;
	}

	public static float getResizeBorder() {
		return resizeBorder;
	}

	public static void setResizeBorder(float resizeBorder) {
		L2DEditor.resizeBorder = resizeBorder;
	}
}