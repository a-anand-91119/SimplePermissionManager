package in.notyouraveragedev.permissionmanagerproject;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

import in.notyouraveragedev.permissionmanager.PermissionManager;
import in.notyouraveragedev.permissionmanager.PermissionManagerBuilder;
import in.notyouraveragedev.permissionmanager.listener.PermissionResponseListener;
import in.notyouraveragedev.permissionmanager.responses.PermissionResponse;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PermissionManager permissionManager = null;

    private TextView smsTextView;
    private TextView locationTextView;
    private TextView callLogTextView;
    private TextView storageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_all).setOnClickListener(this);
        findViewById(R.id.bt_call).setOnClickListener(this);
        findViewById(R.id.bt_location).setOnClickListener(this);
        findViewById(R.id.bt_sms).setOnClickListener(this);
        findViewById(R.id.bt_storage).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);

        smsTextView = findViewById(R.id.tv_sms);
        locationTextView = findViewById(R.id.tv_location);
        callLogTextView = findViewById(R.id.tv_call);
        storageTextView = findViewById(R.id.tv_storage);

        permissionManager = PermissionManagerBuilder.withContext(this)
                .addPermissionResponseListener(new PermissionResponseListener() {
                    @Override
                    public void singlePermissionResponse(PermissionResponse permissionResponse) {
                        updateTextView(permissionResponse);
                    }

                    @Override
                    public void multiplePermissionResponse(List<PermissionResponse> permissionResponses) {
                        for (PermissionResponse permissionResponse : permissionResponses) {
                            updateTextView(permissionResponse);
                        }
                    }

                }).enableSnackbarForSettings(findViewById(R.id.layout_container)).build();

    }

    private void updateTextView(PermissionResponse permissionResponse) {
        String permissionStatus;
        int color;
        if (permissionResponse.getPermissionStatus() == PermissionManager.PERMISSION_GRANTED) {
            permissionStatus = "GRANTED PERMISSION";
            color = ContextCompat.getColor(this, R.color.green);
        } else if (permissionResponse.getPermissionStatus() == PermissionManager.PERMISSION_DENIED) {
            permissionStatus = "DENIED PERMISSION";
            color = ContextCompat.getColor(this, R.color.red);
        } else if (permissionResponse.getPermissionStatus() == PermissionManager.PERMISSION_PERMANENTLY_DENIED) {
            permissionStatus = "DENIED PERMANENTLY";
            color = ContextCompat.getColor(this, R.color.dark_red);
        } else {
            permissionStatus = "UNKNOWN ERROR";
            color = ContextCompat.getColor(this, R.color.black);
        }

        switch (permissionResponse.getPermission()) {
            case Manifest.permission.CALL_PHONE:
                callLogTextView.setText(permissionStatus);
                callLogTextView.setTextColor(color);
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                locationTextView.setText(permissionStatus);
                locationTextView.setTextColor(color);
                break;
            case Manifest.permission.SEND_SMS:
                smsTextView.setText(permissionStatus);
                smsTextView.setTextColor(color);
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                storageTextView.setText(permissionStatus);
                storageTextView.setTextColor(color);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_all:
                permissionManager.requestPermissions(Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.bt_call:
                permissionManager.requestPermission(Manifest.permission.CALL_PHONE);
                break;
            case R.id.bt_location:
                permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case R.id.bt_sms:
                permissionManager.requestPermission(Manifest.permission.SEND_SMS);
                break;
            case R.id.bt_storage:
                permissionManager.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.bt_next:
                openNextActivity();
                break;

        }
    }

    private void openNextActivity() {
        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }

}
