package com.example.grasstrimmer.Fragments;

import android.annotation.SuppressLint;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grasstrimmer.Constants;
import com.example.grasstrimmer.Model.Helpers;
import com.example.grasstrimmer.Model.Message;
import com.example.grasstrimmer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.VIBRATOR_SERVICE;

public class ControlsFragment extends Fragment implements View.OnTouchListener {

    DatabaseReference reference;

    Vibrator vibrator;
    Ringtone myRingtone;

    TextView runstopText;
    TextView trimmerText;
    TextView automaticModeText;

    Switch runstopSwitch;
    Switch trimmerSwitch;
    Switch automaticSwitch;

    TextView movementText;

    ImageButton arrowUp;
    ImageButton arrowLeft;
    ImageButton arrowRight;
    ImageButton arrowDown;

    TextView currentSessionText;
    TextView currentSpeedText;

    TextView currentSessionLabel;
    TextView currentSpeedLabel;

    long millisecondsTime, startTime, timeBuff, updateTime=0L;
    Handler handler;
    int seconds,minutes,milliseconds, sessionLength, averageSpeed, trimTimeTotal;

    Message message;
    MqttAndroidClient client;
    MqttConnectOptions options;

    String timeTrimmed;
    HashMap<String, Object> dataMap;
    ArrayList<Integer>  speedList;
    Date trimStartDate;
    Date trimEndDate;

