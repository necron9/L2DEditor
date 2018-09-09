package com.l2g.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.l2g.editor.L2DEditor;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = L2DEditor.SCREEN_WIDTH;
		config.height = L2DEditor.SCREEN_HEIGHT;
		config.title = L2DEditor.TITLE;
		config.vSyncEnabled = true;
		new LwjglApplication(new L2DEditor(), config);
	}
}