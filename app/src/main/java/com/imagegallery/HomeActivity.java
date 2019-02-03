package com.imagegallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.imagegallery.data.ImageItem;
import com.imagegallery.data.ImageListResponse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

public class HomeActivity extends AppCompatActivity {

    private ImageListAdapter mImageAdapter;
    private RecyclerView mRvImageList;
    private int mPageNo;
    private int mListSavedPos;
    private ArrayList<ImageItem> mImageListResponse = new ArrayList<>();
    String url;
    private TextInputEditText searchEt ;
    private ImageView searchIv ;
    private String searchedQuery = null ;
    GridLayoutManager layoutManager ;
    private  Boolean isLoading  = false;
    private static int PAGE_SIZE = -1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRvImageList = findViewById(R.id.rv_image);
        layoutManager = new GridLayoutManager(this,3);
        mRvImageList.setLayoutManager(layoutManager);
        mImageAdapter = new ImageListAdapter(mImageListResponse, this);
        mRvImageList.setAdapter(mImageAdapter);
        searchEt = findViewById(R.id.search_et);
        searchIv = findViewById(R.id.search_iv);

        mRvImageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        //TOD Page size check
                         callApi(searchedQuery,mPageNo);
                    }
                }
            }
        });
        setListeners();
    }

    private void setListeners(){
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedQuery = searchEt.getText().toString();
                if(isNetworkAvailable(HomeActivity.this)) {
                    mImageListResponse.clear();
                    mImageAdapter.setList(mImageListResponse);
                    mImageAdapter.notifyDataSetChanged();
                    callApi(searchedQuery, mPageNo);
                }
                else
                    Toast.makeText(HomeActivity.this, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApi(String query,int pageNo) {
        if(!TextUtils.isEmpty(query)) {
            isLoading = true ;
            Log.d("Response: ", "" + getUrl(query, pageNo));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, getUrl(query, pageNo), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hidekeyboard();
                            Gson gson = new Gson();
                            ImageListResponse imageListResponse = gson.fromJson(response.toString(), ImageListResponse.class);
                            int size = mImageListResponse.size();
                            mRvImageList.setVisibility(View.VISIBLE);
                            mImageListResponse.addAll(imageListResponse.getPhotos().getPhoto());
                            mImageAdapter.setList(mImageListResponse);
                            mImageAdapter.notifyItemRangeInserted(size, imageListResponse.getPhotos().getPhoto().size());
                            if (mListSavedPos > 0 && mPageNo == 0) {
                                mRvImageList.scrollToPosition(mListSavedPos);
                            }
                            mPageNo++;
                            isLoading = false ;
                            PAGE_SIZE = imageListResponse.getPhotos().getPerPages();
                            Log.d("Response: ", "" + response.toString());
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            mRvImageList.setVisibility(View.GONE);
                            searchEt.setError("Please check your query!");

                        }
                    });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest).setShouldCache(true);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager ConnectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (ConnectMgr == null)
            return false;
        NetworkInfo NetInfo = ConnectMgr.getActiveNetworkInfo();
        if (NetInfo == null)
            return false;

        return NetInfo.isConnected();
    }

    private String getUrl(String query, int pageNo) {
        url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&%20format=json&nojsoncallback=1&safe_search=1&text="+query+"&page="+pageNo;
        if(!URLUtil.isValidUrl(url)){
            return null ;
        }
        return url;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("list_position", ((GridLayoutManager) mRvImageList.getLayoutManager()).findLastVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    public void hidekeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
