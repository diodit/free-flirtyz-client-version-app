package com.ritssupreme.phlurtyz002.api.interfaces;

import com.ritssupreme.phlurtyz002.api.models.CLickAdvertsRequest;
import com.ritssupreme.phlurtyz002.api.models.CLickAdvertsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ClickAdvert{
    @POST
    Call<CLickAdvertsResponse> create(@Body CLickAdvertsRequest cLickAdvertsRequest,
                                      @Url String url);
}
