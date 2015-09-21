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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateRoom extends Activity{
	private TextView test;
	private EditText Rname,Uname,Gnumber;
	private Button Confirm, GoBack;
	private CRHandler mHandler;
	private Socket C2S;
	private CommunicateServer ControlChannel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_room);
		Rname = (EditText)findViewById(R.id.Rname);
		
		Uname = (EditText)findViewById(R.id.Uname);
		Gnumber = (EditText)findViewById(R.id.Gnumber);
		Confirm = (Button)findViewById(R.id.confirm);
		GoBack = (Button)findViewById(R.id.goBack);
		test = (TextView)findViewById(R.id.cr_text);
		test.setText("");
		ControlChannel = CommunicateServer.getInstance();
		mHandler = new CRHandler(this);
		new Thread(){
			public void run() {
				C2S = ControlChannel.Connect(BingoSignal.CREATE);
			};
		}.start();
		/*
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
		}.start();*/
		Confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						String RoomName = Rname.getText().toString();
						String UserName = Uname.getText().toString();
						String GridNumber = Gnumber.getText().toString();
						if(RoomName.isEmpty() || UserName.isEmpty() || GridNumber.isEmpty())
							return;
						//Display wait icon
						int index =  ControlChannel.Create(RoomName + " " +
								UserName + " " +
								GridNumber, C2S);
						if(index == -1){
							//¼u¥X°T®§
							return;
						}						
						Bundle data = new Bundle();
						data.putInt("INDEX", index);
						data.putInt("GRID", Integer.valueOf(GridNumber));
						Message msg = new Message();
						msg.what = 123;
						msg.setData(data);
						mHandler.sendMessage(msg);
					}
				}.start();
			}
		});
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
			switch (msg.what){
			case 0:
				test.setText(test.getText()+msg.getData().getString("Hello")+"\r\n");
				break;
			case 123:
				Bundle data = msg.getData();
				Intent intent = new Intent();
				intent.putExtra("INDEX", data.getInt("INDEX"));
				intent.putExtra("GRID", data.getInt("GRID"));
				intent.putExtra("CREATOR", true);
				intent.setClass(CreateRoom.this, BingoGame.class);
				startActivity(intent);
				finish();
			}
		}
	};
	//use for debug
	private void update(String s){
		Message msg = new Message();
		msg.what = 0;
		Bundle data = new Bundle();
		data.putString("Hello", s);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
