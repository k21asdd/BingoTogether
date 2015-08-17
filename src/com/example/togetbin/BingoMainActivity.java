package com.example.togetbin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BingoMainActivity extends Activity {
	
	private Button single,muti,exit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bingo_main);
		single = (Button) findViewById(R.id.singleBingo);
		exit = (Button) findViewById(R.id.exitBingo);
		muti = (Button) findViewById(R.id.mutiBingo);
		
		single.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BingoGame ac = new BingoGame();
				Intent intent = new Intent();
				intent.setClass(BingoMainActivity.this, BingoGame.class);
				startActivity(intent);
			}
		});
		muti.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RoomList ac = new RoomList();
				Intent intent = new Intent();
				intent.setClass(BingoMainActivity.this, RoomList.class);
				startActivity(intent);
				finish();
			}
		});
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bingo_main, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
