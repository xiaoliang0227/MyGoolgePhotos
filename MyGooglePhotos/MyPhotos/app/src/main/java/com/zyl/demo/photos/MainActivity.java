package com.zyl.demo.photos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyl.demo.photos.comparator.ComparatorUtil;
import com.zyl.demo.photos.enumeration.ViewStatus;
import com.zyl.demo.photos.model.ImageItemModel;
import com.zyl.demo.photos.task.ImageDataFetchTask;
import com.zyl.demo.photos.util.CommonUtil;
import com.zyl.demo.photos.widget.CategerySelectWidget;
import com.zyl.demo.photos.widget.CustomScrollView;
import com.zyl.demo.photos.widget.CustomView;
import com.zyl.demo.photos.widget.ImageItemSelectWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements
    ImageDataFetchTask.ImageDataFetchTaskCallback,
    CustomScrollView.CustomScrollViewScaleChangeListener {

  private static final String TAG = "MainActivity";

  private static final int DEFAULT_MARGIN = 2;

  private ViewStatus status = ViewStatus.STATUS_MONTH;

  private Handler handler;

  private CustomScrollView customScroll;

  private CustomView customView;

  private Map<String, List<ImageItemModel>> imageMap;

  private Toolbar toolbar;

  private boolean selectMode = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
  }

  private void init() {
    initField();
    initTollbar();
    delayFetchData();
  }

  private void initTollbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("相册");
  }

  private void delayFetchData() {
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        fetchData();
      }
    }, 200);
  }

  private void fetchData() {
    ImageDataFetchTask task = new ImageDataFetchTask(this, this);
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  private void initField() {
    handler = new Handler();
    customScroll = (CustomScrollView) findViewById(R.id.custom_scroll);
    customScroll.setCurrentStatus(status);
    customScroll.setCustomScrollViewScaleChangeListener(this);
    customView = (CustomView) findViewById(R.id.custom_view);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
  }

  /**
   * Initialize the contents of the Activity's standard options menu.  You
   * should place your menu items in to <var>menu</var>.
   * <p/>
   * <p>This is only called once, the first time the options menu is
   * displayed.  To update the menu every time it is displayed, see
   * {@link #onPrepareOptionsMenu}.
   * <p/>
   * <p>The default implementation populates the menu with standard system
   * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
   * they will be correctly ordered with application-defined menu items.
   * Deriving classes should always call through to the base implementation.
   * <p/>
   * <p>You can safely hold on to <var>menu</var> (and any items created
   * from it), making modifications to it as desired, until the next
   * time onCreateOptionsMenu() is called.
   * <p/>
   * <p>When you add items to the menu, you can implement the Activity's
   * {@link #onOptionsItemSelected} method to handle them there.
   *
   * @param menu The options menu in which you place your items.
   * @return You must return true for the menu to be displayed;
   * if you return false it will not be shown.
   * @see #onPrepareOptionsMenu
   * @see #onOptionsItemSelected
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
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
    switch (item.getItemId()) {
      case R.id.action_select:
        selectMode = !selectMode;
        displayData(imageMap);
        return true;
      case R.id.action_normal:
        if (!status.equals(ViewStatus.STATUS_MONTH)) {
          status = ViewStatus.STATUS_MONTH;
          displayData(imageMap);
        }
        return true;
      case R.id.action_day:
        if (!status.equals(ViewStatus.STATUS_DAY)) {
          status = ViewStatus.STATUS_DAY;
          displayData(imageMap);
        }
        return true;
      case R.id.action_year:
        if (!status.equals(ViewStatus.STATUS_YEAR)) {
          status = ViewStatus.STATUS_YEAR;
          displayData(imageMap);
        }
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * 获取相片数据后,回调处理
   *
   * @param data
   */
  @Override
  public void showImageData(List<ImageItemModel> data) {
    Log.d(TAG, "data:" + data);
    if (null == data) return;
    imageMap = new HashMap<>();
    for (ImageItemModel model : data) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date(model.getCreateTime()));

      // 当前年
      int year = calendar.get(Calendar.YEAR);
      String yearKey = String.format("year_%d", year);

      // 当前月
      int month = calendar.get(Calendar.MONTH) + 1;
      String monthKey = String.format("month_%d", month);

      // 当前日,某月中的某日
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      String dayKey = String.format("day_%d", day);

      // 整合相同年的数据
      formatData(imageMap, model, yearKey);

      // 整合相同月份的数据
      formatData(imageMap, model, monthKey);

      // 整合相同日的数据
      formatData(imageMap, model, dayKey);
    }

