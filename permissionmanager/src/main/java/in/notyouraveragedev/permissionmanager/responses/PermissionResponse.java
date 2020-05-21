package in.notyouraveragedev.permissionmanager.responses;

/**
 * The wrapper POJO class to provide the permission and its status to calling activity
 * <p>
 * Created by A Anand on 18-05-2020
 */
public class PermissionResponse {
    private String permission;
    private int permissionStatus;

    public PermissionResponse(String permission, int permissionStatus) {
        this.permission = permission;
        this.permissionStatus = permissionStatus;
    }

    public String getPermission() {
        return permission;
    }

    public int getPermissionStatus() {
        return permissionStatus;
    }

}
