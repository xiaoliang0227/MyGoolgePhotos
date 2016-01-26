package com.zyl.demo.photos.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by JasonZhao on 16/1/19.
 */
public class ImageItemModel implements Serializable {

  private long createTime;

  private long modifyTime;

  private Bitmap bitmap;

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public long getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(long modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }
}
