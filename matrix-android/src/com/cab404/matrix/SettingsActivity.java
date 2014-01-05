package com.cab404.matrix;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    ToggleButton
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

        byline_peeker = (ToggleButton) head.findViewById(R.id.cfg_by_line_peeker);

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
        size_peeker.setProgress(Settings.font_size - 4);
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
        colors.setAdapter(new ColorChooserAdapter() {
            @Override public void onChange() {
                updateText();
            }
        });

        final EditText seri = (EditText) findViewById(R.id.seri);
        seri.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }
            @Override public void afterTextChanged(Editable editable) {
                try {
                    Settings.deserializeSettings(editable.toString());
                } catch (Exception ex) {
                    Log.e("Lunalog", "Rec", ex);
                    seri.setText(Settings.serializeSettings());
                }
            }
        });


        updateText();
    }

    public void updateText() {
        speed_value.setText((Settings.speed * 60) + " "
                + (Settings.byLine ?
                getResources().getString(R.string.lps) :
                getResources().getString(R.string.cps)));
        height_value.setText(Settings.position + "/1.0");
        size_value.setText((Settings.font_size) + " px");
        byline_value.setText(Settings.byLine ?
                getResources().getString(R.string.line_by_line) :
                getResources().getString(R.string.char_by_char));

        ((TextView) findViewById(R.id.seri)).setText(Settings.serializeSettings());

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
