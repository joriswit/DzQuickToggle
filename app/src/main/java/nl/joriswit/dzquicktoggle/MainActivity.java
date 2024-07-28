package nl.joriswit.dzquicktoggle;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    ArrayList<Switch> items;

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

        setListAdapter(new AboutLevelSetAdapter(this, android.R.layout.two_line_list_item, items));
    }

    private void refreshDatabaseFromServer() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String serverUrl = defaultSharedPreferences.getString("server_url", null);
        if (serverUrl != null) {
            if (!serverUrl.contains("://")) {
                serverUrl = "http://" + serverUrl;
            }
            new RefreshSwitchesTask().execute(serverUrl);
        } else {
            Toast.makeText(this, R.string.no_configuration_error_text, Toast.LENGTH_SHORT).show();
        }
    }

    private class RefreshSwitchesTask extends AsyncTask<String, Void, ArrayList<Switch>>
    {
        @Override
        protected ArrayList<Switch> doInBackground(String[] objects) {
            String serverUrl = objects[0];
            URL url;
            try {
                url = new URL(serverUrl);
            } catch (MalformedURLException e) {
                return null;
            }
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

    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        Switch sw = items.get(position);
        new SwitchCommandTask(getApplicationContext()).execute(sw.idx);
    }

    private class AboutLevelSetAdapter extends ArrayAdapter<Switch> {

        public AboutLevelSetAdapter(Context context, int textViewResourceId, List<Switch> items) {
            super(context, textViewResourceId, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.switch_list_item, null);
            }
            Switch item = getItem(position);
            TextView switchname = v.findViewById(android.R.id.text1);
            switchname.setText(item.name);

            return v;
        }
    }
}