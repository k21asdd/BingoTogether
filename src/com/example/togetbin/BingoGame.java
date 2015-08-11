package com.example.togetbin;

import android.R.dimen;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

public class BingoGame extends Activity{
	private GridView cheast;
	private ListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_view);
		
		int number = 5;
		
		GridView gridview = (GridView) findViewById(R.id.BingoView);
		gridview.setNumColumns(number);
	    gridview.setAdapter(new ImageAdapter(this,gridview.getWidth()/number-10));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v,
	                int position, long id) {
	            Toast.makeText(BingoGame.this, "" + v.getWidth(),
	                    Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private int width;

	    public ImageAdapter(Context c, int w) {
	        mContext = c;
	        width = w;
	    }

	    public int getCount() {
	        return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // references to our images
	    private Integer[] mThumbIds = {
	            R.drawable.sample_2, R.drawable.sample_3,
	            R.drawable.sample_4, R.drawable.sample_5,
	            R.drawable.sample_6, R.drawable.sample_7,
	            R.drawable.sample_0, R.drawable.sample_1,
	            R.drawable.sample_2, R.drawable.sample_3,
	            R.drawable.sample_4, R.drawable.sample_5,
	            R.drawable.sample_6, R.drawable.sample_7,
	            R.drawable.sample_0, R.drawable.sample_1,
	            R.drawable.sample_2, R.drawable.sample_3,
	            R.drawable.sample_4, R.drawable.sample_5,
	            R.drawable.sample_6, R.drawable.sample_7
	    };

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;

	        if (convertView == null) {
	            // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(width, width));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(5, 5, 5, 5);
	            
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
		}
	}
}
