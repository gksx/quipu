package org.gksx.quipu;

public class QuipuConfiguration implements Configuration {
    
    private String uri = "127.0.0.1";
    private int port = 6379;
    private int poolSize = 10;

    private QuipuConfiguration(){}

    public static ConfigurationBuilder configurationBuilder(){
        return new ConfigurationBuilder();
    }

    public static QuipuConfiguration defaultConfiguration(){
        return new ConfigurationBuilder()
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

    @Override
    public int poolSize() {
        return this.poolSize;
    }

    public static class ConfigurationBuilder {
        private QuipuConfiguration configuration;
        
        private ConfigurationBuilder() {
            configuration = new QuipuConfiguration();
        }

        public ConfigurationBuilder uri(String uri){
            configuration.uri = uri;
            return this;
        }

        public ConfigurationBuilder port(int port){
            configuration.port = port;
            return this;
        }

        public ConfigurationBuilder poolSize(int poolSize) {
            configuration.poolSize = poolSize;
            return this;
        }

        public QuipuConfiguration build(){
            return configuration;
        }
    }
}
