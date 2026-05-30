package nl.joriswit.dzquicktoggle;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener, LocalNetworkPermissionHelper.PermissionCallback {

    ArrayList<Switch> items;
    LocalNetworkPermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items = new ArrayList<Switch>();

        refreshList();

        ListView lv = getListView();
        lv.setFitsSystemWindows(true);
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshDatabaseFromServer();
    }

    private void refreshList() {
        DzDatabase.loadItems(this, items);

        setListAdapter(new ArrayAdapter<Switch>(this, R.layout.switch_list_item, android.R.id.text1, items));
    }

    private void refreshDatabaseFromServer() {
        DzServerUrl urlHelper = new DzServerUrl(this);

        URL url = urlHelper.getUrl();
        if (url != null) {
            permissionHelper = new LocalNetworkPermissionHelper(this, this);
            if (!permissionHelper.needToAskForPermission(urlHelper)) {
                new RefreshSwitchesTask().execute(url);
            }
        } else {
            Toast.makeText(this, R.string.no_configuration_error_text, Toast.LENGTH_SHORT).show();
        }
    }

    private class RefreshSwitchesTask extends AsyncTask<URL, Void, ArrayList<Switch>>
    {
        @Override
        protected ArrayList<Switch> doInBackground(URL[] objects) {
            URL url = objects[0];
            Api api = new Api(url);
            return api.GetSwitches();
        }

        protected void onPostExecute(ArrayList<Switch> result) {
            if (result != null) {
                DzDatabase.update(MainActivity.this, result);
            }

            refreshList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_button) {
            Intent intent = new Intent(this, DzPreferenceActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.switch_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_to_launcher_button) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            Switch sw = items.get(index);

            Intent shortcutIntent = new Intent(getApplicationContext(), ToggleSwitchActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            shortcutIntent.putExtra("idx", sw.idx);

            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, Integer.toString(sw.idx))
                    .setIntent(shortcutIntent)
                    .setShortLabel(sw.name)
                    .setLongLabel(sw.name)
                    .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_lightbulb))
                    .build();

            ShortcutManagerCompat.requestPinShortcut(this, shortcut, null);
        }

        return false;
    }

    int switchIdxToUseAfterPermissionGrant;

    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

        Switch sw = items.get(position);
        permissionHelper = new LocalNetworkPermissionHelper(this, this);
        if (permissionHelper.needToAskForPermission(new DzServerUrl(this))) {
            switchIdxToUseAfterPermissionGrant = sw.idx;
            permissionHelper = new LocalNetworkPermissionHelper(this, this);
            permissionHelper.checkLocalNetworkPermission();
        } else {
            new SwitchCommandTask(getApplicationContext()).execute(sw.idx);
        }
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
        new SwitchCommandTask(getApplicationContext()).execute(switchIdxToUseAfterPermissionGrant);
    }
}