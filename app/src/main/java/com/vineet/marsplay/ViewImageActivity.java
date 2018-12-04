package com.vineet.marsplay;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vineet.marsplay.adapter.ViewImageAdapter;
import com.vineet.marsplay.callback.OnRecyclerItemClickCalback;
import com.vineet.marsplay.event.ImageModel;
import com.vineet.marsplay.provider.ImageProvider;
import com.vineet.marsplay.util.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ViewImageActivity extends AppCompatActivity implements OnRecyclerItemClickCalback {
    ArrayList<ImageModel> listRes = new ArrayList<ImageModel>();
    private LinearLayout ll_progressbar;
    private TextView tv_progressmessage;
    private ProgressBar progressbar;
    private RecyclerView recyclerView;
    private Toolbar toolbar_view;
    private ViewImageAdapter viewImageAdapter;
    private ImageProvider imageProvider;
    private ImageProvider.ImageDataBaseHelper imageDataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimage);
        recyclerView = (RecyclerView) findViewById(R.id.rv_viewimage);
        ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar);
        tv_progressmessage = (TextView) findViewById(R.id.tv_progressmessage);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        toolbar_view = (Toolbar) findViewById(R.id.toolbar_view);
        toolbar_view.setTitle("View");
        setSupportActionBar(toolbar_view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        viewImageAdapter = new ViewImageAdapter(this, this);
        recyclerView.setAdapter(viewImageAdapter);
        imageProvider = new ImageProvider();
        imageDataBaseHelper = imageProvider.new ImageDataBaseHelper(this);

        new loadImageAsyncTask().execute();
    }


    public class loadImageAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_progressbar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loadAllImages();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listRes.size() > 0) {
                ll_progressbar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                viewImageAdapter.addAll(listRes);
            } else {
                ll_progressbar.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                tv_progressmessage.setText("No Image Found!!!");
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    public void loadAllImages() {
        listRes = imageDataBaseHelper.selectImageDetailList();
    }


    @Override
    public void onItemClick(View v, int pos) {
        switch (v.getId()) {
            case R.id.rl_row:
                if (viewImageAdapter != null) {
                    ImageModel item = viewImageAdapter.getItem(pos);
                    Intent intent = new Intent(this, FullScreenImageActivity.class);
                    intent.putExtra(Constant.IMAGEURL, item.getImageUrl());
                    startActivity(intent);
                }
                break;

            default:

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}