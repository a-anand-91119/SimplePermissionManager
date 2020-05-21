package in.notyouraveragedev.permissionmanager;

import android.content.Context;

import in.notyouraveragedev.simplepreference.SimplePreferenceManager;

/**
 * Preference Manager wrapper class over the {@link SimplePreferenceManager}
 * for all {@link android.content.SharedPreferences} operations
 * <p>
 * Created by A Anand on 19-05-2020
 */
class PreferenceManager {
    /**
     * The SimplePreferenceManager
     */
    private SimplePreferenceManager simplePreferenceManager;

    /**
     * Constructor to initialize an instance of PreferenceManager specifying
     * the {@link android.content.SharedPreferences} file name, using {@link Context#MODE_PRIVATE}
     * operation mode and without any object storage support.
     *
     * @param context the activity context
     */
    PreferenceManager(Context context) {
        simplePreferenceManager = new SimplePreferenceManager.SimplePreferenceManagerBuilder(context)
                .havingFileName(Constants.PREFERENCE_NAME)
                .usingOperationMode(Context.MODE_PRIVATE)
                .build();
    }

    /**
     * Checks whether the permission has been denied permanently or not.
     * If the permission has been denied permanently, then an entry will be
     * saved in shared preference file.
     *
     * @param permission the permission to be checked
     * @return whether the permission has been previously denied permanently
     */
    boolean isPermissionPreviouslyPermanentlyDenied(String permission) {
        return simplePreferenceManager.contains(permission);
    }

    /**
     * Method to mark a permission as permanently denied. An entry will be
     * saved in SharedPreference file for permanently denied permissions
     *
     * @param permission the permanently denied permission
     */
    void permissionPermanentlyDenied(String permission) {
        simplePreferenceManager.saveBoolean(permission, true);
    }

    /**
     * Method to remove the permanently denied status from a permission
     *
     * @param permission the permission to be un-marked
     */
    void removePermissionPermanentlyDeniedStatus(String permission) {
        simplePreferenceManager.removeData(permission);
    }
}
