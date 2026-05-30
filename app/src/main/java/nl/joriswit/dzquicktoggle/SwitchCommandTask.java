package nl.joriswit.dzquicktoggle;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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

        DzServerUrl urlHelper = new DzServerUrl(this.context);

        URL url = urlHelper.getUrl();
        if (url != null) {
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