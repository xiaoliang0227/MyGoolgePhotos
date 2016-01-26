package com.zyl.demo.photos.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.zyl.demo.photos.R;
import com.zyl.demo.photos.model.ImageItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonZhao on 16/1/19.
 */
public class ImageDataFetchTask extends AsyncTask<Void, Void, List<ImageItemModel>> {

  private ProgressDialog dialog;

  private Context context;

  private ImageDataFetchTaskCallback callback;

  public ImageDataFetchTask(Context context) {
    this.context = context;
  }

  public ImageDataFetchTask(Context context, ImageDataFetchTaskCallback callback) {
    this.context = context;
    this.callback = callback;
  }

  private int[] images = {R.mipmap.btn_sys_status_active,
      R.mipmap.btn_photos_active, R.mipmap.btn_diskstore_active, R.mipmap.btn_application_active};

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
      model.setCreateTime((long) (System.currentTimeMillis() - Math.random() * 5 * 24 * 60 * 60 * 1000));
      model.setModifyTime(model.getCreateTime());
      Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), images[(int) (Math.random() * 4)]);
      model.setBitmap(bitmap);
      data.add(model);
    }
    return data;
  }

  public interface ImageDataFetchTaskCallback {

    /**
     * 获取相片数据后,回调处理
     * @param data
     */
    void showImageData(List<ImageItemModel> data);
  }
}
