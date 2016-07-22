package com.trams.parkstem.base_activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.activity.AssignActivity;
import com.trams.parkstem.activity.FirstScreenActivity;
import com.trams.parkstem.activity.HistoryParkActivity;
import com.trams.parkstem.activity.HistoryPaymentActivity;
import com.trams.parkstem.activity.InputCarActivity;
import com.trams.parkstem.activity.InputCardActivity;
import com.trams.parkstem.activity.InputNewCardActivity;
import com.trams.parkstem.activity.LoginActivity;
import com.trams.parkstem.activity.ManageLongTicketActivity;
import com.trams.parkstem.activity.ManageTicketActivity;
import com.trams.parkstem.activity.MobileCertificationActivity;
import com.trams.parkstem.activity.ParkStatusActivity;
import com.trams.parkstem.activity.SettingActivity;
import com.trams.parkstem.activity.SplashActivity;
import com.trams.parkstem.activity.TicketMobileListActivity;
import com.trams.parkstem.activity.TicketPurchaseListActivity;
import com.trams.parkstem.gcm.MyGcmListenerService;
import com.trams.parkstem.others.Essentials;

/**
 * Created by Noverish on 2016-07-09.
 */
public class BaseNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_FINISH = 1;
    public static final int RESULT_FINISH = 0;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        registBroadcastReceiver();

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
        getMenuInflater().inflate(R.menu.main, menu);
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
        }
        else if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_assign) {
            Intent intent = new Intent(this, AssignActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_mobile) {
            Intent intent = new Intent(this, MobileCertificationActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_card_new_card) {
            Intent intent = new Intent(this, InputNewCardActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_mobile_ticket) {
            Intent intent = new Intent(this, TicketMobileListActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_purchase_ticket) {
            Intent intent = new Intent(this, TicketPurchaseListActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_first_screen) {
            Intent intent = new Intent(this, FirstScreenActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

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
            Intent intent = new Intent(this, ManageTicketActivity.class);
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
        return true;
    }

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equals(MyGcmListenerService.PUSH_RECEIVE)){
                    Essentials.alertParkState(BaseNavigationActivity.this);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyGcmListenerService.PUSH_RECEIVE));
    }
}
