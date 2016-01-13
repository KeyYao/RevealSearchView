package moe.key.yao.search.listener;

import moe.key.yao.search.annotation.NotProguard;

/**
 * Created by Key on 2015/12/15.
 * <p>
 *     提交搜索事件监听<br>
 *     触发条件：<br>
 *     1.点击了提交按钮<br>
 *     2.点击键盘的search键<br>
 *     3.点击的提示列表Item（根据 OnSuggestListItemClickListener 的 onItemClick 方法 返回值 决定是否执行）
 * </p>
 * @author Key
 */
@NotProguard
public interface OnSearchSubmitListener {
    /**
     * 提交事件
     *
     * @param text    提交搜索的text
     */
    void onSubmit(String text);
}
