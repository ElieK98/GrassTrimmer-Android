package com.example.grasstrimmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.grasstrimmer.Fragments.ControlsFragment;
import com.example.grasstrimmer.Fragments.StatisticsFragment;
import com.example.grasstrimmer.Model.Helpers;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity { //implements View.OnTouchListener { //OnTouchListener

    ViewPager viewPager;
    TabLayout tabLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewPager(viewPager);

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.activity_main,null);




        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item=menu.findItem(R.id.display_name);
        item.setTitle(Helpers.getFromPreferences("displayName",this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out:
                FirebaseAuth.AuthStateListener authStateListener=new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            Helpers.removeFromPreferences("userID",getApplicationContext());
                            Helpers.removeFromPreferences("displayName",getApplicationContext());
                            Helpers.removeFromPreferences("email",getApplicationContext());

                            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                };
                FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
                FirebaseAuth.getInstance().signOut();

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ControlsFragment controlsFragment = new ControlsFragment();
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        adapter.addFragmentToPager(controlsFragment, "Controls");
        adapter.addFragmentToPager(statisticsFragment, "Statistics");
        viewPager.setAdapter(adapter);
    }
}