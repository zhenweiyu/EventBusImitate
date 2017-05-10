package com.example.zwy.eventbusdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.zwy.eventbuslib.EventBus;
import com.example.zwy.eventbuslib.Subscribe;
import com.example.zwy.eventbuslib.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        EventBus.getDefault().register(this);

    }

    public void post(View view) {
        Msg msg = new Msg();
        msg.content="my msg";
        msg.msgId = "1234";
        EventBus.getDefault().post(msg);

    }

    @Subscribe(threadMode = ThreadMode.MainThread,sticky = true)
    public void recvStickyMsg(MsgSticky msg){
        Toast.makeText(this,String.format("msg:%s %s",msg.msgId,msg.content),Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



}
