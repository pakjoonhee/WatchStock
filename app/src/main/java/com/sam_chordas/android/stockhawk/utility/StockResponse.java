package com.sam_chordas.android.stockhawk.utility;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joonheepak on 11/23/16.
 */

public class StockResponse {

    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<StockGetterSetter> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<StockGetterSetter> getResults() {
        return results;
    }

    public void setResults(List<StockGetterSetter> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
