package TestsWithRetrofit;

import TestsWithRestAssured.BoardForTests;
import retrofit2.Call;
import retrofit2.http.*;

public interface BoardService {

    @POST("/1/boards/")
    public Call<BoardForTests> putBoard(
            @Query("key") String key,
            @Query("token") String token,
            @Body BoardForTests board
    );
}
