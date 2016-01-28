package com.zyl.demo.photos.model;

import com.zyl.demo.photos.widget.CategerySelectWidget;
import com.zyl.demo.photos.widget.ImageItemSelectWidget;

import java.io.Serializable;

/**
 * Created by JasonZhao on 16/1/28.
 */
public class ImagelItemSelectorModel implements Serializable {

  private CategerySelectWidget categery;

  private ImageItemSelectWidget sub;

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
}
