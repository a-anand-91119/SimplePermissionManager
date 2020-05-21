package in.notyouraveragedev.permissionmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import in.notyouraveragedev.permissionmanager.listener.InternalPermissionListener;
import in.notyouraveragedev.permissionmanager.listener.PermissionResponseListener;
import in.notyouraveragedev.permissionmanager.responses.PermissionResponse;
import in.notyouraveragedev.permissionmanager.service.PermissionService;
import in.notyouraveragedev.permissionmanager.util.Utility;

/**
 * The Permission Manager class handles the operations for single or group permissions requests,
 * and contains the logic to identify the statuses of these permission based on the users selection.
 * <p>
 * If a permission has been denied by the user, then PermissionManager will automatically show an
 * alert dialog with a message to grant the permission request. This message can be configured and can be used to
 * give an explanation to the user so as to why the permission was requested by the application.
 * <p>
 * If a permission has been permanently denied, then PermissionManager can direct the user
 * to the application info page in settings or prompt the same using a snackbar.
 * They can be selected using the {@link PermissionManagerBuilder}. The default choice is opening
 * the application info page directly.
 * <p>
 * PermissionManger uses a {@link PermissionResponseListener} to send permission status to the calling {@link Activity}.
 * This is mandatory and must be specified while building the PermissionManager.
 * <p>
 * All the permission statuses will be shared with calling activity as a {@link PermissionResponse}.
 * <p>
 * Created by A Anand on 17-05-2020
 */
public class PermissionManager {

    /**
     * The permission status codes.
     */
    public static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final int PERMISSION_DENIED = PackageManager.PERMISSION_DENIED;
    public static final int PERMISSION_PERMANENTLY_DENIED = -3;

    /**
     * The permission request codes
     */
    private static final int SINGLE_PERMISSION_REQUEST_CODE = 1001;
    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 1002;
    /**
     * The InternalPermissionListener used to get used permission choices from the
     * transparent activity to PermissionManager.
     */
    static InternalPermissionListener internalPermissionListener;
    /**
     * The context of the activity from which PermissionManager was created
     */
    private Context context;
    /**
     * Implementation of SharedPreference to store details of the permissions
     * that have been denied permanently by the user
     */
    private PreferenceManager preferenceManager;

    /**
     * The view which is to be used by SnackBar to find a parent from.
     */
    private View snackBarContainer;

    /**
     * Constructor to create a PermissionManager using the used selections from {@link PermissionManagerBuilder}
     *
     * @param permissionManagerBuilder the user created {@link PermissionManagerBuilder}
     */
    PermissionManager(PermissionManagerBuilder permissionManagerBuilder) {
        this.context = permissionManagerBuilder.getContext();
        preferenceManager = new PreferenceManager(this.context);
        /*
         * Registering the response Listener
         */
        StaticKeeper.registerListener(this.context.getClass().getSimpleName(),
                permissionManagerBuilder.getPermissionResponseListener());
        /*
         * Listener used to get user choices from transparent activity
         */
        initializeInternalPermissionListener();
        /*
         * The view for snackbar to find a parent.
         * If the view the provided then snackbar will be used to open
         * the application info page in settings, otherwise a toast will be displayed
         * and the application info page will be opened automatically
         *
         */
        snackBarContainer = permissionManagerBuilder.getSnackBarContainer();
    }

