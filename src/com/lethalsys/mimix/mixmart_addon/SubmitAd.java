package com.lethalsys.mimix.mixmart_addon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class SubmitAd extends Activity {
	
	// number of images to select
	private static final int PICK_IMAGE = 1;
	private Uri outputFileUri;
	private ImageView image;
	private Bitmap bitmap;
	byte[] data;
	String file=null;
	
	String UserID;
	
	String response;
	
	Boolean ErrorDlg = true;
	
	Spinner  cat;
	EditText title;
	EditText desc;
	EditText price;
	
	ProgressDialog pDialog;
	
	public static ConnectivityManager mConnectivityManager;
	public static NetworkInfo mNetworkInfo;
	
	JSONArray Jdata = null;
	String msg;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_ad);
		
	    UserID = MixMartService.UserID;
	    //ShowTost(UserID);
	    
	    cat   = (Spinner)findViewById(R.id.submitadd_cat);
		image = (ImageView)findViewById(R.id.submitadd_img);
		title = (EditText)findViewById(R.id.submitadd_title);
		desc  = (EditText)findViewById(R.id.submitadd_desc);
		price = (EditText)findViewById(R.id.submitadd_price);
		
		

	       Spinner spinner = (Spinner) findViewById(R.id.submitadd_cat);
	       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cat_array, android.R.layout.simple_spinner_item);
	       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       spinner.setPrompt("Choose Category...");
	       spinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter,R.layout.cat_spinner_row_nothing_selected,
	        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional 
	       this));

		
	}
	
	
	
	@Override
	public void onResume() {
	super.onResume();
	ErrorDlg = true;
	}
	
	
	@Override
	public void onPause() {
	super.onPause();
	ErrorDlg = false;
	}
	
	
	
	
	public void el_submit(View v)
	{
     if(file!=null)
     {
	  submit_ad("submit_ad_img");
     }
     else
     {
      submit_ad("submit_ad");
     }
	}

	
	public void get_add_img(View v)
	{
		openImageIntent();
	}
	
	private void openImageIntent() {
	// Determine Uri of camera image to save.
	final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Mimix"+File.separator);
	root.mkdirs();
	final String fname = "img_mimix"+System.currentTimeMillis()+".jpg";//Utils.getUniqueImageFilename();
	final File sdImageMainDirectory = new File(root, fname);
	outputFileUri = Uri.fromFile(sdImageMainDirectory);
	    // Camera.
	    final List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    final PackageManager packageManager = getPackageManager();
	    final List<ResolveInfo> listCam = packageManager.queryIntentActivities
	(captureIntent, 0);
	    for(ResolveInfo res : listCam) {
	        final String packageName = res.activityInfo.packageName;
	        final Intent intent = new Intent(captureIntent);
	        intent.setComponent(new ComponentName
	(res.activityInfo.packageName, res.activityInfo.name));
	        intent.setPackage(packageName);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        cameraIntents.add(intent);
	    }
	    // Filesystem.
	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/*");
	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
	    // Chooser of filesystem options.
	    final Intent chooserIntent = Intent.createChooser(galleryIntent, "SelectSource");
	    // Add the camera options.
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
	cameraIntents.toArray(new Parcelable[]{}));
	    startActivityForResult(chooserIntent, PICK_IMAGE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    if(resultCode == RESULT_OK)
	    {
	        if(requestCode == PICK_IMAGE)
	        {
	            final boolean isCamera;
	            if(data == null)
	            {
	                isCamera = true;
	            }
	            else
	            {
	                final String action = data.getAction();
	                if(action == null)
	                {
	                    isCamera = false;
	                }
	                else
	                {
	                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	                }
	            }
	            Uri selectedImageUri;
	            if(isCamera)
	            {
	                selectedImageUri = outputFileUri;	
	                decodeFile(selectedImageUri.getPath());
	            }
	            else
	            {
	                selectedImageUri = data == null ? null : data.getData();


		            String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImageUri,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					decodeFile(picturePath);
	            }

	           
	        }
	        
	       
	    }
	}
	

	
	/**
	 * The method decodes the image file to avoid out of memory issues. Sets the
	 * selected image in to the ImageView.
	 * 
	 * @param filePath
	 */
	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 256;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		image.setImageBitmap(bitmap);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, bos);
		data = bos.toByteArray();
		int flag = 0;
		file = Base64.encodeToString(data, flag);//encodeBytes(data);
	}
	
	
	
	public void submit_ad(final String Header)
	{
	    mConnectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

	    if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting())
	    {
        

    	
    new AsyncTask<String, Integer, Double>() {
    	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	    pDialog = new ProgressDialog(SubmitAd.this);
		pDialog.setMessage("Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	@Override
	protected Double doInBackground(String... params) {
	
    
    	// Create a new HttpClient and Post Header
			HttpParams httpparameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpparameters,15000);
		    HttpConnectionParams.setSoTimeout(httpparameters,30000);
		    
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.setParams(httpparameters);
			
    	HttpPost httppost = new HttpPost(MixMartService.SERVER+"submit_ad.php");
		
    	 
    	try {
    		
    	// Add your data	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("PacketHead",Header ));
    	nameValuePairs.add(new BasicNameValuePair("UID",UserID));
    	nameValuePairs.add(new BasicNameValuePair("cat",String.valueOf(cat.getSelectedItem())));
    	nameValuePairs.add(new BasicNameValuePair("title", title.getText().toString()));
    	nameValuePairs.add(new BasicNameValuePair("desc", desc.getText().toString()));
    	nameValuePairs.add(new BasicNameValuePair("price", price.getText().toString()));
    	if(Header.compareTo("submit_ad_img")==0)
    	{	
    	nameValuePairs.add(new BasicNameValuePair("img",file));
    	}
    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	 

    	

    	
    	// Execute HTTP Post Request
    	 response = httpclient.execute(httppost, responseHandler);
    	 
    	 

    	 
    	} catch (ClientProtocolException e) {
    	// TODO Auto-generated catch block
    		//pDialog.dismiss();
    		
    	} catch (IOException e) {
    	// TODO Auto-generated catch block
    		pDialog.dismiss();
			if(ErrorDlg == true)
			ShowErrorDialoag();
    	}
   

	 
	

	return null;
	}
	
   	protected void onProgressUpdate(Integer... progress){

   	}

	
	protected void onPostExecute(Double result){
	
		/*if(response.trim().equals("ad submited"))
		{
			ShowTost("ad submited");
			back();
	    
		}
		else
		{
			ShowTost("ad could not submited");
		}*/
		
		if(null!=response)
		{
			
		if(response.trim().length()!=0)
		{

			try{


				JSONObject jsonObj = new JSONObject(response.trim());               	
				

				if(jsonObj.isNull("data")==false)
				{

					Jdata = jsonObj.getJSONArray("data");

					JSONObject d = Jdata.getJSONObject(0);
					String msg = d.getString("msg");


					if("ad submited".compareTo(msg)==0)
					{
						
						pDialog.dismiss();
						ShowTost("ad submited");
						 ClosMe();
						
						
		            	

					}

					else
					{
						pDialog.dismiss();
						ShowTost("ad could not submited");

					}

				}
			} catch (JSONException e){

			} 



		}		
	
	
	}
	}
    
    }.execute();
    
        }
        else
        {
			if(ErrorDlg == true)
			ShowErrorDialoag();
        }	
	    
	    
	}
	
	
	
/*	
	void back()
	{
    	Intent awtintent = new Intent(this, CommerceActivity.class);
    	awtintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    	startActivity(awtintent); 
    	finish();
	}
*/
	
    @SuppressLint("ValidFragment")
	public class NetworkErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setMessage(LoginActivity.neterrormsg)
            builder.setView(getLayoutInflater().inflate(R.layout.network_error_alert, null))
                   .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	     if(file!=null)
                    	     {
                    		  submit_ad("submit_ad_img");
                    	     }
                    	     else
                    	     {
                    	      submit_ad("submit_ad");
                    	     }
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
    
	
	
	
	public void ShowTost(String txt)
	{
		Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.submit_add, menu);
		return true;
	}
	
    public void ClosMe() {

    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
}

}
