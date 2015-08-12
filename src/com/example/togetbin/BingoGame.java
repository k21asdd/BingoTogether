package com.example.togetbin;

import java.util.Stack;

import android.R.dimen;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BingoGame extends Activity{
	private TextView currentNumber;
	private Button GameStart;
	private GridView gridview;
	private int column;
	private int space;
	private boolean begin = false;
	private GenNumber gNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_view);
		
		column = 4;
		space = 20;
		
		gNumber = new GenNumber();
		currentNumber = (TextView)findViewById(R.id.CurrentNumber);
		GameStart = (Button)findViewById(R.id.GameStart);
		GameStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn = (Button)v;
				// TODO Auto-generated method stub
				if(begin){
					begin = false;
					btn.setText("Begin");
					resetGame();
				}else{
					begin = true;
					btn.setText("Restart");
					currentNumber.setText("Game Start !");
				}
			}
		});
		
		gridview = (GridView) findViewById(R.id.BingoView);
		gridview.setNumColumns(column);
		gridview.setHorizontalSpacing(space);
	    gridview.setVerticalSpacing(space);
	    resetGame();
	}
	private void resetGame(){
		currentNumber.setText("1");
		gNumber.setNumber(2);
		GameStart.setBackgroundColor(Color.GRAY);
		GameStart.setClickable(false);
		gridview.setAdapter(new ImageAdapter(BingoGame.this));
	}
	private class GenNumber {
		private Stack<Integer> mStack;
		private int number = 1;
		public GenNumber(){
			mStack = new Stack<Integer>();
		}
		public int getNumber(){
			if(number > column*column)return -1;
			if(mStack.isEmpty())return number++;
			else return mStack.pop();
		}
		public void saveNumber(int n){
			mStack.push(n);
		}
		public void setNumber(int n){
			number = n;
		}
	}
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private int number = 0;
	    private int[] line;
	    public ImageAdapter(Context c) {
	        mContext = c;
	        line = new int[column*2+2];
	        for(int i = 0 ; i < column*2+2 ; i++)
	        	line[i] = column;
	    }

	    public int getCount() {
	        return column*column;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }
	    public boolean r2l(int value){
	    	return value != 0 && value % (column-1) == 0 && (value / (column-1) <= column);
	    }
	    public boolean l2r(int value){
	    	int check = value - value/column;
	    	return check%column == 0;
	    }
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Button btn;

	        if (convertView == null) {
	            // if it's not recycled, initialize some attributes
	        	int columnWidth = (gridview.getWidth() - (column - 1) * space)/column;
	        	int height = 150;
	        	gridview.setColumnWidth(columnWidth);
	        	btn = new Button(mContext);
	        	btn.setLayoutParams(new GridView.LayoutParams(columnWidth, height));
	        	btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Button active = (Button) v;
						if(begin){
							int finLine = 0;
							int col = column + position%column;
							int row = position/column;
							
							line[row]--;
							line[col]--;
							if(l2r(position)){
								line[line.length-2]--;
							}
							if(r2l(position)){
								line[line.length-1]--;
							}
							for(int i : line){
								if(i==0)
									finLine++;
							}
							if(finLine > 0)
								currentNumber.setText("You got "+finLine);
							
							active.setBackgroundColor(Color.YELLOW);
							active.setClickable(false);
						}else{
							if((active.getText().length()) == 0){
								number++;
								active.setText(currentNumber.getText());
								if(number == column*column){
									GameStart.setClickable(true);
									GameStart.setBackgroundColor(Color.LTGRAY);
								}
								else 
									currentNumber.setText(Integer.toString(gNumber.getNumber()));
							}
							else{
								number--;
								gNumber.saveNumber(Integer.valueOf(currentNumber.getText().toString()));
								currentNumber.setText(active.getText());
								active.setText("");
								GameStart.setClickable(false);
								GameStart.setBackgroundColor(Color.GRAY);
							}
						}
					}
				});
	            
	        } else {
	        	btn = (Button) convertView;
	        }

	        return btn;
		}
	}
}
