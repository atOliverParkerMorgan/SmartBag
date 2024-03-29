// taken from https://github.com/vitSkalicky/lepsi-rozvrh/
package com.olivermorgan.ontimev2.main.BakalariAPI.rozvrh;


import com.olivermorgan.ontimev2.main.BakalariAPI.rozvrh.rozvrh3.Rozvrh3;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RozvrhAPIInterface {
    @GET("api/3/timetable/actual?")
    public Call<Rozvrh3> getActualSchedule(@Query("date") String date);

    @GET("api/3/timetable/permanent")
    public Call<Rozvrh3> getPermanentSchedule();
}
