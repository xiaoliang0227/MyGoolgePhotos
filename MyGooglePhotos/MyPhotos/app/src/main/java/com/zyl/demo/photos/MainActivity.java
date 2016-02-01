package com.zyl.demo.photos;

import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyl.demo.photos.comparator.DescendComparatorUtil;
import com.zyl.demo.photos.enumeration.ViewStatus;
import com.zyl.demo.photos.model.ImageItemModel;
import com.zyl.demo.photos.model.ImagelItemSelectorModel;
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

  private List<ImageItemModel> selectedList = new ArrayList<>();

  private Map<String, List<ImagelItemSelectorModel>> selectMap = new HashMap<>();

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
        doActionSelect();
        return true;
      case R.id.action_normal:
        doActionNormal();
        return true;
      case R.id.action_day:
        doActionDay();
        return true;
      case R.id.action_year:
        doActionYear();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Prepare the Screen's standard options menu to be displayed.  This is
   * called right before the menu is shown, every time it is shown.  You can
   * use this method to efficiently enable/disable items or otherwise
   * dynamically modify the contents.
   * <p/>
   * <p>The default implementation updates the system menu items based on the
   * activity's state.  Deriving classes should always call through to the
   * base class implementation.
   *
   * @param menu The options menu as last shown or first initialized by
   *             onCreateOptionsMenu().
   * @return You must return true for the menu to be displayed;
   * if you return false it will not be shown.
   * @see #onCreateOptionsMenu
   */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    Log.d(TAG, "onPrepareOptionsMenu");
    dynamicExchangeMenu(menu);
    return super.onPrepareOptionsMenu(menu);
  }

  private void dynamicExchangeMenu(Menu menu) {
    menu.clear();
    switch (status) {
      case STATUS_YEAR:
        menu.add(Menu.NONE, R.id.action_normal, Menu.FIRST, "标准视图");
        menu.add(Menu.NONE, R.id.action_day, Menu.FIRST + 1, "日视图");
        break;
      case STATUS_MONTH:
        menu.add(Menu.NONE, R.id.action_select, Menu.FIRST, selectMode ? "取消选择..." : "选择...");
        menu.add(Menu.NONE, R.id.action_day, Menu.FIRST + 1, "日视图");
        menu.add(Menu.NONE, R.id.action_year, Menu.FIRST + 2, "年视图");
        break;
      case STATUS_DAY:
        menu.add(Menu.NONE, R.id.action_select, Menu.FIRST, selectMode ? "取消选择..." : "选择...");
        menu.add(Menu.NONE, R.id.action_normal, Menu.FIRST + 1, "标准视图");
        menu.add(Menu.NONE, R.id.action_year, Menu.FIRST + 2, "年视图");
        break;
    }
  }

  private void doActionYear() {
    if (!status.equals(ViewStatus.STATUS_YEAR)) {
      status = ViewStatus.STATUS_YEAR;
      displayData(imageMap);
    }
  }

  private void doActionDay() {
    if (!status.equals(ViewStatus.STATUS_DAY)) {
      status = ViewStatus.STATUS_DAY;
      displayData(imageMap);
    }
  }

  private void doActionNormal() {
    if (!status.equals(ViewStatus.STATUS_MONTH)) {
      status = ViewStatus.STATUS_MONTH;
      displayData(imageMap);
    }
  }

  private void doActionSelect() {
    selectedList.clear();
    selectMode = !selectMode;
    displayData(imageMap);
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
      int day = calendar.get(Calendar.DAY_OF_YEAR);
      String dayKey = String.format("day_%d", day);

      // 整合相同年的数据
      formatData(imageMap, model, yearKey);

      // 整合相同月份的数据
      formatData(imageMap, model, monthKey);

      // 整合相同日的数据
      formatData(imageMap, model, dayKey);
    }
    displayData(imageMap);
  }

  private void displayData(final Map<String, List<ImageItemModel>> data) {
    selectedList.clear();
    customScroll.setCurrentStatus(status);
    customScroll.setSelectMode(selectMode);
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

  private void renderYearViewRealAction(Map<String, List<ImageItemModel>> data) {
    customScroll.setAlpha(1.0f);
    clearView();

    // 生成可排序的key集合
    Set<Integer> keySet = new TreeSet(new DescendComparatorUtil());
    for (String key : data.keySet()) {
      if (key.indexOf("year_") >= 0) {
        keySet.add(Integer.valueOf(key.substring("year_".length())));
      }
    }

    int per = 8;

    Map<String, Map<String, List<ImageItemModel>>> formattedMap = new HashMap<>();
    // 检查和整理排序后的集合
    for (Integer item : keySet) {
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
        itemLabel.setText(new SimpleDateFormat("MM月").format(new Date(value.get(0).getCreateTime())));

        setContainerContent(itemContainer, itemLabel, value, per, null, null);

        partContainer.addView(monthItem);
      }
      customView.addView(partViewGroup);
    }
  }

  private void clearView() {
    // 清空子视图
    if (customView.getChildCount() > 0)
      customView.removeAllViews();
  }

  private void renderDayViewRealAction(Map<String, List<ImageItemModel>> data) {
    customScroll.setAlpha(1.0f);
    customScroll.setSelectMode(selectMode);
    // 清空子视图
    if (customView.getChildCount() > 0)
      customView.removeAllViews();
    // 找出所有日的分类
    // 生成可排序的key集合
    Set<Integer> keySet = new TreeSet(new DescendComparatorUtil());
    for (String key : data.keySet()) {
      if (key.indexOf("day_") >= 0) {
        keySet.add(Integer.valueOf(key.substring("day_".length())));
      }
    }

    int per = 2;
    // 日容器
    ViewGroup partViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.view_common_part, customView, false);
    LinearLayout partContainer = (LinearLayout) partViewGroup.findViewById(R.id.part_container);

    // 管理选中按钮
    selectMap.clear();

    // 检查排序后的集合
    for (Integer item : keySet) {
      final String key = "day_" + item;
      List<ImageItemModel> itemData = data.get(key);
      if (null == itemData || itemData.isEmpty()) continue;

      // 创建日条目
      ViewGroup monthItem = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.normal_part_line_item, null);
      final CategerySelectWidget partBtnSelect = (CategerySelectWidget) monthItem.findViewById(R.id.btn_select);
      partBtnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
      TextView itemLabel = (TextView) monthItem.findViewById(R.id.item_label);
      LinearLayout itemContainer = (LinearLayout) monthItem.findViewById(R.id.item_container);
      itemLabel.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date(itemData.get(0).getCreateTime())));
      setContainerContent(itemContainer, null, itemData, per, partBtnSelect, key);

      // 添加category选项框监听时间
      partBtnSelect.setStateChangeListener(new CategerySelectWidget.OnStateChangeListener() {
        @Override
        public void onStateChangeListener(boolean isChecked) {
          List<ImagelItemSelectorModel> selectorModels = selectMap.get(key);
          if (null != selectorModels && !selectorModels.isEmpty()) {
            for (ImagelItemSelectorModel item : selectorModels) {
              item.getSub().setChecked(isChecked);
              item.getImageView().setAlpha(isChecked ? 0.5f : 1.0f);

              if (isChecked) {
                partBtnSelect.getSelectedList().add(item.getModel());
                selectedList.add(item.getModel());
              } else {
                partBtnSelect.getSelectedList().remove(item.getModel());
                selectedList.remove(item.getModel());
              }
            }
          }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
      });

      // 将月份条目添加到日容器
      partContainer.addView(monthItem);
    }
    customView.addView(partViewGroup);
  }

  private void renderMonthViewRealAction(Map<String, List<ImageItemModel>> data) {
    customScroll.setAlpha(1.0f);
    // 清空子视图
    if (customView.getChildCount() > 0)
      customView.removeAllViews();
    // 找出所有月份的分类
    // 生成可排序的key集合
    Set<Integer> keySet = new TreeSet(new DescendComparatorUtil());
    for (String key : data.keySet()) {
      if (key.indexOf("month_") >= 0) {
        keySet.add(Integer.valueOf(key.substring("month_".length())));
      }
    }

    int per = 4;
    // 月份容器
    ViewGroup partViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.view_common_part, customView, false);
    LinearLayout partContainer = (LinearLayout) partViewGroup.findViewById(R.id.part_container);

    // 管理选中按钮
    selectMap.clear();

    // 检查排序后的集合
    for (Integer item : keySet) {
      final String key = "month_" + item;
      final List<ImageItemModel> itemData = data.get(key);
      if (null == itemData || itemData.isEmpty()) continue;

      // 创建月份条目
      ViewGroup monthItem = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.normal_part_line_item, null);
      final CategerySelectWidget partBtnSelect = (CategerySelectWidget) monthItem.findViewById(R.id.btn_select);
      partBtnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
      TextView itemLabel = (TextView) monthItem.findViewById(R.id.item_label);
      LinearLayout itemContainer = (LinearLayout) monthItem.findViewById(R.id.item_container);
      itemLabel.setText(new SimpleDateFormat("yyyy年MM月").format(new Date(itemData.get(0).getCreateTime())));

      setContainerContent(itemContainer, null, itemData, per, partBtnSelect, key);

      // 将月份条目添加到月份容器
      partContainer.addView(monthItem);

      // 添加category选项框监听时间
      partBtnSelect.setStateChangeListener(new CategerySelectWidget.OnStateChangeListener() {
        @Override
        public void onStateChangeListener(boolean isChecked) {
          List<ImagelItemSelectorModel> selectorModels = selectMap.get(key);
          if (null != selectorModels && !selectorModels.isEmpty()) {
            for (ImagelItemSelectorModel item : selectorModels) {
              item.getSub().setChecked(isChecked);
              item.getImageView().setAlpha(isChecked ? 0.5f : 1.0f);
              if (isChecked) {
                partBtnSelect.getSelectedList().add(item.getModel());
                selectedList.add(item.getModel());
              } else {
                partBtnSelect.getSelectedList().remove(item.getModel());
                selectedList.remove(item.getModel());
              }
            }
          }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
      });
    }
    customView.addView(partViewGroup);
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

  private void setContainerContent(LinearLayout itemContainer, TextView itemLabel, List<ImageItemModel> itemData,
                                   int per, final CategerySelectWidget partBtnSelect, final String key) {
    // 计算要显示的行数
    int lines = CommonUtil.getColumns(itemData.size(), per);
    List<ImagelItemSelectorModel> imageSelectList = new ArrayList<>();
    for (int i = 0; i < lines; i++) {
      int start = i * per;
      int end = start + per;
      end = end > itemData.size() ? itemData.size() : end;

      // 设置每行
      LinearLayout partLine = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_container_line, null);
      int perWidth = 0;
      switch (status) {
        case STATUS_MONTH:
        case STATUS_DAY:
          perWidth = (CommonUtil.getScreenWidth(this) - per * 4) / per;
          break;
        case STATUS_YEAR:
          perWidth = (CommonUtil.getScreenWidth(this) - CommonUtil.getViewWidth(itemLabel) - per * 4) / per;
          break;
      }
      for (int j = start; j < end; j++) {
        final ImageItemModel model = itemData.get(j);
        Drawable drawable = model.getBitmapDrawable();
        if (null != drawable) {
          View imageItem = LayoutInflater.from(this).inflate(R.layout.image_item, partLine, false);
          final ImageItemSelectWidget btnSelect = (ImageItemSelectWidget) imageItem.findViewById(R.id.btn_select);
          final ImageView img = (ImageView) imageItem.findViewById(R.id.img);
          setImageData(img, model);

          if (!status.equals(ViewStatus.STATUS_YEAR)) {
            btnSelect.setVisibility(selectMode ? View.VISIBLE : View.GONE);
          }

          if (null != selectMap) {
            // 选择联动,如果选中子项,则父项应该相应被选中.取消选择子项,如果子项全被取消选中,则取消选中父项
            btnSelect.setStateChangeListener(null);
            btnSelect.setStateChangeListener(new ImageItemSelectWidget.OnStateChangeListener() {
              @Override
              public void onStateChangeListener(boolean isChecked) {
                selectStatusChange(img, isChecked, model, partBtnSelect);
              }

              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

              }
            });

            ImagelItemSelectorModel selectorModel = new ImagelItemSelectorModel();
            selectorModel.setCategery(partBtnSelect);
            selectorModel.setSub(btnSelect);
            selectorModel.setModel(model);
            selectorModel.setImageView(img);
            imageSelectList.add(selectorModel);
          }

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
              if (selectMode && !status.equals(ViewStatus.STATUS_YEAR)) {
                exchangeButtonSelectState(img, btnSelect, model, partBtnSelect);
              } else {
                if (!customScroll.isLock()) {
                  jumpToImageDetailPage(model);
                }
              }
            }
          });
          partLine.addView(imageItem);
        }
      }
      itemContainer.addView(partLine);
    }
    if (null != partBtnSelect) {
      selectMap.put(key, imageSelectList);
    }
  }

  private void selectStatusChange(ImageView img, boolean isChecked, ImageItemModel model, CategerySelectWidget partBtnSelect) {
    if (isChecked) {
      img.setAlpha(0.5f);
      partBtnSelect.getSelectedList().add(model);
      selectedList.add(model);
    } else {
      img.setAlpha(1.0f);
      partBtnSelect.getSelectedList().remove(model);
      selectedList.remove(model);
    }
    partBtnSelect.setChecked(!partBtnSelect.getSelectedList().isEmpty());
  }

  private void setImageData(ImageView img, ImageItemModel model) {
    switch (status) {
      case STATUS_MONTH:
        img.setImageDrawable(model.getNormalDrawable());
        break;
      case STATUS_DAY:
        img.setImageDrawable(model.getDayDrawable());
        break;
      case STATUS_YEAR:
        img.setImageDrawable(model.getYearDrawable());
        break;
    }
  }

  private void exchangeButtonSelectState(ImageView img, ImageItemSelectWidget btnSelect, ImageItemModel model, CategerySelectWidget partBtnSelect) {
    btnSelect.setChecked(!btnSelect.isChecked());
    selectStatusChange(img, btnSelect.isChecked(), model, partBtnSelect);
  }

  private void jumpToImageDetailPage(ImageItemModel model) {
    MyGooglePhotosApplication application = (MyGooglePhotosApplication) getApplication();
    application.setCurrentBitmap(model.getBitmap());
    // TODO (remove this tmp code)
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(MainActivity.this, ImageDetailActivity.class);
        startActivity(intent);
      }
    }, 300);
  }

  private void formatData(Map<String, List<ImageItemModel>> map, ImageItemModel model, String key) {
    if (map.get(key) == null) {
      List<ImageItemModel> data = new ArrayList<>();
      data.add(model);
      map.put(key, data);
    } else {
      map.get(key).add(model);
    }
    Collections.sort(map.get(key), new DescendComparatorUtil());
  }

  @Override
  public void renderViewByScale(ViewStatus status) {
    if (!status.equals(this.status)) {
      this.status = status;
      displayData(imageMap);
    }
  }

  @Override
  public void longPressMoveSelect(PointF primaryP, MotionEvent event) {
    if (null == selectMap || selectMap.isEmpty() || status.equals(ViewStatus.STATUS_YEAR)) return;
    // 从左向右滑动时触发
    if (event.getX() > primaryP.x) {
      Set<String> keySet = selectMap.keySet();
      for (String key : keySet) {
        List<ImagelItemSelectorModel> selectorModels = selectMap.get(key);
        for (ImagelItemSelectorModel item : selectorModels) {
          ImageView imageView = item.getImageView();
          int m_h = CommonUtil.getViewHeight(imageView);
          int m_w = CommonUtil.getViewWidth(imageView);
          int[] location = new int[2];
          imageView.getLocationInWindow(location);
          int m_x = location[0];
          int m_y = location[1];
          if (m_y < event.getRawY() && event.getRawY() > primaryP.y) {
            boolean flag = true;
            flag &= (m_x - primaryP.x >= 0 || (primaryP.x - m_x > 0 && primaryP.x - m_x < m_w)) &&
                event.getRawX() - m_x >= 0 &&
                m_y - primaryP.y >= 0 &&
                (event.getRawY() - m_y >= 0 && event.getRawY() - m_y <= m_h);
            if (flag) {
              item.getCategery().setChecked(true);
              item.getSub().setChecked(true);
              item.getImageView().setAlpha(0.5f);
            }
          } else {
            break;
          }
        }
      }
    }
  }
}
