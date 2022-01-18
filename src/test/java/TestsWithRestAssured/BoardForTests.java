package TestsWithRestAssured;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class BoardForTests {

    private String name;
    private String desc;
    private boolean closed;
    private String id;
}
