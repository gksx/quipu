package org.gksx.quipu;

import java.util.Map;
import java.util.Map.Entry;

public class StreamEntry {
    
    private String entryId;
    private Map<String, String> entries;
    public StreamEntry(String entryId, Map<String, String> entries) {
        this.entryId = entryId;
        this.entries = entries;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        
        for (Entry<String,String> entry : entries.entrySet()) {
            s.append(entry.getKey() + ":");
            s.append(entry.getValue());
        }

        return "StreamEntry [entries=" + entries + ", entryId=" + entryId + "]";
    }
}