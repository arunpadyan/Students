package in.ac.iitm.students;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import in.ac.iitm.students.Fragments.AcademicCalendarFragment;
import in.ac.iitm.students.Fragments.EventsFragment;
import in.ac.iitm.students.Fragments.FeedbackFragment;
import in.ac.iitm.students.Fragments.GalleryFragment;
import in.ac.iitm.students.Fragments.GameradarFragment;
import in.ac.iitm.students.Fragments.MapFragment;
import in.ac.iitm.students.Fragments.ImportantContacts;
import in.ac.iitm.students.Fragments.MessMenuFragment;
import in.ac.iitm.students.Fragments.TheFifthEstateFragment;
import in.ac.iitm.students.Gcm.QuickstartPreferences;
import in.ac.iitm.students.Gcm.RegistrationIntentService;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;
import in.ac.iitm.students.Views.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    RelativeLayout profilePic ;
    DrawerLayout drawer;
    Toolbar toolbar;
    static Menu menu;
    Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);

        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("main activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_map);
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.dispname) ;
        TextView rollno = (TextView) headerView.findViewById(R.id.navrollno) ;
        name.setText(Utils.getprefString(Strings.NAME,this));
        rollno.setText(Utils.getprefString(Strings.ROLLNO,this));

        ImageView circleImageView = (ImageView) headerView.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(getString(R.string.urlImage)+ Utils.getprefString(Strings.ROLLNO,this))
                .centerCrop()
                .crossFade()
                .into(circleImageView);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent intent =new Intent(MainActivity.this,ProfileActivity.class);
              //  startActivity(intent);
            }
        });

        ((CardView) findViewById(R.id.menu_contacts)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_map)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_feedback)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_gameradar)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_messmenu)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_t5)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_academiccalender)).setOnClickListener(this);
        ((CardView) findViewById(R.id.menu_netaccess)).setOnClickListener(this);

      /*  FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new MapFragment());
        fragmentTransaction.commit();*/
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(MainActivity.this));


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.d("hai there", "hai");

        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                Log.d("arun message to you", "token sent to server");
                } else {
                    Log.d("arun message to you", "some error couldn't  sent token to server");
                }
            }
        };
  /*      profilePic =(RelativeLayout) findViewById(R.id.header);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivity = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(startActivity);
            }
        });*/
        restartProfileedit();

    }
    public void openDrawer()
    {
        drawer.openDrawer(Gravity.LEFT);
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main,menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.gameradar_edit_profile) {
            Intent intent = new Intent(this,GameRadarProfileEditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static void hideItem(int index)
    {
        MenuItem mi = menu.getItem(index);
        mi.setVisible(false);

    }

    public static void showItem(int index)
    {
        MenuItem mi = menu.getItem(index);
        mi.setVisible(true);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
           // super.onBackPressed();
        }


        if (doubleBackToExitPressedOnce) {
          //  super.onBackPressed();
            finish();
            return;
        }else {
            this.doubleBackToExitPressedOnce = true;

            ((FrameLayout) findViewById(R.id.fragment_container)).setVisibility(View.GONE);
            ((ScrollView) findViewById(R.id.menu_container)).setVisibility(View.VISIBLE);

            Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }


    }


    public void restartProfileedit(){
        Intent i= getIntent();
        if(i.getBooleanExtra("gameradar",false)){
            ((FrameLayout) findViewById(R.id.fragment_container)).setVisibility(View.VISIBLE);
            ((ScrollView) findViewById(R.id.menu_container)).setVisibility(View.GONE);
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new GameradarFragment());
            fragmentTransaction.commit();

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        ((FrameLayout) findViewById(R.id.fragment_container)).setVisibility(View.VISIBLE);
        ((ScrollView) findViewById(R.id.menu_container)).setVisibility(View.GONE);
        String Action_string="";

        int id = item.getItemId();
        showViews();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_contacts) {
            Action_string ="nav_contacts";
            toolbar.setTitle("Important Contacts");
            fragmentTransaction.replace(R.id.fragment_container, new ImportantContacts());
        } else if (id == R.id.nav_map) {
            Action_string ="nav_map";

            hideViews();
            fragmentTransaction.replace(R.id.fragment_container, new MapFragment());
        } else if  (id == R.id.nav_gallery) {

            fragmentTransaction.replace(R.id.fragment_container, new GalleryFragment());
        }else if (id==R.id.nav_fifthestate){
            toolbar.setTitle("The Fifth Estate");
            Action_string ="nav_t5e";

            fragmentTransaction.replace(R.id.fragment_container, new TheFifthEstateFragment());

        } else if(id==R.id.nav_feedback){
            toolbar.setTitle("FeedBack Portal");
            Action_string ="nav_feedback";

            fragmentTransaction.replace(R.id.fragment_container, new FeedbackFragment());

        }else if(id==R.id.nav_academiccalender){
            toolbar.setTitle("Academic Calendar");
            Action_string ="nav_ac_calender";

            fragmentTransaction.replace(R.id.fragment_container, new AcademicCalendarFragment());

        }else if(id==R.id.nav_messmenu){
            toolbar.setTitle("Mess Menu");
            Action_string ="nav_messmenu";

            fragmentTransaction.replace(R.id.fragment_container, new MessMenuFragment());

        }else if(id==R.id.nav_gameradar){
            toolbar.setTitle("Game Radar");
            Action_string ="nav_game_radar";

            fragmentTransaction.replace(R.id.fragment_container, new GameradarFragment());


        }else if(id==R.id.nav_reportbug){
            Action_string ="nav_bugreport";

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "institutewebops@gmail.com", null));
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            String debug_info = "\n\n\n Device information \n -------------------------------";
            try{
                debug_info += "\n Netaccess App version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            }catch (PackageManager.NameNotFoundException nne){
                Log.e("About", "Name not found exception");
            }
            debug_info += "\n Android Version: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT+ ") \n Model (and product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ") \n Device: " + android.os.Build.DEVICE;

            emailIntent.putExtra(Intent.EXTRA_TEXT, debug_info);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "IITM Students App: Feedback / bug report");
            startActivity(Intent.createChooser(emailIntent, "Send feedback / bug report"));
        }
        else if (id == R.id.nav_netaccess) {
            Action_string ="nav_netaccess";

            Intent intent = new Intent(this,NetaccessActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_share) {
            String string ="https://play.google.com/store/apps/details?id="+getPackageName();
            Intent s = new Intent(android.content.Intent.ACTION_SEND);

            s.setType("text/plain");
            s.putExtra(Intent.EXTRA_SUBJECT, "SAmple");
            s.putExtra(Intent.EXTRA_TEXT, string);

            startActivity(Intent.createChooser(s, "Quote"));
        } else if (id == R.id.web_site) {
            Action_string ="nav_website";

            String url="https://students.iitm.ac.in/";
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
               startActivity(intent);
            }
        }else if (id == R.id.nav_logout){
            Action_string ="nav_logout";

            Utils.clearpref(this);
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        fragmentTransaction.commit();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Navigation Drawer")
                .setAction("hit")
                .setLabel(Action_string)
                .build());

        return true;
    }

    public void hideViews() {
        getSupportActionBar().hide();

    }

    public void showViews() {
        getSupportActionBar().show();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        ((FrameLayout) findViewById(R.id.fragment_container)).setVisibility(View.VISIBLE);
        ((ScrollView) findViewById(R.id.menu_container)).setVisibility(View.GONE);
        int id = v.getId();
        showViews();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        if (id == R.id.menu_contacts) {
            fragmentTransaction.replace(R.id.fragment_container, new ImportantContacts());
        } else if (id == R.id.menu_map) {
            hideViews();
            fragmentTransaction.replace(R.id.fragment_container, new MapFragment());
        } else if  (id == R.id.nav_gallery) {
            fragmentTransaction.replace(R.id.fragment_container, new GalleryFragment());
        }else if (id==R.id.menu_t5){
            fragmentTransaction.replace(R.id.fragment_container, new TheFifthEstateFragment());

        } else if(id==R.id.menu_feedback){
            fragmentTransaction.replace(R.id.fragment_container, new FeedbackFragment());

        }else if(id==R.id.menu_academiccalender){
            fragmentTransaction.replace(R.id.fragment_container, new AcademicCalendarFragment());

        }else if(id==R.id.menu_messmenu){
            fragmentTransaction.replace(R.id.fragment_container, new MessMenuFragment());

        }else if(id==R.id.menu_gameradar){
            fragmentTransaction.replace(R.id.fragment_container, new GameradarFragment());
        }
        else if (id == R.id.menu_netaccess) {
            Intent intent = new Intent(this,NetaccessActivity.class);
            startActivity(intent);
        }
        fragmentTransaction.commit();


    }
}
