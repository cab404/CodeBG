package com.cab404.matrix;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * @author cab404
 */
public class ColorChooserAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return Settings.Color.values().length;
    }

    @Override
    public Object getItem(int position) {
        return Settings.Color.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ColorSelector.create(parent);
        }

        return ColorSelector.convert(Settings.Color.values()[i], convertView);
    }

    private static class ColorSelector {

        public static View create(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.color, parent, false);
        }

        public static View convert(Settings.Color col, View view) {
            TextView title, r_t, g_t, b_t;
            ImageView display;
            SeekBar r, g, b;

            title = (TextView) view.findViewById(R.id.color_title);
            title.setText(col.label);

            display = (ImageView) view.findViewById(R.id.color_display);
            display.setImageDrawable(new ColorDrawable());

            r_t = (TextView) view.findViewById(R.id.color_r_title);
            g_t = (TextView) view.findViewById(R.id.color_g_title);
            b_t = (TextView) view.findViewById(R.id.color_b_title);

            r = (SeekBar) view.findViewById(R.id.color_r);
            g = (SeekBar) view.findViewById(R.id.color_g);
            b = (SeekBar) view.findViewById(R.id.color_b);

            r.setOnSeekBarChangeListener(new OnSeek(new WeakReference<>(r_t), new WeakReference<>(display), col, 'r'));
            g.setOnSeekBarChangeListener(new OnSeek(new WeakReference<>(g_t), new WeakReference<>(display), col, 'g'));
            b.setOnSeekBarChangeListener(new OnSeek(new WeakReference<>(b_t), new WeakReference<>(display), col, 'b'));

            r.setProgress((int) (col.c.r * 255));
            g.setProgress((int) (col.c.g * 255));
            b.setProgress((int) (col.c.b * 255));

            r_t.setText("R: " + r.getProgress());
            g_t.setText("G: " + g.getProgress());
            b_t.setText("B: " + b.getProgress());

            int color = 0;
            color |= 255;
            color <<= 8;
            color |= r.getProgress();
            color <<= 8;
            color |= g.getProgress();
            color <<= 8;
            color |= b.getProgress();

            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bitmap.setPixel(0, 0, color);
            BitmapDrawable disp = new BitmapDrawable(Resources.getSystem(), bitmap);
            display.setImageDrawable(disp);

            return view;
        }


        static class OnSeek implements SeekBar.OnSeekBarChangeListener {
            WeakReference<TextView> title;
            private WeakReference<ImageView> display;
            private Settings.Color color;
            private char element;

            public OnSeek(WeakReference<TextView> title, WeakReference<ImageView> display, Settings.Color color, char element) {
                this.title = title;
                this.display = display;
                this.color = color;
                this.element = element;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (element) {
                    case 'r':
                        color.c.r = progress / 255f;
                        title.get().setText("R: " + progress);
                        break;
                    case 'g':
                        color.c.g = progress / 255f;
                        title.get().setText("G: " + progress);
                        break;
                    case 'b':
                        color.c.b = progress / 255f;
                        title.get().setText("B: " + progress);
                        break;
                }
                int r, g, b;
                r = (int) (color.c.r * 255);
                g = (int) (color.c.g * 255);
                b = (int) (color.c.b * 255);

                int color = 0;
                color |= 255;
                color <<= 8;
                color |= r;
                color <<= 8;
                color |= g;
                color <<= 8;
                color |= b;

                Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                bitmap.setPixel(0, 0, color);
                BitmapDrawable disp = new BitmapDrawable(Resources.getSystem(), bitmap);
                display.get().setImageDrawable(disp);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Settings.write();
            }
        }
    }

}
