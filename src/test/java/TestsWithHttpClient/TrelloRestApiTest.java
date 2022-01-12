package TestsWithHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.*;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;

public class TrelloRestApiTest {

    CloseableHttpClient client;

    @BeforeClass
    public void init(){
        client = HttpClientBuilder.create().build();
    }

    @AfterClass
    public void end() throws IOException {
        client.close();
    }


    @Test
    public void creatingNewBoardTest() throws IOException {
        HttpPost httpPost = new HttpPost("https://trello.com/1/boards/?key&token&name=New");
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(),ObjectNode.class);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(node).isNotNull();
        assertThat(node.get("name").asText()).isEqualTo("New");
    }

    @Test
    public void gettingBoardByIDTest() throws IOException {
        HttpPost httpPost = new HttpPost("https://trello.com/1/boards/?key&token&name=New");
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(),ObjectNode.class);
        String id = node.get("id").asText();
        HttpGet httpGet = new HttpGet(String.format("https://trello.com/1/boards/%s?key&token", id));
        CloseableHttpResponse responseGet = client.execute(httpGet);
        HttpEntity entityGet = responseGet.getEntity();
        ObjectNode nodeGet = new ObjectMapper().readValue(entityGet.getContent(),ObjectNode.class);

        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertThat(nodeGet).isNotNull();
        assertThat(id).isEqualTo(nodeGet.get("id").asText());
        assertThat(nodeGet.get("name").asText()).isEqualTo("New");
    }

    @Test
    public void updateBoard() throws IOException {
        HttpPost httpPost = new HttpPost("https://trello.com/1/boards/?key&token&name=New");
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(), ObjectNode.class);
        String id = node.get("id").asText();
        HttpPut httpPut = new HttpPut(String.format("https://trello.com/1/boards/%s?key&token&name=Changed", id));
        CloseableHttpResponse responsePut = client.execute(httpPut);
        HttpEntity entityPut = responsePut.getEntity();
        ObjectNode nodePut= new ObjectMapper().readValue(entityPut.getContent(),ObjectNode.class);

        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertThat(nodePut).isNotNull();
        assertThat(id).isEqualTo(nodePut.get("id").asText());
        assertThat(nodePut.get("name").asText()).isEqualTo("Changed");
    }

    @Test
    public void deleteBoard() throws IOException {
        HttpPost httpPost = new HttpPost("https://trello.com/1/boards/?key&token&name=New");
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(), ObjectNode.class);
        String id = node.get("id").asText();
        HttpDelete httpDelete = new HttpDelete(String.format("https://trello.com/1/boards/%s?key&token", id));
        CloseableHttpResponse responseDelete = client.execute(httpDelete);

        assertEquals(responseDelete.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }
}
