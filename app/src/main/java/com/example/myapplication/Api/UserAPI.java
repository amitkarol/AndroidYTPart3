package com.example.myapplication.Api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.UserDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Token;
import com.example.myapplication.entities.User;
import com.example.myapplication.retrofit.RetrofitClient;
import com.example.myapplication.utils.CurrentUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserAPI {
    private UserDao userDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<User>> usersLiveData;

    public UserAPI() {
        retrofit = RetrofitClient.getRetrofit();
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
                                    res.getEmail(), res.getPassword(), res.getDisplayName(), res.getPhoto(), res.getVideos()));
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

    public void getUserByEmail(MutableLiveData<User> owner, String email) {
        Call<User> call = webServiceAPI.getUserByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    owner.postValue(user);
                } else {
                    Log.d("test5", "Response false getUserByEmail");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("test5", "Failed getUserByEmail");
            }
        });
    }

    public void createUser(String firstName, String lastName, String email, String password, String displayName, String photo) {
        Call<User> createUser = webServiceAPI.createUser(firstName, lastName, email, password, displayName, photo);
        createUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("test5", "user photo api " + photo);
                    new Thread(() -> {
                        Log.d("test1", "entered thread");
                        User user = response.body();
                        Log.d("test1", "createUser user: " + user);
                        Log.d("test5", "user photo " + user.getPhoto());
                        userDao.insert(new User(user.getFirstName(), user.getLastName(),
                                    user.getEmail(), user.getPassword(), user.getDisplayName(), user.getPhoto(), user.getVideos()));
                        assignToken(user);
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

    public void assignToken (User user) {
        Log.d("test1", "token user: " + user.getEmail());
        String email = user.getEmail();
        String password = user.getPassword();
        Call<Token> login = webServiceAPI.processLogin(email, password);

        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.d("test1", "response token : " + response.isSuccessful());
                CurrentUser currentUser = CurrentUser.getInstance();
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        currentUser.setUser(user);
                        currentUser.setToken(response.body().getToken());
                        Log.d("test1", "user in assignToken: " + currentUser.getUser().getValue());
                    }).start();
                } else {
                    currentUser.getUser().postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                CurrentUser currentUser = CurrentUser.getInstance();
                currentUser.getUser().postValue(null);
            }
        });
    }

    public void assignToken (String email, String password) {
        Call<Token> login = webServiceAPI.processLogin(email, password);

        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.d("test1", "response token : " + response.isSuccessful());
                CurrentUser currentUser = CurrentUser.getInstance();
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        getUserByEmail(currentUser.getUser(), email);
                        Log.d("test1", "user in assignToken 1: " + currentUser.getUser().getValue());
                        //currentUser.setUser(user.);
                        currentUser.setToken(response.body().getToken());
                        Log.d("test1", "user in assignToken: " + currentUser.getUser().getValue());
                    }).start();
                } else {
                    currentUser.getUser().postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                CurrentUser currentUser = CurrentUser.getInstance();
                currentUser.getUser().postValue(null);
            }
        });
    }

    public void login(String email, String password) {
        assignToken(email, password);
    }



    public void updateUser(String email, User user) {
        String token = "bearer " + CurrentUser.getInstance().getToken().getValue();
        Log.d("UserAPI", "Attempting to update user with email: " + email);
        Log.d("UserAPI", "User details: " + user.toString());

        Call<User> call = webServiceAPI.updateUser(token, email, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserAPI", "User updated successfully on server: " + response.body().toString());

                    new Thread(() -> {
                        userDao.update(user);
                        Log.d("UserAPI", "User updated locally in the database: " + user.toString());

                        // Update LiveData with the updated user
                        usersLiveData.postValue(userDao.index());
                        Log.d("UserAPI", "LiveData updated with the new list of users");
                    }).start();
                } else {
                    Log.d("UserAPI", "Response unsuccessful for updateUser. Response code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserAPI", "Failed to update user", t);
            }
        });
    }


    public void deleteUser(String email) {
        String token = "bearer " + CurrentUser.getInstance().getToken().getValue();
        Call<Void> call = webServiceAPI.deleteUser(token, email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        User user = userDao.getUserByEmail(email);
                        if (user != null) {
                            userDao.delete(user);
                            usersLiveData.postValue(userDao.index());
                        }
                    }).start();
                } else {
                    Log.d("UserAPI", "Response unsuccessful for deleteUser");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("UserAPI", "Failed to delete user", t);
            }
        });
    }


    public void checkEmailExists(String email, MutableLiveData<Boolean> emailExists) {
        Call<User> call = webServiceAPI.getUserByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    emailExists.postValue(true);
                } else if (response.code() == 404) {
                    emailExists.postValue(false);
                } else {
                    // Handle other response codes
                    emailExists.postValue(false);
                    Log.d("UserAPI", "Response unsuccessful for checkEmailExists. Response code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                emailExists.postValue(false); // treat as email does not exist or handle error
                Log.d("UserAPI", "Failed to check email existence", t);
            }
        });
    }

}
