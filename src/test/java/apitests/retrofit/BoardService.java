package apitests.retrofit;

import apitests.restassured.BoardForTests;
import retrofit2.Call;
import retrofit2.http.*;

public interface BoardService {

    @POST("/1/boards/")
    public Call<BoardForTests> postBoard(
            @Query("key") String key,
            @Query("token") String token,
            @Body BoardForTests board
    );

    @GET("/1/boards/{id}")
    public Call<BoardForTests> getBoard(
            @Path("id") String id,
            @Query("key") String key,
            @Query("token") String token
    );

    @PUT("/1/boards/{id}")
    public Call<BoardForTests> putBoard(
            @Path("id") String id,
            @Query("key") String key,
            @Query("token") String token,
            @Body BoardForTests board
    );

    @DELETE("/1/boards/{id}")
    public Call<BoardForTests> deleteBoard(
            @Path("id") String id,
            @Query("key") String key,
            @Query("token") String token
    );
}
