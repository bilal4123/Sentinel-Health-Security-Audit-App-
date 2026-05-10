package com.sentinel.app.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("rest/json/cves/2.0")
    suspend fun getRecentVulnerabilities(
        @Query("resultsPerPage") resultsPerPage: Int = 15,
        @Query("keywordSearch") keyword: String = "android"
    ): Response<CveResponse>
}