    /**
     * Method checks whether a particular permission has been granted or no.
     * If the permission has been granted, then true will be returned, otherwise false
     *
     * @param permission the permission to be checked
     * @return true if the permission has been granted, else false
     */
    public boolean hasPermission(String permission) {
        return PermissionService.checkSelfPermission(this.context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Method checks the status of multiple permissions.
     * The current status of all the permissions passed will be checked,
     * and the result will be returned as a {@link List<PermissionResponse>}
     * Each permission response will contain the permission and its current status.
     * <p>
     * 1. {@link PermissionManager#PERMISSION_GRANTED} = 0
     * 2. {@link PermissionManager#PERMISSION_DENIED} = -1
     * 3. {@link PermissionManager#PERMISSION_PERMANENTLY_DENIED} = -2
     *
     * @param permissions the permissions whose current status needs to be checked
     * @return a list of {@link PermissionResponse}
     */
    public List<PermissionResponse> hasPermissions(String... permissions) {
        // For Nougat and above Streams can be used
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(permissions)
                    .map(permission -> new PermissionResponse(
                            permission,
                            PermissionService.checkSelfPermission(this.context, permission)))
                    .collect(Collectors.toList());
        } else {
            List<PermissionResponse> returnResponse = new ArrayList<>();

            for (String permission : permissions) {
                returnResponse.add(new PermissionResponse(
                        permission, PermissionService.checkSelfPermission(this.context, permission)));
            }
            return returnResponse;
        }
    }

    /**
     * Method to request a permission.
     * If the permission has been denied before then a default message
     * will be displayed before requesting the permission
     *
     * @param permission the permission to be requested
     */
    public void requestPermission(String permission) {
        checkAndRequestPermission(permission, context.getResources().getText(R.string.default_request_message).toString());
    }

    /**
     * Method to request a permission.
     * If the permission has been denied before, then a message will be displayed to user informing
     * as to why this particular permission is needed (requestMessage) before the permission will be requested.
     * If no message is provided then the default message will be used
     *
     * @param permission     the permission to be requested
     * @param requestMessage the reason why the permission is needed
     */
    public void requestPermission(String permission, String requestMessage) {
        if (requestMessage == null || requestMessage.isEmpty())
            requestMessage = context.getResources().getText(R.string.default_request_message).toString();

        checkAndRequestPermission(permission, requestMessage);
    }

    /**
     * Method to request a group of permissions.
     * Before making a request the permissions are classified into
     * 1. Denied Permissions
     * 2. Permanently Denied Permissions and
     * 3. New Permission Requests
     * <p>
     * Permanently denied permissions must be granted from settings manually by used.
     * The application info page will be opened for such permission only if there are no other permission
     * belonging to any other groups
     * <p>
     * Denied permissions and new permissions are requested together
     * <p>
     * For denied permissions an alert with default message will be displayed before making permission request
     * similar to simple permission request
     *
     * @param permissions the permissions to be requested
     */
    public void requestPermissions(String... permissions) {
        checkAndRequestPermissions(permissions, context.getResources().getText(R.string.default_request_message).toString());
    }

    /**
     * Method to request a group of permissions.
     * Before making a request the permissions are classified into
     * 1. Denied Permissions
     * 2. Permanently Denied Permissions
     * 3. New Permission Requests and
     * 4. Granted Permissions
     * <p>
     * Permanently denied permissions must be granted from settings manually by used.
     * The application info page will be opened for such permission only if there are no other permission
     * belonging to any other groups
     * <p>
     * Denied permissions and new permissions are requested together
     * <p>
     * Granted permissions will not be requested again
     * <p>
     * For denied permissions an alert with specified message will be displayed before making permission request
     * similar to simple permission request
     *
     * @param requestMessage the message to show before requesting previously denied permissions
     * @param permissions    the permissions that needs to be requested
     */
    public void requestPermissions(String requestMessage, String[] permissions) {
        if (requestMessage == null || requestMessage.isEmpty())
            requestMessage = context.getResources().getText(R.string.default_request_message).toString();

        checkAndRequestPermissions(permissions, requestMessage);
    }

    /**
     * Method checks previous permission requests and chooses an operation.
     * If the permission has not been requested before, then the permission will be requested
     * <p>
     * If the permission has been denied by the user, then an message will be displayed to the user
     * informing why this permission is required giving user the choice to continue with permission
     * request or to stop it.
     * <p>
     * If the permission has been denied before by selected the "Don't Ask Again" option, then
     * user will be directed to the application info page in settings to grant the permission.
     *
     * @param permission     the permission to be requested
     * @param requestMessage the message explaining why the permission is needed by the application
     */
    private void checkAndRequestPermission(String permission, String requestMessage) {
        if (PermissionService.checkSelfPermission(this.context, permission) == PackageManager.PERMISSION_GRANTED) {
            preferenceManager.removePermissionPermanentlyDeniedStatus(permission);
        }
        if (PermissionService.isPermissionDeniedPreviously((Activity) context, permission)) {
            showAlertDialog(SINGLE_PERMISSION_REQUEST_CODE, requestMessage, new String[]{permission});
        } else if (preferenceManager.isPermissionPreviouslyPermanentlyDenied(permission)) {
            openSettings(SINGLE_PERMISSION_REQUEST_CODE, new String[]{permission});
        } else {
            requestAllPermissions(SINGLE_PERMISSION_REQUEST_CODE, new String[]{permission});
        }
    }

    /**
     * For group permissions the incoming permissions are classified into
     * 1. Denied Permissions
     * 2. Permanently Denied Permissions and
     * 3. New Permission Requests
     * <p>
     * Permanently denied permissions must be granted from settings manually by used.
     * The application info page will be opened for such permission only if there are no other permission
     * belonging to any other groups
     * <p>
     * Denied permissions and new permissions are requested together
     * <p>
     * Granted permissions will not be requested again
     *
     * @param permissions    the permissions to be requested
     * @param requestMessage explanation for why the permission has been requested
     */
    private void checkAndRequestPermissions(String[] permissions, String requestMessage) {
        List<String> previouslyDeniedPermissions = new ArrayList<>();
        List<String> permanentlyDeniedPermissions = new ArrayList<>();
        List<String> newPermissionRequests = new ArrayList<>();

        /*
         * Categorizing the permissions
         */
        for (String permission : permissions) {
            if (PermissionService.checkSelfPermission(this.context, permission) == PackageManager.PERMISSION_GRANTED) {
                // Granted permissions can be ignored
                preferenceManager.removePermissionPermanentlyDeniedStatus(permission);
            } else if (PermissionService.isPermissionDeniedPreviously((Activity) context, permission)) {
                // Permissions that have been denied previously by the user
                previouslyDeniedPermissions.add(permission);
            } else if (preferenceManager.isPermissionPreviouslyPermanentlyDenied(permission)) {
                // Permissions that have been permannently denied
                permanentlyDeniedPermissions.add(permission);
            } else {
                // permissions that have not been requested before
                newPermissionRequests.add(permission);
            }
        }

        if (previouslyDeniedPermissions.size() > 0) {
            // New and already denied permissions can be requested together after an alert dialog
            previouslyDeniedPermissions.addAll(newPermissionRequests);
            showAlertDialog(MULTIPLE_PERMISSION_REQUEST_CODE, requestMessage, previouslyDeniedPermissions.toArray(new String[0]));
        } else if (newPermissionRequests.size() > 0) {
            // if there are not denied permissions then, permissions can be requested directly
            requestAllPermissions(MULTIPLE_PERMISSION_REQUEST_CODE, newPermissionRequests.toArray(new String[0]));
        } else if (permanentlyDeniedPermissions.size() > 0) {
            // if all the permissions have been denied permanently, then open application info page
            openSettings(MULTIPLE_PERMISSION_REQUEST_CODE, permanentlyDeniedPermissions.toArray(new String[0]));
        }
    }


    /**
     * Method to make permission requests.
     * This method starts a new Transparent Activity called RequestPermissionsActivity.
     * This permission request will be performed using this activity's context
     * and using the overridden onRequestPermissionsResult()
     * the user choices are returned back to the internalPermissionListener
     * <p>
     * The data passed to this activity are
     * 1. INTENT_OPERATION: INTENT_OPERATION_REQUEST (To make permission requests)
     * 2. INTENT_PERMISSIONS: The permissions to be requested
     * 3. INTENT_PERMISSION_REQUEST_CODE: Permission request code
     * 4. INTENT_CALLING_ACTIVITY: Name of activity from which PermissionManager was created
     *
     * @param requestCode the permission request code
     * @param permissions the permissions to be requested
     */
    private void requestAllPermissions(int requestCode, String[] permissions) {
        Intent requestPermissionIntent = new Intent(context, RequestPermissionsActivity.class);
        requestPermissionIntent.putExtra(Constants.INTENT_OPERATION, Constants.INTENT_OPERATION_REQUEST);
        requestPermissionIntent.putExtra(Constants.INTENT_PERMISSIONS, permissions);
        requestPermissionIntent.putExtra(Constants.INTENT_PERMISSION_REQUEST_CODE, requestCode);
        requestPermissionIntent.putExtra(Constants.INTENT_CALLING_ACTIVITY, context.getClass().getSimpleName());
        context.startActivity(requestPermissionIntent);
    }

    /**
     * Method to open the application info page in settings.
     * If the PermissionManager was created by enabling SnackBar, then a SnackBar will be displayed
     * having the action button to open the application info page.
     * <p>
     * If SnackBar was not enabled by providing the view to find parent while building the PermissionManager,
     * then a Toast will be displayed and the user will be taken directly to the application info page in settings.
     *
     * @param requestCode the permission request code
     * @param permissions the permissions to be requested
     */
    private void openSettings(int requestCode, String[] permissions) {
        if (snackBarContainer != null)
            displaySnackBar(permissions, requestCode);
        else
            showToast(permissions, requestCode);
    }

    /**
     * Method to show a toast and open the application info page in settings.
     *
     * @param permissions the permissions to be requested
     * @param requestCode the permission request code
     */
    private void showToast(String[] permissions, int requestCode) {
        Toast.makeText(context, R.string.manual_permission_message, Toast.LENGTH_LONG).show();
        openPermissionSettings(permissions, requestCode);
    }

    /**
     * Method to display a SnackBar with the option to open application page in settings.
     * If the user doesn't choose to grant the exceptions and the snack bar is dismissed then
     * a {@link PermissionManager#PERMISSION_DENIED} will be returned.
     * <p>
     * If the user choose to goto settings, then the changes will be returned (only for the requested permission)
     *
     * @param permissions the permission being requested
     * @param requestCode the permission request code
     */
    private void displaySnackBar(String[] permissions, int requestCode) {
        Snackbar.make(snackBarContainer, R.string.manual_permission_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_button_text, v -> openPermissionSettings(permissions, requestCode))
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        int[] grantResults = new int[permissions.length];
                        Arrays.fill(grantResults, Constants.PERMISSON_SKIPPED);
                        notifyActivity(requestCode, permissions, grantResults, context.getClass().getSimpleName());
                    }

                    @Override
                    public void onShown(Snackbar transientBottomBar) {
                        super.onShown(transientBottomBar);
                    }
                })
                .show();
    }

    /**
     * Method to open the application info page in settings. This operations is actually
     * implemented in the transparent activity {@link RequestPermissionsActivity}.
     * <p>
     * The data passed to this activity are
     * 1. INTENT_OPERATION: INTENT_OPERATION_SETTINGS (To open the application info page in settings)
     * 2. INTENT_PERMISSION_REQUEST_CODE: Permission request code
     * 3. INTENT_PERMISSIONS: The permissions
     *
     * @param permissions the permissions to be requested
     * @param requestCode the permission request code
     */
    private void openPermissionSettings(String[] permissions, int requestCode) {
        Intent requestSettingsIntent = new Intent(context, RequestPermissionsActivity.class);
        requestSettingsIntent.putExtra(Constants.INTENT_OPERATION, Constants.INTENT_OPERATION_SETTINGS);
        requestSettingsIntent.putExtra(Constants.INTENT_PERMISSION_REQUEST_CODE, requestCode);
        requestSettingsIntent.putExtra(Constants.INTENT_PERMISSIONS, permissions);
        requestSettingsIntent.putExtra(Constants.INTENT_CALLING_ACTIVITY, context.getClass().getSimpleName());
        context.startActivity(requestSettingsIntent);
    }

    /**
     * Method displays a custom alert to the user explaining why the requested permission is needed
     * for the application to work normally.
     *
     * @param requestCode    the permission request code
     * @param requestMessage the reason explaining why the requested permission is needed
     * @param permissions    the permission being requested
     */
    private void showAlertDialog(int requestCode, String requestMessage, final String[] permissions) {
        // Inflating the alert dialog custom layout
        ViewGroup viewGroup = ((Activity) this.context).findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this.context)
                .inflate(in.notyouraveragedev.permissionmanager.R.layout.alert_layout, viewGroup, false);
        Dialog customDialog = new Dialog(this.context, R.style.Theme_AppCompat_Dialog);

        // Setting the explanation in alert text view
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(requestMessage);

        // Setting the image drawable for single and group permission requests
        if (permissions.length > 1)
            ((ImageView) dialogView.findViewById(R.id.iv_permission_logo))
                    .setImageDrawable(Utility.getDrawableImage(this.context, ""));
        else
            ((ImageView) dialogView.findViewById(R.id.iv_permission_logo))
                    .setImageDrawable(Utility.getDrawableImage(this.context, permissions[0]));

        // Adding onclick listener to "Not Now" button
        dialogView.findViewById(R.id.bt_not_now)
                .setOnClickListener(view -> {
                    customDialog.dismiss();
                    int[] grantResults = new int[permissions.length];
                    Arrays.fill(grantResults, Constants.PERMISSON_SKIPPED);
                    notifyActivity(requestCode, permissions, grantResults, this.context.getClass().getSimpleName());
                });

        // Adding onclick listener to "Continue" button
        dialogView.findViewById(R.id.bt_continue)
                .setOnClickListener(view -> {
                    customDialog.dismiss();
                    requestAllPermissions(requestCode, permissions);
                });

        customDialog.setContentView(dialogView);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();
    }

