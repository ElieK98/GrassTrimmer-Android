package com.example.grasstrimmer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.grasstrimmer.Fragments.ControlsFragment;
import com.example.grasstrimmer.Fragments.StatisticsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity { //implements View.OnTouchListener { //OnTouchListener
/*
    MqttAndroidClient client;
    MqttConnectOptions options;
    String topic = "MDP_Grass_Trimming_Robot";
    String subtopic = "MDP_Grass_Trimming_app_esp";
    //String ServerURI="tcp://broker.hivemq.com:1883";
    String ServerURI = "tcp://212.98.137.194:1883";

    Switch TrimmerToggle;
    TextView subText;
    Vibrator vibrator;
    Ringtone myRingtone;
    Switch runstop;
    Switch Automatic;
    ImageButton ArrowUp;
    ImageButton ArrowLeft;
    ImageButton ArrowRight;
    ImageButton ArrowDown;
    TextView CurrentSpeedLabel;
    TextView CurrentSessionLabel;
    TextView CurrentSpeed;
    TextView CurrentSession;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    int Seconds, Minutes, MilliSeconds;
*/
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView profImageview;
    TextView nameTextView;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profImageview=(ImageView) findViewById(R.id.profileImageView);
        nameTextView=(TextView) findViewById(R.id.NameTextView);
        ActionBar actionBar = getSupportActionBar();
        //
        //actionBar.setDisplayShowCustomEnabled(true);
        //
        actionBar.setElevation(0);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewPager(viewPager);
        //
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.activity_main,null);
        actionBar.setCustomView(v);
        if(currentUser!=null){
            profImageview.setImageURI(acct.getPhotoUrl());
            nameTextView.setText("Welcome "+ acct.getDisplayName());
        }
        //
        tabLayout.setupWithViewPager(viewPager);
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