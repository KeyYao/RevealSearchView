package moe.key.yao.search;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import moe.key.yao.search.reveal.animation.ReverseInterpolator;
import moe.key.yao.search.reveal.animation.SupportAnimator;
import moe.key.yao.search.reveal.animation.ViewAnimationUtils;

/**
 * Created by Key on 2015/11/17.<br>
 * <p/>
 *
 *     <b>自定义SearchView，带提示列表和Circular Reveal动画</b><br>
 *     参考
 *     <a href="https://github.com/Quinny898/PersistentSearch/blob/master/library/src/main/java/com/quinny898/library/persistentsearch/SearchBox.java">
 *         SearchBox.java
 *     </a>
 *
 * @author Key
 */
public class SearchView extends RelativeLayout {

    /** 提示列表的LayoutTransition动画时间   default 75ms */
    private static final long DURATION_LAYOUT_TRANSITION = 75L;

    /** 延迟显示键盘所需的时间                default 100ms */
    private static final long DELAY_SHOW_SOFT_KEYBOARD = 100L;

    /** 展开动画所需的时间                   default 150ms */
    private int mRevealDuration = 150;

    /** 隐藏动画所需的时间                   default 350ms */
    private int mHideDuration = 350;

    /** 搜索编辑框 */
    private EditText mSearchEdit;

    /** 搜索提示列表 */
    private ListView mSuggestListView;

    /** 返回按钮 */
    private ImageButton mBackBtn;

    /** 清空内容按钮 */
    private ImageButton mClearBtn;

    /** 提交（搜索）按钮 */
    private ImageButton mSubmitBtn;

    /** 阴影背景View */
    private View mShadowBackground;

    /** 提示列表于搜索框的分隔线 */
    private View mSuggestListDivider;

    /** 搜索提示的Data */
    private List<SearchSuggest> mSuggestData;

    /** 提示列表Adapter */
    private SuggestListAdapter mSuggestAdapter;

    /** 当前依附的Activity */
    private Activity mAttachedActivity;

    /** 当前依附的ActionMenu的id */
    private int mAttachedMenuId;

    /** 是否是打开（显示）状态 */
    private boolean mOpening = false;

    /** 是否在执行动画（展开/隐藏） */
    private boolean isAnimationRunning = false;

    /** OnSearchViewStatusChangedListener */
    private OnSearchViewStatusChangedListener mStatusChangedListener;

    /** OnSearchTextChangedListener */
    private OnSearchTextChangedListener mTextChangedListener;

    /** OnSearchSubmitListener */
    private OnSearchSubmitListener mSubmitListener;

    /** OnSuggestListItemClickListener */
    private OnSuggestListItemClickListener mSuggestListItemClickListener;

    public SearchView(Context context) {
        super(context);
        initSearchView();
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSearchView();
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSearchView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSearchView();
    }

    /**
     * 初始化
     */
    private void initSearchView() {
        /** inflate layout */
        inflate(getContext(), R.layout.layout_seach_view, this);

        /** find view */
        mSearchEdit = (EditText) findViewById(R.id.search_view_edit);
        mSuggestListView = (ListView) findViewById(R.id.search_view_suggest_list);
        mBackBtn = (ImageButton) findViewById(R.id.search_view_back_btn);
        mClearBtn = (ImageButton) findViewById(R.id.search_view_clear_btn);
        mSubmitBtn = (ImageButton) findViewById(R.id.search_view_submit_btn);
        mShadowBackground = findViewById(R.id.search_view_shadow_background);
        mSuggestListDivider = findViewById(R.id.search_view_suggest_list_divider);

        /** init suggests data */
        mSuggestData = new ArrayList<>();

        /** init suggests adapter */
        mSuggestAdapter = new SuggestListAdapter(getContext(), mSuggestData);

        /** set root layout transition animation */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            RelativeLayout searchRoot = (RelativeLayout) findViewById(R.id.search_view_root);
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setDuration(DURATION_LAYOUT_TRANSITION);
            searchRoot.setLayoutTransition(layoutTransition);
        }

        /** set suggest list adapter */
        mSuggestListView.setAdapter(mSuggestAdapter);

