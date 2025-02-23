
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CRaCValueTrackerTest {
    static int testNumber = 3;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName( "CRaC Value Tracker Test")
    @ParameterizedTest(name = "Test case {index}")
    @MethodSource("testNumbers")
    public void cracValueTrackerTest(int i)  throws Exception {
        var args = new String[]{
                getJarPath(i),
                getClassName(i),
                getBackwardFile(i),
                getForwardFile(i)
        };
        CracValueTracker.main(args);

        JsonNode expectedBackward = objectMapper.readTree(new File(getBackwardFile(i)));
        JsonNode actualBackward = objectMapper.readTree(new File(getExpectedBackwardFile(i)));
        assertEquals(expectedBackward, actualBackward, "Backward JSON files do not match");


        JsonNode expectedForward = objectMapper.readTree(new File(getForwardFile(i)));
        JsonNode actualForward = objectMapper.readTree(new File(getExpectedForwardFile(i)));
        assertEquals(expectedForward, actualForward, "Forward JSON files do not match");
    }


    static Stream<Integer> testNumbers() {
        return IntStream.range(0, testNumber).boxed();
    }

    private String getJarPath(int testNumber) {
        return "src/test/java/jars/test" + testNumber + ".jar";
    }

    private String getClassName(int testNumber) {
        return "test.cases.test" + testNumber + ".Main";
    }

    private String getBackwardFile(int testNumber) {
        return "src/test/java/results/test" + testNumber + "/actualBackward.json";
    }

    private String getForwardFile(int testNumber) {
        return "src/test/java/results/test" + testNumber + "/actualForward.json";
    }

    private String getExpectedBackwardFile(int testNumber) {
        return "src/test/java/results/test" + testNumber + "/expectedBackward.json";
    }

    private String getExpectedForwardFile(int testNumber) {
        return "src/test/java/results/test" + testNumber + "/expectedForward.json";
    }


}
