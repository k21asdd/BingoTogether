package com.example.togetbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
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
import android.widget.Toast;

public class RoomList extends Activity{
	private ListView Rlist;
	private ArrayList<String> ll;
	private String[] list = {"Hello"};
	private ArrayAdapter<String> listAdapter;
	private Messenger mMessanger;
	
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
			listAdapter = new ArrayAdapter<String>(RoomList.this, android.R.layout.simple_list_item_1, list);
			listAdapter.notifyDataSetChanged();
			Log.d("Toget", "Msg done");
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_list);
		ll = new ArrayList<String>();
		Rlist = (ListView)findViewById(R.id.room);
		listAdapter = new ArrayAdapter<String>(RoomList.this, android.R.layout.simple_list_item_1, list);
		Rlist.setAdapter(listAdapter);
		mMessanger = new Messenger(new ListHandler(this));
		Rlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position == 0){
					
				}else{
					
				}
			}
		});
		
		new Thread(){
			public void run() {
				try {
					Socket s = new Socket("163.20.34.159", 5566);
					BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
					PrintWriter out = new PrintWriter(s.getOutputStream());
					this.sleep(500);
					out.println(BingoSignal.QUERY);
					out.flush();
					if(Integer.valueOf(in.readLine()) == BingoSignal.QUERY){
						String data = in.readLine();
						while(data.compareTo("Q_DONE") != 0){
							ll.add(data);
							data = in.readLine();
						}
						list = new String[ll.size()];
						list = ll.toArray(list);
						for(String ss : list){
							Log.d("Toget", ss + "haha");
						}
						Message msg = new Message();
						msg.what = 123;
						try {
							mMessanger.send(msg);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
}
