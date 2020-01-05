package com.example.petime.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.petime.Fragments.HomeFragment;
import com.example.petime.Fragments.PostFragment;
import com.example.petime.Fragments.ProfileFragment;
import com.example.petime.Model.Model;
import com.example.petime.R;
import com.google.android.material.navigation.NavigationView;

import static com.example.petime.PeTimeApp.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//           @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "New post was added", Snackbar.LENGTH_LONG)
//                        .setAction("Action", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //Gal Test
//                                //verifyStoragePermissions(MainActivity.this);
//                                //End Test
//                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostFragment()).commit();
//                                //startActivity(new Intent(MainActivity.this, PostActivity.class));
//                            }
//                        }).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else {
            // Setting the default fragment to Home Fragment at startup
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        if (Model.instance.getCurrentUser() == null){
            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent1);
            return;
        }
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView displayName = findViewById(R.id.email_tv);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        if(Model.instance.getCurrentUser() == null){
            displayName.setText(prefs.getString("email", "none"));
        }else{
            displayName.setText(Model.instance.getCurrentUser().getEmail());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
       //     return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        switch(item.getItemId()){
            case R.id.nav_home:
                selectedFragment = new HomeFragment();
                if (selectedFragment != null & (currentFragment instanceof HomeFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("nav").commit();
                }
                break;
            case R.id.nav_profile:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", Model.instance.getCurrentUser().getUid());
                editor.putString("displayname", Model.instance.getCurrentUser().getDisplayName());
                editor.apply();
                selectedFragment = new ProfileFragment();
                if (selectedFragment != null & (currentFragment instanceof ProfileFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("nav").commit();
                }
                break;
            case R.id.nav_new_post:
                selectedFragment = new PostFragment();
                if (selectedFragment != null & (currentFragment instanceof PostFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("nav").commit();
                }
                break;
            case R.id.nav_logout:
                signOut();
                break;
        }

//        if (selectedFragment != null){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("nav").commit();
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        try {
            Model.instance.signOut();
            finish();
//            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
  //          startActivity(intent1);
        } catch (Exception e) {
            Log.d("TAG", "Error while signing out!");
        }
    }
}