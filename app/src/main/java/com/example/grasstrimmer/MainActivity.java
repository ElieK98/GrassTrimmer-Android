package com.example.grasstrimmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MqttAndroidClient client;
    MqttConnectOptions options;
    String topic = "MDP_Grass_Trimming_Robot";
    String subtopic="MDP_Grass_Trimming_app_esp";
    String ServerURI="tcp://212.98.137.194:1883";
    TextView Trimmer;
    Switch TrimmerToggle;
    TextView subText;
    Vibrator vibrator;
    Ringtone myRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subText=(TextView)findViewById(R.id.SubscribeText);
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone=RingtoneManager.getRingtone(getApplicationContext(),uri);
        ImageButton ArrowUp=(ImageButton)findViewById(R.id.ArrowUp);
        ArrowUp.setOnClickListener(this);
        ImageButton ArrowRight=(ImageButton)findViewById(R.id.ArrowRight);
        ArrowRight.setOnClickListener(this);
        ImageButton ArrowLeft=(ImageButton)findViewById(R.id.ArrowLeft);
        ArrowLeft.setOnClickListener(this);
        ImageButton ArrowDown=(ImageButton)findViewById(R.id.ArrowDown);
        ArrowLeft.setOnClickListener(this);

        Trimmer = (TextView)findViewById(R.id.TrimmerText);
        Trimmer.setOnClickListener(this);
        TrimmerToggle=(Switch)findViewById(R.id.switch1);
        TrimmerToggle.setOnClickListener(this);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),ServerURI , clientId);

        options = new MqttConnectOptions();
        options.setUserName("iotleb");
        options.setPassword("iotleb".toCharArray());

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
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.ArrowUp) {
                if (TrimmerToggle.isChecked()){
                    Trimmer.setText("Trimmer is ON");
                    Move(1, 0, 1, 0,1);
                }else{  Trimmer.setText("Trimmer is OFF");Move(1, 0, 1, 0,0);}

            }
            if (v.getId() == R.id.ArrowLeft) {
                if (TrimmerToggle.isChecked()) {
                    Trimmer.setText("Trimmer is ON");
                    Move(0, 0, 1, 0, 1);
                }else{ Trimmer.setText("Trimmer is OFF");Move(0, 0, 1, 0, 0);}
            }
            if (v.getId() == R.id.ArrowRight) {
                if (TrimmerToggle.isChecked()) {
                    Trimmer.setText("Trimmer is ON");
                    Move(1, 0, 0, 0, 1);
                }else{  Trimmer.setText("Trimmer is OFF");Move(1, 0, 0, 0, 0);}
            }
            if (v.getId() == R.id.ArrowDown) {
                if (TrimmerToggle.isChecked()) {
                    Trimmer.setText("Trimmer is ON");
                    Move(0, 1, 0, 1, 1);
                }else{ Trimmer.setText("Trimmer is OFF");Move(0, 1, 0, 1, 0);}
            }
// Move(0,1,0,1);
            if (TrimmerToggle.isChecked()){
                Trimmer.setText("Trimmer is ON");
            }
            if (!TrimmerToggle.isChecked()){
                Trimmer.setText("Trimmer is OFF");
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
