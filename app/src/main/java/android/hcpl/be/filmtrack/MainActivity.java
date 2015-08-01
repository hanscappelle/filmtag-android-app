package android.hcpl.be.filmtrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * main entry point of app
 */
public class MainActivity extends AppCompatActivity {

    // TODO fix icon, needs background

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
    }

    public void switchContent(Fragment fragment){
        // TODO see if we need backstack history and so
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

}
