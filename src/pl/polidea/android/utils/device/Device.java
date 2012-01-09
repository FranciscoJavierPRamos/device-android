/*
 Copyright (c) 2011, POLIDEA
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package pl.polidea.android.utils.device;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * Class for getting basic info about a device like: android version, screen size, does device supports SMS sending, etc. 
 */
public class Device {

    public static final int SDK_VERSION = Integer.parseInt(Build.VERSION.SDK);
    
    public static boolean isSDCardMOunted() {
        return Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState());
    }
    
    public static boolean isTablet(Context context) {
        final Configuration conf = context.getResources().getConfiguration();
        final int size = conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return size == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    
    public static int getScreenWidth(Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }
    
    public static Intent buildSendSMSIntent(String number, String message) {
        final String uri = "smsto:" + number;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        intent.putExtra("sms_body", message);
        intent.putExtra("compose_mode", true);
        return intent;
    }
    
    public static boolean canSendSMS(Context context) {
        return buildSendSMSIntent("1111", "text message").resolveActivity(context.getPackageManager()) != null;
    }
    
    public static int getScreenHeight(Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
    
    public synchronized static String udid(Context context) {
        final Object telephonyObj = context.getSystemService(Context.TELEPHONY_SERVICE);
        if ( telephonyObj != null && telephonyObj instanceof TelephonyManager ) {
            final String imei = ((TelephonyManager)telephonyObj).getDeviceId();
            if ( !TextUtils.isEmpty(imei) ) {
                return "IMEI_" + imei;
            }
        }
        final String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if ( !TextUtils.isEmpty(androidId) ) {
            return "ANDROID_ID_" + androidId;
        }
        return "UUID_" + installationUDID(context);
    }
        
    private static String installationUDID(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences("installation", 0);
        final String udid = prefs.getString("udid", null);
        if ( TextUtils.isEmpty(udid) ) {
            final String newUDID = UUID.randomUUID().toString();
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString("udid", newUDID);
            editor.commit();
            return newUDID;
        } else {
            return udid;
        }
    }
}
