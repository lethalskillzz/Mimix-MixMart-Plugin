package com.lethalsys.mimix.mixmart_addon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MixMartList extends Activity {
	
	public final static String Disp_adUname = "com.lethalsys.mimix.Disp_adUname";
	public final static String Disp_adTitle = "com.lethalsys.mimix.Disp_adTitle";
	public final static String Disp_adDesc = "com.lethalsys.mimix.Disp_adDesc";
	public final static String Disp_adPrice = "com.lethalsys.mimix.Disp_adPrice";
	public final static String Disp_adDate = "com.lethalsys.mimix.Disp_adDate";
	public static byte[] Disp_adimage = null;
	
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;

	private String UserID;
	
	ListView list;
	
	Boolean ErrorDlg = true;
	
	Cursor model=null;
	MixMartDatabase helper=null;
	CommerceListAdapter adapter=null;
	String iresponse;
	InputStream response;
	String cat;
	
	JSONArray data = null;
	
	//private LinearLayout Loader;
	public ProgressBar CProgress;
	public TextView CProgresstxt;
	
	Boolean isLoading = false;
    Boolean isDONE = false;

	LinearLayout emptyview;
	 

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_commerce_list);
		
		FrameLayout MixMartList_wrapper  = (FrameLayout) findViewById(R.id.MixMartList_wrapper);

		
        try{
		Resources rc = getPackageManager().getResourcesForApplication("com.lethalsys.mimix");
		getActionBar().setBackgroundDrawable(rc.getDrawable(rc.getIdentifier("com.lethalsys.mimix:drawable/fadeactionbarbg","drawable", null)));
		MixMartList_wrapper.setBackgroundDrawable(rc.getDrawable(rc.getIdentifier("com.lethalsys.mimix:drawable/metro","drawable", null)));

        }catch(NameNotFoundException e)
        {      	
        }
 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//this.setProgressBarIndeterminate(true);
		
		UserID = MixMartService.UserID;	 
		
		helper=new MixMartDatabase(this);
		
		
		Intent intent = getIntent();
	    cat = intent.getStringExtra(MixMart.EXTRA_CAT);
	    
	    setTitle(cat);	
		
		list=(ListView)findViewById(R.id.comm_list);
		
		/*Loader =(LinearLayout)findViewById(R.id.comm_loader);
		Loader.setVisibility(View.GONE);*/
		
		  LayoutInflater LInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		   emptyview = (LinearLayout) LInflater.inflate(
					R.layout.lv_set_empty, null, false);
			
		
        LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout mFooterView = (LinearLayout) mInflater.inflate(
				R.layout.loader_footer, null, false);
		
		CProgress  = (ProgressBar) mFooterView.findViewById(R.id.row_loading);
		//CProgress.setVisibility(View.GONE);
		 
		CProgresstxt  = (TextView) mFooterView.findViewById(R.id.row_loading_txt);
		        
		list.addFooterView(mFooterView);
		list.setFooterDividersEnabled(false);
		
		helper.ClearComm();
		helper.ClearCommPostLoc();
		
		model=helper.getAllComm();
		startManagingCursor(model);
		adapter=new CommerceListAdapter(model);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(onAddClick);
		list.setOnScrollListener(onscroll);
		
		//get_ads("get_ads_cat");
		if(isDONE==false)
		{ 
			get_ads("refresh_ads_cat");
			isDONE=true;
		}

	}
	
	
	public void onResume()
	{
		super.onResume();
		ErrorDlg = true;	
	    helper.ClearCommPostLoc();
	}
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.commerce_list, menu);
		return true;
	}*/
	
	
	
	
	private OnScrollListener onscroll= new OnScrollListener() {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
		if(list.getAdapter().getCount()>0)
		{
			if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1
					&& list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {

				if(list.getFirstVisiblePosition() == 0 && (list.getChildCount()==0 || list.getChildAt(0).getTop() == 0))
				{
					
				}else{
					
				if(isLoading==false)
				{ 
					get_ads("get_ads_cat");
				}
				
				}
			}
		}
		else
		{
			if(isDONE==false)
			{ 
				get_ads("refresh_ads_cat");
				isDONE=true;
			}
		}
			
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
		}};
	
	
	
	
	private AdapterView.OnItemClickListener onAddClick=new
			AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
			View view, int position,
			long id) {
			model.moveToPosition(position);
			String duname = helper.getCommUser(model);
			String dtitle = helper.getCommTitle(model);
			String ddesc = helper.getCommDesc(model);
			String dprice = helper.getCommPrice(model);
			String ddate = helper.getCommDate(model);
			
			if(helper.getCommIMG(model).equals("YES")) 
			{
				Disp_adimage = helper.getCommAd_Img(model);
				
			    
		    }
			else
			{
				Disp_adimage = null;
			}
			
		    
			Go_AdDisplay(duname,dtitle,ddesc,dprice,ddate);
			
			}
			};
			
			
			
			
			public void Go_AdDisplay(String uname, String title, String desc, String price, String date)
			{
		    	Intent intent = new Intent(this, DisplayAd.class);
		    	//intent.addFlags(Intent.);
		    	intent.putExtra(Disp_adUname, uname);
		    	intent.putExtra(Disp_adTitle, title);
		    	intent.putExtra(Disp_adDesc, desc);
		    	intent.putExtra(Disp_adPrice, price);
		    	intent.putExtra(Disp_adDate, date);
		    	startActivity(intent);
			}
		



	public void get_ads(final String Header)
	{
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        
       new AsyncTask<String, Integer, Double>() {
    	
	   @Override
	   protected void onPreExecute() {
		 super.onPreExecute();
		 isLoading = true;
		 
	   /*pDialog = new ProgressDialog(HomeActivity.this);
		pDialog.setMessage("loging out...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();*/
	    //setProgressBarVisibility(true);
		//Loader.setVisibility(View.VISIBLE);
		CProgress.setVisibility(View.VISIBLE);
		CProgresstxt.setVisibility(View.VISIBLE);

		
	}
	
	@Override
	protected Double doInBackground(String... params) {
	
    
		String url = MixMartService.SERVER+"get_ads.php"; 
		
		String query=null;

		try {
			
			if(Header.equals("refresh_ads_cat"))
			{
			     query = String.format("PacketHead=%s&UID=%s&cat=%s", 
			     URLEncoder.encode(Header, "UTF-8"),
			     URLEncoder.encode(UserID, "UTF-8"),
			     URLEncoder.encode(cat.trim(), "UTF-8"));
			     
			}
			
			else
			{
			     
			     query = String.format("PacketHead=%s&UID=%s&cat=%s&limit=%s", 
			     URLEncoder.encode(Header, "UTF-8"),
			     URLEncoder.encode(UserID, "UTF-8"),
			     URLEncoder.encode(cat.trim(), "UTF-8"),
			     URLEncoder.encode(String.valueOf(helper.getCommPostLoc()) , "UTF-8"));
		
			}

		
		/*URLConnection connection = new URL(url + "?" + query).openConnection();
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		InputStream response = connection.getInputStream()*/
		   
 
	    URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(30000);
		//connection.setDoInput(true);
		java.io.OutputStream out=connection.getOutputStream();
		out.write(query.getBytes("UTF-8"));
		
		response = connection.getInputStream();
		
		out.flush();
		out.close();

		/*String mResponse=null; 
		
		BufferedReader br = new BufferedReader(new InputStreamReader(response));
		StringBuilder sb = new StringBuilder();
		while((mResponse = br.readLine())!=null)
		{
			sb.append(mResponse);
		}
		br.close();
		
		response.close();
		
		iresponse = sb.toString();
		sb.setLength(0);*/
		
		/*ByteArrayOutputStream oas = new ByteArrayOutputStream();
		copyStream(response,oas); 
		
		iresponse = oas.toString();
		oas.close();
		oas = null;*/
		
		iresponse = IOUtils.toString(response);
		
		}catch (IOException e) {
			if(ErrorDlg == true)
			ShowNoNetErrorDialoag();
		} 
		
   

	 
	

	return null;
	}
	
   	protected void onProgressUpdate(Integer... progress){

   	}
 
	
	@SuppressWarnings("deprecation")
	protected void onPostExecute(Double result)
	{
	
		if(null != iresponse)
		{
			if(iresponse.trim().length()!=0)
			{
			
			
	  try{
		  
			JSONObject jsonObj = new JSONObject(iresponse.trim());      	
        	
        	if(jsonObj.isNull("ads")==false)
        	{
        		//ShowTost("hurray");
        	data = jsonObj.getJSONArray("ads");
        		
        	if(data.length()>0)
        	{
        		
             	if(jsonObj.isNull("extra")==false)
            	{
             		helper.ClearComm();
           		    helper.ClearCommPostLoc();
        		    helper.insertCommPostLoc(data.length());
        		    
        		    //ShowTost(String.valueOf(data.length()));
            	}
             	else
             	{
             		 int Incr = helper.getCommPostLoc()+data.length();
            		 helper.ClearCommPostLoc();
            		 helper.insertCommPostLoc(Incr);
            		 
         		    //ShowTost(String.valueOf(data.length()));

             	}
             	
			
        	
            for(int i=0;i<data.length();i++)
            {
            	JSONObject c = data.getJSONObject(i);
            	String ad_id = c.getString("ad_id");
            	String user = c.getString("user");
            	String title = c.getString("title");
            	String description = c.getString("description");
            	String price = c.getString("price");
            	String ad_img = c.getString("ad_img");
            	String stamp = c.getString("stamp");
            	String IMG = c.getString("IMG");
            	String email = c.getString("email");
            	String phone = c.getString("phone");
         	
            	
            	//ShowTost(phone+email);
                	
                 	if(IMG.compareTo("NO")==0)
                	{                	
            		    helper.insertComm(ad_id,user,title,description,price,null,stamp,"NO",email,phone);            			
            	    } 
                	else
                	{
                    	byte[] imgdata = Base64.decode(ad_img, 0);
                		
                		helper.insertComm(ad_id,user,title,description,price,imgdata,stamp,"YES",email,phone);
                		
                	}
               	
            
            }
            
			}
        	else if(data.length()<1)
    		{
    		}
        	
        	}
    		
            model.requery();
			
		 } catch (JSONException e){
         	
         }
			
		}
		
		             
		}
		isLoading = false;
		//ShowTost(iresponse);
		//ShowErrorDialoag();
		//Loader.setVisibility(View.GONE);
		CProgress.setVisibility(View.GONE);
		CProgresstxt.setVisibility(View.GONE);
		
		if(adapter.isEmpty())
		{
			list.removeFooterView(emptyview);
		    list.addFooterView(emptyview);
		}
		else
		{
			list.removeFooterView(emptyview);
		}
		

	
	}
    
    }.execute();
    
        }
        else
        {
			if(ErrorDlg == true)
				ShowNoNetErrorDialoag();
        }	
	    
	    
	}
	
	  
    


