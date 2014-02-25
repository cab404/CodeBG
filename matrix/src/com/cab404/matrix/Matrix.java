package com.cab404.matrix;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

public class Matrix implements ApplicationListener {
    private SpriteBatch batch;
    private Array<String> lines;

    public int w, h;
    private CodeCreator code;
    private int lines_num;
    private int fontHeight;
    public static boolean isPreview = false;
    public boolean paused = false;

    public static BitmapFont def_font;
    private FreeTypeFontGenerator gen;
    private int temporary_size = Settings.font_size;

    public static final String chars =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz"
                    + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
                    + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
                    + "1234567890-=!\"№;%:?*()_+\\/|<>.,'[]{}";

    @Override
    public void create() {
        Settings.read();

        lines = new Array<>();
        code = new CodeCreator();

        FileHandle font_file = Gdx.files.internal("data/SourceCodePro-Regular.ttf");
        gen = new FreeTypeFontGenerator(font_file);

        genFonts();

        batch = new SpriteBatch();

        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.drawPixel(0, 0);

        bg = new Sprite(new Texture(px));
    }

    @Override
    public void dispose() {
        def_font.dispose();
        batch.dispose();
        lines.clear();
        code = null;
        gen.dispose();
    }

    public void genFonts() {
        def_font = gen.generateFont(Settings.font_size, chars, false);
        def_font.setUseIntegerPositions(true);
        temporary_size = Settings.font_size;

        fontHeight = (int) def_font.getLineHeight();
        lines_num = h / fontHeight;
    }

    Sprite bg;
    @Override
    public void render() {
        Gdx.gl.glClearColor(Settings.Color.bg.c.r, Settings.Color.bg.c.g, Settings.Color.bg.c.b, 0.9f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        update();

        bg.setColor(Settings.Color.bg.c);
        bg.setPosition(0, 0);
        bg.setSize(w, h);

        if (temporary_size != Settings.font_size) {
            genFonts();
        }

        batch.begin();
        bg.draw(batch);
        {
            while (lines.size > lines_num * Settings.position) {
                lines.removeIndex(0);
            }
            lines.shrink();
            for (int i = 0; i < lines.size; i++) {
                renderLine(batch, lines.get(i), (int) def_font.getSpaceWidth(), h - fontHeight * i - 2);
            }
        }
        batch.end();
    }

    String typing = "";

    public void update() {

        for (int i = 0; i != Settings.speed; i++) {
            if (Settings.byLine) {
                lines.add(code.nextLine());
                typing = "";
            } else {
                if (typing.isEmpty()) {
                    lines.add("");
                    typing = code.nextLine();
                } else {
                    lines.set(lines.size - 1, lines.peek() + typing.charAt(0));
                    typing = typing.substring(1);
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        w = width;
        h = height;

        lines_num = h / fontHeight;
        batch = new SpriteBatch();
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
        genFonts();
        resize(w, h);
    }

    /**
     * Эта вещь обрабатывает цветовые маркеры в тексте, по типу &color&, и проставляет нужные цвета.
     * К примеру:
     * This text gonna be green!&green& This text gonna be red!&red&
     */
    public void renderLine(SpriteBatch batch, String line, int x, int y) {
        int index = 0;

        while (index != -1) {
            try {
                def_font.setColor(Settings.Color.sign.c);
            } catch (Throwable t) {
                Luna.log("JaEWC");
            }
            int new_index = line.indexOf('&', index);

            String subline = "";

            if (new_index == -1) {
                if (index < line.length())
                    subline = line.substring(index);
            } else {

                subline = line.substring(index, new_index);

                String subline_color = "sign";

                int end_index = line.indexOf("&", new_index + 1);

                if (new_index < line.length() && end_index != -1)
                    subline_color = line.substring(new_index + 1, end_index);

                try {
                    def_font.setColor(Settings.Color.valueOf(subline_color + "").c);
                } catch (Throwable t) {
                    Luna.log("JaEWC");
                }

                new_index += subline_color.length() + 2;
            }

            int width = (int) def_font.getBounds(subline).width;

            try {
                def_font.draw(batch, subline, x, y);
            } catch (Throwable t) {
                Luna.log("JaEWR");
            }

            x += width;
            index = new_index;
        }
    }
}

