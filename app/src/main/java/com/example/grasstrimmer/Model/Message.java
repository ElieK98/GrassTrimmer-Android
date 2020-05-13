package com.example.grasstrimmer.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    int motor1;
    int motor2;
    int motor3;
    int motor4;
    int trimmer;
    int automatic;

//    Forward(1, 0, 1, 0, 0, 0),
//    Left(0, 1, 1, 0, 0, 0),
//    Right(1, 0, 0, 1, 0, 0),
//    Backward(0, 1, 0, 1, 0, 0),
//    ForwardWithTrimmer(1, 0, 1, 0, 1, 0),
//    LeftWithTrimmer(0, 1, 1, 0, 1, 0),
//    RightWithTrimmer(1, 0, 0, 1, 1, 0),
//    BackwardWithTrimmer(0, 1, 0, 1, 1, 0),
//    Trimmer(0, 0, 0, 0, 1, 0),
//    OnAutomatic(0, 0, 0, 0, 1, 1),
//    Stop(0, 0, 0, 0, 0, 0);

    public Message(int motor1, int motor2, int motor3, int motor4, int trimmer, int automatic){
        this.motor1 = motor1;
        this.motor2 = motor2;
        this.motor3 = motor3;
        this.motor4 = motor4;
        this.trimmer = trimmer;
        this.automatic = automatic;
    }

    public String createJSONString() throws JSONException {
        JSONObject jo=new JSONObject();
        jo.put("Motor 1", this.motor1);
        jo.put("Motor 2", this.motor2);
        jo.put("Motor 3", this.motor3);
        jo.put("Motor 4", this.motor4);
        jo.put("Trimmer", this.trimmer);
        jo.put("Automatic", this.automatic);
        return jo.toString();
    }
}
