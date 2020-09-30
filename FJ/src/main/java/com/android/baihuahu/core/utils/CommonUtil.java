/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.baihuahu.core.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.act_other.LoginActivity;
import com.android.baihuahu.dialogfragment.OneBtDialogFragment;
import com.android.baihuahu.exception.NoSDCardException;
import com.android.baihuahu.App;

import java.io.File;

/**
 * 常用工具类
 *
 * @author flan
 * @date 2015年11月11日
 */
public class CommonUtil {

    private static final String TAG = CommonUtil.class.getSimpleName();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] READ_PHONE_STATE = {
            Manifest.permission.READ_PHONE_STATE};

    /**
     * 坚持系统是否有读写SDCard的权限
     *
     * @param activity
     */
    public static void requestStoragePermissions(BaseFgActivity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 木有权限
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 是否有读写手机硬件信息 如设备id，手机版本
     *
     * @param activity
     */
    public static void verifyStatePermissions(Activity activity) {
        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.READ_PHONE_STATE);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, READ_PHONE_STATE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 获取文件下载存放基础路径
     *
     * @return
     */
    public static String getFileLoadBasePath() throws NoSDCardException {

        String path;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "ngame" + File.separator +
                    "download" + File.separator;
        } else {
            throw new NoSDCardException("设备上没有找到SDCard");
        }
        return path;
    }

    /**
     * 获取SD卡上的Android/文件夹路径
     *
     * @return
     */
    public static String getSystemAndroidPath() throws NoSDCardException {

        String path;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "Android" + File.separator;
        } else {
            throw new NoSDCardException("设备上没有找到SDCard");
        }
        return path;
    }

    /**
     * 获取图片存放基础路径
     *
     * @return
     */
    public static String getImageBasePath() throws NoSDCardException {

        String path;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "taskallo" + File.separator +
                    "image" + File.separator;
        } else {
            throw new NoSDCardException("设备上没有找到SDCard");
        }
        return path;
    }

    /**
     * 将文件的尺寸，由字节单位转为相应单位表示
     *
     * @param size    文件的尺寸，单位为字节
     * @param decimal 保留小数位数，0表示取整
     * @return
     */
    public static String formatFileSize(double size, int decimal) {

        String sizeStr;
        size = size / 1024;
        if (size > 1024) {
            size = size / 1024;
            if (size > 1024) {

                sizeStr = String.valueOf(size / 1024);
                sizeStr = sizeStr.format("%." + decimal + "f") + "G";
            } else {
                sizeStr = String.valueOf(size);
                sizeStr = sizeStr.format("%." + decimal + "f") + "M";
            }

        } else {
            sizeStr = String.valueOf(size);
            sizeStr = sizeStr.format("%." + decimal + "f") + "K";
        }

        return sizeStr;
    }


    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
