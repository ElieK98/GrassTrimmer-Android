package com.example.grasstrimmer;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MqttAndroidClient client;
    MqttConnectOptions options;
    String topic = "MDP_Grass_Trimming_Robot";
    String subtopic="MDP_Grass_Trimming_app_esp";
    String ServerURI="tcp://broker.hivemq.com:1883";
    //String ServerURI="tcp://212.98.137.194:1883";

    Switch TrimmerToggle;
    TextView subText;
    Vibrator vibrator;
    Ringtone myRingtone;
    Switch runstop;
    ImageButton ArrowUp;
    ImageButton ArrowLeft;
    ImageButton ArrowRight;
    ImageButton ArrowDown;
    TextView CurrentSpeedLabel;
    TextView CurrentSessionLabel;
    TextView CurrentSpeed;
    TextView CurrentSession;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subText=(TextView)findViewById(R.id.SubscribeText);
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone=RingtoneManager.getRingtone(getApplicationContext(),uri);

        CurrentSession=(TextView)findViewById(R.id.currentsession);
        CurrentSpeed=(TextView)findViewById(R.id.currentspeed);

        ArrowUp=(ImageButton)findViewById(R.id.ArrowUp);
        ArrowUp.setOnClickListener(this);
        ArrowUp.setEnabled(false);


        ArrowRight=(ImageButton)findViewById(R.id.ArrowRight);
        ArrowRight.setOnClickListener(this);
        ArrowRight.setEnabled(false);


        ArrowLeft=(ImageButton)findViewById(R.id.ArrowLeft);
        ArrowLeft.setOnClickListener(this);
        ArrowLeft.setEnabled(false);


        ArrowDown=(ImageButton)findViewById(R.id.ArrowDown);
        ArrowDown.setOnClickListener(this);
        ArrowDown.setEnabled(false);



        TrimmerToggle=(Switch)findViewById(R.id.switch1);
        TrimmerToggle.setOnClickListener(this);

        runstop=(Switch)findViewById(R.id.switch2);
        runstop.setOnClickListener(this);

        handler=new Handler();

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),ServerURI , clientId);

        options = new MqttConnectOptions();
        //options.setUserName("iotleb");
        //options.setPassword("iotleb".toCharArray());

        //if not works check connect options



        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "connected!", Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Unable to connect!", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload()));
                VibrationEffect vibe = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(vibe);
                myRingtone.play();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });




    }
    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            CurrentSession.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };

    public void Move(int M1,int M2, int M3, int M4,int T) throws JSONException {
        JSONObject jo=new JSONObject();
        jo.put("Motor 1",M1);
        jo.put("Motor 2",M2);
        jo.put("Motor 3",M3);
        jo.put("Motor 4",M4);
        jo.put("Trimmer",T);
        String reqPayload = jo.toString();
        MqttMessage reqMessage = new MqttMessage();
        reqMessage.setPayload(reqPayload.getBytes());
        reqMessage.setQos(0);
        try {

            final IMqttDeliveryToken publish = client.publish(topic, reqMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v){
        try {
            if (v.getId() == R.id.ArrowUp) {
                if (TrimmerToggle.isChecked()){

                    Move(1, 0, 1, 0,1);
                }else{ Move(1, 0, 1, 0,0);}

            }
            if (v.getId() == R.id.ArrowLeft) {
                if (TrimmerToggle.isChecked()) {

                    Move(0, 0, 1, 0, 1);
                }else{ Move(0, 0, 1, 0, 0);}
            }
            if (v.getId() == R.id.ArrowRight) {
                if (TrimmerToggle.isChecked()) {

                    Move(1, 0, 0, 0, 1);
                }else{  Move(1, 0, 0, 0, 0);}
            }
            if (v.getId() == R.id.ArrowDown) {
                if (TrimmerToggle.isChecked()) {

                    Move(0, 1, 0, 1, 1);
                }else{ Move(0, 1, 0, 1, 0);}
            }
// Move(0,1,0,1);

            if (runstop.isChecked()) {
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                ArrowUp.setEnabled(true);
                ArrowUp.setAlpha(1.0f);

                ArrowLeft.setEnabled(true);
                ArrowLeft.setAlpha(1.0f);

                ArrowRight.setEnabled(true);
                ArrowRight.setAlpha(1.0f);

                ArrowDown.setEnabled(true);
                ArrowDown.setAlpha(1.0f);

            }
            else{
                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);
                ArrowUp.setAlpha(0.5f);
                ArrowLeft.setAlpha(0.5f);
                ArrowRight.setAlpha(0.5f);
                ArrowDown.setAlpha(0.5f);

                ArrowUp.setEnabled(false);
                ArrowRight.setEnabled(false);
                ArrowLeft.setEnabled(false);
                ArrowDown.setEnabled(false);
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                CurrentSession.setText("00:00:00");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setSubscription(){
        try{
            client.subscribe(subtopic,0);
        }catch(MqttException e){e.printStackTrace();}

    }





}