    /**
     * Implementation for the {@link InternalPermissionListener}.
     * Whenever the internal permission listener is notified, based on the type of permission request,
     * the current status of the requested permissions are returned to the calling activity that called
     * {@link PermissionManager}. The responses of the permissions are returned to Application Activity
     * that initiated the request as a {@link List<PermissionResponse>} through the {@link PermissionResponseListener}.
     * <p>
     * <p>
     * For Each Permission,
     * <p>
     * 1. If the permissions are marked as {@link Constants#PERMISSON_SKIPPED}
     * (NOT NOW of alert clicked or SnackBar popup ignored), then the status of that permission is set as denied
     * <p>
     * 2. If the permission was not granted and denied by choosing "Don't Ask Again", then
     * the status of the permission is set as {@link PermissionManager#PERMISSION_PERMANENTLY_DENIED}
     * and its details are stored in a
     * {@link android.content.SharedPreferences} using {@link in.notyouraveragedev.simplepreference.SimplePreferenceManager}
     * <p>
     * 3. Otherwise the obtained {@code grantResults} is set as the status of the permission
     *
     * @param requestCode     the permission request code
     * @param permissions     the permissions being requested
     * @param grantResults    the current status of the permissions
     * @param callingActivity the name of the activity to which response needs to be sent
     */
    private void notifyActivity(int requestCode, String[] permissions, int[] grantResults, String callingActivity) {

        if (requestCode == SINGLE_PERMISSION_REQUEST_CODE) {
            String permission = permissions[0];
            PermissionResponse permissionResponse;

            if (grantResults[0] == Constants.PERMISSON_SKIPPED) {
                // Permission skipped
                permissionResponse = new PermissionResponse(permission, PERMISSION_DENIED);
            } else if (grantResults[0] != PERMISSION_GRANTED &&
                    PermissionService.isPermissionPermanentlyDenied((Activity) this.context, permission)) {
                // Permission permanently denied
                permissionResponse = new PermissionResponse(permission, PERMISSION_PERMANENTLY_DENIED);
                // Permanently denied permissions details are stored in a SharedPreference for future use
                preferenceManager.permissionPermanentlyDenied(permission);
            } else {
                permissionResponse = new PermissionResponse(permission, grantResults[0]);
            }
            StaticKeeper.getListener(callingActivity).singlePermissionResponse(permissionResponse);

        } else if (requestCode == MULTIPLE_PERMISSION_REQUEST_CODE) {
            List<PermissionResponse> permissionResponses;
            // Using streams for Nougat and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                permissionResponses = IntStream.range(0, permissions.length)
                        .mapToObj(i -> {
                            if (grantResults[i] == Constants.PERMISSON_SKIPPED) {
                                // Permission request skipped
                                return new PermissionResponse(permissions[i], PERMISSION_DENIED);
                            } else if (grantResults[i] != PERMISSION_GRANTED &&
                                    PermissionService.isPermissionPermanentlyDenied((Activity) this.context, permissions[i])) {
                                // Permission permanently denied
                                // Permanently denied permissions details are stored in a SharedPreference for future use
                                preferenceManager.permissionPermanentlyDenied(permissions[i]);
                                return new PermissionResponse(permissions[i], PERMISSION_PERMANENTLY_DENIED);
                            }
                            // Setting obtained status as permission status
                            return new PermissionResponse(permissions[i], grantResults[i]);
                        })
                        .collect(Collectors.toList());
            } else {
                permissionResponses = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++)
                    if (grantResults[i] == Constants.PERMISSON_SKIPPED) {
                        // Permission request skipped
                        permissionResponses.add(new PermissionResponse(permissions[i], PERMISSION_DENIED));
                    } else if (grantResults[i] != PERMISSION_GRANTED &&
                            PermissionService.isPermissionPermanentlyDenied((Activity) this.context, permissions[i])) {
                        // Permission permanently denied
                        permissionResponses.add(new PermissionResponse(permissions[i], PERMISSION_PERMANENTLY_DENIED));
                        // Permanently denied permissions details are stored in a SharedPreference for future use
                        preferenceManager.permissionPermanentlyDenied(permissions[i]);
                    } else {
                        // Setting obtained status as permission status
                        permissionResponses.add(new PermissionResponse(permissions[i], grantResults[i]));
                    }
            }
            // Notifying the listener from Application Activity
            StaticKeeper.getListener(callingActivity).multiplePermissionResponse(permissionResponses);
        }
    }

    /**
     * Method to initialize the {@link InternalPermissionListener}
     */
    private void initializeInternalPermissionListener() {
        internalPermissionListener = this::notifyActivity;
    }

}
