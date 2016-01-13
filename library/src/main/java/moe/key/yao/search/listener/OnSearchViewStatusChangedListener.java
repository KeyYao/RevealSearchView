package moe.key.yao.search.listener;

import moe.key.yao.search.annotation.NotProguard;

/**
 * Created by Key on 2015/12/15.
 * <p>
 *     SearchView状态改变事件监听
 * </p>
 * @author Key
 */
@NotProguard
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
