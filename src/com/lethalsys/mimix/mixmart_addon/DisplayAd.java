package com.lethalsys.mimix.mixmart_addon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayAd extends Activity {
	
	
	private ImageView image=null;
	private TextView title=null;
	private TextView desc=null;
	private TextView price=null;
	private TextView date=null;
	private TextView mail=null;
	private TextView phone=null;

	String user;
	String dtitle;
	String ddesc;
	String dprice;
	String ddate;
	
	//UtilDatabase utilhelper;
	MixMartDatabase helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_ad);
	
	    helper = new MixMartDatabase(this);
		
		image=(ImageView)findViewById(R.id.dispadd_img);
		title=(TextView)findViewById(R.id.dispadd_title);
		desc=(TextView)findViewById(R.id.dispadd_desc);
		price=(TextView)findViewById(R.id.dispadd_price);
		date=(TextView)findViewById(R.id.dispadd_date);
		mail=(TextView)findViewById(R.id.dispadd_mail);
		phone=(TextView)findViewById(R.id.dispadd_phone);
		Intent intent = getIntent();
		   
		user  = intent.getStringExtra(MixMartList.Disp_adUname);
		dtitle= intent.getStringExtra(MixMartList.Disp_adTitle);
		ddesc = intent.getStringExtra(MixMartList.Disp_adDesc);
		dprice = intent.getStringExtra(MixMartList.Disp_adPrice);
		ddate  = intent.getStringExtra(MixMartList.Disp_adDate);
		
		setTitle(dtitle);
		
		DisplayAd(dtitle,ddesc,dprice,ddate);
		
	}


	void DisplayAd(String dtitle,String ddesc,String dprice,String ddate)
	{
		

	    if(MixMartList.Disp_adimage!=null)
	    {
	       image.setImageBitmap(BitmapFactory.decodeByteArray(MixMartList.Disp_adimage, 0, MixMartList.Disp_adimage.length));
	    }
	    else
	    {
	    	image.setImageResource(R.drawable.def_postpic);
	    }
		
		
		title.setText(dtitle);
		desc.setText(ddesc);
		price.setText(dprice);
		date.setText(ddate);
		phone.setText(helper.getCommPhn(user));
		mail.setText(helper.getCommMail(user));
		
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_add, menu);
		return true;
	}*/

}
