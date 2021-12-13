package org.gksx.quipu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gksx.quipu.PubSub.OnEvent;
import org.gksx.quipu.PubSub.PubSubQuipu;

public class Quipu extends PubSubQuipu implements Commands {

    private static final byte DOLLAR_BYTE = '$';
    private static final byte ASTERISK_BYTE = '*';
    private static final byte PLUS_BYTE = '+';
    private static final byte MINUS_BYTE = '-';
    private static final byte COLON_BYTE = ':';   
    private static final char CARRIAGE_RETURN = '\r';
    private static final int NILVALUE = -1;
    
    private String uri = "127.0.0.1";
    private int port = 6379;

    private QuipuStream quipuStream;

    public Quipu(String uri, int port) throws IOException {
        this.uri = uri;
        this.port = port;
        connect();
    }

    public Quipu() throws IOException {
        connect();
    }

    public Quipu(Configuration configuration) throws IOException{
        this.port = configuration.getPort();
        this.uri = configuration.getUri();
        connect();
    }

    public Quipu(QuipuStream quipuStream){
        this.quipuStream = quipuStream;
    }

    public Quipu(String channel) {
    }

    private void connect() throws IOException {
        quipuStream = new QuipuStream(this.uri, this.port);                                                        
    }

    private Object callRaw(String... args) throws IOException, QuipuException {
        var formatted = CommandFactory.build(args);
        quipuStream.writeAndFlush(formatted);
        return proccessReply();
    }
    
    public String call(String... args) throws IOException, QuipuException{
        var resp = callRawByteArray(args);
        return toString(resp);
    }

    public int parse() throws IOException, QuipuException{
        int len = 0;

        char p = quipuStream.read();

        while (p != CARRIAGE_RETURN){
            
            if (p == '-'){
                quipuStream.moveToEndOfLine();
                return NILVALUE;
            }

            len = (len*10) + (p - '0');
            p = quipuStream.read();
        }
        quipuStream.read();
        
        return len;
    }

    private byte[] parseBulkString(int len) throws IOException, QuipuException{

        if (len == 0) return null;
        
        byte[] buf = new byte[len];
        
        quipuStream.readBuf(buf, 0, len);
        quipuStream.moveToEndOfLine();
        return buf;
    }

    private Object proccessReply() throws IOException, QuipuException {
        char prefix = quipuStream.read();

        switch (prefix){
            case DOLLAR_BYTE:{
                int len = parse();
                if (len == NILVALUE)
                    return null;
                var q = parseBulkString(len);
                return q;
            }
            case ASTERISK_BYTE:{
                return parseBulkArray();                
            }
            case PLUS_BYTE:{
                return quipuStream.readLine();
            }
            case MINUS_BYTE:{
                String errorMessage = new String(quipuStream.readLine());
                throw new QuipuException(errorMessage);
            }
            case COLON_BYTE:{
                return quipuStream.readLine();
            }
            default:
                throw new QuipuException("somethin went wrong");
        }                  
    }

    private List<String> parseBulkArray() throws IOException, QuipuException {
        int elemnts = parse();

        List<String> list = new ArrayList<>();

        for(var i = 0; i < elemnts; i++) {
            list.add(new String((byte[])proccessReply()));
        }

        return list;
    }

    public void close() throws IOException {
        quipuStream.close();
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

    private byte[] callRawByteArray(String... args) throws IOException, QuipuException{
        Object returnObject = callRaw(args);
        if (returnObject == null)
            return null;
        return (byte[])returnObject;
    }

    @Override
    public String get(String key) throws IOException, QuipuException {
        byte[] response = callRawByteArray(GET, key);
        return toString(response);
    }

    @Override
    public void set(String key, String value) throws IOException, QuipuException {
        callRaw(SET, key, value);
    }

    @Override
    public Long incr(String key) throws IOException, QuipuException {
        byte[] resp = callRawByteArray(INCR, key);
        return toLong(resp);
    }

    @Override
    public void setEx(String key, Long seconds, String value) throws IOException, QuipuException {
        callRaw(SETEX, key, seconds.toString(), value);
    }

    @Override
    public Long ttl(String key) throws IOException, QuipuException {
        byte[] resp = (byte[])callRaw(TTL, key);
        return toLong(resp);
    }

    @Override
    public Long incrBy(String key, Long value) throws IOException, QuipuException {
        byte[] resp = callRawByteArray(INCRBY, key, value.toString());
        return toLong(resp);
    }

    @Override
    public void set(String key, Long value) throws IOException, QuipuException {
        set(key, value.toString());
    }

    @Override
    public Long append(String key, String value) throws IOException, QuipuException {
        byte[] resp = callRawByteArray(APPEND, key, value);
        return toLong(resp);
    }

    @Override
    public Long del(String key) throws IOException, QuipuException {
        byte[] resp = callRawByteArray(DEL, key);
        return toLong(resp);
    }

    @Override
    public String getDel(String key) throws IOException, QuipuException {
        byte[] resp = callRawByteArray(GETDEL, key);
        return toString(resp);
    }

    @Override
    public Long hset(String key, Map<String, String> map) throws IOException, QuipuException {
        List<String> list = new ArrayList<>();
        
        list.add(HSET);
        list.add(key);

        for (Map.Entry<String, String> entry : map.entrySet()){
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        
        String[] array = list.stream().toArray(String[] ::new);

        byte[] resp = callRawByteArray(array);
        
        return toLong(resp);
    }

    @Override
    public PubSubQuipu subscribe(String channel) throws IOException, QuipuException {
        var quipu = new Quipu();
        quipu.callRawByteArray(SUBSCRIBE, channel);
        return quipu;
    }

    @Override
    public void onEvent(OnEvent onEvent) {
        while(quipuStream.isOpen()){
            byte[] bytes;
            try {
                bytes = quipuStream.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
}