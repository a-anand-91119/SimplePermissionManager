package in.notyouraveragedev.permissionmanager;

import java.util.HashMap;
import java.util.Map;

import in.notyouraveragedev.permissionmanager.listener.PermissionResponseListener;

/**
 * Class to keep track of listeners registered from different activities
 * <p>
 * Created by A Anand on 21-05-2020
 */
class StaticKeeper {

    private static Map<String, PermissionResponseListener> subscribedListeners = new HashMap<>();

    static PermissionResponseListener getListener(String listenerOwner) {
        return subscribedListeners.get(listenerOwner);
    }

    static void registerListener(String listenerOwner, PermissionResponseListener listener) {
        subscribedListeners.put(listenerOwner, listener);
    }
}