        /** set listener */
        mSearchEdit.setOnEditorActionListener(new SearchEditorActionListener());
        mSearchEdit.addTextChangedListener(new SearchTextChangedListener());
        mBackBtn.setOnClickListener(new ClickListener());
        mClearBtn.setOnClickListener(new ClickListener());
        mSubmitBtn.setOnClickListener(new ClickListener());
        mShadowBackground.setOnClickListener(new ClickListener());
        mSuggestListView.setOnItemClickListener(new SuggestItemClickListener());

        /** set view elevation */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(9.0f);
        }

    }

    /**
     * 获取当前 打开/隐藏 状态
     *
     * @return 是否是打开状态
     */
    public boolean isOpened() {
        return mOpening;
    }

    /**
     * 设置依附的Activity和对应的ActionMenu的id，<b>必须在初始化的时候设置</b>
     *
     * @param activity    SearchView所依附的Activity
     * @param menuId      ActionMenu的Id
     */
    public void attachActivity(Activity activity, int menuId) {
        mAttachedActivity = activity;
        mAttachedMenuId = menuId;
    }

    /**
     * 设置搜索框的 hint text
     *
     * @param hint    text
     */
    public void setSearchHint(String hint) {
        mSearchEdit.setHint(hint);
    }

    /**
     * 设置搜索框的 hint text
     *
     * @param resId    text resId
     */
    public void setSearchHint(int resId) {
        mSearchEdit.setHint(resId);
    }

    /**
     * 设置搜索框的 text
     *
     * @param text    text
     */
    public void setSearchText(String text) {
        mSearchEdit.setText(text);
    }

    /**
     * 设置搜索框的 text
     *
     * @param resId    text resId
     */
    public void setSearchText(int resId) {
        mSearchEdit.setText(resId);
    }

    /**
     * 设置展开动画的时间
     *
     * @param duration    动画持续时间，单位ms
     */
    public void setOpenAnimationDuration(int duration) {
        mRevealDuration = duration;
    }

    /**
     * 设置隐藏动画的时间
     *
     * @param duration    动画持续时间，单位ms
     */
    public void setCloseAnimationDuration(int duration) {
        mHideDuration = duration;
    }

    /**
     * 添加提示（单项）
     *
     * @param suggest   item
     * @param clearData 是否清空原数据源
     */
    public void addSearchSuggest(SearchSuggest suggest, boolean clearData) {
        /** clear data */
        if (clearData) {
            mSuggestData.clear();
        }

        /** call add */
        addSearchSuggest(suggest);
    }

    /**
     * 添加提示（集合）
     *
     * @param suggests  list
     * @param clearData 是否清空原数据源
     */
    public void addSearchSuggest(List<SearchSuggest> suggests, boolean clearData) {
        /** clear data */
        if (clearData) {
            mSuggestData.clear();
        }

        /** call add */
        addSearchSuggest(suggests);
    }

    /**
     * 添加提示（单项）
     *
     * @param suggest    item
     */
    private void addSearchSuggest(SearchSuggest suggest) {
        /** add data */
        mSuggestData.add(suggest);

        /** show or hide suggest divider */
        mSuggestListDivider.setVisibility(!mSuggestData.isEmpty() ? View.VISIBLE : View.GONE);

        /** notify adapter */
        mSuggestAdapter.notifyDataSetChanged();
    }

    /**
     * 添加提示（集合）
     *
     * @param suggests    list
     */
    private void addSearchSuggest(List<SearchSuggest> suggests) {
        /** add data */
        mSuggestData.addAll(suggests);

        /** show or hide suggest divider */
        mSuggestListDivider.setVisibility(!mSuggestData.isEmpty() ? View.VISIBLE : View.GONE);

        /** notify adapter */
        mSuggestAdapter.notifyDataSetChanged();
    }

    /**
     * 清空提示数据
     */
    public void clearSuggestData() {
        /** clear data */
        mSuggestData.clear();

        /** hide suggest divider */
        mSuggestListDivider.setVisibility(View.GONE);

        /** notify adapter */
        mSuggestAdapter.notifyDataSetChanged();
    }

    /**
     * 打开SearchView
     */
    public void openSearchView() {
        /** check attach activity */
        if (mAttachedActivity == null || mAttachedMenuId == -1) {
            throw new RuntimeException("Must call 'attachActivity(activity, menuId)' before !");
        }

        /** check is running animation */
        if (isAnimationRunning) {
            return;
        }

        /** show SearchView */
        setVisibility(View.VISIBLE);

        /** find action menu view */
        View actionMenu = mAttachedActivity.findViewById(mAttachedMenuId);

        /** check action menu view */
        if (actionMenu == null) {
            throw new IllegalStateException("Can't find action menu ! Please check the 'menuId' !");
        }

        /** get action menu location and calculate view center point */
        int[] location = new int[2];
        actionMenu.getLocationInWindow(location);
        int x = location[0] + (actionMenu.getWidth() / 2);
        int y = location[1];

        /** start reveal animation */
        animateReveal(x, y, mAttachedActivity);
    }

    /**
     * 关闭SearchView
     */
    public void closeSearchView() {
        /** check is running animation */
        if (isAnimationRunning) {
            return;
        }

        /** find action menu view */
        View actionMenu = mAttachedActivity.findViewById(mAttachedMenuId);

        /** check action menu view */
        if (actionMenu == null) {
            throw new IllegalStateException("Can't find action menu ! Please check the 'menuId' !");
        }

        /** hide soft keyboard */
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);

        /** get action menu location and calculate view center point */
        int[] location = new int[2];
        actionMenu.getLocationInWindow(location);
        int x = location[0] + (actionMenu.getWidth() / 2);
        int y = location[1];

        /** start hide animation */
        animateHide(x, y, mAttachedActivity);
    }

    /**
     * 动画展开
     *
     * @param x           圆心的x坐标
     * @param y           圆心的y坐标
     * @param activity    依附的Activity
     */
    private void animateReveal(int x, int y, Activity activity) {
        /** find view */
        FrameLayout contentLayout = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        RelativeLayout rootLayout = (RelativeLayout) this.findViewById(R.id.search_view_root);

        /** calculate radius */
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, r.getDisplayMetrics());
        int finalRadius = (int) Math.max(contentLayout.getWidth(), px);

        /** create animator */
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(mRevealDuration);
        animator.addListener(new RevealAnimatorListener());

        /** start animator */
        animator.start();

        /** OnSearchViewStatusChangedListener onOpenStart callback */
        if (mStatusChangedListener != null) {
            mStatusChangedListener.onOpenStart();
        }

        /** show shadow background */
        mShadowBackground.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_shadow_show));
        mShadowBackground.setVisibility(View.VISIBLE);
    }

    /**
     * 动画隐藏
     *
     * @param x           圆心的x坐标
     * @param y           圆心的y坐标
     * @param activity    依附的Activity
     */
    private void animateHide(int x, int y, Activity activity) {
        /** find view */
        FrameLayout contentLayout = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        RelativeLayout rootLayout = (RelativeLayout) this.findViewById(R.id.search_view_root);

        /** Calculate Radius */
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, r.getDisplayMetrics());
        int finalRadius = (int) Math.max(contentLayout.getWidth() * 1.5, px);

        /** create animator */
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        animator.setInterpolator(new ReverseInterpolator());
        animator.setDuration(mHideDuration);
        animator.addListener(new HideAnimatorListener());

        /** start animator */
        animator.start();

        /** OnSearchViewStatusChangedListener onCloseStart callback */
        if (mStatusChangedListener != null) {
            mStatusChangedListener.onCloseStart();
        }

        /** hide shadow background */
        mShadowBackground.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_shadow_hide));
        mShadowBackground.setVisibility(View.GONE);
    }

    /**
     * 打开状态
     */
    private void opened() {
        /** request focus */
        requestFocus();
        mSearchEdit.requestFocus();

        /** show or hide suggest divider */
        mSuggestListDivider.setVisibility(!mSuggestData.isEmpty() ? View.VISIBLE : View.GONE);

        /** show suggest list */
        mSuggestListView.setVisibility(View.VISIBLE);

        /** notify suggest list data changed */
        mSuggestAdapter.notifyDataSetChanged();

        /** OnSearchViewStatusChangedListener onOpened callback */
        if (mStatusChangedListener != null) {
            mStatusChangedListener.onOpened();
        }

        /** delay open soft keyboard */
        postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInputFromWindow(mSearchEdit.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        }, DELAY_SHOW_SOFT_KEYBOARD);

        /** change status */
        mOpening = true;
    }

    /**
     * 关闭状态
     */
    private void closed() {
        /** clear text */
        mSearchEdit.setText("");

        /** clear focus */
        mSearchEdit.clearFocus();
        clearFocus();

        /** hide suggest divider */
        mSuggestListDivider.setVisibility(View.GONE);

        /** hide suggest list */
        mSuggestListView.setVisibility(View.GONE);

        /** hide search view */
        setVisibility(View.GONE);

        /** OnSearchViewStatusChangedListener onClosed callback */
        if (mStatusChangedListener != null) {
            mStatusChangedListener.onClosed();
        }

        /** change status */
        mOpening = false;

    }

    /**
     * 提交搜索
     *
     * @param text    search text
     */
    private void submit(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        /** OnSearchSubmitListener onSubmit callback */
        if (mSubmitListener != null) {
            mSubmitListener.onSubmit(text);
        }

        /** close */
        closeSearchView();

    }

    /**
     * 清空搜索内容
     */
    private void clearSearchText() {
        mSearchEdit.setText("");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && isOpened()) {
            /** close when back key pressed */
            closeSearchView();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 点击事件
     */
    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == R.id.search_view_back_btn) {
                /** close */
                closeSearchView();
                return;
            }

            if (id == R.id.search_view_clear_btn) {
                clearSearchText();
                /** OnSearchTextChangedListener onClear callback */
                if (mTextChangedListener != null) {
                    mTextChangedListener.onClear();
                }
                return;
            }

            if (id == R.id.search_view_submit_btn) {
                /** submit search */
                submit(mSearchEdit.getText().toString());
                return;
            }

            if (id == R.id.search_view_shadow_background) {
                /** close */
                closeSearchView();
            }
        }

    }

    /**
     * 搜索编辑框Action事件监听
     */
    private class SearchEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /** submit search when search key pressed */
                submit(mSearchEdit.getText().toString());
                return true;
            }
            return false;
        }
    }

    /**
     * 搜索编辑框内容改变事件监听
     */
    private class SearchTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            /** show or hide clear button */
            if (s.length() <= 0) {
                mClearBtn.setVisibility(View.GONE);
            } else {
                mClearBtn.setVisibility(View.VISIBLE);
            }

            /** OnSearchTextChangedListener onChanged callback */
            if (mTextChangedListener != null) {
                mTextChangedListener.onChanged(s.toString());
            }
        }
    }

    /**
     * 提示列表Adapter
     */
    private class SuggestListAdapter extends BaseAdapter {

        private Context mContext;
        private List<SearchSuggest> mData;
        private ViewHolder mHolder;

        public SuggestListAdapter(Context context, List<SearchSuggest> data) {
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                /** init convert view */
                mHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_search_suggest, parent, false);

                /** load view */
                mHolder.text = (TextView) convertView.findViewById(R.id.text);
                mHolder.leftIcon = (ImageView) convertView.findViewById(R.id.left_icon);
                mHolder.rightIcon = (ImageView) convertView.findViewById(R.id.right_icon);

                /** set tag */
                convertView.setTag(mHolder);
            } else {
                /** get tag */
                mHolder = (ViewHolder) convertView.getTag();
            }

            /** get item */
            SearchSuggest item = mData.get(position);

            /** set text */
            mHolder.text.setText(item.getText());

            /** set left icon */
            if (item.getLeftIconResource() != -1) {
                mHolder.leftIcon.setImageResource(item.getLeftIconResource());
                mHolder.leftIcon.setVisibility(View.VISIBLE);
            } else {
                mHolder.leftIcon.setVisibility(View.INVISIBLE);
            }

            /** set right icon */
            if (item.getRightIconResResource() != -1) {
                mHolder.rightIcon.setImageResource(item.getRightIconResResource());
                mHolder.rightIcon.setVisibility(View.VISIBLE);
            } else {
                mHolder.rightIcon.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        /** ViewHolder */
        class ViewHolder {
            TextView text;
            ImageView leftIcon;
            ImageView rightIcon;
        }
    }

    /**
     * 提示列表Item点击事件
     */
    private class SuggestItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /** OnSuggestListItemClickListener onItemClick callback */
            if (mSuggestListItemClickListener != null) {
                if (mSuggestListItemClickListener.onItemClick(position, mSuggestData.get(position))) {
                    return;
                }

            }

            /** default submit search */
            submit(mSuggestData.get(position).getText());
        }
    }

    /**
     * 展开动画的监听
     */
    private class RevealAnimatorListener implements SupportAnimator.AnimatorListener {

        @Override
        public void onAnimationStart() {
            isAnimationRunning = true;
        }

        @Override
        public void onAnimationEnd() {
            isAnimationRunning = false;
            opened();
        }

        @Override
        public void onAnimationCancel() {
            isAnimationRunning = false;
        }

        @Override
        public void onAnimationRepeat() {

        }
    }

    /**
     * 隐藏动画的监听
     */
    private class HideAnimatorListener implements SupportAnimator.AnimatorListener {

        @Override
        public void onAnimationStart() {
            isAnimationRunning = true;
        }

        @Override
        public void onAnimationEnd() {
            isAnimationRunning = false;
            closed();
        }

        @Override
        public void onAnimationCancel() {
            isAnimationRunning = false;
        }

        @Override
        public void onAnimationRepeat() {

        }
    }

    /**
     * SearchView状态改变事件监听
     */
    public interface OnSearchViewStatusChangedListener {
        /**
         * 搜索框打开时（展开动画刚启动时）
         */
        void onOpenStart();

        /**
         * 搜索框已经打开（展开动画已经执行完毕时）
         */
        void onOpened();

        /**
         * 搜索框关闭时（关闭动画刚启动时）
         */
        void onCloseStart();

        /**
         * 搜索框已经关闭（关闭动画执行完毕时）
         */
        void onClosed();
    }

    /**
     * 搜索框内容改变事件监听
     */
    public interface OnSearchTextChangedListener {
        /**
         * 内容改变时
         *
         * @param text    当前EditText中显示的text
         */
        void onChanged(String text);

        /**
         * 清空内容时（clear按钮点击时）
         */
        void onClear();
    }

    /**
     * 提交搜索事件监听<br>
     * 触发条件：<br>
     *      1.点击了提交按钮<br>
     *      2.点击键盘的search键<br>
     *      3.点击的提示列表Item（根据 OnSuggestListItemClickListener 的 onItemClick 方法 返回值 决定是否执行）
     */
    public interface OnSearchSubmitListener {
        /**
         * 提交事件
         *
         * @param text    提交搜索的text
         */
        void onSubmit(String text);
    }

    /**
     * 提示列表的Item点击事件监听
     */
    public interface OnSuggestListItemClickListener {
        /**
         * Item点击事件
         *
         * @param position    item 的 position
         * @param item        item
         * @return true表示覆盖默认操作，false表示继续执行默认操作
         */
        boolean onItemClick(int position, SearchSuggest item);
    }

    /**
     * 设置 OnSearchViewStatusChangedListener
     *
     * @param listener    listener
     */
    public void setOnSearchViewStatusChangedListener(OnSearchViewStatusChangedListener listener) {
        this.mStatusChangedListener = listener;
    }

    /**
     * 设置 OnSearchTextChangedListener
     *
     * @param listener    listener
     */
    public void setOnSearchTextChangedListener(OnSearchTextChangedListener listener) {
        this.mTextChangedListener = listener;
    }

    /**
     * 设置 OnSearchSubmitListener
     *
     * @param listener    listener
     */
    public void setOnSearchSubmitListener(OnSearchSubmitListener listener) {
        this.mSubmitListener = listener;
    }

    /**
     * 设置 OnSuggestListItemClickListener
     *
     * @param listener    listener
     */
    public void setOnSuggestListItemClickListener(OnSuggestListItemClickListener listener) {
        this.mSuggestListItemClickListener = listener;
    }


}
