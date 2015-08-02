package be.hcpl.android.filmtag;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * main entry point of app
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_CONTENT = "current_content";
    /**
     * app preferences, only kept here
     */
    private SharedPreferences prefs;

    /**
     * the currently visible fragment
     */
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // As we're using a Toolbar, we should retrieve it and set it
        // to be our ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        // load the prefs here
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FragmentManager fm = getSupportFragmentManager();
        // always starts with the same initial fragment
//        fm.beginTransaction().replace(R.id.container, FilmRollListFragment.newInstance()).commit();
        // restore last fragment state if possible
        if (savedInstanceState != null) {
            mContent = fm.getFragment(savedInstanceState, KEY_CURRENT_CONTENT);
        }

        if (mContent == null && fm.getFragments() == null || fm.getFragments().isEmpty() || fm.getFragments().size() == 1) {
            mContent = getInitialFragment();
        }

        if (mContent != null && !mContent.isAdded()) {
            switchContent(mContent);
        }
    }

    private Fragment getInitialFragment() {
        return FilmRollListFragment.newInstance();
    }

    /**
     * use for changing currently visible fragment
     * @param fragment
     */
    public void switchContent(Fragment fragment) {
        // store current fragment
        mContent = fragment;
        // switch content with history
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        // false by default
        setHomeAsUp(false);
    }

    /**
     * use for preferences
     * @return
     */
    public SharedPreferences getPrefs() {
        return prefs;
    }

    @Override
    public void onBackPressed() {
        // finish if content is the film Roll overview fragment
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof FilmRollListFragment) {
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
