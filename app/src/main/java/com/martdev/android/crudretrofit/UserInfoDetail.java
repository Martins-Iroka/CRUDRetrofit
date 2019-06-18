package com.martdev.android.crudretrofit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoDetail extends AppCompatActivity {
    private static final String USER_INFO = "com.martdev.android.crudretrofit.userInfo";

    public static Intent newIntent(Context packageContext, UserInfo userInfo) {
        Intent intent = new Intent(packageContext, UserInfoDetail.class);
        intent.putExtra(USER_INFO, userInfo);
        return intent;
    }

    private EditText mTitleView, mBodyView;
    private ProgressBar mProgressBar;

    private ApiService mApiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_detail);

        mApiService = Client.getRetrofit().create(ApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTitleView = findViewById(R.id.addedit_title);
        mBodyView = findViewById(R.id.addedit_body);
        Button button = findViewById(R.id.save_update);
        mProgressBar = findViewById(R.id.progressBar2);

        final UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra(USER_INFO);
        if (userInfo == null) {
            actionBar.setTitle("Add User");
        } else {
            actionBar.setTitle("Edit User");
            mTitleView.setText(userInfo.getTitle());
            mBodyView.setText(userInfo.getBody());
            button.setText(getString(R.string.update_user));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitleView.getText().toString();
                String body = mBodyView.getText().toString();

                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(body)) {
                    Toast.makeText(UserInfoDetail.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                if (userInfo == null) {
                    UserInfo userInfo1 = new UserInfo();
                    userInfo1.setUserId(1);
                    userInfo1.setId(101);
                    userInfo1.setTitle(title);
                    userInfo1.setBody(body);
                    saveUser(userInfo1);
                } else {
                    userInfo.setTitle(title);
                    userInfo.setBody(body);
                    updateUser(userInfo.getId(), userInfo);
                }
            }
        });
    }

    private void saveUser(UserInfo userInfo) {
        Call<UserInfo> call = mApiService.saveUser(userInfo);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UserInfoDetail.this, "User saved. ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(UserInfoDetail.this, "Error saving user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(final int id, UserInfo info) {
        Call<UserInfo> call = mApiService.updateUser(id, info);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response.body().getId() == id) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UserInfoDetail.this, "User updated. ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(UserInfoDetail.this, "Error updating user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
