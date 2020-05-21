package in.notyouraveragedev.permissionmanagerproject;

import android.Manifest;
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

public class NextActivity extends AppCompatActivity implements View.OnClickListener {

    private PermissionManager permissionManager = null;

    private TextView bodyTextView;
    private TextView calenderTextView;
    private TextView contactTextView;
    private TextView recordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        findViewById(R.id.bt_next_all).setOnClickListener(this);
        findViewById(R.id.bt_next_body).setOnClickListener(this);
        findViewById(R.id.bt_next_calender).setOnClickListener(this);
        findViewById(R.id.bt_next_contact).setOnClickListener(this);
        findViewById(R.id.bt_next_record).setOnClickListener(this);

        bodyTextView = findViewById(R.id.tv_next_body);
        calenderTextView = findViewById(R.id.tv_next_calender);
        contactTextView = findViewById(R.id.tv_next_contact);
        recordTextView = findViewById(R.id.tv_next_record);

        permissionManager = PermissionManagerBuilder.withContext(this).addPermissionResponseListener(new PermissionResponseListener() {
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
        }).build();
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
            case Manifest.permission.BODY_SENSORS:
                bodyTextView.setText(permissionStatus);
                bodyTextView.setTextColor(color);
                break;
            case Manifest.permission.READ_CALENDAR:
                calenderTextView.setText(permissionStatus);
                calenderTextView.setTextColor(color);
                break;
            case Manifest.permission.READ_CONTACTS:
                contactTextView.setText(permissionStatus);
                contactTextView.setTextColor(color);
                break;
            case Manifest.permission.RECORD_AUDIO:
                recordTextView.setText(permissionStatus);
                recordTextView.setTextColor(color);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_next_all:
                permissionManager.requestPermissions(Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.BODY_SENSORS);
                break;
            case R.id.bt_next_record:
                permissionManager.requestPermission(Manifest.permission.RECORD_AUDIO);
                break;
            case R.id.bt_next_calender:
                permissionManager.requestPermission(Manifest.permission.READ_CALENDAR);
                break;
            case R.id.bt_next_contact:
                permissionManager.requestPermission(Manifest.permission.READ_CONTACTS);
                break;
            case R.id.bt_next_body:
                permissionManager.requestPermission(Manifest.permission.BODY_SENSORS);
                break;
        }
    }
}
