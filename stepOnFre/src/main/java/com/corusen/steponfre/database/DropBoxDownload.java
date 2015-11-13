package com.corusen.steponfre.database;
//package com.corusen.steponfre.database;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//
//import android.content.DialogInterface.OnClickListener;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.widget.Toast;
//
//
//import com.corusen.steponfre.R;
//import com.dropbox.client2.DropboxAPI;
//import com.dropbox.client2.DropboxAPI.Entry;
//import com.dropbox.client2.exception.DropboxException;
//import com.dropbox.client2.exception.DropboxIOException;
//import com.dropbox.client2.exception.DropboxParseException;
//import com.dropbox.client2.exception.DropboxPartialFileException;
//import com.dropbox.client2.exception.DropboxServerException;
//import com.dropbox.client2.exception.DropboxUnlinkedException;
//
//public class DropBoxDownload extends AsyncTask<Void, Long, Boolean>
//{
//    private Activity mContext;
//    private final ProgressDialog mDialog;
//    private DropboxAPI<?> mApi;
//    private String mPath;
//    ArrayList<Entry> thumbs = new ArrayList<Entry>();
//    ArrayList<DropBoxBean> dbb = new ArrayList<DropBoxBean>();
//    private FileOutputStream mFos;
//
//    private boolean mCanceled;
//    private Long mFileLen;
//    private String mErrorMsg;
//
//    //private final static String IMAGE_FILE_NAME = "dbroulette.txt";
//
//    public DropBoxDownload(Activity context, DropboxAPI<?> api,String dropboxPath)
//    {
//        mContext = context;
//        mApi = api;
//        mPath = dropboxPath;
//        mDialog = new ProgressDialog(context);
//        mDialog.setMessage(mContext.getString(R.string.dropbox_downloading));
//        mDialog.setButton("Cancel", new OnClickListener()
//        {
//        	public void onClick(DialogInterface dialog, int which)
//        	{
//                mCanceled = true;
//                mErrorMsg = mContext.getString(R.string.cancelled); //"Canceled";
//                
//                if (mFos != null)
//                {
//                	try
//                	{
//                        mFos.close();
//                    }
//                	catch (IOException e)
//                	{
//                    }
//                }
//            }
//        });
//        mDialog.show();
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... params)
//    {
//        try
//        {
//            if (mCanceled)
//            {
//                return false;
//            }
//
//            Entry dirent = mApi.metadata(mPath, 1000, null, true, null);
//
//            if (!dirent.isDir || dirent.contents == null)
//            {
//                // It's not a directory, or there's nothing in it
//                mErrorMsg = mContext.getString(R.string.google_drive_file_not_exist); //"File or empty directory";
//                return false;
//            }
//
//            
//            for (Entry ent: dirent.contents) {
//            	if(ent.fileName().matches(Constants.DATABASE_FILENAME))
//            	{
//            		thumbs.add(ent);
//            		String url = "dl.dropbox.com/u/" + mApi.accountInfo().uid + "/" + ent.fileName();
//            		dbb.add(new DropBoxBean(ent.fileName(),url));
//                	//System.out.println("Public URL: dl.dropbox.com/u/" + mApi.accountInfo().uid + "/" + ent.fileName());
//                	//System.out.println("Entries from dropbox: "+mContext.getCacheDir().getAbsolutePath() +""+ent.path);
//                }
//            	//System.out.println("File exists:" + ent.fileName());
//            }
//
//            if (mCanceled) {
//                return false;
//            }
//
//            
//            if (thumbs.size() == 0) {
//                mErrorMsg = mContext.getString(R.string.google_drive_file_not_exist); //"Accupedo.db does not exist";
//                return false;
//            }
//            
//            Entry ent = thumbs.get(0);
//            mFileLen = ent.bytes;
//            
//            File data = Environment.getDataDirectory();
//            //String cachePath = mContext.getCacheDir().getAbsolutePath() + "/" + Constants.DATABASE_NAME;
//            String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
//					+ Constants.DATABASE_NAME;
//            File currentDB = new File(data, currentDBPath);
//            
//            try
//            {
//                mFos = new FileOutputStream(currentDB);
//            }
//            catch (FileNotFoundException e)
//            {
//            	mErrorMsg = mContext.getString(R.string.dropbox_error_message7);
//            	//mErrorMsg = "Couldn't create a local file to store the image";
//                return false;
//            }
//            
//            mApi.getFileStream(ent.path,null).copyStreamToOutput(mFos,null);
//            
////			FileChannel src = new FileInputStream(cachePath).getChannel();
////			FileChannel dst = new FileOutputStream(currentDBPath).getChannel();
////			dst.transferFrom(src, 0, src.size());
////			src.close();
////			dst.close();
//            
//            if (mCanceled) {
//                return false;
//            }
//            
//            return true;
//
//        } catch (DropboxUnlinkedException e) {
//            // The AuthSession wasn't properly authenticated or user unlinked.
//        } catch (DropboxPartialFileException e) {
//            // We canceled the operation
//            mErrorMsg = "Download canceled";
//        } catch (DropboxServerException e) {
//            // Server-side exception.  These are examples of what could happen,
//            // but we don't do anything special with them here.
//            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
//                // won't happen since we don't pass in revision with metadata
//            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
//                // Unauthorized, so we should unlink them.  You may want to
//                // automatically log the user out in this case.
//            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
//                // Not allowed to access this
//            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
//            	System.out.println("File Is Not Found: " + e);
//                // path not found (or if it was the thumbnail, can't be
//                // thumbnailed)
//            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
//                // too many entries to return
//            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
//                // can't be thumbnailed
//            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
//                // user is over quota
//            } else {
//                // Something else
//            }
//            // This gets the Dropbox error, translated into the user's language
//            mErrorMsg = e.body.userError;
//            if (mErrorMsg == null) {
//                mErrorMsg = e.body.error;
//            }
//        } catch (DropboxIOException e) {
//            // Happens all the time, probably want to retry automatically.
//            mErrorMsg = "Network error.  Try again." + e;
//        } catch (DropboxParseException e) {
//            // Probably due to Dropbox server restarting, should retry
//            mErrorMsg = "Dropbox error.  Try again.";
//        } catch (DropboxException e) {
//            // Unknown error
//            mErrorMsg = "Unknown error.  Try again.";
//        } 
//        return false;
//    }
//
//    @Override
//    protected void onProgressUpdate(Long... progress) {
//        int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
//        mDialog.setProgress(percent);
//    }
//
//    @Override
//    protected void onPostExecute(Boolean result) {
//        mDialog.dismiss();
//        if (result)
//        {
////        	Iterator<DropBoxBean> iter = dbb.iterator();
////        	while(iter.hasNext())
////        	{
////        		DropBoxBean d = iter.next();
////        		System.out.println("File: " + d.getFileName() + ". URL: " + d.getPublicURL());
////        	}
////            Intent intent = new Intent(this.mContext,myListView.class);
////            intent.putExtra("obj",dbb);
////            mContext.startActivity(intent);
////            mContext.finish();
//            
//        } else {
//            // Couldn't download it, so show an error
//            showToast(mErrorMsg);
//        }
//    }
//
//    private void showToast(String msg) {
//        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
//        System.out.println(msg);
//        error.show();
//    }
//
//
//}
