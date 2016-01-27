package com.zyl.demo.photos;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zyl.demo.photos.util.CommonUtil;

/**
 * Created by JasonZhao on 16/1/25.
 */
public class ImageDetailActivity extends AppCompatActivity implements View.OnTouchListener {

  private static final String TAG = "ImageDetailActivity";

  private Bitmap imgData;

  private LinearLayout imageContainer;

  private ImageView img;

  private Toolbar toolbar;

  private PointF preP = new PointF();

  private PointF currentP = new PointF();

  private PointF secondP = new PointF();

  private int distance = 0;

  private int maxWidth = 0, maxHeight = 0, minWidth = 400, minHight = 400;

  private int comparedWidth = minWidth, comparedHeight = minHight;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_image_detail);
    init();
  }

  private void init() {
    initField();
    initToolbar();
    initRange();
    // TODO (remove this tmp code)
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        initData();
      }
    }, 300);
  }

  private void initRange() {
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    maxWidth = dm.widthPixels;
    maxHeight = CommonUtil.getScreenHeight(this) - toolbar.getBottom() - CommonUtil.getStatusBarHeight(this);
  }

  private void initToolbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("图片详情");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  }

  private void initData() {
    try {
      MyGooglePhotosApplication application = (MyGooglePhotosApplication) getApplication();
      this.imgData = application.getCurrentBitmap();
      img.setImageBitmap(imgData);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void initField() {
    img = (ImageView) findViewById(R.id.img);
    img.setOnTouchListener(this);
    imageContainer = (LinearLayout) findViewById(R.id.image_container);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
  }

  /**
   * This hook is called whenever an item in your options menu is selected.
   * The default implementation simply returns false to have the normal
   * processing happen (calling the item's Runnable or sending a message to
   * its Handler as appropriate).  You can use this method for any items
   * for which you would like to do processing without those other
   * facilities.
   * <p/>
   * <p>Derived classes should call through to the base class for it to
   * perform the default menu handling.</p>
   *
   * @param item The menu item that was selected.
   * @return boolean Return false to allow normal menu processing to
   * proceed, true to consume it here.
   * @see #onCreateOptionsMenu
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Called when a touch event is dispatched to a view. This allows listeners to
   * get a chance to respond before the target view.
   *
   * @param v     The view the touch event has been dispatched to.
   * @param event The MotionEvent object containing full information about
   *              the event.
   * @return True if the listener has consumed the event, false otherwise.
   */
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      // 主点按下
      case MotionEvent.ACTION_DOWN:
        initRange();
        preP.set(event.getX(), event.getY());
        break;
      // 副点按下
      case MotionEvent.ACTION_POINTER_DOWN:
        preP.set(event.getX(0), event.getY(0));
        secondP.set(event.getX(1), event.getY(1));
        distance = (int) Math.sqrt((secondP.x - preP.x) * (secondP.x - preP.x) + (secondP.y - preP.y) * (secondP.y - preP.y));
        break;
      // 移动or缩放
      case MotionEvent.ACTION_MOVE:
        if (event.getPointerCount() > 1) {
          // 缩放
          doScale(event);
        } else {
          // 移动
          doDrag(event);
        }
        break;
      // 副点收起
      case MotionEvent.ACTION_POINTER_UP:
        comparedWidth = img.getWidth();
        comparedHeight = img.getHeight();
        break;
      // 主点收起
      case MotionEvent.ACTION_UP:
        break;
    }
    return true;
  }

  private void doScale(MotionEvent event) {
    preP.set(event.getX(0), event.getY(0));
    int pointDistance = (int) Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) +
        (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));
    if (Math.abs(pointDistance - distance) >= 30) {
      int status = pointDistance >= distance ? 1 : -1;
      double scale = pointDistance * 0.5 / distance;
      if (status < 0) {
        scale = distance * 0.2 / pointDistance;
      }

      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
      int width = (int) (comparedWidth + comparedWidth * scale * status);
      int height = (int) (comparedHeight + comparedHeight * scale * status);
      if (width <= minWidth) {
        width = minWidth;
      }
      if (width >= 2 * maxWidth) {
        width = 2 * maxWidth;
      }
      if (height <= minHight) {
        height = minHight;
      }
      if (height >= 2 * maxHeight) {
        height = 2 * maxHeight;
      }
      params.width = width;
      params.height = height;
      img.setLayoutParams(params);
    }
  }

  private void doDrag(MotionEvent event) {
    currentP.set(event.getX(), event.getY());

    float distanceX = currentP.x - preP.x;
    float distanceY = currentP.y - preP.y;
    if (Math.abs(distanceX) >= 20 || Math.abs(distanceY) >= 20) {
      int left = (int) (img.getLeft() + distanceX);
      int top = (int) (img.getTop() + distanceY);
      int right = (int) (img.getRight() + distanceX);
      int bottom = (int) (img.getBottom() + distanceY);
      // 边界检查,不出左右边界
      if (img.getWidth() < maxWidth) {
        // 左边界
        if (left <= 0) {
          left = 0;
          right = left + img.getWidth();
        }
        // 右边界
        if (right >= maxWidth) {
          right = maxWidth;
          left = right - img.getWidth();
        }
      } else {
        // 左边界
        if (left <= -maxWidth) {
          left = -maxWidth;
          right = left + img.getWidth();
        }
        // 右边界
        if (right >= 2 * maxWidth) {
          right = 2 * maxWidth;
          left = right - img.getWidth();
        }
      }

      if (img.getHeight() < maxHeight) {
        // 上边界
        if (top <= 0) {
          top = 0;
          bottom = top + img.getHeight();
        }

        // 底边界
        if (bottom >= maxHeight) {
          bottom = maxHeight;
          top = bottom - img.getHeight();
        }
      } else {
        // 上边界
        if (top <= -maxHeight) {
          top = -maxHeight;
          bottom = top + img.getHeight();
        }

        // 底边界
        if (bottom >= 2 * maxHeight) {
          bottom = 2 * maxHeight;
          top = bottom - img.getHeight();
        }
      }
      img.layout(left, top, right, bottom);
    }
  }
}
