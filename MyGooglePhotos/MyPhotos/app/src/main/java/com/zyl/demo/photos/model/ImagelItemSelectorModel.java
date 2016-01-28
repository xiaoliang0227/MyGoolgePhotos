package com.zyl.demo.photos.model;

import android.widget.ImageView;

import com.zyl.demo.photos.widget.CategerySelectWidget;
import com.zyl.demo.photos.widget.ImageItemSelectWidget;

import java.io.Serializable;

/**
 * Created by JasonZhao on 16/1/28.
 */
public class ImagelItemSelectorModel implements Serializable {

  private CategerySelectWidget categery;

  private ImageItemSelectWidget sub;

  private ImageView imageView;

  private ImageItemModel model;

  public CategerySelectWidget getCategery() {
    return categery;
  }

  public void setCategery(CategerySelectWidget categery) {
    this.categery = categery;
  }

  public ImageItemSelectWidget getSub() {
    return sub;
  }

  public void setSub(ImageItemSelectWidget sub) {
    this.sub = sub;
  }

  public ImageItemModel getModel() {
    return model;
  }

  public void setModel(ImageItemModel model) {
    this.model = model;
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }
}
