package moe.key.yao.search.listener;

import moe.key.yao.search.SearchSuggest;
import moe.key.yao.search.annotation.NotProguard;

/**
 * Created by Key on 2015/12/15.
 * <p>
 *     提示列表的Item点击事件监听
 * </p>
 * @author Key
 */
@NotProguard
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
