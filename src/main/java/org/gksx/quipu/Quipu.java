package org.gksx.quipu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gksx.quipu.pubsub.OnMessageAction;
import org.gksx.quipu.pubsub.PubSubQuipu;

public class Quipu extends PubSubQuipu implements Commands, Stream, AutoCloseable {
    private StreamHandler streamHandler;

    public Quipu(String uri, int port)  {
        Configuration configuration = QuipuConfiguration
            .configurationBuilder()
            .uri(uri)
            .port(port)
            .build();

        connect(configuration);
    }

    public Quipu()  {
        Configuration configuration = QuipuConfiguration.defaultConfiguration();
        connect(configuration);
    }

    public Quipu(Configuration configuration) {
        connect(configuration);
    }

    private void connect(Configuration configuration)  {
        Connection connection = new Connection(configuration);
        streamHandler = new StreamHandler(connection);                                                        
    }

    private Long toLong(byte[] resp){
        if (resp == null)
            return null;
        return Long.valueOf(new String(resp));
    }

    private String toString(byte[] resp){
        if (resp == null)
            return null;
        return new String(resp);
    }

    private byte[] callRawByteArray(String... args){
        QuipuResponse returnObject = streamHandler.prepareArgsProcessReply(args);
        if (returnObject == null)
            return null;

        return returnObject.buffer();
    }

    private String[] callRawExpectList(String... args){

        Object rObject = streamHandler.prepareArgsProcessReply(args);
        if (rObject == null)
            return null;
        return (String[])rObject;
    }

    public String call(String... args){
        var resp = callRawByteArray(args);
        return toString(resp);
    }

    @Override
    public String multi(){
        return call(Commands.Keys.MULTI);
    }

    @Override
    public String[] exec(){
        return callRawExpectList(Commands.Keys.EXEC);
    }   

    @Override
    public void close()  {
        streamHandler.close();
    }

    @Override
    public String get(String key) {
        byte[] response = callRawByteArray(Commands.Keys.GET, key);
        return toString(response);
    }

    @Override
    public void set(String key, String value) {
        call(Commands.Keys.SET, key, value);
    }

    @Override
    public Long incr(String key) {
        byte[] resp = callRawByteArray(Commands.Keys.INCR, key);
        return toLong(resp);
    }

    @Override
    public void setEx(String key, Long seconds, String value) {
        call(Commands.Keys.SETEX, key, seconds.toString(), value);
    }

    @Override
    public Long ttl(String key) {
        byte[] resp = callRawByteArray(Commands.Keys.TTL, key);
        return toLong(resp);
    }

    @Override
    public Long incrBy(String key, Long value) {
        byte[] resp = callRawByteArray(Commands.Keys.INCRBY, key, value.toString());
        return toLong(resp);
    }

    @Override
    public void set(String key, Long value) {
        set(key, value.toString());
    }

    @Override
    public Long append(String key, String value) {
        byte[] resp = callRawByteArray(Commands.Keys.APPEND, key, value);
        return toLong(resp);
    }

    @Override
    public Long del(String key) {
        byte[] resp = callRawByteArray(Commands.Keys.DEL, key);
        return toLong(resp);
    }

    @Override
    public String getDel(String key) {
        byte[] resp = callRawByteArray(Commands.Keys.GETDEL, key);
        return toString(resp);
    }

    @Override
    public Long hset(String key, Map<String, String> map) {
        List<String> list = new ArrayList<>();
        
        list.add(Commands.Keys.HSET);
        list.add(key);

        for (Map.Entry<String, String> entry : map.entrySet()){
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        
        String[] commandArray = list.stream().toArray(String[] ::new);

        byte[] resp = callRawByteArray(commandArray);
        
        return toLong(resp);
    }

    @Override
    public PubSubQuipu subscribe(String channel)  {
        String[] resp = callRawExpectList(Commands.Keys.SUBSCRIBE, channel);
        setChannel(resp[1]);
        return this;
    }
    
    @Override
    public void listen(OnMessageAction onMessageAction) {
        while(true) {
            Object resp = streamHandler.processReply();
            if (resp instanceof String[]) {
                String[] bulkArray = (String[]) resp;
                if (isEventFromCurrentChannel(bulkArray[1])) {
                    onMessageAction.perform(bulkArray[2]);
                }
            }
        }
    }

    @Override
    public Long strlen(String key) {
        var resp = callRawByteArray(Commands.Keys.STRLEN, key);
        return toLong(resp);
    }

    @Override
    public Long publish(String channel, String message) {
        var resp = callRawByteArray(Commands.Keys.PUBLISH, channel, message);
        return toLong(resp);
    }

    @Override
    public Long lpush(String key, String element) {
        var resp = callRawByteArray(Commands.Keys.LPUSH, key, element);
        return toLong(resp);
    }

    @Override
    public Long lpush(String key, List<String> elements) {
        var list = new ArrayList<String>();
        list.add(Commands.Keys.LPUSH);
        list.add(key);
        list.addAll(elements);
        String[] commandArray = list.stream().toArray(String[] ::new);
        var resp = callRawByteArray(commandArray);
        return toLong(resp);
    }

    @Override
    public String lpop(String key) {
        var resp = callRawByteArray(Commands.Keys.LPOP, key);
        return toString(resp);
    }

    @Override
    public String[] lpop(String key, Long count) {
        var resp = callRawExpectList(Commands.Keys.LPOP, key, count.toString());
        return resp;
    }

    @Override
    public Long llen(String key) {
        var resp = callRawByteArray(Commands.Keys.LLEN, key);
        return toLong(resp);
    }

    @Override
    public Long setRange(String key, Long offset, String value) {
        var resp = callRawByteArray(Commands.Keys.SETRANGE, key, offset.toString(), value);
        return toLong(resp);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        var resp = callRawExpectList(Commands.Keys.HGETALL, key);
        HashMap<String, String> hash = new HashMap<>();

        for (int i = 0; i < resp.length; i+=2) {
            hash.put(resp[i], resp[i+1]);
        }
        
        return hash;
    }

    @Override
    public String hget(String key, String field) {
        var resp = callRawByteArray(Commands.Keys.HGET, key, field);
        return toString(resp);
    }

    @Override
    public String xAdd(String stream, String entryKey, Map<String, String> values) {
        String[] args = new String[(values.size() * 2) +3];

        args[0] = Commands.Keys.XADD;
        args[1] = stream;
        args[2] = entryKey;
        var entries = values.entrySet();
        int i = 3;
        for (Entry<String,String> entry : entries) {
            args[i] = entry.getKey();
            args[i+1] = entry.getValue();
            i += 2;
        }

        return call(args);
    }

    @Override
    public List<StreamEntry> xRange(String stream, String start, String end) {
        var resp = callRawByteArray(Commands.Keys.XRANGE, stream, start, end);
        return null;
    }
}   