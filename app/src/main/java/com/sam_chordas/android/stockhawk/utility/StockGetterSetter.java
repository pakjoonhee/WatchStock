package com.sam_chordas.android.stockhawk.utility;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joonheepak on 11/23/16.
 */

public class StockGetterSetter {
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("title")
    private String title;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("vote_average")
    private String voteAverage;
    @SerializedName("overview")
    private String overview;
    @SerializedName("id")
    private int id;


    public StockGetterSetter() {

    }


    public String getPosterPath() { return posterPath; }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() { return releaseDate; }

    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getVoteAverage() { return voteAverage; }

    public void setVoteAverage(String voteAverage) { this.voteAverage = voteAverage; }

    public String getOverview() { return overview; }

    public void setOverview(String overview) { this.overview = overview; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

}
