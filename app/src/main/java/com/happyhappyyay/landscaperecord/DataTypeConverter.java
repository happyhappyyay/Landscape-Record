package com.happyhappyyay.landscaperecord;

        import android.arch.persistence.room.TypeConverter;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import java.lang.reflect.Type;
        import java.util.List;

public class DataTypeConverter {

    @TypeConverter
    public String fromServicesList(List<Service> services) {
        if (services == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>() {
        }.getType();
        return gson.toJson(services, type);
    }

    @TypeConverter
    public List<Service> toServicesList(String services) {
        if (services == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>() {}.getType();
        return gson.fromJson(services, type);
    }
}