@SuppressLint("ValidFragment")
public class NetworkErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(iresponse)//LoginActivity.neterrormsg)
        //builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
               .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   
                	   get_ads("get_ads_cat");
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}


public void ShowErrorDialoag() {
    DialogFragment newFragment = new NetworkErrorDialogFragment();
    newFragment.show(getFragmentManager(), "neterror");
}



@SuppressLint("ValidFragment")
public class NoNetErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setMessage(LoginActivity.noneterrormsg)
        builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
               .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	
                		get_ads("get_ads_cat");
           			
                
                   }
               })
               
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	  
                   }
               });
              
        // Create the AlertDialog object and return it
        return builder.create();
    }
}  


public void ShowNoNetErrorDialoag() {
    DialogFragment newFragment = new NoNetErrorDialogFragment();
    newFragment.show(getFragmentManager(), "noneterror");
}







class CommerceListAdapter extends CursorAdapter {

@SuppressWarnings("deprecation")
CommerceListAdapter(Cursor c) {
super(MixMartList.this,c);
}
@Override
public void bindView(View row, Context ctxt,Cursor c) {
CommerceListHolder holder=(CommerceListHolder)row.getTag();
holder.populateFrom(c, helper);
}
@Override
public View newView(Context ctxt, Cursor c,	ViewGroup parent) {
LayoutInflater inflater=getLayoutInflater();
View row=inflater.inflate(R.layout.comm_row, parent, false);
CommerceListHolder holder=new CommerceListHolder(row);
row.setTag(holder);
return(row);
}

}    





