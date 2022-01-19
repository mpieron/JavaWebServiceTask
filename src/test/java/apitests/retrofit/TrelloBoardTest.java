package apitests.retrofit;

import apitests.restassured.BoardForTests;
import okhttp3.OkHttpClient;
import org.testng.annotations.AfterClass;
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

public class TrelloBoardTest {

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

    @AfterClass
    public void clean() throws IOException {
        BoardService service = retrofit.create(BoardService.class);
        Call<BoardForTests> callSync;
        for (String id : idListOfCreatedBoards){
            callSync = service.deleteBoard(id, key, token);
            callSync.execute();
        }
    }

    @Test
    public void creatingNewBoardTest() throws IOException {
        BoardForTests board = new BoardForTests();
        board.setName("NewBoard");
        board.setDesc("This is new board!");
        board.setClosed(false);

        BoardService service = retrofit.create(BoardService.class);
        Call<BoardForTests> callSync = service.postBoard(key, token, board);

        Response<BoardForTests> response = callSync.execute();
        BoardForTests createdBoard = response.body();

        if (createdBoard != null) {
            idListOfCreatedBoards.add(createdBoard.getId());
        }

        assertEquals(response.code(), 200);
        assertThat(createdBoard).hasFieldOrPropertyWithValue("name", "NewBoard")
                    .hasFieldOrPropertyWithValue("desc", "This is new board!")
                    .hasFieldOrPropertyWithValue("closed", false);

    }

    @Test
    public void gettingBoardByIDTest() throws IOException {
        BoardForTests board = new BoardForTests();
        board.setName("NewBoard");
        board.setDesc("This is new board!");
        board.setClosed(false);

        BoardService service = retrofit.create(BoardService.class);
        Call<BoardForTests> callSyncPost = service.postBoard(key, token, board);

        Response<BoardForTests> responsePost = callSyncPost.execute();
        BoardForTests createdBoard = responsePost.body();

        if (createdBoard != null) {
            idListOfCreatedBoards.add(createdBoard.getId());

            Call<BoardForTests> callSyncGet = service.getBoard(createdBoard.getId(), key, token);
            Response<BoardForTests> responseGet = callSyncGet.execute();
            BoardForTests receivedBoard = responseGet.body();

            assertEquals(responseGet.code(), 200);
            assertThat(receivedBoard).hasFieldOrPropertyWithValue("id", createdBoard.getId())
                    .hasFieldOrPropertyWithValue("name", "NewBoard")
                    .hasFieldOrPropertyWithValue("desc", "This is new board!")
                    .hasFieldOrPropertyWithValue("closed", false);
            }
    }

    @Test
    public void updateBoardTest() throws IOException {
        BoardForTests board = new BoardForTests();
        board.setName("NewBoard");
        board.setDesc("This is new board!");
        board.setClosed(false);

        BoardService service = retrofit.create(BoardService.class);
        Call<BoardForTests> callSyncPost = service.postBoard(key, token, board);

        Response<BoardForTests> responsePost = callSyncPost.execute();
        BoardForTests createdBoard = responsePost.body();

        if (createdBoard != null) {
            idListOfCreatedBoards.add(createdBoard.getId());
            board.setName("ChangedBoard");
            board.setDesc("This board has been changed!");

            Call<BoardForTests> callSyncPut = service.putBoard(createdBoard.getId(), key, token, board);

            Response<BoardForTests> responsePut = callSyncPut.execute();
            BoardForTests updatedBoard = responsePut.body();

            assertEquals(responsePut.code(), 200);
            assertThat(updatedBoard).hasFieldOrPropertyWithValue("id", createdBoard.getId())
                    .hasFieldOrPropertyWithValue("name", "ChangedBoard")
                    .hasFieldOrPropertyWithValue("desc", "This board has been changed!")
                    .hasFieldOrPropertyWithValue("closed", false);
        }
    }

    @Test
    public void deleteBoardTest() throws IOException {
        BoardForTests board = new BoardForTests();
        board.setName("NewBoard");
        board.setDesc("This is new board!");
        board.setClosed(false);

        BoardService service = retrofit.create(BoardService.class);
        Call<BoardForTests> callSyncPost = service.postBoard(key, token, board);

        Response<BoardForTests> responsePost = callSyncPost.execute();
        BoardForTests createdBoard = responsePost.body();

        if (createdBoard != null) {
            Call<BoardForTests> callSyncDelete = service.deleteBoard(createdBoard.getId(), key, token);
            Response<BoardForTests> responseDelete = callSyncDelete.execute();

            Call<BoardForTests> callSyncGet = service.getBoard(createdBoard.getId(), key, token);
            Response<BoardForTests> responseGet = callSyncGet.execute();

            assertEquals(responseDelete.code(), 200);
            assertEquals(responseGet.code(), 404);
        }
    }
}
