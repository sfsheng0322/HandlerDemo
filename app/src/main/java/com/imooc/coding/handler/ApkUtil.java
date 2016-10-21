package com.imooc.coding.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by sunfusheng on 2016/10/21.
 */

public class ApkUtil {

    public static void install(Context context, String apkPath){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(apkPath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
