package com.example.togetbin;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import android.R.dimen;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
	private static Button[] Btns;
	private TextView currentNumber, Debug;
	private Button GameStart;
	private GridView gridview;
	private int column;
	private boolean begin = false;
	private boolean first = true;
	private boolean self = true;
	private GenNumber gNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_view);
		
		column = 4;
		
		gNumber = new GenNumber();
		Btns = new Button[column*column];
		Debug = (TextView)findViewById(R.id.debug);
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
					first = false;
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
	    resetGame();
	}
	
	private void resetGame(){
		currentNumber.setText("1");
		gNumber.setNumber(2);
		GameStart.setBackgroundColor(Color.GRAY);
		GameStart.setTextColor(Color.WHITE);
		GameStart.setClickable(false);
		gridview.setAdapter(new ImageAdapter(BingoGame.this));
	}
	private class GenNumber {
		private Stack<Integer> mStack;
		private int number = 0;
		public GenNumber(){
			mStack = new Stack<Integer>();
		}
		public int getNumber(){
			if(!mStack.isEmpty())return mStack.pop();
			if(number > column*column)return -1;
			return number++;
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
	    
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Button btn;
	        if (convertView == null) {
	            // if it's not recycled, initialize some attributes
	        	int columnWidth = (gridview.getWidth() - (column - 1) * gridview.getVerticalSpacing())/column;
	        	int height = 150;
	        	gridview.setColumnWidth(columnWidth);
	        	btn = new Button(mContext);
	        	btn.setTag(Integer.valueOf(position));
	        	btn.setHeight(height);
	        	btn.setWidth(columnWidth);
	        	btn.setOnClickListener(new BingoClick());
	            
	        } else {
	        	btn = (Button) convertView;
	        }
	        return btn;
		}
		private class BingoClick implements OnClickListener{
			private int max;
			
			public BingoClick() {
				// TODO Auto-generated constructor stub
				max = column * column;
			}
			private boolean r2l(int value){
		    	return (value != 0) && (value % (column-1) == 0) && (value / (column-1) <= column);
		    }
			private boolean l2r(int value){
		    	int check = value - value/column;
		    	return check%column == 0;
		    }
			private void updateLine(int row, int col, boolean positive, boolean negative){
				line[row]--;
				line[col]--;
				if(positive)
					line[line.length-2]--;
				if(negative)
					line[line.length-1]--;
			}
			private int finLine(){
				int finLine = 0;
				for(int i : line) if(i==0) finLine++;
				return finLine;
			}
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Button active = (Button) v;
				if(begin){
					int position = (int)active.getTag();
					int colorCode = self ? Color.YELLOW : Color.BLUE;
					if(first){
						if(position == 0)position = max-1;
						else position--;
					}
					updateLine(position/column,
							column + position%column,
							l2r(position),
							r2l(position));
					active.setBackgroundColor(colorCode);
					active.setClickable(false);
					if(finLine()>=column){
						currentNumber.setText("You got "+finLine()+"\n"
							+ "You Win !!");
						for(Button b : BingoGame.Btns) b.setClickable(false);
						return;
					}
					else currentNumber.setText("You got "+finLine());
					if(self){
						self = false;
						WaitForOpponent();
					}else{
						self = true;
					}
				}else{
					if((active.getText().length()) == 0){
						number++;
						active.setText(currentNumber.getText());
						BingoGame.Btns[Integer.valueOf(
								String.valueOf(
										currentNumber.getText()))-1] = active;
						if(number == max){
							GameStart.setClickable(true);
							GameStart.setBackgroundColor(Color.LTGRAY);
							GameStart.setTextColor(Color.BLACK);
						}
						else 
							currentNumber.setText(Integer.toString(gNumber.getNumber()));
					}
					else{
						if(number != max) 
							gNumber.saveNumber(Integer.valueOf(currentNumber.getText().toString()));
						number--;
						currentNumber.setText(active.getText());
						active.setText("");
						GameStart.setClickable(false);
						GameStart.setBackgroundColor(Color.GRAY);
						GameStart.setTextColor(Color.WHITE);
					}
				}
			}
		}
		private void WaitForOpponent(){
			//very bad algorithm
			int r;
			ArrayList<Integer> nn;
			nn = new ArrayList<Integer>();
			for(int i = 0 ; i<column*column;i++)
				if(Btns[i].isClickable())nn.add(Integer.valueOf(i));
			Random ran = new Random();
			self = false;
			Btns[(int)nn.get(ran.nextInt(nn.size()))].performClick();
		}
	}
	
}
