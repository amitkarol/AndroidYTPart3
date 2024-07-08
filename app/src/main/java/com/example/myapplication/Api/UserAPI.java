package com.example.myapplication.Api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.UserDao;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Token;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.retrofit.RetrofitClient;

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
    private MutableLiveData<User> currentUser;
    private MutableLiveData<String> token;;

    public UserAPI() {
        retrofit = RetrofitClient.getRetrofit();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        // Initialize userDao
        AppDB db = AppDB.getInstance();
        userDao = db.userDao();

        // Initialize LiveData
        usersLiveData = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        token = new MutableLiveData<>();
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

    public void createUser(String firstName, String lastName, String email, String password, String displayName, String photo) {
        Call<User> createUser = webServiceAPI.createUser(firstName, lastName, email, password, displayName, photo);
        createUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Log.d("test1", "entered thread");
                        User user = response.body();
                        Log.d("test1", "createUser user: " + user);
                        userDao.insert(new User(user.getFirstName(), user.getLastName(),
                                    user.getEmail(), user.getPassword(), user.getDisplayName(), user.getPhotoUri(), user.getVideos()));
                        assignToken(user, currentUser, token);
                    }).start();
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("test1", "failed api");
            }
        });
    }

    public void assignToken (User user, MutableLiveData<User> currentUser, MutableLiveData<String> token) {
        Log.d("test1", "token user: " + user.getEmail());
        String email = user.getEmail();
        String password = user.getPassword();
        Call<Token> login = webServiceAPI.processLogin(email, password);

        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.d("test1", "response token : " + response.isSuccessful());
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        currentUser.postValue(user);
                        token.postValue(response.body().getToken());
                        Log.d("test1", "token: " + token);
                        Log.d("test1", "token: " + response.body().getToken());
                    }).start();
                } else {
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
            }
        });
    }
}
