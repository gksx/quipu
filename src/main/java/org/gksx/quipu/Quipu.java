package org.gksx.quipu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Quipu extends PubSubQuipu implements Commands {

    private static final byte DOLLAR_BYTE = '$';
    private static final byte ASTERISK_BYTE = '*';
    private static final byte PLUS_BYTE = '+';
    private static final byte MINUS_BYTE = '-';
    private static final byte COLON_BYTE = ':';   
    private static final char CARRIAGE_RETURN = '\r';
    private static final int NILVALUE = -1;
  
    private Connection connection;

    public Quipu(String uri, int port)  {
        Configuration configuration = QuipuConfiguration
            .builder()
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

    public Quipu(Connection connection){
        this.connection = connection;
    }

    private void connect(Configuration configuration)  {
        connection = new Connection(configuration);                                                        
    }

    private Object callRaw(String... args) {
        var formatted = CommandFactory.build(args);
        connection.writeAndFlush(formatted);
        return proccessReply();
    }

    
    public String call(String... args){
        var resp = callRawByteArray(args);
        return toString(resp);
    }

    public String multi(){
        return call(MULTI);
    }

    public String[] exec(){
        return callRawExpectList(EXEC);
    }

    public int parse(){
        int len = 0;

        char p = connection.read();

        while (p != CARRIAGE_RETURN){
            
            if (p == '-'){
                connection.moveToEndOfLine();
                return NILVALUE;
            }

            len = (len*10) + (p - '0');
            p = connection.read();
        }
        connection.read();
        
        return len;
    }

    private byte[] parseBulkString(int len){

        if (len == 0) return null;
        
        byte[] buf = new byte[len];
        
        connection.readBuf(buf, 0, len);
        connection.moveToEndOfLine();
        return buf;
    }

    private Object proccessReply() {
        char prefix = connection.read();

        switch (prefix){
            case DOLLAR_BYTE:{
                int len = parse();
                if (len == NILVALUE)
                    return null;
                var q = parseBulkString(len);
                return q;
            }
            case ASTERISK_BYTE:
                return parseBulkArray();                
            case PLUS_BYTE:
                return connection.readLine();
            case COLON_BYTE:
                return connection.readLine();
            case MINUS_BYTE:{
                String errorMessage = new String(connection.readLine());
                throw new QuipuException(errorMessage);
            }
            default:
                throw new QuipuException("somethin went wrong");
        }                  
    }

    private String[] parseBulkArray() {
        int elemnts = parse();

        String[] list = new String[elemnts];

        for(var i = 0; i < elemnts; i++) {
            list[i] = new String((byte[])proccessReply());
        }

        return list;
    }

    @Override
    public void close()  {
        connection.close();
    }

    private Long toLong(byte[] data){
        if (data == null)
            return null;
        return Long.valueOf(new String(data));
    }

    private String toString(byte[] data){
        if (data == null)
            return null;
        return new String(data);
    }

    private byte[] callRawByteArray(String... args){
        Object returnObject = callRaw(args);
        if (returnObject == null)
            return null;
        return (byte[])returnObject;
    }

    private String[] callRawExpectList(String... args){

        Object rObject = callRaw(args);
        if (rObject == null)
            return null;
        return (String[])rObject;
    }

    @Override
    public String get(String key) {
        byte[] response = callRawByteArray(GET, key);
        return toString(response);
    }

    @Override
    public void set(String key, String value) {
        callRaw(SET, key, value);
    }

    @Override
    public Long incr(String key) {
        byte[] resp = callRawByteArray(INCR, key);
        return toLong(resp);
    }

    @Override
    public void setEx(String key, Long seconds, String value) {
        callRaw(SETEX, key, seconds.toString(), value);
    }

    @Override
    public Long ttl(String key) {
        byte[] resp = (byte[])callRaw(TTL, key);
        return toLong(resp);
    }

    @Override
    public Long incrBy(String key, Long value) {
        byte[] resp = callRawByteArray(INCRBY, key, value.toString());
        return toLong(resp);
    }

    @Override
    public void set(String key, Long value) {
        set(key, value.toString());
    }

    @Override
    public Long append(String key, String value) {
        byte[] resp = callRawByteArray(APPEND, key, value);
        return toLong(resp);
    }

    @Override
    public Long del(String key) {
        byte[] resp = callRawByteArray(DEL, key);
        return toLong(resp);
    }

    @Override
    public String getDel(String key) {
        byte[] resp = callRawByteArray(GETDEL, key);
        return toString(resp);
    }

    @Override
    public Long hset(String key, Map<String, String> map) {
        List<String> list = new ArrayList<>();
        
        list.add(HSET);
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
        String[] resp = callRawExpectList(SUBSCRIBE, channel);
        setChannel(resp[1]);
        return this;
    }

    
    @Override
    public void listen(OnMessageAction onMessageAction) {
        while(true) {
            Object resp = proccessReply();
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
        var resp = callRawByteArray(STRLEN, key);
        return toLong(resp);
    }

    @Override
    public Long publish(String channel, String message) {
        var resp = callRawByteArray(PUBLISH, channel, message);
        return toLong(resp);
    }
}