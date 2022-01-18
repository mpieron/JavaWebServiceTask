package TestsWithRetrofit;

import TestsWithRestAssured.BoardForTests;
import okhttp3.OkHttpClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.testng.Assert.assertEquals;

public class TrelloRestApiTest {

    String key;
    String token;
    Retrofit retrofit;
    List<String> idListOfCreatedBoards;

    @BeforeClass
    public void init(){

        idListOfCreatedBoards = new ArrayList<>();
        Properties properties = new Properties();
        String pathToFile = "src/main/resources/trello.properties";

        try {
            properties.load(new FileInputStream(pathToFile));
            key = properties.getProperty("key");
            token = properties.getProperty("token");
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://trello.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    @Test
    public void creatingNewBoardTest(){

        BoardForTests board = new BoardForTests();
        board.setName("NewBoard");
        board.setDesc("This is new board!");
        board.setClosed(false);

        BoardService service = retrofit.create(BoardService.class);
        Call<BoardForTests> callSync = service.putBoard(key, token, board);

        try {
            Response<BoardForTests> response = callSync.execute();
            BoardForTests createdBoard = response.body();

            if (createdBoard != null) {
                idListOfCreatedBoards.add(createdBoard.getId());
            }

            assertEquals(response.code(), 200);
            assertThat(createdBoard).hasFieldOrPropertyWithValue("name", "NewBoard")
                    .hasFieldOrPropertyWithValue("desc", "This is new board!")
                    .hasFieldOrPropertyWithValue("closed", false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
