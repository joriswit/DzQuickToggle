package nl.joriswit.dzquicktoggle;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class ToggleSwitchActivity extends Activity implements LocalNetworkPermissionHelper.PermissionCallback {

    private LocalNetworkPermissionHelper permissionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionHelper = new LocalNetworkPermissionHelper(this, this);
        if (permissionHelper.needToAskForPermission(new DzServerUrl(this))){
            permissionHelper = new LocalNetworkPermissionHelper(this, this);
            permissionHelper.checkLocalNetworkPermission();
        } else {
            toggleSwitch();
        }
    }

    private void toggleSwitch()
    {
        int idx = getIntent().getExtras().getInt("idx", 0);
        new SwitchCommandTask(getApplicationContext()).execute(idx);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPermissionGranted() {
        toggleSwitch();
    }
}
