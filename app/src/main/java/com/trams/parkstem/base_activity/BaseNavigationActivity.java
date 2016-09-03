package com.trams.parkstem.base_activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.activity.AssignActivity;
import com.trams.parkstem.activity.FirstScreenActivity;
import com.trams.parkstem.activity.HistoryParkActivity;
import com.trams.parkstem.activity.HistoryPaymentActivity;
import com.trams.parkstem.activity.InputCarActivity;
import com.trams.parkstem.activity.InputCardActivity;
import com.trams.parkstem.activity.LoginActivity;
import com.trams.parkstem.activity.ManageLongTicketActivity;
import com.trams.parkstem.activity.ManageShortTicketActivity;
import com.trams.parkstem.activity.ParkStatusActivity;
import com.trams.parkstem.activity.SettingActivity;
import com.trams.parkstem.activity.SplashActivity;
import com.trams.parkstem.activity.TicketMobileListActivity;
import com.trams.parkstem.activity.TicketPurchaseListActivity;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-09.
 */
public class BaseNavigationActivity extends BaseActivity {
    public static final int REQUEST_FINISH = 1004;
    public static final int RESULT_FINISH = 444;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.base_activity_nav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTStd-LtEx.otf");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(myTypeface);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TextView userNameInNav = (TextView) findViewById(R.id.parkstem_menu_bar_user_name);
        try {
            userNameInNav.setText(ServerClient.getInstance().memberInfo().name);
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("ERROR","ServerClient memberInfo occurred Error");
            userNameInNav.setText("ERROR");
        }

        TextView menuBarParkstem = (TextView) findViewById(R.id.parkstem_menu_bar_parkstem);
        menuBarParkstem.setTypeface(myTypeface);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        FrameLayout content = (FrameLayout) findViewById(R.id.activity_main_content);

        LayoutInflater.from(this).inflate(layoutResID, content);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_splash) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_assign) {
            Intent intent = new Intent(this, AssignActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_mobile_ticket) {
            Intent intent = new Intent(this, TicketMobileListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_purchase_ticket) {
            Intent intent = new Intent(this, TicketPurchaseListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_first_screen) {
            Intent intent = new Intent(this, FirstScreenActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onNavigationItemSelected(View view) {
        // Handle navigation view item clicks here.
        int id = view.getId();

        /*LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_park_status);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_car_number);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_credit_card);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_long_ticket);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_park_ticket);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_park_list);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_pay_list);
        LinearLayout parkStatus = (LinearLayout) findViewById(R.id.nav_setting);*/

        if (id == R.id.nav_park_status) {
            Intent intent = new Intent(this, ParkStatusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_car_number) {
            Intent intent = new Intent(this, InputCarActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_credit_card) {
            Intent intent = new Intent(this, InputCardActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_long_ticket) {
            Intent intent = new Intent(this, ManageLongTicketActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_park_ticket) {
            Intent intent = new Intent(this, ManageShortTicketActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_park_list) {
            Intent intent = new Intent(this, HistoryParkActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_pay_list) {
            Intent intent = new Intent(this, HistoryPaymentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_FINISH);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}
