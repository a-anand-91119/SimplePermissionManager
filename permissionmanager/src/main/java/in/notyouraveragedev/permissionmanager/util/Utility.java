package in.notyouraveragedev.permissionmanager.util;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import in.notyouraveragedev.permissionmanager.R;

/**
 * Utility class
 * Created by A Anand on 20-05-2020
 */
public class Utility {

    /**
     * Method to select an appropriate icon for alert based on type or permission
     *
     * @param context    the activity context
     * @param permission the permission in question
     * @return a drawable
     */
    public static Drawable getDrawableImage(Context context, String permission) {
        switch (permission) {
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.READ_SMS:
                return ContextCompat.getDrawable(context, R.drawable.baseline_sms_white_48);
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                return ContextCompat.getDrawable(context, R.drawable.baseline_calendar_today_white_48);
            case Manifest.permission.CAMERA:
                return ContextCompat.getDrawable(context, R.drawable.baseline_camera_alt_white_48);
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                return ContextCompat.getDrawable(context, R.drawable.baseline_contacts_white_48);
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return ContextCompat.getDrawable(context, R.drawable.baseline_location_on_white_48);
            case Manifest.permission.RECORD_AUDIO:
                return ContextCompat.getDrawable(context, R.drawable.baseline_record_voice_over_white_48);
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.READ_PHONE_NUMBERS:
            case Manifest.permission.ANSWER_PHONE_CALLS:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
                return ContextCompat.getDrawable(context, R.drawable.baseline_phone_white_48);
            case Manifest.permission.ADD_VOICEMAIL:
                return ContextCompat.getDrawable(context, R.drawable.baseline_voicemail_white_48);
            case Manifest.permission.USE_SIP:
                return ContextCompat.getDrawable(context, R.drawable.baseline_dialer_sip_white_48);
            case Manifest.permission.BODY_SENSORS:
                return ContextCompat.getDrawable(context, R.drawable.baseline_accessibility_new_white_48);
            case Manifest.permission.RECEIVE_MMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
                return ContextCompat.getDrawable(context, R.drawable.baseline_mms_white_48);
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return ContextCompat.getDrawable(context, R.drawable.baseline_storage_white_48);
            default:
                return ContextCompat.getDrawable(context, R.drawable.baseline_security_white_48);
        }
    }
}
