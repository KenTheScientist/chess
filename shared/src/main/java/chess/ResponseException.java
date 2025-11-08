package chess;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public static ResponseException fromJson(String json) {
        String message = "";
        try {
            var map = new Gson().fromJson(json, HashMap.class);
            message = map.get("message").toString();
        }
        catch(JsonSyntaxException e){
            message = json;
        }
        return new ResponseException(Code.ServerError, message);
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }

}