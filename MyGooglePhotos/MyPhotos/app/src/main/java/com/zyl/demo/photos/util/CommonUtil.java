package com.zyl.demo.photos.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by JasonZhao on 16/1/21.
 */
public class CommonUtil {

  public static int getScreenWidth(Activity act) {
    DisplayMetrics dm = new DisplayMetrics();
    act.getWindowManager().getDefaultDisplay().getMetrics(dm);
    return dm.widthPixels;
  }

  public static int getScreenHeight(Activity act) {
    DisplayMetrics dm = new DisplayMetrics();
    act.getWindowManager().getDefaultDisplay().getMetrics(dm);
    return dm.heightPixels;
  }

  public static int getColumns(int size, int per) {
    return size / per + (size % per > 0 ? 1 : 0);
  }

  /**
   * 放大缩小图片
   */
  public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    Matrix matrix = new Matrix();
    float scaleWidht = ((float) w / width);
    float scaleHeight = ((float) h / height);
    matrix.postScale(scaleWidht, scaleHeight);
    Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    return newbmp;
  }

  public static int getViewWidth(View view) {
    int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    view.measure(w, h);
    return view.getMeasuredWidth();
  }

  public static int getViewHeight(View view) {
    int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    view.measure(w, h);
    return view.getMeasuredHeight();
  }

  public static int getStatusBarHeight(Context context) {
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }
}
