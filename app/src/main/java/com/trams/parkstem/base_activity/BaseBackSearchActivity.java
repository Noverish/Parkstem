package com.trams.parkstem.base_activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trams.parkstem.R;

/**
 * Created by Noverish on 2016-07-09.
 */
public class BaseBackSearchActivity extends BaseActivity
        implements SearchView.OnQueryTextListener, View.OnClickListener, SearchView.OnCloseListener{
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private boolean searchEnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.base_activity_back);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTStd-LtEx.otf");
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(myTypeface);

        setSearchEnable(false);
        setBackEnable(true);

    }

    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }

    public void setTitleEnable(boolean showTitle) {
        if(showTitle)
            toolbarTitle.setVisibility(View.VISIBLE);
        else
            toolbarTitle.setVisibility(View.INVISIBLE);
    }

    public void setSearchEnable(boolean showSearch) {
        this.invalidateOptionsMenu();
        searchEnable = showSearch;
        onCreateOptionsMenu(toolbar.getMenu());
    }

    public void setBackEnable(boolean showBack) {
        if(showBack)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        FrameLayout content = (FrameLayout) findViewById(R.id.base_activity_back_content);

        LayoutInflater.from(this).inflate(layoutResID, content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(searchEnable) {
            getMenuInflater().inflate(R.menu.search_menu, menu);

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            searchView.setOnQueryTextListener(this);
            searchView.setOnSearchClickListener(this);
            searchView.setOnCloseListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        setTitleEnable(false);
    }

    @Override
    public boolean onClose() {
        setTitleEnable(true);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
