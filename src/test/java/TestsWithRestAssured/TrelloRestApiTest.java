package TestsWithRestAssured;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import  static  io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;

public class TrelloRestApiTest {

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
                .post("https://trello.com/1/boards/?key&token")
                .then().log().body()
                .extract().response();

        BoardForTests boardFromResponse = response.as(BoardForTests.class);

        assertThat(boardFromResponse).usingRecursiveComparison()
                .comparingOnlyFields("name", "closed", "desc")
                .isEqualTo(boardToCreate);
    }
}
