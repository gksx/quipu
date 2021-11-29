package org.gksx.quipu;

public class CommandFactory {
    
    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";

    public static byte[] build(String... args){
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("*%d%s", args.length, CARRIAGE_RETURN_LINE_FEED));

        for (String arg : args) {
            sb.append(String.format("$%d%s", arg.length(), CARRIAGE_RETURN_LINE_FEED));
            sb.append(String.format("%s%s", arg, CARRIAGE_RETURN_LINE_FEED));
        }

        return sb.toString().getBytes();
    }
}
