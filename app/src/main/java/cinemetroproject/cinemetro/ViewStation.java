package cinemetroproject.cinemetro;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import cinemetroproject.cinemetro.util.PictureUtils;

/**
 * Created by kiki__000 on 20-Jul-14.
 * Se ayto to activity anaparistatai to ViewStation
 * Dimiourgeitai otan epilegetai mia stasi apo LinesActivity
 */
public class ViewStation extends ActionBarActivity implements View.OnClickListener {

    private int idStation;
    private int actors; //count of actors
    private int movieImages;
    private TextView textViewTitle;
    private LinearLayout actorsScrollView;
    private TextView textViewDirector;
    private Button directorImage;
    private TextView textViewInfo;
    private ImageButton showInMap;
    private ImageButton goAheadButton;
    private ImageButton share;
    private Button facebookButton;
    private Button twitterButton;
    private Button instagramButton;
    private Button pinterestButton;
    private AlertDialog.Builder dialog;
    private GoogleMap mMap;

    private int imag;
    private ImageView image;
    private  LinearLayout layout1;
    private  LinearLayout general_layout;
    private ViewFlipper vf;
    private TextView info_cast;
    private Station st;
    private Movie mv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sPrefs = getSharedPreferences("myAppsPreferences", 0);
        String bCheck = sPrefs.getString("lang", getResources().getString(R.string.language));

        String lang = bCheck;
        LanguageActivity.language = bCheck;

        if (lang.equals("el")) {
            Configuration c = new Configuration(getResources().getConfiguration());
            c.locale = new Locale("el", "EL");
            getResources().updateConfiguration(c, getResources().getDisplayMetrics());
            DbAdapter.getInstance().changeLanguage(Language.GREEK);
        } else {
            Configuration c = new Configuration(getResources().getConfiguration());
            c.locale = new Locale("en", "EN");
            getResources().updateConfiguration(c, getResources().getDisplayMetrics());
            DbAdapter.getInstance().changeLanguage(Language.ENGLISH);
        }







