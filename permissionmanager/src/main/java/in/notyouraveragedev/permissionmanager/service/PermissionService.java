package in.notyouraveragedev.permissionmanager.service;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import in.notyouraveragedev.permissionmanager.PermissionManager;

/**
 * The wrapper class for all static calls to the Android Permission System
 * <p>
 * Created by A Anand on 17-05-2020
 */
public class PermissionService {

    /**
     * Method to check whether a given permission has been granted or not.
     *
     * @param context    the activity context
     * @param permission the permission to check
     * @return the current status of the permission
     */
    public static int checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return PermissionChecker.checkSelfPermission(context, permission);
    }

    /**
     * Method to make a permission request to the android permission system.
     * All permission requests are made from the transparent activity RequestPermissionsActivity
     *
     * @param activity    the activity from which permission has been requested.
     * @param permissions the permissions to be reqeusted
     * @param requestCode the permission request code
     */
    public static void requestPermissions(
            @Nullable Activity activity, @NonNull String[] permissions, int requestCode) {
        if (activity == null)
            return;

        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * Wrapper on top of android shouldShowRequestPermissionRationale()
     *
     * @param activity   the activity
     * @param permission the permission to checks
     * @return shouldShowRequestPermissionRationale
     */
    private static boolean shouldShowRequestPermissionRationale(
            @Nullable Activity activity, @NonNull String permission) {
        if (activity == null)
            return false;

        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * Method to check whether the permission was denied by choosing "Don't ask again".
     *
     * @param activity   the activity
     * @param permission the permission to check
     * @return true if permission was denied by selecting "Don't ask again", otherwise return false
     */
    public static boolean isPermissionPermanentlyDenied(@NonNull Activity activity, @NonNull String permission) {
        return checkSelfPermission(activity, permission) != PermissionManager.PERMISSION_GRANTED &&
                !shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * Method to check whether permission was denied previously.
     *
     * @param activity   the activity
     * @param permission the permission to check
     * @return true if permission has been denied previously,
     * and false if permission has been denied by choosing "Don't ask again"
     */
    public static boolean isPermissionDeniedPreviously(@NonNull Activity activity, @NonNull String permission) {
        return shouldShowRequestPermissionRationale(activity, permission);
    }
}
