package com.example.togetbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RoomList extends Activity{
	private ListView Rlist;
	private List<HashMap<String, String>> list;
	private SimpleAdapter listAdapter;
	private Messenger mMessanger;
	private CommunicateServer ControlChannel;
	
	private class ListHandler extends Handler{
		WeakReference<RoomList> mAct;
		public ListHandler(RoomList mm){
			// TODO Auto-generated constructor stub
			mAct = new WeakReference<RoomList>(mm);
		}
		@Override
		public void handleMessage(Message msg){
			RoomList act = mAct.get();
			if(act == null)return;
			listAdapter.notifyDataSetChanged();
			Log.d("Toget", "Msg done");
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_list);
		list = new ArrayList<HashMap<String,String>>();
		listAdapter = new SimpleAdapter(
				getApplicationContext(), 
				list, 
				R.layout.list_view_1, 
				new String[]{"RoomName","UserName","Grid", "number"}, 
				new int[]{R.id.RoomName,R.id.UserName,R.id.Grid,R.id.number});
		Rlist = (ListView)findViewById(R.id.room);
		Rlist.setAdapter(listAdapter);
		Rlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String number = ((TextView)view.findViewById(R.id.number)).getText().toString();
				String grid = ((TextView)view.findViewById(R.id.Grid)).getText().toString();
				Intent intent = new Intent();
				intent.putExtra("INDEX", Integer.valueOf(number));
				intent.putExtra("GRID", Integer.valueOf(grid));
				intent.putExtra("CREATOR", false);
				intent.setClass(RoomList.this, BingoGame.class);
				startActivity(intent);
				finish();
			}
		});
		ControlChannel = CommunicateServer.getInstance();
		mMessanger = new Messenger(new ListHandler(this));
		new Thread(){
			public void run() {
				try {
					list.clear();
					ControlChannel.Query(
							new String[]{"RoomName","UserName","Grid","number"}, 
							list,
							CommunicateServer.getInstance().Connect(BingoSignal.QUERY));
					Message msg = new Message();
					msg.what = 123;
					mMessanger.send(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
}
