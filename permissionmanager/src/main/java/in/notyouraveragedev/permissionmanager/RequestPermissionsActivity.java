package in.notyouraveragedev.permissionmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Objects;

import in.notyouraveragedev.permissionmanager.listener.InternalPermissionListener;
import in.notyouraveragedev.permissionmanager.service.PermissionService;

/**
 * The transparent activity used by Permission Manager to make permission requests
 * to the Android Permission System and to Open the Application Info Page in settings.
 * <p>
 * A separate activity is used so that the results (overriding methods onRequestPermissionsResult() and onActivityResult())
 * can be obtained and given back to PermissionManager.
 * <p>
 * All data are returned to PermissionManager using the InternalPermissionListener of Permission Manager.
 * <p>
 * Created by A Anand on 18-05-2020
 */
public class RequestPermissionsActivity extends AppCompatActivity {

    /**
     * The internal permission listener from PermissionManager
     */
    private InternalPermissionListener internalPermissionListener = PermissionManager.internalPermissionListener;

    /**
     * The permissions to be requested
     */
    private String[] permissions;

    /**
     * The permission request code
     */
    private int requestCode;

    /**
     * The calling activity name
     */
    private String callingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        /*
         * Getting the Intent Extra data.
         * These include the type of operation to perform:
         *                  INTENT_OPERATION  [INTENT_OPERATION_SETTINGS / INTENT_OPERATION_REQUEST]
         * The permission to work with: INTENT_PERMISSIONS
         * The request code to use: INTENT_PERMISSION_REQUEST_CODE
         */
        Bundle extras = Objects.requireNonNull(getIntent().getExtras());
        permissions = extras.getStringArray(Constants.INTENT_PERMISSIONS);
        requestCode = extras.getInt(Constants.INTENT_PERMISSION_REQUEST_CODE, Constants.DEFAULT_REQUEST_CODE);
        callingActivity = extras.getString(Constants.INTENT_CALLING_ACTIVITY);

        if (Objects.equals(extras.get(Constants.INTENT_OPERATION), Constants.INTENT_OPERATION_SETTINGS)) {
            /*
             * The operation to perform is to open the Application Info page in settings
             * so that the user can grant the permissions manually
             */
            Intent permissionIntent = new Intent();
            permissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(Constants.URI_SCHEME, getPackageName(), null);
            permissionIntent.setData(uri);
            startActivityForResult(permissionIntent, requestCode);

        } else if (Objects.equals(extras.get(Constants.INTENT_OPERATION), Constants.INTENT_OPERATION_REQUEST)) {
            /*
             * The operation to be performed is to request for permissions
             */
            PermissionService.requestPermissions(this, Objects.requireNonNull(permissions), requestCode);
        }
        Log.e("Test", String.valueOf(getCallingActivity()));
    }

    /**
     * The method to get the result of startActivityForResult() on application info settings page.
     * This method has been overridden to know when the user has returned back to the transparent activity.
     * The user might have granted the permissions or not.
     * <p>
     * Once the user returns back to this RequestPermissionsActivity, a check is made to know the current status
     * of the permissions requested by PermissionManager. These results are then provided to the PermissionManager
     * through the internal permission listener.
     * <p>
     * Once the listener has been notified, the transparent activity is finished
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult()
     * @param resultCode  The integer result code returned by the child activity
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.permissions == null || this.permissions.length == 0) {
            Log.e("Permission Manager", "Permissions array is null / empty: " + Arrays.toString(this.permissions));
        } else if (internalPermissionListener == null) {
            Log.e("Permission Manager", "InternalPermissionListener is null");
        } else if (this.requestCode == requestCode) {
            /*
             * Gets the current status of the permissions and notify the listener
             */
            prepareAndSendResults(this.requestCode, this.permissions, null);
        }
        finish();
    }

    /**
     * Overridden the method to pass the responses back to PermissionManager using the InternalPermissionListener.
     * Once the listener has been notified, the transparent activity is finished
     *
     * @param requestCode  the permission request code
     * @param permissions  the permissions requested
     * @param grantResults the status of reqeusted permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        prepareAndSendResults(requestCode, permissions, grantResults);
        finish();
    }

    /**
     * Method notifies the listener regarding the current status of the permissions.
     * If the permission status are not available, then they are fetched manually
     *
     * @param requestCode  the permission request code
     * @param permissions  the permissions needed
     * @param grantResults the current permission status
     */
    private void prepareAndSendResults(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults == null) {
            grantResults = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                grantResults[i] = PermissionService.checkSelfPermission(this, permissions[i]);
            }
        }
        /*
         * Notifying the listener
         */
        internalPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults, callingActivity);
    }

}
