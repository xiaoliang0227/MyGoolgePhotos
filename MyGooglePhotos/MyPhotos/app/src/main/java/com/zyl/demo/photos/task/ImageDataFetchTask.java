package com.zyl.demo.photos.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.zyl.demo.photos.R;
import com.zyl.demo.photos.model.ImageItemModel;
import com.zyl.demo.photos.util.CommonUtil;
import com.zyl.demo.photos.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonZhao on 16/1/19.
 */
public class ImageDataFetchTask extends AsyncTask<Void, Void, List<ImageItemModel>> {

  private static final String TAG = "ImageDataFetchTask";

  private ProgressDialog dialog;

  private Context context;

  private ImageDataFetchTaskCallback callback;

  public ImageDataFetchTask(Context context) {
    this.context = context;
  }

  enum DataType {
    NORMAL,
    DAY,
    YEAR
  }

  public ImageDataFetchTask(Context context, ImageDataFetchTaskCallback callback) {
    this.context = context;
    this.callback = callback;
  }

  private int[] images = {R.drawable.test1,
      R.drawable.test2,
      R.drawable.test3,
      R.drawable.test4,
      R.drawable.test5,
      R.drawable.test6,
      R.drawable.test7,
      R.drawable.test8,
      R.drawable.test9,
      R.drawable.test10,

  };

  /**
   * Runs on the UI thread before {@link #doInBackground}.
   *
   * @see #onPostExecute
   * @see #doInBackground
   */
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    dialog = ProgressDialog.show(context, "", "获取相册图片中");
  }

  /**
   * <p>Runs on the UI thread after {@link #doInBackground}. The
   * specified result is the value returned by {@link #doInBackground}.</p>
   * <p/>
   * <p>This method won't be invoked if the task was cancelled.</p>
   *
   * @param data The result of the operation computed by {@link #doInBackground}.
   * @see #onPreExecute
   * @see #doInBackground
   * @see #onCancelled(Object)
   */
  @Override
  protected void onPostExecute(List<ImageItemModel> data) {
    super.onPostExecute(data);
    if (null != dialog && dialog.isShowing()) {
      dialog.dismiss();
    }
    if (null != callback) {
      callback.showImageData(data);
    }
  }

  /**
   * Override this method to perform a computation on a background thread. The
   * specified parameters are the parameters passed to {@link #execute}
   * by the caller of this task.
   * <p/>
   * This method can call {@link #publishProgress} to publish updates
   * on the UI thread.
   *
   * @param params The parameters of the task.
   * @return A result, defined by the subclass of this task.
   * @see #onPreExecute()
   * @see #onPostExecute
   * @see #publishProgress
   */
  @Override
  protected List<ImageItemModel> doInBackground(Void... params) {
    List<ImageItemModel> data = new ArrayList<>();
    for (int i = 0; i < 35; i++) {
      ImageItemModel model = new ImageItemModel();
      model.setCreateTime((long) (System.currentTimeMillis() - Math.random() * images.length * 24 * 60 * 60 * 1000));
      model.setModifyTime(model.getCreateTime());
      Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), images[(int) (Math.random() * images.length)]);
      Log.d(TAG, String.format("original bitmap,width:%d,height:%d", bitmap.getWidth(), bitmap.getHeight()));
      BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
      model.setBitmapDrawable(drawable);
      model.setBitmap(bitmap);
      setViewData(model, DataType.NORMAL, 4);
      setViewData(model, DataType.DAY, 2);
      setViewData(model, DataType.YEAR, 8);
      data.add(model);
    }
    return data;
  }

  private void setViewData(ImageItemModel model, DataType type, int per) {
    int perWidth = (CommonUtil.getScreenWidth((Activity) context) - per * 4) / per;
    Bitmap bitmap = ImageUtil.zoomBitmap(model.getBitmap(), perWidth, perWidth);
    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
    switch (type) {
      case NORMAL:
        Log.d(TAG, String.format("normalBitmap width:%d, height:%d", bitmap.getWidth() , bitmap.getHeight()));
        model.setNormalBitmap(bitmap);
        model.setNormalDrawable(drawable);
        break;
      case DAY:
        Log.d(TAG, String.format("dayBitmap width:%d, height:%d", bitmap.getWidth() , bitmap.getHeight()));
        model.setDayBitmap(bitmap);
        model.setDayDrawable(drawable);
        break;
      case YEAR:
        Log.d(TAG, String.format("yearBitmap width:%d, height:%d", bitmap.getWidth() , bitmap.getHeight()));
        model.setYearBitmap(bitmap);
        model.setYearDrawable(drawable);
        break;
    }
  }

  public interface ImageDataFetchTaskCallback {

    /**
     * 获取相片数据后,回调处理
     *
     * @param data
     */
    void showImageData(List<ImageItemModel> data);
  }
}
