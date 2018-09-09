package com.l2g.editor.utils.utils;

import com.badlogic.gdx.utils.Array;

public class Scene {
    private Array<Object> objects;

    private transient boolean isEmpty;

    public Scene() {

    }

    public Scene(int s) {
        objects = new Array<Object>();

        isEmpty = true;
    }

    public void add(Object object) {
        objects.add(object);

        isEmpty = false;
    }

    public Array<Object> getObjects() {
        return objects;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}