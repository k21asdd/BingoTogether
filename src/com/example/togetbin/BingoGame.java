package com.example.togetbin;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BingoGame extends Activity{
	private static Button[] Btns;
	private TextView currentNumber, Debug;
	private Button GameStart, GameExit;
	private TableLayout game;
	private int column;
	private int[] line;
	private boolean begin = false;
	private boolean self = true;
	private GenNumber gNumber = new GenNumber();
	private int number = 0;
	private Messenger mMessenger;
	//³s½u
	private boolean GameOver;
	private boolean Creator;
	private int Index;
	private CommunicateOpponent Opponent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_view);
		
		Debug = (TextView)findViewById(R.id.debug);
		mMessenger = new Messenger(new CatchView(this));
		currentNumber = (TextView)findViewById(R.id.CurrentNumber);
		Index = getIntent().getIntExtra("INDEX", -1);
		
		if(Index != -1) {
			Opponent = CommunicateOpponent.getInstance();
			column = getIntent().getIntExtra("GRID", 0);
			Creator = getIntent().getBooleanExtra("CREATOR", false);
			GameOver = false;
			if(Creator){
				Opponent.waitForOpponent(mMessenger);
				self = true;
			}else{
				Opponent.joinOpponent(Index, mMessenger);
				self = false;
			}
		}else column = 5;
		line = new int[column*2+2];
		Btns = new Button[column*column];
		GameStart = (Button)findViewById(R.id.GameStart);
		GameStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn = (Button)v;
				// TODO Auto-generated method stub
				if(begin){
					if(Index != -1 && !GameOver){
						//Cannot restart when game is not over in a game
						return;
					}
					begin = false;
					btn.setText("Begin");
					setNewGame();
				}else{
					if(Opponent != null){
						if(!Opponent.AmIReady())
							Opponent.Tell_Opponent_Im_Ready();
						if(!Opponent.isExist()) {
							Debug.setText("No Opponent");
							return;
						}else if(!Opponent.isReady()){
							try {
								Debug.setText(Debug.getText() + "\nReady, and wait");
								Opponent.Wait_Opponent_Ready(mMessenger);
								GameStart.setClickable(false);
								GameStart.setBackgroundColor(Color.GRAY);
								return;
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
					begin = true;
					GameOver = false;
					btn.setText("Restart");
					currentNumber.setText("Game Start !");
					if(Opponent != null & !self) {
						WaitForOpponent();
						Debug.setText("Wait Opponent !");
					}else{
						Debug.setText("Your turn !");
					}
				}
			}
		});
		GameExit = (Button)findViewById(R.id.GameExit);
		GameExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Index != -1){
					if(Opponent.isExist()){
						if(!Opponent.TearDown()){
							//Display error
							return;
						}
					}
					try {
						CommunicateServer.getInstance().Teardown(Index);
						Opponent.Exit();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				finish();
			}
		});
	    game = (TableLayout)findViewById(R.id.tablelayout1); 
	}
	private void setNewGame(){
		int columnWidth = (game.getWidth() - (column - 1) * game.getPaddingBottom())/column;
    	int height = 150;
    	
        for(int i = 0 ; i < column*2+2 ; i++)
        	line[i] = column;
        game.removeAllViews();
	    for(int i=0;i<column;i++)
	    {
	    	TableRow tR = new TableRow(this);
	    	for(int j = 0 ; j < column ; j++){
	    		int position = i*column+j;
	    		Button btn = new Button(this);
	        	btn.setTag(Integer.valueOf(position));
	        	btn.setHeight(height);
	        	btn.setWidth(columnWidth);
	        	btn.setMinimumWidth(100);
	        	btn.setOnClickListener(new BingoClick());
	        	Btns[position] = btn;
	        	tR.addView(btn);
	    	}
	    	game.addView(tR);
	    }
	    currentNumber.setText("1");
		gNumber.setNumber(2);
		number = 0;

		GameExit.setBackgroundColor(Color.LTGRAY);
		GameStart.setBackgroundColor(Color.GRAY);
		GameStart.setTextColor(Color.WHITE);
		GameStart.setClickable(false);
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		setNewGame();
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
				if(active.isPressed() && !self) return;
				int position = (int)active.getTag();
				int colorCode = self ? Color.YELLOW : Color.BLUE;
				updateLine(position/column,
						column + position%column,
						l2r(position),
						r2l(position));
				active.setBackgroundColor(colorCode);
				active.setClickable(false);
				if(finLine()>=column){
					currentNumber.setText("You got "+finLine()+"\n"
						+ "You Win !!");
					for(Button btn : Btns){
						btn.setClickable(false);
					}
					if(Opponent != null){
						Opponent.Win(Integer.valueOf(active.getText().toString()));
					}
					GameOver = true;
					return;
				}
				else currentNumber.setText("You got "+finLine());
					
				if(self){
					if(Opponent != null){
					try {
							Debug.setText("Wait Opponent !");
							Opponent.Step(Integer.valueOf(active.getText().toString()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					self = false;
					WaitForOpponent();
				}else{
					self = true;
					if(Opponent != null)Debug.setText("Your turn !");
				}
			}else{
				if((active.getText().length()) == 0){
					number++;
					active.setText(currentNumber.getText());
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
	private void GameFinish(String s) throws IOException{
		currentNumber.setText(s);
		for(Button btn : Btns){
			btn.setClickable(false);
		}
		Opponent.Exit();
	}
	private void WaitForOpponent(){
		//very bad algorithm
		if(Opponent != null) Opponent.OpponentStep(mMessenger);
		else Robot();
	}
	private void Robot(){
		ArrayList<Button> nn = new ArrayList<Button>();
		for(Button btn : Btns){
			if(btn.isClickable())
				nn.add(btn);
		}
		Random ran = new Random();
		nn.get(ran.nextInt(nn.size())).performClick();
	}
	public interface stepClick{
		public void step(final int index);
	}
	private static class CatchView extends Handler{
		private final WeakReference<BingoGame> mActivity;
		public CatchView(BingoGame BG) {
			// TODO Auto-generated constructor stub
			mActivity = new WeakReference<BingoGame>(BG);
		} 
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			BingoGame mAct = mActivity.get();
			super.handleMessage(msg);
			switch (msg.what){
			case BingoSignal.CONNECT:
				mAct.Debug.setText(mAct.Debug.getText() + "\nConnect");
				break;
			case BingoSignal.GAME_READY:
				mAct.GameStart.performClick();
				break;
			case BingoSignal.STEP:
				for(Button btn:Btns)
					if(btn.getText().toString().compareTo(msg.getData().getString("INDEX")) == 0)
						btn.performClick();
				break;
			case BingoSignal.WIN:
				try {
					mAct.GameFinish("You lose !");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case BingoSignal.TEARDOWN:
				try {
					mAct.GameFinish("Opponent is out !");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}
	/*
	gridview = (GridView) findViewById(R.id.BingoView);
	gridview.setNumColumns(column);
	gridview.setAdapter(new GirdAdapter(BingoGame.this));
	public class GirdAdapter extends BaseAdapter {
	    private Context mContext;
	    private int number = 0;
	    private int[] line;
	    
	    public GirdAdapter(Context c) {
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
		
		
	}
	*/
}
