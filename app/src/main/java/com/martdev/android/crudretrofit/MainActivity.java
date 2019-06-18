package com.martdev.android.crudretrofit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private UserInfoAdapter mAdapter;
    private ApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiService = Client.getRetrofit().create(ApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mAdapter = new UserInfoAdapter(new ArrayList<UserInfo>(0));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton button = findViewById(R.id.create_user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserInfoDetail.newIntent(MainActivity.this, null);
                startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        UserInfo userInfo = mAdapter.getInfoList().get(viewHolder.getAdapterPosition());
                        deleteUserInfo(userInfo.getId());
                    }
                });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void getUserInfo() {
        Call<List<UserInfo>> listCall = mApiService.getUsers();
        listCall.enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserInfo>> call, @NonNull Response<List<UserInfo>> response) {

                if(response.isSuccessful()) {
                    Log.i("MainActivity", "onResponse: " + response.toString());
                    mProgressBar.setVisibility(View.GONE);
                    mAdapter.setInfoList(response.body());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserInfo>> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Request failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserInfo(final int id) {
        Call<UserInfo> call = mApiService.deleteUser(id);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "View id " + id + " deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to delete view.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UserInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private UserInfo mUserInfo;
        private TextView mTitle, mBody, mId;

        UserInfoHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.list_item, viewGroup, false));
            itemView.setOnClickListener(this);

            mTitle = itemView.findViewById(R.id.title_view);

            mBody = itemView.findViewById(R.id.body_view);

            mId = itemView.findViewById(R.id.id);
        }

        private void bind(UserInfo userInfo) {
            mUserInfo = userInfo;
            mTitle.setText(userInfo.getTitle());
            mBody.setText(userInfo.getBody());
            mId.setText(String.valueOf(userInfo.getId()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = UserInfoDetail.newIntent(MainActivity.this, mUserInfo);
            startActivity(intent);
        }
    }

    private class UserInfoAdapter extends RecyclerView.Adapter<UserInfoHolder> {

        private List<UserInfo> mInfoList;

        UserInfoAdapter(List<UserInfo> infoList) {
            mInfoList = infoList;
        }

        @NonNull
        @Override
        public UserInfoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            return new UserInfoHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull UserInfoHolder userInfoHolder, int i) {
            UserInfo userInfo = mInfoList.get(i);
            userInfoHolder.bind(userInfo);
        }

        @Override
        public int getItemCount() {
            return mInfoList == null ? 0 : mInfoList.size();
        }

        private void setInfoList(List<UserInfo> infoList) {
            mInfoList = infoList;
        }

        private List<UserInfo> getInfoList() {
            return mInfoList;
        }
    }
}
