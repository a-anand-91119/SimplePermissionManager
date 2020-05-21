package in.notyouraveragedev.permissionmanager.listener;

/**
 * The listener used within the {@link in.notyouraveragedev.permissionmanager.PermissionManager} to
 * get permission status updates from the transparent activity
 * {@link in.notyouraveragedev.permissionmanager.RequestPermissionsActivity}
 * <p>
 * Created by A Anand on 18-05-2020
 */
public interface InternalPermissionListener {
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, String callingActivity);
}
