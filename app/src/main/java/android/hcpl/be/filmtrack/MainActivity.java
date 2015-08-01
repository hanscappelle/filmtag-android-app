package android.hcpl.be.filmtrack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * main entry point of app
 */
public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // As we're using a Toolbar, we should retrieve it and set it
        // to be our ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        // always starts with the same initial fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, FilmRollListFragment.newInstance()).commit();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void switchContent(Fragment fragment){
        // switch content with history
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        // false by default
        setHomeAsUp(false);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    @Override
    public void onBackPressed() {
        // finish if content is the film Roll overview fragment
        if( getSupportFragmentManager().findFragmentById(R.id.container) instanceof FilmRollListFragment ){
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void setHomeAsUp(final boolean enable) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
            //getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}
