package com.cab404.matrix;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

/**
 * @author cab404
 */
public class SettingsActivity extends Activity {
    TextView
            speed_title, speed_value,
            height_title, height_value,
            size_title, size_value,
            byline_title, byline_value;
    SeekBar
            speed_peeker, height_peeker, size_peeker;
    Switch
            byline_peeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.local_resolver = getFilesDir().getAbsolutePath() + "/";

        String[] names = getResources().getStringArray(R.array.colors);

        for (int i = 0; i != names.length; i++)
            Settings.Color.values()[i].label = names[i];

        Settings.read();

        setContentView(R.layout.settings);

        View head = getLayoutInflater().inflate(R.layout.settings_layout, null, false);

        speed_title = (TextView) head.findViewById(R.id.cfg_speed_title);
        height_title = (TextView) head.findViewById(R.id.cfg_height_title);
        byline_title = (TextView) head.findViewById(R.id.cfg_by_line_title);
        size_title = (TextView) head.findViewById(R.id.cfg_text_size_title);

        speed_value = (TextView) head.findViewById(R.id.cfg_speed_value);
        height_value = (TextView) head.findViewById(R.id.cfg_height_value);
        byline_value = (TextView) head.findViewById(R.id.cfg_by_line_value);
        size_value = (TextView) head.findViewById(R.id.cfg_text_size_value);

        speed_peeker = (SeekBar) head.findViewById(R.id.cfg_speed_peeker);
        height_peeker = (SeekBar) head.findViewById(R.id.cfg_height_peeker);
        size_peeker = (SeekBar) head.findViewById(R.id.cfg_text_size_peeker);

        byline_peeker = (Switch) head.findViewById(R.id.cfg_by_line_peeker);

        speed_peeker.setMax(30);
        speed_peeker.setProgress(Settings.speed);
        speed_peeker.setOnSeekBarChangeListener(new onSeekBar() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.speed = progress;
                updateText();
            }
        });

        height_peeker.setMax(1000);
        height_peeker.setProgress((int) (1000 * Settings.position));
        height_peeker.setOnSeekBarChangeListener(new onSeekBar() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.position = progress / 1000f;
                updateText();
            }
        });

        size_peeker.setMax(60 - 4);
        size_peeker.setProgress(Settings.font_size - 10);
        size_peeker.setOnSeekBarChangeListener(new onSeekBar() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.font_size = progress + 4;
                updateText();
            }
        });

        byline_peeker.setChecked(!Settings.byLine);
        byline_peeker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Settings.byLine = byline_peeker.isChecked();
                updateText();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Settings.write();
                        return null;
                    }
                }.execute();
                return false;
            }
        });

        ListView colors = (ListView) findViewById(R.id.root_list);

        colors.addHeaderView(head);
        colors.setAdapter(new ColorChooserAdapter());

        updateText();
    }

    public void updateText() {
        speed_value.setText((Settings.speed * 60) + " " + (Settings.byLine ? "лин" : "зн") + "/сек");
        height_value.setText(Settings.position + "/1.0");
        size_value.setText((Settings.font_size) + " px");
        byline_value.setText(Settings.byLine ?
                "Код выводится построчно." :
                "Код выводится познаково.");
    }

    abstract class onSeekBar implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Settings.write();
                    return null;
                }
            }.execute();
        }
    }
}