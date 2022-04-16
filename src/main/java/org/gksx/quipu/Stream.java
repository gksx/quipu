package org.gksx.quipu;

import java.util.Map;

public interface Stream {
    StreamEntry xAdd(String stream, String entryKey, Map<String, String> values);
}
