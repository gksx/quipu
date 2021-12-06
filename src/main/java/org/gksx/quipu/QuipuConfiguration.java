package org.gksx.quipu;

public class QuipuConfiguration implements Configuration {
    private String uri;
    private int port;

    private QuipuConfiguration(){}

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    public static class Builder{
        private QuipuConfiguration configuration;
        private Builder(){
            configuration = new QuipuConfiguration();
        }

        public Builder withUri(String uri){
            configuration.uri = uri;
            return this;
        }

        public Builder withPort(int port){
            configuration.port = port;
            return this;
        }

        public QuipuConfiguration build(){
            return configuration;
        }
    }
}