//    Log.d(TAG, "输出整合的数据");
//    Log.d(TAG, "**************************");
//    Set<Map.Entry<String, List<ImageItemModel>>> tmpData = imageMap.entrySet();
//    for (Map.Entry<String, List<ImageItemModel>> item : tmpData) {
//      Log.d(TAG, String.format("************%s**************", item.getKey()));
//      if (null != item.getValue()) {
//        for (ImageItemModel itemModel : item.getValue()) {
//          Log.d(TAG, new SimpleDateFormat("yyyy-MM-dd").format(new Date(itemModel.getCreateTime())));
//        }
//      } else {
//        Log.d(TAG, "没有数据");
//      }
//      Log.d(TAG, String.format("************%s**************", item.getKey()));
//    }

    displayData(imageMap);
  }

  private void displayData(final Map<String, List<ImageItemModel>> data) {
    customScroll.setCurrentStatus(status);
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        switch (status) {
          case STATUS_YEAR:
            // 年视图
            renderYearView(data);
            break;
          case STATUS_MONTH:
            // 月视图
            renderMonthView(data);
            break;
          case STATUS_DAY:
            // 日视图
            renderDayView(data);
            break;
          default:
            // 月视图
            renderMonthView(data);
            break;
        }
      }
    }, 200);
  }

  private void renderYearView(final Map<String, List<ImageItemModel>> data) {
    Log.d(TAG, "render year view");
    if (null == data || data.isEmpty()) return;
    AlphaAnimation aa = new AlphaAnimation(1.0f, 0.5f);
    aa.setDuration(200);
    aa.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        renderYearViewRealAction(data);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    customScroll.startAnimation(aa);
  }

  private void renderYearViewRealAction(Map<String, List<ImageItemModel>> data) {
    customScroll.setAlpha(1.0f);
    // 清空子视图
    if (customView.getChildCount() > 0)
      customView.removeAllViews();

    // 生成可排序的key集合
    Set<Integer> keySet = new TreeSet();
    for (String key : data.keySet()) {
      if (key.indexOf("year_") >= 0) {
        keySet.add(Integer.valueOf(key.substring("year_".length())));
      }
    }

    int per = 8;

    Map<String, Map<String, List<ImageItemModel>>> formattedMap = new HashMap<>();
    // 检查和整理排序后的集合
    for (Integer item : keySet) {
      Log.d(TAG, "key:year_" + item);
      List<ImageItemModel> itemData = data.get("year_" + item);
      if (null == itemData || itemData.isEmpty()) continue;

      // 重新格式化数据,在年的基础之上进行月份分组
      Map<String, List<ImageItemModel>> tmpMap = new HashMap<>();
      for (ImageItemModel model : itemData) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(model.getCreateTime()));
        // 当前月
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthKey = String.format("month_%d", month);
        formatData(tmpMap, model, monthKey);
        formattedMap.put("year_" + item, tmpMap);
      }
      // 年容器
      ViewGroup partViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.view_common_part, customView, false);
      TextView partTitle = (TextView) partViewGroup.findViewById(R.id.part_title);
      LinearLayout partContainer = (LinearLayout) partViewGroup.findViewById(R.id.part_container);
      partTitle.setText(new SimpleDateFormat("yyyy年").format(new Date(itemData.get(0).getCreateTime())));
      partTitle.setVisibility(View.VISIBLE);

      Map<String, List<ImageItemModel>> monthMap = formattedMap.get("year_" + item);
      for (String key : monthMap.keySet()) {
        List<ImageItemModel> value = monthMap.get(key);
        // 创建年条目
        ViewGroup monthItem = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.year_part_line_item, null);
        TextView itemLabel = (TextView) monthItem.findViewById(R.id.item_label);
        LinearLayout itemContainer = (LinearLayout) monthItem.findViewById(R.id.item_container);
        itemLabel.setText(new SimpleDateFormat("MM月").format(new Date(itemData.get(0).getCreateTime())));

        // 计算要显示的行数
        int lines = CommonUtil.getColumns(value.size(), per);
        Log.d(TAG, "lines:" + lines);

        for (int i = 0; i < lines; i++) {
          int start = i * per;
          int end = start + per;
          end = end > value.size() ? value.size() : end;

          // 设置每行
          LinearLayout partLine = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_container_line, null);
          int perWidth = (CommonUtil.getScreenWidth(this) - CommonUtil.getViewWidth(itemLabel) - per * 4) / per;
          for (int j = start; j < end; j++) {
            final ImageItemModel model = value.get(j);
            Bitmap bitmap = model.getBitmap();
            if (null != bitmap) {
              View imageItem = LayoutInflater.from(this).inflate(R.layout.image_item, partLine, false);
              ImageView img = (ImageView) imageItem.findViewById(R.id.img);
              img.setImageBitmap(bitmap);
              LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT);
              params.width = perWidth;
              params.height = perWidth;
              params.leftMargin = DEFAULT_MARGIN;
              params.topMargin = DEFAULT_MARGIN;
              params.rightMargin = DEFAULT_MARGIN;
              params.bottomMargin = DEFAULT_MARGIN;
              imageItem.setLayoutParams(params);
              imageItem.setOnClickListener(new View.OnClickListener() {

                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                  jumpToImageDetailPage(model);
                }
              });
              partLine.addView(imageItem);
            }
          }
          itemContainer.addView(partLine);
        }

        partContainer.addView(monthItem);
      }
      customView.addView(partViewGroup);
    }
  }

  private void renderDayView(final Map<String, List<ImageItemModel>> data) {
    Log.d(TAG, "render day view");
    if (null == data || data.isEmpty()) return;

    AlphaAnimation aa = new AlphaAnimation(1.0f, 0.5f);
    aa.setDuration(200);
    aa.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        renderDayViewRealAction(data);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    customScroll.startAnimation(aa);
  }

  private void renderDayViewRealAction(Map<String, List<ImageItemModel>> data) {
    customScroll.setAlpha(1.0f);
    // 清空子视图
    if (customView.getChildCount() > 0)
      customView.removeAllViews();
    // 找出所有日的分类
    // 生成可排序的key集合
    Set<Integer> keySet = new TreeSet();
    for (String key : data.keySet()) {
      if (key.indexOf("day_") >= 0) {
        keySet.add(Integer.valueOf(key.substring("day_".length())));
      }
    }

    int per = 2;
    // 日容器
    ViewGroup partViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.view_common_part, customView, false);
    LinearLayout partContainer = (LinearLayout) partViewGroup.findViewById(R.id.part_container);

    // 检查排序后的集合
    for (Integer item : keySet) {
      Log.d(TAG, "key:day_" + item);
      List<ImageItemModel> itemData = data.get("day_" + item);
      if (null == itemData || itemData.isEmpty()) continue;

      // 创建日条目
      ViewGroup monthItem = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.normal_part_line_item, null);
      final CategerySelectWidget partBtnSelect = (CategerySelectWidget) monthItem.findViewById(R.id.btn_select);
      partBtnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
      TextView itemLabel = (TextView) monthItem.findViewById(R.id.item_label);
      LinearLayout itemContainer = (LinearLayout) monthItem.findViewById(R.id.item_container);
      itemLabel.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date(itemData.get(0).getCreateTime())));

      // 计算要显示的行数
      int lines = CommonUtil.getColumns(itemData.size(), per);
      Log.d(TAG, "lines:" + lines);

      for (int i = 0; i < lines; i++) {
        int start = i * per;
        int end = start + per;
        end = end > itemData.size() ? itemData.size() : end;

        // 设置每行
        LinearLayout partLine = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_container_line, null);
        int perWidth = (CommonUtil.getScreenWidth(this) - per * 4) / per;
        for (int j = start; j < end; j++) {
          final ImageItemModel model = itemData.get(j);
          Bitmap bitmap = model.getBitmap();
          if (null != bitmap) {
            View imageItem = LayoutInflater.from(this).inflate(R.layout.image_item, partLine, false);
            final ImageItemSelectWidget btnSelect = (ImageItemSelectWidget) imageItem.findViewById(R.id.btn_select);
            btnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
            ImageView img = (ImageView) imageItem.findViewById(R.id.img);
            img.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = perWidth;
            params.height = perWidth;
            params.leftMargin = DEFAULT_MARGIN;
            params.topMargin = DEFAULT_MARGIN;
            params.rightMargin = DEFAULT_MARGIN;
            params.bottomMargin = DEFAULT_MARGIN;
            imageItem.setLayoutParams(params);
            imageItem.setOnClickListener(new View.OnClickListener() {

              /**
               * Called when a view has been clicked.
               *
               * @param v The view that was clicked.
               */
              @Override
              public void onClick(View v) {
                if (selectMode) {
                  exchangeButtonSelectState(btnSelect);
                } else {
                  jumpToImageDetailPage(model);
                }
              }
            });
            partLine.addView(imageItem);
          }
        }
        itemContainer.addView(partLine);
      }

      // 将月份条目添加到日容器
      partContainer.addView(monthItem);
    }
    customView.addView(partViewGroup);
  }

  private void exchangeButtonSelectState(ImageItemSelectWidget btnSelect) {
    btnSelect.setChecked(!btnSelect.isChecked());
  }

  private void renderMonthView(final Map<String, List<ImageItemModel>> data) {
    Log.d(TAG, "render month view");
    if (null == data || data.isEmpty()) return;
    AlphaAnimation aa = new AlphaAnimation(1.0f, 0.5f);
    aa.setDuration(200);
    aa.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        Log.d(TAG, "render month view animation end");
        renderMonthViewRealAction(data);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    customScroll.startAnimation(aa);
  }

  private void renderMonthViewRealAction(Map<String, List<ImageItemModel>> data) {
    customScroll.setAlpha(1.0f);
    // 清空子视图
    if (customView.getChildCount() > 0)
      customView.removeAllViews();
    // 找出所有月份的分类
    // 生成可排序的key集合
    Set<Integer> keySet = new TreeSet();
    for (String key : data.keySet()) {
      if (key.indexOf("month_") >= 0) {
        keySet.add(Integer.valueOf(key.substring("month_".length())));
      }
    }

    int per = 4;
    // 月份容器
    ViewGroup partViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.view_common_part, customView, false);
    LinearLayout partContainer = (LinearLayout) partViewGroup.findViewById(R.id.part_container);

    // 检查排序后的集合
    for (Integer item : keySet) {
      Log.d(TAG, "key:month_" + item);
      final List<ImageItemModel> itemData = data.get("month_" + item);
      if (null == itemData || itemData.isEmpty()) continue;

      // 创建月份条目
      ViewGroup monthItem = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.normal_part_line_item, null);
      final CategerySelectWidget partBtnSelect = (CategerySelectWidget) monthItem.findViewById(R.id.btn_select);
      partBtnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
      TextView itemLabel = (TextView) monthItem.findViewById(R.id.item_label);
      LinearLayout itemContainer = (LinearLayout) monthItem.findViewById(R.id.item_container);
      itemLabel.setText(new SimpleDateFormat("yyyy年MM月").format(new Date(itemData.get(0).getCreateTime())));

      // 计算要显示的行数
      int lines = CommonUtil.getColumns(itemData.size(), per);
      Log.d(TAG, "lines:" + lines);

      for (int i = 0; i < lines; i++) {
        int start = i * per;
        int end = start + per;
        end = end > itemData.size() ? itemData.size() : end;

        // 设置每行
        LinearLayout partLine = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_container_line, null);
        int perWidth = (CommonUtil.getScreenWidth(this) - per * 4) / per;
        for (int j = start; j < end; j++) {
          final ImageItemModel model = itemData.get(j);
          Bitmap bitmap = model.getBitmap();
          if (null != bitmap) {
            View imageItem = LayoutInflater.from(this).inflate(R.layout.image_item, partLine, false);
            final ImageItemSelectWidget btnSelect = (ImageItemSelectWidget) imageItem.findViewById(R.id.btn_select);
            btnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
            btnSelect.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                exchangeButtonSelectState(btnSelect);
              }
            });
            ImageView img = (ImageView) imageItem.findViewById(R.id.img);
            img.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = perWidth;
            params.height = perWidth;
            params.leftMargin = DEFAULT_MARGIN;
            params.topMargin = DEFAULT_MARGIN;
            params.rightMargin = DEFAULT_MARGIN;
            params.bottomMargin = DEFAULT_MARGIN;
            imageItem.setLayoutParams(params);
            imageItem.setOnClickListener(new View.OnClickListener() {

              /**
               * Called when a view has been clicked.
               *
               * @param v The view that was clicked.
               */
              @Override
              public void onClick(View v) {
                if (selectMode) {
                  exchangeButtonSelectState(btnSelect);
                } else {
                  jumpToImageDetailPage(model);
                }
              }
            });
            partLine.addView(imageItem);
          }
        }
        itemContainer.addView(partLine);
      }

      // 将月份条目添加到月份容器
      partContainer.addView(monthItem);
    }
    customView.addView(partViewGroup);
  }

  private void jumpToImageDetailPage(ImageItemModel model) {
    Intent intent = new Intent(this, ImageDetailActivity.class);
    intent.putExtra("data", model.getBitmap());
    startActivity(intent);
  }

  private void formatData(Map<String, List<ImageItemModel>> map, ImageItemModel model, String key) {
    if (map.get(key) == null) {
      List<ImageItemModel> data = new ArrayList<>();
      data.add(model);
      map.put(key, data);
    } else {
      map.get(key).add(model);
    }
    Collections.sort(map.get(key), new ComparatorUtil());
  }

  @Override
  public void renderViewByScale(ViewStatus status) {
    if (!status.equals(this.status)) {
      this.status = status;
      displayData(imageMap);
    }
  }
}
