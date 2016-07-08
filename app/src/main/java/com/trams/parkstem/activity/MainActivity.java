package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.server.ServerClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RelativeLayout hipassButton;
    private boolean hipassOn;

    private final String TAG = getClass().getSimpleName();
    ServerClient SC = new ServerClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        hipassOn = false;
        hipassButton = (RelativeLayout) findViewById(R.id.activity_hipass_on_off_button);
        hipassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHipassButtonClicked();
            }
        });

        RelativeLayout move1, move2;
        move1 = (RelativeLayout) findViewById(R.id.activity_hipass_tocarregister);
        move1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movefromHipasstoCarRegister();
            }
        });
        move2 = (RelativeLayout) findViewById(R.id.activity_hipass_tocardregister);
        move2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movefromHipasstoCardRegister();
            }
        });
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
            Intent intent = new Intent(this, MobileActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_card_new_card) {
            Intent intent = new Intent(this, InputNewCardActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_mobile_ticket) {
            Intent intent = new Intent(this, TicketActivity.class);
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
            // Handle the camera action
        } else if (id == R.id.nav_car_number) {
            Intent intent = new Intent(this, InputCarActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_credit_card) {
            Intent intent = new Intent(this, InputCardActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_long_ticket) {

        } else if (id == R.id.nav_park_ticket) {

        } else if (id == R.id.nav_park_list) {

        } else if (id == R.id.nav_pay_list) {

        } else if (id == R.id.nav_setting) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onHipassButtonClicked() {
        ImageView human = (ImageView) findViewById(R.id.activity_highpass_human_image);
        hipassButton.removeAllViews();

        if(hipassOn) {
            getLayoutInflater().inflate(R.layout.hipass_button_off, hipassButton);
            human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person));
            SC.HipassOn("Y");
        } else {
            getLayoutInflater().inflate(R.layout.hipass_button_on, hipassButton);
            human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person_2));
            SC.HipassOn("N");
        }

        hipassOn = !hipassOn;
    }


    private void movefromHipasstoCarRegister(){
        Intent intent = new Intent(this, InputCarActivity.class);
        startActivity(intent);
    }
    private void movefromHipasstoCardRegister(){
        Intent intent = new Intent(this, InputCardActivity.class);
        startActivity(intent);
    }
}
