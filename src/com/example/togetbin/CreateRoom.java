package com.example.togetbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class CreateRoom extends Activity{
	private TextView test;
	private CRHandler mHandler;
	private CommunicateServer ControlChannel;
	public CreateRoom(){}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_room);
		test = (TextView)findViewById(R.id.cr_text);
		test.setText("");
		ControlChannel = CommunicateServer.getInstance();
		mHandler = new CRHandler(this);
		new Thread(){
			public void run(){
				try {
					Socket ComTun = null;
					update("Start");
					int index = ControlChannel.Create("AAA Bian 7*7",ComTun);
					update("Create Success");
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}.start();
	}
	private class CRHandler extends Handler{
		WeakReference<CreateRoom> mAct;
		public CRHandler(CreateRoom mm){
			// TODO Auto-generated constructor stub
			mAct = new WeakReference<CreateRoom>(mm);
		}
		@Override
		public void handleMessage(Message msg){
			CreateRoom act = mAct.get();
			if(act == null)return;
			test.setText(test.getText()+msg.getData().getString("Hello")+"\r\n");
		}
	};
	private void update(String s){
		Message msg = new Message();
		msg.what = 0;
		Bundle data = new Bundle();
		data.putString("Hello", s);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
