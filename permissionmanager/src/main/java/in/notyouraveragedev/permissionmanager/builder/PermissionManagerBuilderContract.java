package in.notyouraveragedev.permissionmanager.builder;

import android.view.View;

import in.notyouraveragedev.permissionmanager.PermissionManager;
import in.notyouraveragedev.permissionmanager.PermissionManagerBuilder;
import in.notyouraveragedev.permissionmanager.listener.PermissionResponseListener;

/**
 * Interface specifying the contract to create a {@link PermissionManager}
 * <p>
 * Created by A Anand on 18-05-2020
 */
public interface PermissionManagerBuilderContract {
    PermissionManager build();

    PermissionManagerBuilder enableSnackbarForSettings(View view);

    interface Listener {
        PermissionManagerBuilderContract addPermissionResponseListener(PermissionResponseListener permissionResponseListener);
    }
}
