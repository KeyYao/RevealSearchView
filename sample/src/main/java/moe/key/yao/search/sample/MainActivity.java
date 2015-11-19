package moe.key.yao.search.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import moe.key.yao.search.SearchSuggest;
import moe.key.yao.search.SearchView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initToolbar();
        mSearchView = (SearchView) findViewById(R.id.search_view);

        /** attachActivity */
        mSearchView.attachActivity(this, R.id.action_search);

        List<SearchSuggest> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SearchSuggest item = new SearchSuggest();
            item.setText("history " + (i + 1));
            item.setLeftIconResource(R.drawable.ic_history);
            list.add(item);
        }
        mSearchView.addSearchSuggest(list, true);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            /** open search view */
            mSearchView.openSearchView();
        }

        return super.onOptionsItemSelected(item);
    }

}
