package com.example.drappoint.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.drappoint.R;

public class SliderAdapter extends PagerAdapter {

    private Context nContext;

    public SliderAdapter(Context context){
        this.nContext = context;
    }

    private int[] imageSlide =
            new int[]{R.drawable.slide0, R.drawable.slide1};


    @Override
    public int getCount() {
        return imageSlide.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(nContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(imageSlide[position]);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}












