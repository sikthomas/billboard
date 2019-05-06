package com.glac;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.academics.Accademics;
import com.glac.academics.MyClassAvailablePosst;
import com.glac.academics.SchoolRegister;
import com.glac.account.MyAccountASettings;
import com.glac.account.MyAccountSetting;
import com.glac.account.UserProfile;
import com.glac.ecommerce.E_Commerce;
import com.glac.ecommerce.MainFragment;
import com.glac.ecommerce.MyCarts;
import com.glac.ecommerce.MyPosts;
import com.glac.ecommerce.PostAdapter;
import com.glac.ecommerce.Posting;
import com.glac.ecommerce.PotsItem;
import com.glac.mpesab2c.MpesaB2c;
import com.glac.transport.MainTransport;
import com.glac.transport.Transport;
import com.glac.transport.Transportation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.bloder.magic.view.MagicButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainPanelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String user_id;
    private String fname,lname,email,fullname,image_url;
    private TextView Username,Email;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private FloatingActionButton postFB,cartFB;
    private CircleImageView profileImage;
    private PostAdapter postAdapter;
    private List<PotsItem> postItems;
    private Context context = this ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);
        setupWindowAnimationsExit();

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore  = FirebaseFirestore.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        postItems = new ArrayList<>();
        postAdapter = new PostAdapter(postItems);
        postFB = (FloatingActionButton)findViewById(R.id.fab);
        cartFB = (FloatingActionButton)findViewById(R.id.fabCart);

        Advance3DDrawerLayout drawer = (Advance3DDrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.setViewScale(Gravity.START, 0.9f); //set height scale for main view (0f to 1f)
        drawer.setViewElevation(Gravity.START, 20);//set main view elevation when drawer open (dimension)
        drawer.setViewScrimColor(Gravity.START, Color.TRANSPARENT);//set drawer overlay coloe (color)
        drawer.setDrawerElevation(Gravity.START, 20);//set drawer elevation (dimension)

        drawer.setRadius(Gravity.START, 25);//set end container's corner radius (dimension)
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationView navigationView1 = (NavigationView)findViewById(R.id.nav_view_notification);
        navigationView1.setNavigationItemSelectedListener(this);
        //setting user rights
        settingRights(postFB);

        postFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPanelActivity.this,Posting.class));
            }
        });
        cartFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setTitle("My Cart");
                MyCarts mycarts = new MyCarts();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_section,mycarts).commit();
                postFB.setVisibility(View.GONE);
                cartFB.setVisibility(View.GONE);
            }
        });

        //OPENING THE INITIAL FRAGMENT

        toolbar.setTitle("Hot Deals");
        /*E_Commerce posts = new E_Commerce();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_section,posts).commit();*/
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_section,mainFragment).commit();

        detailsLoading();


    }

    public void detailsLoading(){

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if (task.getResult().exists()){
                        fname= task.getResult().getString("fname");
                        lname= task.getResult().getString("lname");
                        email= task.getResult().getString("email");
                        image_url = task.getResult().getString("imageUrl");


                        fullname = fname+" "+lname;

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View view = navigationView.getHeaderView(0);

                        Username = (TextView)view.findViewById(R.id.tvUserName);
                        Email = (TextView)view.findViewById(R.id.tvUserEmail);
                        profileImage  = (CircleImageView)view.findViewById(R.id.imageProfile_nav);


                        if (image_url != null){


                            try{
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();
                                requestOptions.placeholder(R.color.lightGrray);

                                Glide.with(MainPanelActivity.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(profileImage);

                            }catch (Exception e){
                                Toast.makeText(MainPanelActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }


                        Username.setText(fullname);
                        Email.setText(email);


                        profileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                              // startActivity(new Intent(MainPanelActivity.this,UserProfile.class));
                                Intent intent = new Intent(view.getContext(), UserProfile.class);
                                intent.putExtra("image_url",image_url);
                                view.getContext().startActivity(intent);
                            }
                        });


                    }

                }

            }
        });
    }


    @SuppressLint("NewApi")
    private void setupWindowAnimationsExit() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(slide);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();


        if (current_user != null)
        {
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful())
                    {
                        if (!task.getResult().exists())
                        {
                            startActivity(new Intent(MainPanelActivity.this,MyAccountSetting.class));

                        }

                    }

                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailsLoading();
    }

    boolean twice = false;
    @Override
    public void onBackPressed() {
        Advance3DDrawerLayout drawer = (Advance3DDrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {



            if(twice){
                super.onBackPressed();
                return;
            }

            toolbar.setTitle("Hot Deals");
            MainFragment mainFragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_section,mainFragment).commit();
            settingRights(postFB);
            cartFB.setVisibility(View.VISIBLE);

            this.twice = true;
            Toast.makeText(MainPanelActivity.this,"Press Back again to Exit",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    twice=false;
                }
            },500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_panel, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_myaccount) {
            startActivity(new Intent(MainPanelActivity.this,MyAccountSetting.class));
        }
        if (id == R.id.acction_share){
            String message = "Hi....!! You can now use GLAC App to upload or search for any item for sale in your local area of your choice for more information follow this link https://developers.facebook.com/";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,message);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share Glac app via");
            startActivity(sendIntent);

        }
        if (id == R.id.action_help){

        }
        if (id == R.id.action_myclass){
            startActivity(new Intent(MainPanelActivity.this, MyClassAvailablePosst.class));
        }
        if (id == R.id.action_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exiting...");
            builder.setMessage("Are you sure you want to logout from your account?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(MainPanelActivity.this,Login.class));
                    auth.signOut();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        if (id ==R.id.action_notification){

            NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view_notification);
            navigationView.setNavigationItemSelectedListener(this);

        }
        if (id == R.id.action_rate_us){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=GLAC")));

        }
        if (id == R.id.action_scoolediting){
            startActivity(new Intent(MainPanelActivity.this, SchoolRegister.class));

        }

        if (id == R.id.action_mywalet){
            startActivity(new Intent(MainPanelActivity.this,MpesaB2c.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void settingRights(final FloatingActionButton floatingActionButton){
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String admin = task.getResult().getString("admin");
                        if (admin.equals("admin")){
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ecommerce) {

            toolbar.setTitle("Hot Deals");
            MainFragment mainFragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_section,mainFragment).commit();
            postFB.setVisibility(View.VISIBLE);
            cartFB.setVisibility(View.VISIBLE);


        } else if (id == R.id.nav_schoolmarket) {
            toolbar.setTitle("School Marketing");
            Accademics accademics = new Accademics();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_section,accademics).commit();
            postFB.setVisibility(View.GONE);
            cartFB.setVisibility(View.GONE);


        } else if (id == R.id.nav_transport) {
            toolbar.setTitle("Transport");
            Transportation transport = new Transportation();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_section,transport).commit();
            postFB.setVisibility(View.GONE);
            cartFB.setVisibility(View.GONE);

        } else if (id == R.id.nav_share) {
            String message = "Hi....!! You can now use GLAC App to upload or search for any item for sale in your local area of your choice for more information "+"market://details?id=GLAC";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,message);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share Glac app via");
            startActivity(sendIntent);
            postFB.setVisibility(View.GONE);
            cartFB.setVisibility(View.GONE);

        } else if (id == R.id.nav_About) {
            toolbar.setTitle("About GLAC");
            About about = new About();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_section,about).commit();
            postFB.setVisibility(View.GONE);
            cartFB.setVisibility(View.GONE);


        } else if (id == R.id.nav_Logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exiting...");
            builder.setMessage("Are you sure you want to logout from your account?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(MainPanelActivity.this,Login.class));
                    auth.signOut();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }else if (id == R.id.nav_carts){

            toolbar.setTitle("My Cart");
            MyCarts mycarts = new MyCarts();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_section,mycarts).commit();
            postFB.setVisibility(View.GONE);
            cartFB.setVisibility(View.GONE);
        }else if (id == R.id.nav_follow){
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.follow_us);
            dialog.setCancelable(true);

            // set the custom dialog components - text, image and button
            MagicButton facebook = (MagicButton)dialog.findViewById(R.id.btnFacebook);
            MagicButton insta = (MagicButton)dialog.findViewById(R.id.btnInstagram);
            MagicButton twitter = (MagicButton)dialog.findViewById(R.id.btnTwitter);
            MagicButton youtube = (MagicButton)dialog.findViewById(R.id.btnYoutube);




            Button dialogButton = (Button) dialog.findViewById(R.id.btnCancel);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            facebook.setMagicButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fb = getOpenFacebookIntent(context);
                    startActivity(fb);
                }
            });
            twitter.setMagicButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent twt = getOpenTwitter(context);
                    startActivity(twt);
                }
            });
            insta.setMagicButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent insta = getOpenInsta(context);
                    startActivity(insta);
                }
            });
            youtube.setMagicButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtube = getOpenYoutube(context);
                    startActivity(youtube);
                }
            });

            dialog.show();

        }

        Advance3DDrawerLayout  drawer = (Advance3DDrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/DalTuclnnxq"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/JShadrak Mulle"));
        }
    }
    public static Intent getOpenTwitter(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/DalTuclnnxq"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Shadrak Mulle"));
        }
    }
    public static Intent getOpenInsta(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.instagram.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/DalTuclnnxq"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/Shadrak Mulle"));
        }
    }
    public static Intent getOpenYoutube(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.google.android.youtube", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/DalTuclnnxq"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/GLAC"));
        }
    }

}
