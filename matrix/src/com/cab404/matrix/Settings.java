package com.cab404.matrix;

import com.badlogic.gdx.Gdx;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author cab404
 */
public class Settings {
    public static float position = 0.9f;
    public static int speed = 20;
    public static int font_size = 10;
    public static int offsetX = 0, offsetY = 0;
    public static boolean byLine = false;
    public static String local_resolver = "";

    public static void read() {
        File settings;
        settings = new File(local_resolver + "settings.cfg");

        if (settings.exists())
            try {
                Scanner scan = new Scanner(new FileInputStream(settings), "utf-8");

                position = Float.valueOf(scan.nextLine());
                speed = Integer.valueOf(scan.nextLine());
                byLine = Boolean.valueOf(scan.nextLine());
                font_size = Integer.valueOf(scan.nextLine());

                for (Color col : Color.values()) {
                    col.c.set(com.badlogic.gdx.graphics.Color.valueOf(scan.nextLine()));
                }

                scan.close();
            } catch (Exception e) {
                Gdx.app.log("Luna Log", "Error while reading", e);
                Luna.log("Recreating settings...");
                write();
            }
        else {
            Luna.log("Creating settings...");
            write();
        }
    }

    public static void write() {

        File settings;
        settings = new File(local_resolver + "/settings.cfg");

        try {
            if (!settings.delete() && !settings.createNewFile())
                Luna.log("No permissions to settings file!");

            PrintStream out = new PrintStream(new FileOutputStream(settings), true, "utf-8");

            out.println(position + "");
            out.println(speed + "");
            out.println(byLine + "");
            out.println(font_size + "");

            for (Color col : Color.values()) {
                out.println(col.c.toString().substring(0, 6));
            }

            out.close();
        } catch (IOException e) {
            Luna.log(e);
        }
    }

    enum Color {
        bg("001200", "Фон"),
        name("15a61e", "Функции"),
        sign("1ec321", "Знаки"),
        str("1c6d61", "Строки"),
        var("086110", "Переменные"),
        num("1e8b21", "Числа"),
        kw("1ba71c", "Ключевые слов");

        public com.badlogic.gdx.graphics.Color c;
        public String label;

        Color(String hex, String label) {
            this.label = label;
            c = com.badlogic.gdx.graphics.Color.valueOf(hex);
        }
    }

    public static String serializeSettings() {
        StringBuilder builder = new StringBuilder();

        for (Color col : Color.values()) {
            builder.append(col.c.toString().substring(0, 6)).append(',');
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static void deserializeSettings(String settings) {
        List<String> parts = Arrays.asList(settings.split(","));

        for (int i = 0; i < parts.size(); i++) {
            Color.values()[i].c = com.badlogic.gdx.graphics.Color.valueOf(parts.get(i));
        }

        Settings.write();

    }

}

