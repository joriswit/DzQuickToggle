package nl.joriswit.dzquicktoggle;

import android.app.Activity;
import android.os.Bundle;

public class ToggleSwitchActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idx = getIntent().getExtras().getInt("idx", 0);
        new SwitchCommandTask(getApplicationContext()).execute(idx);
        finish();
    }
}
