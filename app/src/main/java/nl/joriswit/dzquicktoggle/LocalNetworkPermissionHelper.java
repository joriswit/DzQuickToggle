package nl.joriswit.dzquicktoggle;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocalNetworkPermissionHelper {

    public interface PermissionCallback {
        void onPermissionGranted();
    }

    private final Activity activity;
    private final PermissionCallback permissionCallback;

    public LocalNetworkPermissionHelper(Activity activity, PermissionCallback permissionCallback) {
        this.activity = activity;
        this.permissionCallback = permissionCallback;
    }

    public boolean needToAskForPermission(DzServerUrl serverUrl) {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CINNAMON_BUN
            && serverUrl.isLocalNetworkUrl()
            && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_LOCAL_NETWORK) != PackageManager.PERMISSION_GRANTED;
    }

    private static final int REQ_LOCAL_NETWORK = 1001;

    public void checkLocalNetworkPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_LOCAL_NETWORK)
                == PackageManager.PERMISSION_GRANTED) {
            permissionCallback.onPermissionGranted();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.ACCESS_LOCAL_NETWORK)) {

                showRationaleDialog();

            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.ACCESS_LOCAL_NETWORK},
                        REQ_LOCAL_NETWORK
                );
            }
        }
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.local_network_permission_needed_title_text)
                .setMessage(R.string.local_network_permission_rationale_text)
                .setPositiveButton(R.string.local_network_permission_allow_button_text, (dialog, which) -> {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.ACCESS_LOCAL_NETWORK},
                            REQ_LOCAL_NETWORK
                    );
                })
                .setNegativeButton(R.string.local_network_permission_cancel_button_text, (dialog, which) -> {
                    Toast.makeText(activity, R.string.local_network_permission_denied_text, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCAL_NETWORK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionCallback.onPermissionGranted();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity, Manifest.permission.ACCESS_LOCAL_NETWORK)) {

                    showGoToSettingsDialog();
                } else {
                    Toast.makeText(activity, R.string.local_network_permission_denied_text, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.local_network_permission_needed_title_text)
                .setMessage(R.string.local_network_permission_open_settings_text)
                .setPositiveButton(R.string.local_network_permission_open_settings_button_text, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                    activity.startActivity(intent);
                })
                .setNegativeButton(R.string.local_network_permission_cancel_button_text, null)
                .show();
    }
}
