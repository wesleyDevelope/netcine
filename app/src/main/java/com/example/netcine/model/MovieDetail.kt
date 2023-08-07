package com.example.netcine.model

data class MovieDetail(
    val movie: Movie,
    val similars:List<Movie>
)