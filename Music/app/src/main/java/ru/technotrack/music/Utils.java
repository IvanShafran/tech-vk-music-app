package ru.technotrack.music;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for(;;) {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch(Exception e) {
        }
    }

    public static int getDevicePixels(Context context, int densityPixels) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixels, metrics);
    }
}
