package org.gksx.quipu;

import java.util.Map;

public interface Stream {
    String xAdd(String stream, String entryKey, Map<String, String> values);
}
