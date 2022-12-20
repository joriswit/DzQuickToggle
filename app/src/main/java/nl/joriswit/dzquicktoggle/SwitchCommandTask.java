package nl.joriswit.dzquicktoggle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class SwitchCommandTask extends AsyncTask<Integer, Void, Boolean>
{
    private final Context context;

    public SwitchCommandTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Integer[] objects) {

        int idx = objects[0];

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = defaultSharedPreferences.getString("server_url", null);
        if (serverUrl != null) {
            if (!serverUrl.contains("://")) {
                serverUrl = "http://" + serverUrl;
            }
            URL url;
            try {
                url = new URL(serverUrl);
            } catch (MalformedURLException e) {
                return false;
            }
            Api api = new Api(url);
            return api.Toggle(idx);
        } else {
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, R.string.switch_success_text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.switch_failed_text, Toast.LENGTH_SHORT).show();
        }
    }
}