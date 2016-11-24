package com.sam_chordas.android.stockhawk.utility;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by joonheepak on 11/23/16.
 */

public interface ApiInterface {
    @GET("{symbol}")
    Call<StockResponse> getPopularMovies(@Path("symbol") String symbol);

    @GET("movie/top_rated")
    Call<StockResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<StockResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
