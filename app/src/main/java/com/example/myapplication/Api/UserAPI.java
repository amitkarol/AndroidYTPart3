package com.example.myapplication.Api;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.UserDao;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.User;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    private UserDao userDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<User>> usersLiveData;

    public UserAPI() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        // Initialize userDao
        AppDB db = AppDB.getInstance();
        userDao = db.userDao();

        // Initialize LiveData
        usersLiveData = new MutableLiveData<>();
    }

    public void get() {
        Call<List<User>> call = webServiceAPI.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<User> users = response.body();
                        for (User res : users) {
                            userDao.insert(new User(res.getFirstName(), res.getLastName(),
                                    res.getEmail(), res.getPassword(), res.getDisplayName(), res.getPhotoUri(), res.getVideos()));
                        }
                        // Update LiveData with the new list of users
                        usersLiveData.postValue(users);
                    }).start();
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle the failure case
                // For example, you could log the error or notify the user
                // Log.e("UserAPI", "Failed to fetch Users", t);
            }
        });
    }
}
