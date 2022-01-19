package apitests.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;

public class TrelloBoardTest {

    CloseableHttpClient client;
    URI baseURI;
    String keyAndToken;
    List<String> idListOfCreatedBoards;

    @BeforeClass
    public void init() throws URISyntaxException {
        idListOfCreatedBoards = new ArrayList<>();
        client = HttpClientBuilder.create().build();

        Properties properties = new Properties();
        baseURI = new URI("https://trello.com/1/boards/");
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
    public void end() throws IOException {

        for(String id : idListOfCreatedBoards){
            HttpDelete httpDelete = new HttpDelete(baseURI + id + keyAndToken);
            client.execute(httpDelete);
        }
        client.close();
    }

    @Test
    public void creatingNewBoardTest() throws IOException {
        HttpPost httpPost = new HttpPost(baseURI + keyAndToken + "&name=New");
        CloseableHttpResponse response = client.execute(httpPost);

        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(),ObjectNode.class);
        idListOfCreatedBoards.add(node.get("id").asText());

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(node).isNotNull();
        assertThat(node.get("name").asText()).isEqualTo("New");
    }

    @Test
    public void gettingBoardByIDTest() throws IOException {
        HttpPost httpPost = new HttpPost(baseURI + keyAndToken + "&name=New");
        CloseableHttpResponse response = client.execute(httpPost);

        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(),ObjectNode.class);
        idListOfCreatedBoards.add(node.get("id").asText());

        String id = node.get("id").asText();
        HttpGet httpGet = new HttpGet(baseURI + id + keyAndToken);
        CloseableHttpResponse responseGet = client.execute(httpGet);

        HttpEntity entityGet = responseGet.getEntity();
        ObjectNode nodeGet = new ObjectMapper().readValue(entityGet.getContent(),ObjectNode.class);

        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertThat(nodeGet).isNotNull();
        assertThat(id).isEqualTo(nodeGet.get("id").asText());
        assertThat(nodeGet.get("name").asText()).isEqualTo("New");
    }

    @Test
    public void updateBoardTest() throws IOException {
        HttpPost httpPost = new HttpPost(baseURI + keyAndToken + "&name=New");
        CloseableHttpResponse response = client.execute(httpPost);

        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(), ObjectNode.class);
        idListOfCreatedBoards.add(node.get("id").asText());

        String id = node.get("id").asText();
        HttpPut httpPut = new HttpPut(baseURI + id + keyAndToken + "&name=Changed");
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
        HttpPost httpPost = new HttpPost(baseURI + keyAndToken + "&name=New");
        CloseableHttpResponse response = client.execute(httpPost);

        HttpEntity entity = response.getEntity();
        ObjectNode node = new ObjectMapper().readValue(entity.getContent(), ObjectNode.class);

        String id = node.get("id").asText();
        HttpDelete httpDelete = new HttpDelete(baseURI + id + keyAndToken);
        CloseableHttpResponse responseDelete = client.execute(httpDelete);

        HttpGet httpGet = new HttpGet(baseURI + id + keyAndToken);
        CloseableHttpResponse responseGet = client.execute(httpGet);

        assertEquals(responseDelete.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        assertEquals(responseGet.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);

    }
}
