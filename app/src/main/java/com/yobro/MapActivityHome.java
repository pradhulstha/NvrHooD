package com.yobro;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.yobro.JavaClasses.Coordinates;
import com.yobro.JavaClasses.FirebaseHelper;
import com.yobro.JavaClasses.UserProfile;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Firebase Variables
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    FirebaseUser mUser;

    private Toolbar mToolbar;

    //For Map to Load Toggle
    protected static boolean Night_Mode = false;
    Bundle bundle = new Bundle();

    //Button
    Switch onlineBtn;

    //Fragment
    Fragment fragment = new MapFragment();

    //UserProfile Object to Store Retrieved user Info from Database
    private final static String key = "UserData";
    ArrayList<String> retrieveInfo = new ArrayList<>();


    //Navigation Bar Variables
    TextView user_Name;
    TextView user_Email;
    CircleImageView userProfileView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_home);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);

        //Loading the Default Fragment

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        bundle.putBoolean("NightMode", Night_Mode);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction Replace = fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment);
        Replace.commit();

// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        user_Name = headerView.findViewById(R.id.userFirstName);
        user_Email = headerView.findViewById(R.id.userEmail);
        userProfileView = headerView.findViewById(R.id.userProfilePic);

        if (mUser == null) {
            signInSilently();
            Intent loginIntent = new Intent(MapActivityHome.this, LoginAct.class);
            startActivity(loginIntent);
            finish();

        }else{
            Snackbar.make(findViewById(android.R.id.content), "Signed In", Snackbar.LENGTH_SHORT).show();
            getUserProfile();}






            //Swtich
        SwitchCompat drawerSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_item).getActionView();


        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // do stuff
                    Night_Mode = true;
                    fragment = new MapFragment();
                    Snackbar.make(MapActivityHome.this.findViewById(android.R.id.content), "Set Mode On", Snackbar.LENGTH_SHORT).show();

                    bundle.putBoolean("NightMode", Night_Mode);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction Replace = fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment);
                    Replace.commit();


                } else {
                    // do other stuff
                    Night_Mode = false;
                    fragment = new MapFragment();
                    Snackbar.make(MapActivityHome.this.findViewById(android.R.id.content), "Set Mode Off", Snackbar.LENGTH_SHORT).show();

                    bundle.putBoolean("NightMode", Night_Mode);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction Replace = fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment);
                    Replace.commit();

                }
            }
        });

    }

    private void signInSilently() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_SIGN_IN);
        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            GoogleSignInAccount signedInAccount = task.getResult();

                        } else {
                            // Player will need to sign-in explicitly using via UI
                            /*Intent loginIntent = new Intent(MapActivityHome.this, LoginAct.class);
                            startActivity(loginIntent);
                            finish();*/
                        }
                    }
                });
    }


    private void getUserProfile() {


       retrieveInfo = firebaseHelper.getUserInfo();
        Bundle bundle = new Bundle();

        if(!retrieveInfo.isEmpty()){
            user_Name.setText(retrieveInfo.get(0));
            user_Email.setText(retrieveInfo.get(2));
            Uri uri = Uri.parse(retrieveInfo.get(1));
            Picasso.get()
                    .load(uri)
                    .noFade()
                    .into(userProfileView);
            bundle.putStringArrayList(key, retrieveInfo);
            fragment.setArguments(bundle);
        }
        else
        {
            user_Name.setText(getResources().getText(R.string.app_name));
            user_Email.setText(getResources().getText(R.string.action_sign_in_short));
            Uri uri = Uri.parse("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
            Picasso.get()
                    .load(uri)
                    .noFade()
                    .into(userProfileView);

        }


    }

    private void makeUserOffline() {
        Snackbar.make(findViewById(android.R.id.content), "Set Offline", Snackbar.LENGTH_SHORT).show();
    }

    private void makeUserOnline() {

        String personLatitude = "53.00";
        String personLongitude = "-7.77832031";

        Coordinates cord = new Coordinates(personLatitude, personLongitude);

        //if(firebaseHelper.makeUserOnline(cord))
            Snackbar.make(findViewById(android.R.id.content), "Set Online", Snackbar.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_home) {
            // Handle the camera action
            //startActivity(new Intent(this, GoogleMaps.class));
            fragment = new MapFragment();


        } else if (id == R.id.nav_setting) {
            NavUtils.navigateUpFromSameTask(this);

        } else if (id == R.id.nav_history) {
            NavUtils.navigateUpFromSameTask(this);
            Toast.makeText(MapActivityHome.this, "No History to Show", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_signout) {

            signOutUser();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction Replace = fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment );
        Replace.addToBackStack(null).commit();

        item.setChecked(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //
            // item.setIconTintMode(R.color.colorAccent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private void signOutUser() {

        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...

                        Snackbar.make(findViewById(android.R.id.content), "Sign Out Successful", Snackbar.LENGTH_SHORT).show();
                        Intent homeMapIntent = new Intent(MapActivityHome.this, LoginAct.class);
                        startActivity(homeMapIntent);

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();


        mUser = mAuth.getCurrentUser();

        }

    public void popUpMenu(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.START);

    }

    }



