package com.example.slideup;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.slideup.adapter.PopUpTabAdapter;
import com.example.slideup.adapter.holder.BaseHolder;
import com.example.slideup.helper.DefaultItemTouchHelpCallback;
import com.example.slideup.helper.DefaultItemTouchHelper;
import com.example.slideup.model.TabModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by ZD on 2017/8/16.
 * 首页tab拖动
 */
public class HomeTabPopUpWindow<T extends TabModel> extends BasePopupWindow implements OnClickListener {

    private RecyclerView mXrFocusList;
    private RecyclerView mXrAllList;
    private List<T> mAllTab;
    private List<T> mFocusTab;
    private PopUpTabAdapter mFocusAdapter;
    private PopUpTabAdapter mAllAdapter;
    private DefaultItemTouchHelper helper;
    private Callback mPopUpCallback;
    private ImageView ivClose;
    private CheckBox cbEdit;
    //容器
    private RelativeLayout relativeLayout;
    //记录拖拽的点
    private int[] target;
    private int tabMargin = 0;//属性动画实现
    private ValueAnimator valueAnimator;
    private PathMeasure mPathMeasure;
    //记录从起始点->目标点的坐标
    private float[] mCurrentPosition = new float[2];
    //父容器的坐标 作为点击的point和目标point的 相对值
    private int parentLoc[] = new int[2];
    //起始坐标
    private int startLoc[] = new int[2];
    //最终移动坐标
    private int endLoc[] = new int[2];
    //用于在父容器实现移动效果的view
    private View startView;
    //记录最后需要移动到哪个view的旁边
    private View endView;
    //记录移动的坐标
    private Path path;
    //位移的时间
    private int duration = 300;
//    ImageView canMoveView;

    private String FOCUSTAB = "focusTab";
    private String ALLTAB = "allTab";

    public HomeTabPopUpWindow(Context context) {
        super(context);

    }

    public void setBindData(List<T> tab, List<T> mFocusTab) {
        this.mAllTab = tab;
        this.mFocusTab = mFocusTab;
        target = new int[]{-1, -1};
        path = new Path();
        mPathMeasure = new PathMeasure();
        init();
    }

    public void update() {
        if (mAllAdapter != null)
            mAllAdapter.notifyDataSetChanged();
        if (mFocusAdapter != null)
            mFocusAdapter.notifyDataSetChanged();
    }

