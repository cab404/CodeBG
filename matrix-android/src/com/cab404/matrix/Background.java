package com.cab404.matrix;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;

/**
 * @author cab404
 */
public class Background extends AndroidLiveWallpaperService {

    @Override
    public ApplicationListener createListener(boolean isPreview) {
        Settings.local_resolver = getFilesDir().getAbsolutePath() + "/";
        Matrix.isPreview = isPreview;
        return new Matrix();
    }

    @Override
    public AndroidApplicationConfiguration createConfig() {

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        return cfg;

    }

    @Override
    public void offsetChange(ApplicationListener listener, float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
