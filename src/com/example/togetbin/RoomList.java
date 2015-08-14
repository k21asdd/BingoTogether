package com.example.togetbin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RoomList extends Activity{
	private ListView Rlist;
	private String[] list = {"¹]µ§","­ì¤lµ§","¿ûµ§","¤òµ§","±m¦âµ§"};
	private ArrayAdapter<String> listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_list);
		Rlist = (ListView)findViewById(R.id.room);
		listAdapter = new ArrayAdapter<String>(RoomList.this, android.R.layout.simple_list_item_1, list);
		Rlist.setAdapter(listAdapter);
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
	}
}