static class CommerceListHolder {
private ImageView commimg=null;
private TextView commdate=null;
private TextView commtitle=null;
private TextView commuser=null;
private TextView commprice=null;
//private View row=null;
CommerceListHolder(View row) {
//this.row=row;

commdate=(TextView)row.findViewById(R.id.commrow_date);
commimg=(ImageView)row.findViewById(R.id.commrow_img);
commtitle=(TextView)row.findViewById(R.id.commrow_title);
commuser=(TextView)row.findViewById(R.id.commrow_user);
commprice=(TextView)row.findViewById(R.id.commrow_price);

}


void populateFrom(Cursor c, MixMartDatabase helper) {
	commdate.setText(helper.getCommDate(c));
	commtitle.setText(helper.getCommTitle(c));
	
	Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
	String atMentionScheme = "profile://";
	 

	TransformFilter transformFilter = new TransformFilter() {
	        //skip the first character to filter out '@'
	        public String transformUrl(final Matcher match, String url) {
	                return match.group(1);
	        }
	};
	 
	commuser.setText("@"+helper.getCommUser(c));

	if(helper.getCommUser(c).equals(MixMartService.USER))
	{
	Linkify.addLinks(commuser, atMentionPattern, "myprofile://", null, transformFilter); 
	}
	else
	{
	Linkify.addLinks(commuser, atMentionPattern, atMentionScheme, null, transformFilter); 
	}

	commprice.setText(helper.getCommPrice(c));
	


if(helper.getCommIMG(c).equals("YES"))
{
	commimg.setImageBitmap(BitmapFactory.decodeByteArray(helper.getCommAd_Img(c), 0, helper.getCommAd_Img(c).length));
}
else
{
	commimg.setImageResource(R.drawable.def_postpic);//ic_dummy_img);
}


}
} 



public void ShowTost(String txt)
{
	Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
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
	startActivity(intent);
	}*/  
    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
	}  

}
