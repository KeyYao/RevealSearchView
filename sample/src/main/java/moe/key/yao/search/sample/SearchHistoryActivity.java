package moe.key.yao.search.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moe.key.yao.search.SearchSuggest;
import moe.key.yao.search.SearchView;

/**
 * Created by Key on 2015/11/20.
 *
 * @author Key
 */
public class SearchHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SAMPLE_PREF = "sample_pref";
    private static final String KEY_HISTORY_JSON = "history_json";
    private static final String KEY_HISTORY_TEXT = "text";

    private Toolbar mToolbar;
    private SearchView mSearchView;
    private AppCompatButton mClearHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        initToolbar();
        init();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.attachActivity(this, R.id.action_search);
        mSearchView.setSearchHint("请输入关键字");
        mSearchView.setOnSearchSubmitListener(new SearchSubmitListener());
        mSearchView.setOnSuggestListItemClickListener(new SuggestItemClickListener());
        mSearchView.setOpenAnimationDuration(500);
        mSearchView.setCloseAnimationDuration(750);

        mClearHistoryBtn = (AppCompatButton) findViewById(R.id.clear_history_btn);
        mClearHistoryBtn.setOnClickListener(this);
    }

    private void openSearch() {
        SharedPreferences sp = getSharedPreferences(SAMPLE_PREF, Context.MODE_PRIVATE);
        String historyJson = sp.getString(KEY_HISTORY_JSON, "");
        if (!"".equals(historyJson)) {
            try {
                JSONArray array = new JSONArray(historyJson);
                List<SearchSuggest> historyList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    SearchSuggest item = new SearchSuggest(array.getJSONObject(i).getString(KEY_HISTORY_TEXT), R.drawable.ic_history);
                    historyList.add(item);
                }
                mSearchView.addSearchSuggest(historyList, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mSearchView.openSearchView();
    }

    private void saveSearchHistory(String text) {
        try {
            SharedPreferences sp = getSharedPreferences(SAMPLE_PREF, Context.MODE_PRIVATE);
            String historyJson = sp.getString(KEY_HISTORY_JSON, "");
            JSONArray array;
            if (!"".equals(historyJson)) {
                array = new JSONArray(historyJson);
            } else {
                array = new JSONArray();
            }

            if (array.length() > 0) {
                // 判断输入的关键字是否和历史记录有重复，有则无需重复保存
                for (int i = 0; i < array.length(); i++) {
                    if (text.equals(array.getJSONObject(i).getString(KEY_HISTORY_TEXT))) {
                        return;
                    }
                }
            }

            JSONObject item = new JSONObject();
            item.put(KEY_HISTORY_TEXT, text);
            array.put(item);

            sp.edit().putString(KEY_HISTORY_JSON, array.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_search) {
            openSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.clear_history_btn) {
            SharedPreferences sp = getSharedPreferences(SAMPLE_PREF, Context.MODE_PRIVATE);
            sp.edit().clear().apply();
            mSearchView.clearSuggestData();
        }
    }

    private class SearchSubmitListener implements SearchView.OnSearchSubmitListener {

        @Override
        public void onSubmit(String text) {
            saveSearchHistory(text);
        }

    }

    private class SuggestItemClickListener implements SearchView.OnSuggestListItemClickListener {

        @Override
        public boolean onItemClick(int position, SearchSuggest item) {
            mSearchView.closeSearchView();
            return true;
        }
    }

}
