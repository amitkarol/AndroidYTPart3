package com.example.myapplication.converters;
import androidx.room.TypeConverter;

import com.example.myapplication.entities.Video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
public class VideoConverter {
        @TypeConverter
        public static List<Video> fromString(String value) {
            Type listType = new TypeToken<List<Video>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public static String fromArrayList(List<Video> list) {
            Gson gson = new Gson();
            return gson.toJson(list);
        }
    }

