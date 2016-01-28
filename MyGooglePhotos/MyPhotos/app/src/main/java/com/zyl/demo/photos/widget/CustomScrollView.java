package com.zyl.demo.photos.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.zyl.demo.photos.enumeration.ViewStatus;

/**
 * Created by JasonZhao on 16/1/26.
 */
public class CustomScrollView extends ScrollView {

  private static final String TAG = "CustomScrollView";

  private PointF primaryP = new PointF();

  private PointF secondP = new PointF();

  private boolean lock = false;

  private boolean move = false;

  private boolean selectMode = false;

  private int distance = 0;

  private ViewStatus currentStatus;

  public void setSelectMode(boolean selectMode) {
    this.selectMode = selectMode;
  }

  public void setCurrentStatus(ViewStatus currentStatus) {
    this.currentStatus = currentStatus;
  }

  private CustomScrollViewScaleChangeListener customScrollViewScaleChangeListener;

  public void setCustomScrollViewScaleChangeListener(CustomScrollViewScaleChangeListener customScrollViewScaleChangeListener) {
    this.customScrollViewScaleChangeListener = customScrollViewScaleChangeListener;
  }

  public CustomScrollView(Context context) {
    super(context);
  }

  public CustomScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * {@inheritDoc}
   *
   * @param ev
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    Log.d(TAG, "ev.getAction():" + ev.getAction());
    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        primaryP.set(ev.getX(), ev.getY());
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        primaryP.set(ev.getX(0), ev.getY(0));
        secondP.set(ev.getX(1), ev.getY(1));
        distance = (int) Math.sqrt((secondP.x - primaryP.x) * (secondP.x - primaryP.x) +
            (secondP.y - primaryP.y) * (secondP.y - primaryP.y));
        break;
      case MotionEvent.ACTION_MOVE:
        // 选择模式
        if (ev.getPointerCount() == 1 && selectMode) {
          Log.d(TAG, "now is long pressed status, ev:" + ev);
        } else {
          move = true;
          if (!lock) {
            if (ev.getPointerCount() >= 2) {
              int pointDistance = (int) Math.sqrt((ev.getX(0) - ev.getX(1)) * (ev.getX(0) - ev.getX(1)) +
                  (ev.getY(0) - ev.getY(1)) * (ev.getY(0) - ev.getY(1)));
              if (distance == 0) {
                distance = pointDistance;
              }
              if (Math.abs(pointDistance - distance) >= 30) {
                int status = pointDistance >= distance ? 1 : -1;
                double scale = pointDistance * 0.5 / distance;
                if (status < 0) {
                  scale = distance * 0.2 / pointDistance;
                }
                if (status > 0) {
                  if (currentStatus.equals(ViewStatus.STATUS_YEAR) && scale >= 0.5) {
                    lock = true;
                    customScrollViewScaleChangeListener.renderViewByScale(ViewStatus.STATUS_MONTH);
                  } else if (currentStatus.equals(ViewStatus.STATUS_MONTH) && scale >= 0.5) {
                    lock = true;
                    customScrollViewScaleChangeListener.renderViewByScale(ViewStatus.STATUS_DAY);
                  }
                } else {
                  if (currentStatus.equals(ViewStatus.STATUS_DAY) && scale <= 0.5) {
                    lock = true;
                    customScrollViewScaleChangeListener.renderViewByScale(ViewStatus.STATUS_MONTH);
                  } else if (currentStatus.equals(ViewStatus.STATUS_MONTH) && scale <= 0.5) {
                    lock = true;
                    customScrollViewScaleChangeListener.renderViewByScale(ViewStatus.STATUS_YEAR);
                  }
                }
              }
            }
          }
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        distance = 0;
        lock = false;
        move = false;
        break;
    }
    if (lock) {
      return true;
    } else {
      return super.onTouchEvent(ev);
    }
  }

  public interface CustomScrollViewScaleChangeListener {

    void renderViewByScale(ViewStatus status);

  }
}
