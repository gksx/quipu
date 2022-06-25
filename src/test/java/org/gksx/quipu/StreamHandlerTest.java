package org.gksx.quipu;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class StreamHandlerTest {
    

    @Test
    public void respArray() {
        String partOne = "*2\r\n";
        String partTwo = "$5\r\nhello\r\n$5\r\nhello\r\n";
        Connection connection = mock(Connection.class);

        when(connection.read())
            .thenReturn(
                partOne.charAt(0),
                partOne.charAt(1),
                partOne.charAt(2),
                partOne.charAt(3),
                partTwo.charAt(0),
                partTwo.charAt(1),
                partTwo.charAt(2)
            );

        when(connection.read(5))
            .thenReturn("hello".getBytes());

        StreamHandler sh = new StreamHandler(connection);

        var q = sh.processReply();
        
    }
}
