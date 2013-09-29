package com.cab404.matrix;

import com.badlogic.gdx.Gdx;

/**
 * @author cab404
 */
public class Luna {
    public static boolean DEBUG = true;

    public static void log(Object toLog) {
        if (!DEBUG) return;
        Gdx.app.log("Luna Log", toLog != null ? toLog.toString() : "NULL");
    }
}
