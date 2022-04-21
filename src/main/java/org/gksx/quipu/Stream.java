package org.gksx.quipu;

import java.util.List;
import java.util.Map;

public interface Stream {
    String xAdd(String stream, String entryKey, Map<String, String> values);
    List<StreamEntry> xRange(String stream, String start, String end);
}
