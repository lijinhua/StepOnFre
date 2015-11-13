//package com.corusen.steponfre.database;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.List;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.widget.Toast;
//
//import com.corusen.steponfre.R;
//import com.corusen.steponfre.base.Pedometer;
//import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
//import com.google.api.client.http.FileContent;
//import com.google.api.client.http.GenericUrl;
//import com.google.api.client.http.HttpResponse;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
//import com.google.api.services.drive.model.ParentReference;
//
//public class GdriveExportManager {
//	private ProgressDialog mProgressDialog;
//	private Context mContext;
//
//	public GdriveExportManager(Context context) {
//		mContext = context;
//	}
//
//	public void saveFileToDrive(final Drive service) {
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext
//				.getString(R.string.wait_for_exporting_db));
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.show();
//
//		final Handler handlerExport = new Handler() {
//			public void handleMessage(Message msg) {
//				mProgressDialog.dismiss();
//			}
//		};
//
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					// String parentId = findFolderAndCreate(service);
//					java.io.File dataFolder = Environment.getDataDirectory();
//					String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
//							+ Constants.DATABASE_NAME;
//					java.io.File currentDB = new java.io.File(dataFolder,
//							currentDBPath);
//					FileContent mediaContent = new FileContent(
//							"application/x-sqlite3", currentDB);
//					File body = new File();
//					body.setTitle(Constants.DATABASE_FILENAME);
//					body.setMimeType("application/x-sqlite3");
//					// body.setParents(Arrays.asList(new ParentReference()
//					// .setId(parentId)));
//					com.google.api.services.drive.Drive.Files file = service
//							.files();
//					com.google.api.services.drive.Drive.Files.Insert insert = file
//							.insert(body, mediaContent);
//					// showToast("inserted");
//					com.google.api.services.drive.model.File file2 = insert
//							.execute();
//					// showToast("executed");
//					// File file2 = service.files().insert(body, mediaContent)
//					// .execute();
//
//					if (file != null) {
//						showToast(mContext
//								.getString(R.string.google_drive_uploaded)); // "Data file uploaded: "
//																				// +
//																				// file.getTitle());
//					}
//				} catch (UserRecoverableAuthIOException e) {
//					Pedometer.getInstance().startActivityForResult(
//							e.getIntent(),
//							Constants.GDRIVE_REQUEST_AUTHORIZATION);
//				} catch (IOException e) {
//					showToast("error");
//					e.printStackTrace();
//				}
//
//				handlerExport.sendEmptyMessage(0);
//			}
//		});
//		t.start();
//	}
//
//	public void dowloadFilefromDrive(final Drive service) {
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage(mContext
//				.getString(R.string.wait_for_importing_db));
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.show();
//
//		final Handler handlerExport = new Handler() {
//			public void handleMessage(Message msg) {
//				mProgressDialog.dismiss();
//			}
//		};
//
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				InputStream inputStream;
//				FileOutputStream outputStream;
//				try {
//					// String parentId = findFolder(service);
//					// if (parentId == "") {
//					// showToast("Accupedo folder does not exist!");
//					// return;
//					// }
//
//					String fileId = findFile(service);
//					if (fileId == "") {
//						showToast(mContext.getString(R.string.google_drive_file_not_exist));
//						handlerExport.sendEmptyMessage(0);
//						return;
//					}
//
//					File file = service.files().get(fileId).execute();
//
//					// Log.i("FBackup", "Execute file catch");
//					if (file.getDownloadUrl() != null
//							&& file.getDownloadUrl().length() > 0) {
//						HttpResponse resp = service
//								.getRequestFactory()
//								.buildGetRequest(
//										new GenericUrl(file.getDownloadUrl()))
//								.execute();
//						inputStream = resp.getContent();
//						// Log.i("FBackup", "Done inputStream");
//
//						java.io.File data = Environment.getDataDirectory();
//						String currentDBPath = Constants.ACCUPEDO_INTERNAL_DATABASE_PATH
//								+ Constants.DATABASE_NAME;
//						java.io.File currentDB = new java.io.File(data,
//								currentDBPath);
//						outputStream = new FileOutputStream(currentDB);
//						// Log.i("FBackup", "start file writing");
//						int read = 0;
//						byte[] bytes = new byte[1024];
//
//						while ((read = inputStream.read(bytes)) != -1) {
//							outputStream.write(bytes, 0, read);
//						}
//						inputStream.close();
//						outputStream.close();
//						showToast(mContext
//								.getString(R.string.google_drive_downloaded)); // "File imported: "
//																			// +
//																			// file.getTitle());
//					}
//				} catch (IOException e) {
//					// Log.e("WriteToFile", e.toString());
//					e.printStackTrace();
//				}
//
//				handlerExport.sendEmptyMessage(0);
//
//			}
//		});
//		t.start();
//	}
//
//	private String findFolderAndCreate(Drive service) throws IOException {
//		String parentId = "";
//		try {
//			parentId = findFolder(service);
//			if (parentId == "") {
//				File folderBody = new File();
//				folderBody.setTitle(Constants.ACCUPEDO_FOLDERNAMWOSLASH);
//				folderBody.setMimeType("application/vnd.google-apps.folder");
//				File folderFile = service.files().insert(folderBody).execute();
//				parentId = folderFile.getId();
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return parentId;
//	}
//
//	private String findFolder(Drive service) throws IOException {
//
//		String folderId = "";
//		String title = Constants.ACCUPEDO_FOLDERNAMWOSLASH;
//
//		try {
//			com.google.api.services.drive.Drive.Files.List request = service
//					.files()
//					.list()
//					.setQ("mimeType='application/vnd.google-apps.folder' "
//							+ "and title='" + title + "'");
//			// Log.i("FBackup", "Execute folder search");
//			FileList files = request.execute();
//			if (files.getItems().size() == 0) {
//				ParentReference parentReference = new ParentReference();
//				List<ParentReference> parents = Arrays
//						.asList(new ParentReference().setId(folderId));
//				parentReference.setId(folderId);
//				parents.add(parentReference);
//				File body = new File();
//				body.setTitle(title);
//				body.setMimeType("application/vnd.google-apps.folder");
//				body.setParents(parents);
//				File file = service.files().insert(body).execute();
//				folderId = file.getId();
//				// Log.i("FBackup", "Not found folder");
//			} else {
//				folderId = files.getItems().get(0).getId();
//				// Log.i("FBackup", "Found folder:" +
//				// files.getItems().get(0).getTitle());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return folderId;
//	}
//
//	private String findFile(Drive service) throws IOException {
//		String fileId = "";
//		String title = Constants.DATABASE_FILENAME;
//		try {
//			com.google.api.services.drive.Drive.Files.List request = service
//					.files()
//					.list()
//					.setQ("mimeType='application/x-sqlite3' " + "and title='"
//							+ title + "'");
//			// Log.i("FBackup", "Execute file search");
//			FileList files = request.execute();
//			if (files.getItems().size() == 0) {
//				// Log.i("FBackup", "Not found file");
//			} else {
//				fileId = files.getItems().get(0).getId();
//				// Log.i("FBackup", "Found file");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return fileId;
//	}
//
//	public void showToast(final String toast) {
//		Pedometer.getInstance().runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(Pedometer.getInstance(), toast,
//						Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
//
//}
