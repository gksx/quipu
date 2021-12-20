package org.gksx.quipu;

public class QuipuConfiguration implements Configuration {
    
    private String uri = "127.0.0.1";
    private int port = 6379;

    private QuipuConfiguration(){}

    public static Builder builder(){
        return new Builder();
    }

    public static QuipuConfiguration defaultConfiguration(){
        return new Builder()
            .build();
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    public static class Builder {
        private QuipuConfiguration configuration;
        
        private Builder() {
            configuration = new QuipuConfiguration();
        }

        public Builder uri(String uri){
            configuration.uri = uri;
            return this;
        }

        public Builder port(int port){
            configuration.port = port;
            return this;
        }

        public QuipuConfiguration build(){
            return configuration;
        }
    }
}
