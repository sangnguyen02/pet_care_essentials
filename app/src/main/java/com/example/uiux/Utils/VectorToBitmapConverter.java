package com.example.uiux.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import androidx.core.content.res.ResourcesCompat;

public class VectorToBitmapConverter {

    public static Bitmap convertVectorToBitmap(Context context, int vectorResId) {
        // Tải VectorDrawable từ resource
        VectorDrawable vectorDrawable = (VectorDrawable) ResourcesCompat.getDrawable(context.getResources(), vectorResId, null);

        // Tạo Bitmap với kích thước mong muốn
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // Tạo Canvas để vẽ vào Bitmap
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return bitmap;
    }
}
