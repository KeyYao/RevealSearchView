package moe.key.yao.search.sample;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import moe.key.yao.search.SearchSuggest;
import moe.key.yao.search.SearchView;

/**
 * Created by Key on 2015/11/20.
 *
 * @author Key
 */
public class SearchSuggestActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SearchView mSearchView;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_suggest);
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
        mSearchView.setOnSearchTextChangedListener(new SearchTextChangedListener());
        initData();
    }

    private void initData() {
        mData = new ArrayList<>();
        AssetManager am = getAssets();
        try {
            InputStream is = am.open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String str = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(str);
            JSONArray result = jsonObject.getJSONArray("data");

            for (int i = 0; i < result.length(); i++) {
                String item = result.getJSONObject(i).getString("text");
                mData.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSuggest(String text) {
        List<SearchSuggest> suggests = new ArrayList<>();
        for (String str : mData) {
            if (str.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))) {
                SearchSuggest item = new SearchSuggest(str, R.drawable.ic_search);
                suggests.add(item);
            }
        }

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

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_search) {
            mSearchView.openSearchView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchTextChangedListener implements SearchView.OnSearchTextChangedListener {

        @Override
        public void onChanged(String text) {
            if (!"".equals(text)) {
                updateSuggest(text);
            } else {
                mSearchView.clearSuggestData();
            }
        }

        @Override
        public void onClear() {
//            mSearchView.clearSuggestData();
        }
    }

}
