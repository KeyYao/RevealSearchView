# RevealSearchView


带有Circular Reveal动画的SearchView，符合Material Design<br>
仿 [Bilibili](https://play.google.com/store/apps/details?id=tv.danmaku.bili) 、google play store 的搜索框<br>

参考[CircularReveal](https://github.com/ozodrukh/CircularReveal)、[PersistentSearch](https://github.com/Quinny898/PersistentSearch)<br>

## Using

* layout
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                android:layout_width="match_parent"
                android:layout_height="match_parent">

    <android.support.v7.widget.Toolbar ... />

    <moe.key.yao.search.SearchView
        android:id="@+id/search_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"/>

</RelativeLayout>
```

* activity
```java
  private SearchView mSearchView;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ...
      mSearchView = (SearchView) findViewById(R.id.search_view);
      mSearchView.attachActivity(this, R.id.action_search);
      
      List<SearchSuggest> suggests = new ArrayList<>();
      ...
      mSearchView.addSearchSuggest(suggests, true);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_search_suggest, menu);
      return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.action_search) {
        mSearchView.openSearchView();
        return true;
      }
      return super.onOptionsItemSelected(item);
  }
```
* public method
```java
void attachActivity(Activity activity, int menuId)
boolean isOpened()
void setSearchHint(String hint)
void setSearchText(String text)
void setOpenAnimationDuration(int duration)
void setCloseAnimationDuration(int duration)
void addSearchSuggest(SearchSuggest suggest, boolean clearData)
void addSearchSuggest(List<SearchSuggest> suggests, boolean clearData)
void clearSuggestData()
void openSearchView()
void closeSearchView()
```
* listener
```java
public interface OnSearchViewStatusChangedListener {
    void onOpenStart();
    void onOpened();
    void onCloseStart();
    void onClosed();
}

public interface OnSearchTextChangedListener {
    void onChanged(String text);
    void onClear();
}

public interface OnSearchSubmitListener {
    void onSubmit(String text);
}

public interface OnSuggestListItemClickListener {
    boolean onItemClick(int position, SearchSuggest item);
}
```

### Screenshot
![screenshot](https://github.com/KeyYao/RevealSearchView/blob/master/screenshot.gif)
