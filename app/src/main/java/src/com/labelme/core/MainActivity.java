package src.com.labelme.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import src.com.labelme.R;
import src.com.labelme.fragment.HomeFragment;
import src.com.labelme.fragment.LabelFragment;
import src.com.labelme.fragment.RatedLabelsFragment;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.SessionManager;

public class MainActivity extends AppCompatActivity {
    private static String TAG;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    // user fields
    private TextView user_name;
    private TextView user_email;

    private SessionManager session;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        doubleBackToExitPressedOnce = false;

        // set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // find our drawer view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        drawerToggle = setupDrawerToggle();

        firstFragment();

        // setup drawer view
        setupDrawerContent(navigationView);
        drawerLayout.setDrawerListener(drawerToggle);

        session = new SessionManager(getApplicationContext());

        user_name = (TextView) findViewById(R.id.user_name);
        user_email = (TextView) findViewById(R.id.user_email);
        user_name.setText(session.getUser_name());
        user_email.setText(session.getUser_email());
    }

    private void firstFragment() {
        // start with home fragment
        Fragment fragment = null;
        Class fragmentClass = HomeFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            TAG = getResources().getString(R.string.title_home);
            getSupportActionBar().setTitle(TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // insert the fragment by replacing any exiting fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(final MenuItem menuItem) {
        // create a new fragment and specify the fragment to show based on position
        Fragment fragment = null;
        Class fragmentClass = null;
        int result = CheckNetwork.isInternetAvailable(this);
        if (result == 1) {
            switch (menuItem.getItemId()) {
                case R.id.home_fragment:
                    fragmentClass = HomeFragment.class;
                    TAG = getResources().getString(R.string.title_home);
                    getSupportActionBar().setTitle(TAG);
                    break;
                case R.id.label_fragment:
                    fragmentClass = LabelFragment.class;
                    TAG = getResources().getString(R.string.title_labels);
                    getSupportActionBar().setTitle(TAG);
                    break;
                case R.id.ratings_label_fragment:
                    fragmentClass = RatedLabelsFragment.class;
                    TAG = getResources().getString(R.string.title_ratings);
                    getSupportActionBar().setTitle(TAG);
                    break;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // insert the fragment by replacing any exiting fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // highlight the selected item, update the title and close the drawer
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
            drawerLayout.closeDrawers();
        } else {
            showSnackBar(result);
        }
    }

    private void showSnackBar(int type) {
        String message = "";
        switch (type) {
            case -1:
                message = getResources().getString(R.string.airplane_mode_on);
                break;
            case 0:
                message = getResources().getString(R.string.connection_off);
                break;
        }
        LinearLayout root_layout = (LinearLayout) findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                settings();
                break;
            case R.id.action_help:
                break;
            case R.id.action_info:
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds item to the toolbar is it's present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void settings() {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    private void logout() {
        session.setLogout(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog);
        //set title
        builder.setMessage(getResources().getString(R.string.logout_question));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_logout);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if this button is clicked, close current activity and clear user session
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getResources().getString(R.string.logout));
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 1500);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if this button is clicked, just close the dialog box and do nothing
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = builder.create();
        // show it
        alertDialog.show();

    }
}
