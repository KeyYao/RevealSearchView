package moe.key.yao.search.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import moe.key.yao.search.SearchSuggest;
import moe.key.yao.search.SearchView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private AppCompatButton mHistoryBtn;
    private AppCompatButton mSuggestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        init();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void init() {
        mHistoryBtn = (AppCompatButton) findViewById(R.id.history_btn);
        mSuggestBtn = (AppCompatButton) findViewById(R.id.suggests_btn);

        mHistoryBtn.setOnClickListener(this);
        mSuggestBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.history_btn) {
            Intent intent = new Intent(this, SearchHistoryActivity.class);
            startActivity(intent);
            return;
        }

        if (id == R.id.suggests_btn) {
            Intent intent = new Intent(this, SearchSuggestActivity.class);
            startActivity(intent);
        }
    }
}