    private void init() {
        setContentView(R.layout.pop_home_tab);
        setAnimationStyle(R.style.AnimationUp);
        if (mView != null)
            mView.setAllowScroll(true);
        mXrFocusList = findViewById(R.id.xrFocusList);
        mXrAllList = findViewById(R.id.xrAllList);
        relativeLayout = findViewById(R.id.tbs_ll);
        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        cbEdit = findViewById(R.id.cb_edit);
        cbEdit.setOnClickListener(this);

        mFocusAdapter = new PopUpTabAdapter(getContext(), mFocusTab, FOCUSTAB);
        mFocusAdapter.setCallback(mAdapterCallback);
        mAllAdapter = new PopUpTabAdapter(getContext(), mAllTab, ALLTAB);
        mAllAdapter.setCallback(mAdapterCallback);

        //与recycleview绑定
        helper = new DefaultItemTouchHelper(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        helper.attachToRecyclerView(mXrFocusList);

        mXrFocusList.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mXrFocusList.setAdapter(mFocusAdapter);

        mXrAllList.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mXrAllList.setAdapter(mAllAdapter);

        tabMargin = ScreenUtil.dip2px(getContext(), 10);

    }

    public void refreshData() {
        if (mFocusAdapter != null)
            mFocusAdapter.notifyDataSetChanged();
        if (mAllAdapter != null)
            mAllAdapter.notifyDataSetChanged();
    }

    @Override
    protected boolean allowBackgroundTranslucent() {
        return false;
    }

    @Override
    protected void initPopWindowFinish() {
        super.initPopWindowFinish();
        getScrollView().setListener(new SlideUpView.ScrollListener() {
            @Override
            public void scroll(float start, float end, float top, int height) {
                //255 不透明
                int alpha = 255 * (height - (int) end) / height;
                if (alpha > 180)
                    alpha = 180;
                setAlpha(alpha);
                if (alpha == 0)
                    dismiss();
            }
        });
    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
//             滑动删除的时候，从数据源移除，并刷新这个Item。
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            //最后一次不允许移动
            if (mFocusTab != null && targetPosition != 0) {
                if (target[0] < 0)
                    target[0] = srcPosition;
                target[1] = targetPosition;
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mFocusAdapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }

        @Override
        public void onMoveComplete(RecyclerView.ViewHolder viewHolder) {
            //交换tab位置
            if (target[0] > -1 && target[1] > -1) {
                // 更换数据源中的数据Item的位置
                Collections.swap(mFocusTab, target[0], target[1]);
                //通知首页更新tab数据
                if (mPopUpCallback != null)
                    mPopUpCallback.swap(target);
                //清空记录的位置
                target = new int[]{-1, -1};
            }

            mFocusAdapter.notifyDataSetChanged();
        }
    };

    public void setCallback(Callback callback) {
        mPopUpCallback = callback;
    }

    //adapter回调
    private Callback mAdapterCallback = new Callback() {
        //        int p[] = new int[2];
        @Override
        public void callback(Object obj) {
            super.callback(obj);
            BaseHolder holder = (BaseHolder) obj;
            if (holder.getItemIndex() > 0)
                helper.startDrag(holder);
//            if(canMoveView == null) {
//                View childAt = holder.itemView;
//                childAt.setDrawingCacheEnabled(true);
//                childAt.buildDrawingCache(true);
//                final Bitmap
//                        localBitmap = Bitmap.createBitmap(childAt.getDrawingCache());
//                childAt.setDrawingCacheEnabled(false);
//
//                canMoveView = new ImageView(getContext());
//                canMoveView.setImageBitmap(localBitmap);
//                canMoveView.setLayoutParams(new LinearLayout.LayoutParams(childAt.getWidth(),childAt.getHeight()));
//                linearLayout.addView(canMoveView);
//            }
//            canMoveView.getLocationInWindow(p);
//            Log.e("tag", p[0] + "@" + p[1]);
        }

        @Override
        public void onItemClick(int position, View v) {
            super.onItemClick(position, v);
            if (FOCUSTAB.equals(v.getTag().toString())) {
                dismiss();
                Toast.makeText(getContext(), "->" + v.getTag().toString(), Toast.LENGTH_SHORT).show();
                if (mPopUpCallback != null)
                    mPopUpCallback.onItemClick(position);
            } else {
                moveView(v, position, v.getTag().toString());
            }
        }

        @Override
        public void remove(int position, View v) {
            super.remove(position, v);
            moveView(v, position, v.getTag().toString());
        }
    };

    //点击alltab区域则代表添加关注的tab
    private boolean isClickAllTab(String tag) {
        return ALLTAB.equals(tag);
    }

    private void moveView(final View view, final int position, final String tag) {
        if (valueAnimator != null && valueAnimator.isRunning()) return;
        relativeLayout.getLocationInWindow(parentLoc);
        view.getLocationInWindow(startLoc);

        startView = view;
        startView.getLayoutParams().height = view.getHeight();
        startView.getLayoutParams().width = view.getWidth();
        if (isClickAllTab(tag))
            mXrAllList.removeView(view);
        else
            mXrFocusList.removeView(view);
        relativeLayout.addView(startView);

        float toX, toY;
        //进行判断
        int i = mFocusTab.size();
        if (!isClickAllTab(tag)) {
            i = mAllTab.size();
        }
        if (i == 0) {
            toX = 0;//view.getWidth();
            toY = 0;//view.getHeight();
        } else if (i % 4 == 0) {
            endView = mXrFocusList.getChildAt(i - 4);
            if (!isClickAllTab(tag)) {
                endView = mXrAllList.getChildAt(i - 4);
            }
            endView.getLocationInWindow(endLoc);
            toX = endLoc[0] - parentLoc[0];
            toY = endLoc[1] + view.getHeight() - parentLoc[1];
            //加上顶部间隔
            toY += tabMargin;
        } else {
            endView = mXrFocusList.getChildAt(i - 1);
            if (!isClickAllTab(tag)) {
                endView = mXrAllList.getChildAt(i - 1);
            }
            endView.getLocationInWindow(endLoc);
            toX = endLoc[0] + view.getWidth() - parentLoc[0];
            toY = endLoc[1] - parentLoc[1];
        }
        //这是强制固定位移到第一位
        if (!isClickAllTab(tag)) {
            toX = 0;
            toY = mXrAllList.getTop();
        }

        float startX = startLoc[0] - parentLoc[0];
        float startY = startLoc[1] - parentLoc[1];

        path.reset();
        path.moveTo(startX, startY);
        //加上左边间隔
        path.lineTo(toX + tabMargin, toY);
        //设置路径进行计算
        mPathMeasure.setPath(path, false);
        //属性动画实现
        valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(duration);
        // 匀速插值器
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                startView.setX(mCurrentPosition[0]);
                startView.setY(mCurrentPosition[1]);
//                Log.e("tag", mCurrentPosition[0] + "@" + mCurrentPosition[1]);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            T model;

            @Override
            public void onAnimationStart(Animator animation) {
                if (isClickAllTab(tag)) {
                    //更新删除动画
                    mAllAdapter.notifyItemRemoved(position);
                    model = mAllTab.get(position);
                    mAllTab.remove(position);
                } else {
                    mFocusAdapter.notifyItemRemoved(position);
                    model = mFocusTab.get(position);
                    mFocusTab.remove(position);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isClickAllTab(tag)) {
                    mFocusTab.add(mFocusTab.size(), model);
                    //先更新数据
                    mAllAdapter.notifyDataSetChanged();
                    mFocusAdapter.notifyDataSetChanged();
                    mFocusAdapter.notifyItemInserted(mFocusTab.size());
                    relativeLayout.removeView(startView);
                } else {
                    mAllTab.add(0, model);
                    //先更新数据
                    mFocusAdapter.notifyDataSetChanged();
                    mAllAdapter.notifyDataSetChanged();
                    mAllAdapter.notifyItemInserted(0);//mAllTab.size()
                    relativeLayout.removeView(startView);
                }
                if (mPopUpCallback != null)
                    mPopUpCallback.callback(position, isClickAllTab(tag), model);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        valueAnimator.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mPopUpCallback != null)
            mPopUpCallback.refresh();
        if (cbEdit != null && mFocusAdapter != null) {
            cbEdit.setChecked(false);
            mFocusAdapter.showEdit(cbEdit.isChecked());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (mView != null)
                    mView.close();
                break;
            case R.id.cb_edit:
                if (mFocusAdapter != null)
                    mFocusAdapter.showEdit(cbEdit.isChecked());
                if (mPopUpCallback != null && !cbEdit.isChecked())
                    mPopUpCallback.synchronizationData(!cbEdit.isChecked());
                break;
        }
    }
}
