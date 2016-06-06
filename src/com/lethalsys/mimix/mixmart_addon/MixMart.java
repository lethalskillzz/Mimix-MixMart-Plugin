package com.lethalsys.mimix.mixmart_addon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MixMart extends Activity {
	public final static String EXTRA_CAT = "com.lethalsys.mimix.CAT";

	GridView gridView;

	static final String[] MOBILE_OS = new String[] { "Electronics - Gadgets", "Books - Literature", "Jobs - Services", "Sports - Hobbies", "Beauty - Fashion", "Food - Provisions" };
	
	Gridadapter Adapter;


	private FrameLayout MixMart_wrapper;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mix_mart);
		
		MixMart_wrapper  = (FrameLayout) findViewById(R.id.MixMart_wrapper);
		
		
		//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.fadeactionbarbg));
        try{
		Resources rc = getPackageManager().getResourcesForApplication("com.lethalsys.mimix");
		getActionBar().setBackgroundDrawable(rc.getDrawable(rc.getIdentifier("com.lethalsys.mimix:drawable/fadeactionbarbg","drawable", null)));
		MixMart_wrapper.setBackgroundDrawable(rc.getDrawable(rc.getIdentifier("com.lethalsys.mimix:drawable/metro","drawable", null)));

        }catch(NameNotFoundException e)
        {      	
        }
        
        getActionBar().setDisplayHomeAsUpEnabled(true);

		
		Adapter = new Gridadapter(this, MOBILE_OS);
		
		gridView = (GridView) findViewById(R.id.mixmart_gridView);

		gridView.setAdapter(Adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
			
			if(((TextView) v.findViewById(R.id.comm_label)).getText()!=null)
	    	{
				String cat = ((TextView)v.findViewById(R.id.comm_label)).getText().toString();
		        GoComm(cat);
	    	}

			}
		});

	
		
	}
	
	
	
	public void GoComm(String cat)
	{
		
    	Intent intent = new Intent(this, MixMartList.class);
    	intent.putExtra(EXTRA_CAT, cat);
    	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_TASK_ON_HOME);
    	startActivity(intent);
	}

	
	public void GoSubmitAdd(View v)
	{
    	Intent intent = new Intent(this, SubmitAd.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    	startActivity(intent);
	}
	
	
	

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.commerce, menu);
		return true;
	}*/
	
	
	
	
    class Gridadapter extends BaseAdapter {
	private Context context;
	private final String[] activity;

	public Gridadapter(Context context, String[] activity) {
		this.context = context;
		this.activity = activity;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);

			gridView = inflater.inflate(R.layout.comm_grid, null);

			TextView textView = (TextView) gridView.findViewById(R.id.comm_label);
			
			textView.setText(activity[position]);

			ImageView flag = (ImageView) gridView .findViewById(R.id.comm_item);

			String mobile = activity[position];

			if (mobile.equals("Electronics - Gadgets")) {
				flag.setImageResource(R.drawable.electronics_gadgets);
			} else if (mobile.equals("Beauty - Fashion")) {
				flag.setImageResource(R.drawable.beauty_fashion);			
			} else if (mobile.equals("Jobs - Services")) {
				flag.setImageResource(R.drawable.jobs_services);			
			} else if (mobile.equals("Books - Literature")) {
				flag.setImageResource(R.drawable.books_literature);			
			} else if (mobile.equals("Food - Provisions")) {
				flag.setImageResource(R.drawable.food_provisions);			
			} else if (mobile.equals("Sports - Hobbies")) {
				flag.setImageResource(R.drawable.sports_hobbies);			
			}
			/*else {
				flag.setImageResource(R.drawable.britishflag);
			}*/

		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	@Override
	public int getCount() {
		return activity.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            
            case android.R.id.home:
            	Go_Home();
            	return true;

 

            default:
                return super.onOptionsItemSelected(item);
        }
    } 
    
    
    public void Go_Home() {
    	/*Intent intent = new Intent(this, HomeActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);*/
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
    	}  
    


	

}
