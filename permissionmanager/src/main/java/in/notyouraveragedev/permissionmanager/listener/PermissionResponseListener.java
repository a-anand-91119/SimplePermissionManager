package in.notyouraveragedev.permissionmanager.listener;

import java.util.List;

import in.notyouraveragedev.permissionmanager.responses.PermissionResponse;

/**
 * The permission response listener that used by the {@link in.notyouraveragedev.permissionmanager.PermissionManager}
 * to notify the calling activity of permission status changes.
 * <p>
 * Created by A Anand on 18-05-2020
 */
public interface PermissionResponseListener {
    void singlePermissionResponse(PermissionResponse permissionResponse);

    void multiplePermissionResponse(List<PermissionResponse> permissionResponses);
}
