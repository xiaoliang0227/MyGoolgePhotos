package com.zyl.demo.photos.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by JasonZhao on 16/1/19.
 */
public class ImageItemModel implements Serializable {

  private long createTime;

  private long modifyTime;

  /**
   * 根据不同的情况生成不同的图片缩略图,以减少内存耗用
   * 原始图片:bitmap,bitmapDrawable
   * 标准视图:normalBitmap,normalDrawable
   * 日视图:dayBitmap,dayDrawable
   * 年视图:yearBitmap,yearDrawable
   * 模糊标准视图(选中):mohuNormalBitmap,mohuNormalDrawable
   * 模糊日视图(选中):mohuDayBitmap,mohuDayDrawable
   */

  private Bitmap bitmap;

  private Drawable bitmapDrawable;

  private Bitmap normalBitmap;

  private Drawable normalDrawable;

  private Bitmap dayBitmap;

  private Drawable dayDrawable;

  private Bitmap yearBitmap;

  private Drawable yearDrawable;

  private Bitmap mohuNormalBitmap;

  private Drawable mohuNormalDrawable;

  private Bitmap mohuDayBitmap;

  private Drawable mohuDayDrawable;


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

  public Drawable getBitmapDrawable() {
    return bitmapDrawable;
  }

  public void setBitmapDrawable(Drawable bitmapDrawable) {
    this.bitmapDrawable = bitmapDrawable;
  }

  public Bitmap getNormalBitmap() {
    return normalBitmap;
  }

  public void setNormalBitmap(Bitmap normalBitmap) {
    this.normalBitmap = normalBitmap;
  }

  public Drawable getNormalDrawable() {
    return normalDrawable;
  }

  public void setNormalDrawable(Drawable normalDrawable) {
    this.normalDrawable = normalDrawable;
  }

  public Bitmap getDayBitmap() {
    return dayBitmap;
  }

  public void setDayBitmap(Bitmap dayBitmap) {
    this.dayBitmap = dayBitmap;
  }

  public Drawable getDayDrawable() {
    return dayDrawable;
  }

  public void setDayDrawable(Drawable dayDrawable) {
    this.dayDrawable = dayDrawable;
  }

  public Bitmap getYearBitmap() {
    return yearBitmap;
  }

  public void setYearBitmap(Bitmap yearBitmap) {
    this.yearBitmap = yearBitmap;
  }

  public Drawable getYearDrawable() {
    return yearDrawable;
  }

  public void setYearDrawable(Drawable yearDrawable) {
    this.yearDrawable = yearDrawable;
  }

  public Bitmap getMohuNormalBitmap() {
    return mohuNormalBitmap;
  }

  public void setMohuNormalBitmap(Bitmap mohuNormalBitmap) {
    this.mohuNormalBitmap = mohuNormalBitmap;
  }

  public Drawable getMohuNormalDrawable() {
    return mohuNormalDrawable;
  }

  public void setMohuNormalDrawable(Drawable mohuNormalDrawable) {
    this.mohuNormalDrawable = mohuNormalDrawable;
  }

  public Bitmap getMohuDayBitmap() {
    return mohuDayBitmap;
  }

  public void setMohuDayBitmap(Bitmap mohuDayBitmap) {
    this.mohuDayBitmap = mohuDayBitmap;
  }

  public Drawable getMohuDayDrawable() {
    return mohuDayDrawable;
  }

  public void setMohuDayDrawable(Drawable mohuDayDrawable) {
    this.mohuDayDrawable = mohuDayDrawable;
  }
}
