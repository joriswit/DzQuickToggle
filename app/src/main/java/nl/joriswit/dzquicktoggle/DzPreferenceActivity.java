package nl.joriswit.dzquicktoggle;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DzPreferenceActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
