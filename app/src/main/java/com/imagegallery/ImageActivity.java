package com.imagegallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.imagegallery.util.ImageLoader;
import com.imagegallery.data.ImageItem;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {


    private int currentPos;
    private ImageView imageView;
    private ArrayList<ImageItem> mImageListResponse = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mImageListResponse = getIntent().getParcelableArrayListExtra("image_list");
        currentPos = getIntent().getIntExtra("image_position",0);
        imageView = findViewById(R.id.image_iv);
        ImageLoader.getInstance(getApplicationContext()).loadImage(generateUrl(mImageListResponse.get(currentPos)) , imageView, R.mipmap.ic_launcher);


        Button prevButton = findViewById(R.id.button3);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos - 1 >= 0) {
                    ImageLoader.getInstance(getApplicationContext()).loadImage(generateUrl(mImageListResponse.get(--currentPos)), imageView, R.mipmap.ic_launcher);
                }
            }
        });
        Button nextButton = findViewById(R.id.button4);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos + 1 < mImageListResponse.size()) {
                    ImageLoader.getInstance(getApplicationContext()).loadImage(generateUrl(mImageListResponse.get(++currentPos)), imageView, R.mipmap.ic_launcher);
                }
            }
        });
    }

    private String generateUrl(ImageItem imageItem) {
        String url = "http://farm"+imageItem.getFarm()+".static.flickr.com/"+imageItem.getServer()+"/"+imageItem.getId()+"_"+imageItem.getSecret()+".jpg";
        Log.d("Response: " ,""+ url);
        return url;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
