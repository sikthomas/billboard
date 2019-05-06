package com.glac.ecommerce;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glac.R;
import com.glac.image_slider.ImageModel;
import com.glac.image_slider.SlidingImage_Adapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;

    private int[] myImageList = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3,R.drawable.slide4
            ,R.drawable.slide5,R.drawable.slide6,R.drawable.slide7,R.drawable.slide8,R.drawable.slide9,R.drawable.slide11,R.drawable.slide12,R.drawable.slide13,R.drawable.slide14,R.drawable.slide15,R.drawable.slide16};
    private View  view;
    private CardView mPhones,mMen,mWomen,mGrocery,mSports,mElec,mComps,mHome,mHealth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_main, container, false);

        //initilizing
        mComps= (CardView)view.findViewById(R.id.cardcomputing);
        mElec = (CardView)view.findViewById(R.id.cardelectronics);
        mPhones = (CardView)view.findViewById(R.id.cardphonestablets);
        mMen = (CardView)view.findViewById(R.id.cardMensFashion);
        mWomen = (CardView)view.findViewById(R.id.cardWomenFashion);
        mGrocery = (CardView)view.findViewById(R.id.cardGrocery);
        mSports = (CardView)view.findViewById(R.id.cardSporting);
        mHealth = (CardView)view.findViewById(R.id.cardHealthandBeauty);
        mHome = (CardView)view.findViewById(R.id.cardHomeAndOffice);


        //passing the values on click
        mComps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Computing");
                startActivity(intent);
            }
        });
        mElec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Electronics");
                startActivity(intent);
            }
        });
        mPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Phones and Tablets");
                startActivity(intent);
            }
        });
        mMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Men Fashion");
                startActivity(intent);
            }
        });
        mWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Women Fashion");
                startActivity(intent);
            }
        });
        mHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Health and Beauty");
                startActivity(intent);
            }
        });
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Home and Office");
                startActivity(intent);
            }
        });
        mSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Sporting");
                startActivity(intent);
            }
        });
        mGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main_List_Holder.class);
                intent.putExtra("value","Grocery");
                startActivity(intent);
            }
        });

        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();

        return  view;
    }

    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 15; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void init() {

        mPager = (ViewPager)view.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(getContext(),imageModelArrayList));

        CirclePageIndicator indicator = (CirclePageIndicator)view.
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

}
