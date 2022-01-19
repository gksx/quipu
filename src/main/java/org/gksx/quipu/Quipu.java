package org.gksx.quipu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Quipu extends PubSubQuipu implements Commands {
  
    private Connection connection;

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
        return call(Commands.Keys.MULTI);
    }

    public String[] exec(){
        return callRawExpectList(Commands.Keys.EXEC);
    }

    public int parse(){
        int len = 0;

        char p = connection.read();

        while (p != RespConstants.CARRIAGE_RETURN){
            
            if (p == '-'){
                connection.moveToEndOfLine();
                return RespConstants.NILVALUE;
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
            case RespConstants.DOLLAR_BYTE:{
                int len = parse();
                if (len == RespConstants.NILVALUE)
                    return null;
                var q = parseBulkString(len);
                return q;
            }
            case RespConstants.ASTERISK_BYTE:
                return parseBulkArray();                
            case RespConstants.PLUS_BYTE:
                return connection.readLine();
            case RespConstants.COLON_BYTE:
                return connection.readLine();
            case RespConstants.MINUS_BYTE:{
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
        byte[] response = callRawByteArray(Commands.Keys.GET, key);
        return toString(response);
    }

    @Override
    public void set(String key, String value) {
        callRaw(Commands.Keys.SET, key, value);
    }

    @Override
    public Long incr(String key) {
        byte[] resp = callRawByteArray(Commands.Keys.INCR, key);
        return toLong(resp);
    }

    @Override
    public void setEx(String key, Long seconds, String value) {
        callRaw(Commands.Keys.SETEX, key, seconds.toString(), value);
    }

    @Override
    public Long ttl(String key) {
        byte[] resp = (byte[])callRaw(Commands.Keys.TTL, key);
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
        var resp = callRawByteArray(Commands.Keys.STRLEN, key);
        return toLong(resp);
    }

    @Override
    public Long publish(String channel, String message) {
        var resp = callRawByteArray(Commands.Keys.PUBLISH, channel, message);
        return toLong(resp);
    }
}