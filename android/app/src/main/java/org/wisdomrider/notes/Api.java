package org.wisdomrider.notes;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
    @POST("login")
    Call<LoginPage.LoginResponse> Login(@Body LoginPage.Login login);

    @POST("register")
    Call<Register.RResponse> Register(@Body Register.Register login);

    @GET("notes")
    Call<LoginPage.NotesResponse> Notes();

    @POST("add/note")
    Call<Home.Response> addNote(@Body LoginPage.Add add);

    @POST("edit/note/{id}")
    Call<LoginPage.LoginResponse> editNote(@Body LoginPage.Add add, @Path("id") String id);

    @POST("delete/note/{id}")
    Call<LoginPage.LoginResponse> Delete(@Path("id") String id);

}
