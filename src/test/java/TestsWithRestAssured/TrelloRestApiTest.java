package TestsWithRestAssured;

import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import  static  io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.testng.Assert.assertEquals;

public class TrelloRestApiTest {

    String keyAndToken;
    List<String> idListOfCreatedBoards;

    @BeforeClass
    public void init(){
        idListOfCreatedBoards = new ArrayList<>();
        Properties properties = new Properties();

        baseURI = "https://trello.com/1/boards/";
        String path = "src/main/resources/trello.properties";

        try {
            properties.load(new FileInputStream(path));
            keyAndToken = String.format("?key=%s&token=%s", properties.getProperty("key"), properties.getProperty("token"));
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @AfterClass
    public void clean(){
        for(String id : idListOfCreatedBoards){
            delete(id + keyAndToken);
        }
    }

    @Test
    public void creatingNewBoardTest(){

        BoardForTests boardToCreate = new BoardForTests();
        boardToCreate.setName("NewBoard");
        boardToCreate.setClosed(false);
        boardToCreate.setDesc("This is new board!");

        Response response = given().contentType("application/json")
                .body(boardToCreate)
                .log().body()
                .when()
                .post(keyAndToken)
                .then().log().body()
                .extract().response();

        BoardForTests boardFromResponse = response.as(BoardForTests.class);
        idListOfCreatedBoards.add(boardFromResponse.getId());

        assertEquals(response.getStatusCode(), 200);
        assertThat(boardFromResponse).usingRecursiveComparison()
                .comparingOnlyFields("name", "closed", "desc")
                .isEqualTo(boardToCreate);
    }

    @Test
    public void gettingBoardByIDTest(){

        BoardForTests boardToCreate = new BoardForTests();
        boardToCreate.setName("NewBoard");
        boardToCreate.setClosed(false);
        boardToCreate.setDesc("This is new board!");

        BoardForTests createdBoard = given().contentType("application/json")
                .body(boardToCreate)
                .when()
                .post(keyAndToken)
                .then().log().body()
                .extract().response()
                .as(BoardForTests.class);

        String id = createdBoard.getId();

        Response response = given().contentType("application/json")
                .body(boardToCreate)
                .when()
                .get(id + keyAndToken)
                .then().log().body()
                .extract().response();

        BoardForTests boardFromResponse = response.as(BoardForTests.class);
        idListOfCreatedBoards.add(boardFromResponse.getId());

        assertEquals(response.getStatusCode(), 200);
        assertThat(boardFromResponse).usingRecursiveComparison()
                .isEqualTo(createdBoard);
    }

    @Test
    public void updateBoardTest() {
        BoardForTests boardToCreate = new BoardForTests();
        boardToCreate.setName("NewBoard");
        boardToCreate.setClosed(false);
        boardToCreate.setDesc("This is new board!");

        BoardForTests createdBoard = given().contentType("application/json")
                .body(boardToCreate)
                .when()
                .post(keyAndToken)
                .then().log().body()
                .extract().response()
                .as(BoardForTests.class);

        String id = createdBoard.getId();

        Response response = given().contentType("application/json")
                .param("name", "ChangedBoard")
                .param("desc", "This board has been changed!")
                .when()
                .put(id + keyAndToken)
                .then().log().body()
                .extract().response();

        BoardForTests changedBoard = response.as(BoardForTests.class);
        idListOfCreatedBoards.add(id);

        assertEquals(response.getStatusCode(), 200);
        assertThat(changedBoard).hasFieldOrPropertyWithValue("name", "ChangedBoard")
                .hasFieldOrPropertyWithValue("desc", "This board has been changed!")
                .hasFieldOrPropertyWithValue("id", id);
    }

    @Test
    public void deleteBoard(){
        BoardForTests boardToCreate = new BoardForTests();
        boardToCreate.setName("NewBoard");
        boardToCreate.setClosed(false);
        boardToCreate.setDesc("This is new board!");

        BoardForTests createdBoard = given().contentType("application/json")
                .body(boardToCreate)
                .when()
                .post(keyAndToken)
                .then().log().body()
                .extract().response()
                .as(BoardForTests.class);

        String id = createdBoard.getId();

        Response responseDelete = given().contentType("application/json")
                .when()
                .delete(id + keyAndToken)
                .then().log().body()
                .extract().response();

        Response responseGet = given().contentType("application/json")
                .when()
                .get(id + keyAndToken)
                .then().log().body()
                .extract().response();

        BoardForTests deletedBoard = responseDelete.as(BoardForTests.class);

        assertEquals(responseDelete.getStatusCode(), 200);
        assertEquals(responseGet.getStatusCode(), 404);
    }
}
