package moe.key.yao.search;

/**
 * Created by Key on 2015/11/18.<br>
 * <p/>
 * 搜索提示Item
 *
 * @author Key
 */
public class SearchSuggest {

    public SearchSuggest() {
    }

    public SearchSuggest(String text) {
        this.text = text;
    }

    public SearchSuggest(String text, int leftIconResource) {
        this.text = text;
        this.leftIconResource = leftIconResource;
    }

    public SearchSuggest(String text, int leftIconResource, int rightIconResResource) {
        this.text = text;
        this.leftIconResource = leftIconResource;
        this.rightIconResResource = rightIconResResource;
    }

    /**
     * 显示的文本
     */
    private String text;

    /**
     * 左侧Icon的ResId
     */
    private int leftIconResource = -1;

    /**
     * 右侧Icon的ResId
     */
    private int rightIconResResource = -1;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLeftIconResource() {
        return leftIconResource;
    }

    public void setLeftIconResource(int leftIconResource) {
        this.leftIconResource = leftIconResource;
    }

    public int getRightIconResResource() {
        return rightIconResResource;
    }

    public void setRightIconResResource(int rightIconResResource) {
        this.rightIconResResource = rightIconResResource;
    }

}
