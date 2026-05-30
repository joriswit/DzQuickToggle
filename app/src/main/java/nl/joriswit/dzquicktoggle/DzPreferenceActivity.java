package nl.joriswit.dzquicktoggle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.NonNull;

public class DzPreferenceActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    LocalNetworkPermissionHelper permissionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getListView().setFitsSystemWindows(true);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && key.equals("server_url")) {
            DzServerUrl serverUrl = new DzServerUrl(this);
            permissionHelper = new LocalNetworkPermissionHelper(this, new LocalNetworkPermissionHelper.PermissionCallback() {
                @Override
                public void onPermissionGranted() { }
            });

            if (permissionHelper.needToAskForPermission(serverUrl)) {
                permissionHelper.checkLocalNetworkPermission();
            }
        }
    }
}