    Long startTrim;
    Long endTrim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls, container, false);

        reference = FirebaseDatabase.getInstance().getReference("ESP1");


        final GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());

        trimTimeTotal=0;


        Date c= Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        final String date=df.format(c);

        handler = new Handler();
        runstopSwitch = view.findViewById(R.id.runStopSwitch);
        runstopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    toggleButtons(true);
                    dataMap = new HashMap<>();
                    speedList = new ArrayList<>();
                } else {
                    sessionLength=(minutes * 60) + seconds;
                    averageSpeed=calculateAverage(speedList);
                    dataMap.put("averageSpeed",averageSpeed);
                    dataMap.put("averageDistance",averageSpeed*sessionLength);
                    dataMap.put("sessionLength", sessionLength);
                    dataMap.put("trimTimeTotal",trimTimeTotal);
                    Integer sessionCount = Helpers.getIntFromPreferences(date + " - sessionCounter", getContext());
                    timeBuff += millisecondsTime;
                    millisecondsTime = 0L;
                    startTime = 0L;
                    timeBuff = 0L;
                    updateTime = 0L;
                    milliseconds = 0;
                    seconds = 0;
                    minutes = 0;
                    handler.removeCallbacks(runnable);

                    if (trimmerSwitch.isChecked()) {
                        Date trimEndDate = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        final String trimEndString = df.format(trimEndDate);
                        dataMap.put(trimEndString, "trimEnd");
                    }

                    if(account!=null) {
                        /** upload data to firebase**/
                        reference.child(account.getId()).child(date).child("Session " + sessionCount).setValue(dataMap);
                        Helpers.addIntToPreferences(date + " - sessionCounter", sessionCount + 1, getContext());
                        dataMap = null;



                    }
                    speedList=null;
                    currentSessionText.setText("00:00:00");
                    toggleButtons(false);
                }
            }
        });

        trimmerSwitch = view.findViewById(R.id.trimmerSwitch);
        trimmerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    message = new Message(0, 0, 0, 0, 1, 0);
                    trimStartDate = Calendar.getInstance().getTime();

                    startTrim=new java.util.Date().getTime();

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    final String trimStartString = df.format(trimStartDate);
                    dataMap.put(trimStartString, "trimStart");
                } else {
                    message = new Message(0, 0, 0, 0, 0, 0);
                    trimEndDate = Calendar.getInstance().getTime();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    final String trimEndString = df.format(trimEndDate);
                    dataMap.put(trimEndString, "trimEnd");

                    endTrim=new java.util.Date().getTime();

                    Long diffInMillies=Math.abs(startTrim-endTrim);
                    long diff= TimeUnit.SECONDS.convert(diffInMillies,TimeUnit.MILLISECONDS);

                    trimTimeTotal+=diff;



                }
            }
        });
        automaticSwitch = view.findViewById(R.id.automaticSwitch);
        automaticSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    message = new Message(0, 0, 0, 0, 1, 1);
                }

                if (!isChecked) {
                    message = new Message(0, 0, 0, 0, 0, 0);
                }
                sendMessage();
            }
        });

        arrowUp = view.findViewById(R.id.arrowUp);
        arrowUp.setOnTouchListener(this);
        arrowLeft = view.findViewById(R.id.arrowLeft);
        arrowLeft.setOnTouchListener(this);
        arrowRight = view.findViewById(R.id.arrowRight);
        arrowRight.setOnTouchListener(this);
        arrowDown = view.findViewById(R.id.arrowDown);
        arrowDown.setOnTouchListener(this);

        toggleButtons(false);

        currentSpeedText = view.findViewById(R.id.speedTextView);
        currentSessionText = view.findViewById(R.id.sessionTextView);

        vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getContext(),uri);

        setupMqtt();

        return view;
    }

    private void toggleButtons(Boolean enabled) {
        arrowUp.setEnabled(enabled);
        arrowLeft.setEnabled(enabled);
        arrowRight.setEnabled(enabled);
        arrowDown.setEnabled(enabled);
        trimmerSwitch.setEnabled(enabled);
        automaticSwitch.setEnabled(enabled);
        if (enabled) {
            arrowUp.setAlpha(1.0F);
            arrowLeft.setAlpha(1.0F);
            arrowRight.setAlpha(1.0F);
            arrowDown.setAlpha(1.0F);
            trimmerSwitch.setAlpha(1.0F);
            automaticSwitch.setAlpha(1.0F);
        } else {
            arrowUp.setAlpha(0.5F);
            arrowLeft.setAlpha(0.5F);
            arrowRight.setAlpha(0.5F);
            arrowDown.setAlpha(0.5F);
            trimmerSwitch.setAlpha(0.5F);
            automaticSwitch.setAlpha(0.5F);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.arrowUp) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (trimmerSwitch.isChecked()) {
                    message = new Message(1,0,1,0,1,0);
                } else {
                    message = new Message(1,0,1,0,0,0);

                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onTouchUp();
            }
        }
        if (v.getId() == R.id.arrowLeft) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (trimmerSwitch.isChecked()) {
                    message = new Message(0,1,1,0,1,0);
                } else {
                    message = new Message(0,1,1,0,0,0);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onTouchUp();
            }
        }
        if (v.getId() == R.id.arrowRight) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (trimmerSwitch.isChecked()) {
                    message = new Message(1,0,0,1,1,0);
                } else {
                    message = new Message(1,0,0,1,0,0);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onTouchUp();
            }
        }
        if (v.getId() == R.id.arrowDown) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (trimmerSwitch.isChecked()) {
                    message = new Message(0,1,0,1,1,0);
                } else {
                    message = new Message(0,1,0,1,0,0);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onTouchUp();
            }
        }
        sendMessage();
        return false;
    }

    public void onTouchUp() {

            message = new Message(0,0,0,0,0,0);

    }

    public Runnable runnable = new Runnable() {

        public void run() {
            millisecondsTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondsTime;
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (updateTime % 1000);
            currentSessionText.setText("" + minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
            handler.postDelayed(this, 0);
        }

    };

    //MQTT
    public void setupMqtt() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getContext(), Constants.serverURI, clientId);

        options = new MqttConnectOptions();
        options.setUserName(Constants.usernameMqtt);
        options.setPassword(Constants.passwordMqtt.toCharArray());

        //if not works check connect options

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getContext(), "Connected!", Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getContext(), "Unable to connect!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }


            @SuppressLint("SetTextI18n")
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                byte[] bytes=message.getPayload();
                String msg=new String(bytes);
                JSONObject jsonObject=new JSONObject(msg);

                int distance=jsonObject.getInt("distance");
                int speed=jsonObject.getInt("vitesse");

                if (!Integer.toString(distance).equals("0")) {
                    Toast.makeText(getContext(), "WARNING! Obstacle at " + distance + " cm", Toast.LENGTH_LONG).show();
                    VibrationEffect vibe = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.vibrate(vibe);
                    myRingtone.play();
                }


                if(speedList.size()==100){
                    speedList.add(speed);
                    speedList.remove(0);
                }else{
                    speedList.add(speed);

                }

                currentSpeedText.setText(Integer.toString(speed) + " Km/h");

 /*




                Toast.makeText(getContext(), new String(message.getPayload()), Toast.LENGTH_LONG).show();
                VibrationEffect vibe = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(vibe);
                myRingtone.play();
*/
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    public void sendMessage() {
        try {
            String json = message.createJSONString();
            MqttMessage reqMessage = new MqttMessage();
            reqMessage.setPayload(json.getBytes());
            reqMessage.setQos(0);
            try {
                final IMqttDeliveryToken publish = client.publish(Constants.topic, reqMessage);
            } catch (MqttException e) { e.printStackTrace(); }
        } catch (JSONException e) { e.printStackTrace(); }
    }


    private void setSubscription(){
        try {
            client.subscribe(Constants.subtopic,0);
        } catch(MqttException e) { e.printStackTrace(); }
    }

    private int calculateAverage(List<Integer> array){
        int sum=0;
        if(!array.isEmpty()){
            for(Integer elem : array){
                sum+=elem;
            }
            return sum/array.size();
        }

        return sum;
    }
}