package com.example.zwy.eventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zwy.eventbuslib.EventBus;
import com.example.zwy.eventbuslib.Subscribe;
import com.example.zwy.eventbuslib.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);


    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void recvMsg(Msg msg){
        Toast.makeText(this,String.format("msg:%s %s",msg.msgId,msg.content),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void jump(View view) {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);

    }

    public void postSticky(View view) {
        MsgSticky msg = new MsgSticky();
        msg.msgId="1";
        msg.content="粘性事件";
        EventBus.getDefault().postSticky(msg);

    }
}
