package com.demo.android.watchlist.model

import com.google.gson.annotations.SerializedName

data class MovieApiResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<MovieModel>
)