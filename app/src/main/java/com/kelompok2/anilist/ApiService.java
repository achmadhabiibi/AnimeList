package com.kelompok2.anilist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("top/anime")
    Call<AnimeResponse> getTopAnime();
    
    @GET("anime")
    Call<AnimeResponse> searchAnime(@Query("q") String query);
}
