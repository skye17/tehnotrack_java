package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.junit.Before;
import org.junit.Test;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.QueryExecutor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JsonProtocolTest {

    public static final String TEST_STRING = "some string";
    private ObjectProtocol objectProtocol;

    @Before
    public void setUp() throws Exception {
        objectProtocol = new JsonProtocol();
    }

    @Test
    public void testDecodeNullObject() throws Exception {
        ResponseMessage message = objectProtocol.decode(null);
        assertNull(message);
    }

    @Test
    public void testEncodeNullMessage() throws Exception {
        Object result = objectProtocol.encode(null);
        assertNull(result);
    }

    @Test
    public void testDecodeStringObject() throws Exception {
        String testString = "\"" + TEST_STRING + "\"";
        ResponseMessage message = objectProtocol.decode(TEST_STRING);
        assertEquals(message.getResultClass(), String.class.getName());
        assertEquals(message.getResponse(), testString);
    }

    @Test
    public void testDecodeEncodeStringObject() {
        ResponseMessage message = objectProtocol.decode(TEST_STRING);
        Object result = objectProtocol.encode(message);
        assertEquals(result.getClass(), String.class);
        assertEquals(result, TEST_STRING);
    }

    @Test
    public void testDecodeEncodeListObject() {
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(4);
        ResponseMessage message = objectProtocol.decode(testList);
        Object result = objectProtocol.encode(message);
        assertEquals(result.getClass(), testList.getClass());
        List<Integer> castedResult = (ArrayList<Integer>) result;
        assertEquals(castedResult.size(), testList.size());
        for (Integer num : testList) {
            assertTrue(castedResult.contains(num));
        }
    }

    @Test
    public void testDecodeCustomObjectReturnsMessageWithError() {
        QueryExecutor queryExecutor = new QueryExecutor();
        ResponseMessage message = objectProtocol.decode(queryExecutor);
        assertTrue(message.getStatus());
    }

}