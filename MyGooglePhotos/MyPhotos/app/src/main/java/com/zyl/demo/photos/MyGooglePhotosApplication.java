package com.zyl.demo.photos;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by JasonZhao on 16/1/27.
 */
public class MyGooglePhotosApplication extends Application {

  private Bitmap currentBitmap;

  public Bitmap getCurrentBitmap() {
    return currentBitmap;
  }

  public void setCurrentBitmap(Bitmap currentBitmap) {
    this.currentBitmap = currentBitmap;
  }

  /**
   * Called when the application is starting, before any activity, service,
   * or receiver objects (excluding content providers) have been created.
   * Implementations should be as quick as possible (for example using
   * lazy initialization of state) since the time spent in this function
   * directly impacts the performance of starting the first activity,
   * service, or receiver in a process.
   * If you override this method, be sure to call super.onCreate().
   */
  @Override
  public void onCreate() {
    super.onCreate();
  }
}
