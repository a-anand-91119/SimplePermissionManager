package in.notyouraveragedev.permissionmanager;

import android.content.Context;
import android.view.View;

import in.notyouraveragedev.permissionmanager.builder.PermissionManagerBuilderContract;
import in.notyouraveragedev.permissionmanager.listener.PermissionResponseListener;

/**
 * Builder to create a {@link PermissionManager}.
 * The activity context from which {@link PermissionManager} will be used and
 * a {@link PermissionResponseListener} must be added to create an instance of PermissionMananger.
 * <p>
 * An optional choice to use a SnackBar or a Toast while opening application info page,
 * in case of permanently denied permissions
 * <p>
 * Created by A Anand on 18-05-2020
 */
public class PermissionManagerBuilder implements PermissionManagerBuilderContract, PermissionManagerBuilderContract.Listener {
    /**
     * The activity context
     */
    private Context context;
    /**
     * The permission response listener
     */
    private PermissionResponseListener permissionResponseListener;
    /**
     * The view that snack bar can use to find a parent
     */
    private View snackBarContainer;

    private PermissionManagerBuilder(Context context) {
        this.context = context;
        this.snackBarContainer = null;
    }

    /**
     * Specify the context from which PermissionManager will be used
     *
     * @param context the activity context
     * @return an instance of PermissionManagerBuilder
     */
    public static Listener withContext(Context context) {
        return new PermissionManagerBuilder(context);
    }

    @Override
    public PermissionManager build() {
        return new PermissionManager(this);
    }

    @Override
    public PermissionManagerBuilder enableSnackbarForSettings(View snackBarContainer) {
        this.snackBarContainer = snackBarContainer;
        return this;
    }

    @Override
    public PermissionManagerBuilderContract addPermissionResponseListener(PermissionResponseListener permissionResponseListener) {
        this.permissionResponseListener = permissionResponseListener;
        return this;
    }

    Context getContext() {
        return this.context;
    }

    PermissionResponseListener getPermissionResponseListener() {
        return this.permissionResponseListener;
    }

    View getSnackBarContainer() {
        return snackBarContainer;
    }
}
