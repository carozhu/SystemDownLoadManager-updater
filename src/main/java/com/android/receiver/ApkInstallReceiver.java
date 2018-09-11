package com.android.receiver;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.android.updater.UpdateLogger;
import com.android.updater.UpdaterUtils;

import java.io.File;

public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long localDownloadId = UpdaterUtils.getLocalDownloadId(context);
            Log.i("ApkInstallReceiver","update ..................localDownloadId："+localDownloadId+" downloadApkId:"+downloadApkId);
            if (downloadApkId == localDownloadId) {
                UpdateLogger.get().d("download complete. downloadId is " + downloadApkId);
                installApk(context, downloadApkId);
            }
        }
    }



    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Activity context) {
        //注意这个是8.0新API
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void installApk(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        String apkPath = getRealFilePath(context,downloadFileUri);
        OpenNewVersion(context,apkPath);

    }

    /**
     * Try to return the absolute file path from the given Uri
     * Android 读取拍照或相册uri 转换成图片的绝对路径
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    private static void OpenNewVersion(Context context,String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getUriFromFile(context,location), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);


    }

    private static Uri getUriFromFile(Context context,String location) {

        if(Build.VERSION.SDK_INT<24){
            return   Uri.fromFile(new File(location ));
        }
        else{
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", new File(location));
        }
    }
}