        setContentView(R.layout.activity_view_station);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_ViewStation));

        image=(ImageView)findViewById(R.id.image);
        general_layout=(LinearLayout)findViewById(R.id.general_layout);
        vf = (ViewFlipper) findViewById(R.id.vf);


        // (ViewStation)getActivity().getSupportActionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idStation = intent.getIntExtra("button_id", 0);

        st = DbAdapter.getInstance().getStationByID(idStation);
        mv = DbAdapter.getInstance().getMovieByStation(st.getId());

        textViewTitle =(TextView)findViewById(R.id.titleYear);
        textViewTitle.setText(mv.getTitle() + " ");
        // textViewTitle.setBackgroundColor(getResources().getColor(R.color.line2));

        /** Getting a reference to the ViewPager defined the layout file */
        // ViewPager pager = (ViewPager) findViewById(R.id.pagerImage);




        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();




        /** Instantiating FragmentPagerAdapter */
       /* ImageAdapter pagerAdapter = new ImageAdapter(fm, movieImages/2);
       ImageFragment.id=idStation;
       ImageFragment.line=2;

        /** Setting the pagerAdapter to the pager object */
        /*pager.setAdapter(pagerAdapter);




*/
        for (int i = 0; i < DbAdapter.getInstance().getMainPhotosOfMovie(mv.getId()).size(); i++) {
            try {
                Class res = R.drawable.class;
                Field field = res.getField(DbAdapter.getInstance().getMainPhotosOfMovie(mv.getId()).get(i).getName());
                int drawableId = field.getInt(null);
                BitmapDrawable scaledDrawable = PictureUtils.getScaledDrawable(this, drawableId);
                ImageView imageView = new ImageView(ViewStation.this);
                imageView.setImageBitmap(scaledDrawable.getBitmap());
                vf.addView(imageView);

            } catch (Exception e) {}
        }


        vf.setAutoStart(true);
        vf.setFlipInterval(4000);
        vf.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        vf.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        vf.startFlipping();

        actors = mv.getActors().size() + 1;
        actorsScrollView = (LinearLayout)findViewById(R.id.actorsHsw);

        View director = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.actor, null);
        ImageView imageDirector = (ImageView) director.findViewById(R.id.actorImage);
        Button nameDirector = (Button) director.findViewById(R.id.actorName);
        nameDirector.setBackgroundColor(Color.WHITE);

        try {
            Class res = R.drawable.class;
            Field field = res.getField(DbAdapter.getInstance().getActorPhotosOfMovie(mv.getId()).get(0).getName());
            int drawableId = field.getInt(null);
            BitmapDrawable scaledDrawable = PictureUtils.getScaledDrawable(ViewStation.this, drawableId);
            imageDirector.setImageBitmap(scaledDrawable.getBitmap());
        } catch (Exception e) {}
        String string1 = mv.getDirector();
        String[] parts1 = string1.split(" ");
        nameDirector.setText(parts1[0] + "\n" + parts1[1]);
        nameDirector.setTextSize(12);
        nameDirector.setTextColor(Color.argb(255, 9, 0, 100));

        actorsScrollView.addView(director);

        for (int i=1; i<actors; i++) {
            View actor = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.actor, null);
           ImageView imageActor = (ImageView) actor.findViewById(R.id.actorImage);
            Button nameActor = (Button) actor.findViewById(R.id.actorName);
            nameActor.setBackgroundColor(Color.WHITE);

            try {
                Class res = R.drawable.class;
                Field field = res.getField(DbAdapter.getInstance().getActorPhotosOfMovie(mv.getId()).get(i).getName());
                int drawableId = field.getInt(null);
                BitmapDrawable scaledDrawable = PictureUtils.getScaledDrawable(ViewStation.this, drawableId);
                imageActor.setImageBitmap(scaledDrawable.getBitmap());
            } catch (Exception e) {}
            String string = mv.getActors().get(i - 1);
            Log.i("name",string);
            String[] parts = string.split(" ");
            nameActor.setText(parts[0] + "\n" + parts[1]);
            nameActor.setTextSize(12);
            nameActor.setTextColor(Color.argb(255, 9, 0, 100));

            actorsScrollView.addView(actor);
        }


        textViewInfo = (TextView)findViewById(R.id.info);
        textViewInfo.setText(mv.getDescription());
        layout1=(LinearLayout)findViewById(R.id.layout1);

        showInMap = (ImageButton)findViewById(R.id.showInMap);
        showInMap.setOnClickListener(showInMapButtonOnClickListener);

        goAheadButton = (ImageButton)findViewById(R.id.go_ahead);
        goAheadButton.setOnClickListener(goAheadButtonOnClickListener);

        share = (ImageButton)findViewById(R.id.share);
        share.setOnClickListener(ShareButtonOnClickListener);



        facebookButton = (Button) findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(new sharingOnClickListener("facebook"));
        facebookButton.setVisibility(View.INVISIBLE);

        twitterButton = (Button) findViewById(R.id.twitter_button);
        twitterButton.setOnClickListener(new sharingOnClickListener("twitter"));
        twitterButton.setVisibility(View.INVISIBLE);

        instagramButton = (Button) findViewById(R.id.instagram_button);
        instagramButton.setOnClickListener(new sharingOnClickListener("instagram"));
        instagramButton.setVisibility(View.INVISIBLE);

        pinterestButton = (Button) findViewById(R.id.pinterest_button);
        pinterestButton.setOnClickListener(new sharingOnClickListener("pinterest"));
        pinterestButton.setVisibility(View.INVISIBLE);

        dialog = new AlertDialog.Builder(this);

        info_cast=(TextView)findViewById(R.id.info_cast);
        StringBuilder sb = new StringBuilder();
        sb.append(Html.fromHtml(getString(R.string.director)));
        sb.append(" ").append(mv.getDirector() + "\n");
        sb.append("\n"); sb.append(getString(R.string.cast));
        for (int i=1; i<actors; i++) {
            sb.append(" ").append(mv.getActors().get(i - 1));
            if (i != (actors -1)) {
                sb.append(", ");
            }
            sb.toString();
        }
        info_cast.setText(sb);

        this.setUpMap();
        if(mMap!=null){
            MyPoint point = st.getMyPoint();
            mMap.addMarker(new MarkerOptions()
                    .position(point.getLng())
                    .title(point.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(MapActivity.MARKER_LINE2)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point.getLng(), 11));

        }

    }


    public void setUpMap() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {
            mMap.clear();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_station, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                shareIt();
                return true;
            default:
                onBackPressed();
                return true;
        }
    }

    //from actionbar
    private void shareIt() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "#CineMetro#" + mv.getTitle();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CineMetro");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    View.OnClickListener showInMapButtonOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            MapActivity.showInMap(MapActivity.LINE2, idStation);

            Intent intent = new Intent(ViewStation.this, MapActivity.class);
            ViewStation.this.startActivity(intent);
        }};

    View.OnClickListener goAheadButtonOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            Log.i("id"," "+DbAdapter.getInstance().getUserRatingForStation(idStation, DbAdapter.getInstance().getActiveUser().getUsername()));
            //user should sign up
            if(DbAdapter.getInstance().getActiveUser() == null){
                dialog.setTitle(getResources().getString(R.string.title_activity_RateActivity));
                dialog.setMessage(getResources().getString(R.string.must_sign_up));
                dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.setCancelable(true);
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }

            //user has already rated for this station
            else if (DbAdapter.getInstance().getUserRatingForStation(idStation, DbAdapter.getInstance().getActiveUser().getUsername()) > 0){

                dialog.setTitle(getResources().getString(R.string.title_activity_RateActivity));
                dialog.setMessage(getResources().getString(R.string.already_rate));
                dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.setCancelable(true);
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
            //user can rate
            else {
                Intent intent = new Intent(ViewStation.this, RateActivity.class);
                intent.putExtra("line", MapActivity.LINE2);
                intent.putExtra("button_id", st.getId());
                ViewStation.this.startActivity(intent);
            }
        }};

    public class sharingOnClickListener implements View.OnClickListener {
        String appName;

        public sharingOnClickListener(String appName) {
            this.appName = appName;
        }
        @Override
        public void onClick(View view) {
            boolean found = false;
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "#CineMetro#" + mv.getTitle());
            PackageManager pm = view.getContext().getPackageManager();
            List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
            for (final ResolveInfo app : activityList) {
                if ((app.activityInfo.name).contains(appName)) {
                    final ActivityInfo activity = app.activityInfo;
                    final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    shareIntent.setComponent(name);
                    view.getContext().startActivity(shareIntent);
                    found = true;
                    break;
                }
            }
            if (found == false) {
                dialog.setTitle(appName);
                dialog.setMessage(R.string.noApp);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.setCancelable(true);
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        }
    }


    View.OnClickListener ShareButtonOnClickListener  = new View.OnClickListener() {

        @Override
        public void onClick(View view) {



            layout1.setBackgroundColor(Color.parseColor("#ff0b3f64"));
            share.setVisibility(View.INVISIBLE);
            goAheadButton.setVisibility(View.INVISIBLE);
            showInMap.setVisibility(View.INVISIBLE);
           // facebookButton.setVisibility(View.VISIBLE);
            twitterButton.setVisibility(View.VISIBLE);
           // instagramButton.setVisibility(View.VISIBLE);
           // pinterestButton.setVisibility(View.VISIBLE);
            general_layout.setOnClickListener(layoutOnClickListener);

        }
    };

    View.OnClickListener layoutOnClickListener  = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            layout1.setBackgroundColor(Color.parseColor("#ff0b3f64"));
            share.setVisibility(View.VISIBLE);
            goAheadButton.setVisibility(View.VISIBLE);
            showInMap.setVisibility(View.VISIBLE);
            facebookButton.setVisibility(View.INVISIBLE);
            twitterButton.setVisibility(View.INVISIBLE);
            instagramButton.setVisibility(View.INVISIBLE);
            pinterestButton.setVisibility(View.INVISIBLE);


        }
    };



    @Override
    public void onClick(View view) {




    }



}
