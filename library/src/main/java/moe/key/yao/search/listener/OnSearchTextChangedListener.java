package moe.key.yao.search.listener;

import moe.key.yao.search.annotation.NotProguard;

/**
 * Created by Key on 2015/12/15.
 * <p>
 *     搜索框内容改变事件监听
 * </p>
 * @author Key
 */
@NotProguard
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
