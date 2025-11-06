package chess.server;

import chess.ResponseException;
import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ClientLoginResult register(String username, String password, String email) throws ResponseException {
        // returns username & authToken strings
        var request = buildRequest("POST", "/user", null,
                "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"email\": \"" + email + "\"\n" +
                "}");
        var response = sendRequest(request);

        return handleResponse(response, ClientLoginResult.class);
    }

    public ClientLoginResult login(String username, String password) throws ResponseException {
        //Returns username and authToken strings
        var request = buildRequest("POST", "/session", null,
                "{\n" +
                        "  \"username\": \"" + username + "\",\n" +
                        "  \"password\": \"" + password + "\"\n" +
                        "}");
        var response = sendRequest(request);

        return handleResponse(response, ClientLoginResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        //Doesn't return anything!
        var request = buildRequest("DELETE", "/session", authToken, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ClientListGamesResult listGames(String authToken) throws ResponseException {
        //Returns a complex system of games. Example:
//        200: OK
//
//        {
//            "games": [
    //            {
    //                "gameID": 3102,
    //                    "gameName": "gameName"
    //            },
    //            {
    //                "gameID": 9027,
    //                    "gameName": "gameName"
    //            }
//              ]
//        }
        var request = buildRequest("GET", "/game", authToken, null);
        var response = sendRequest(request);
        return handleResponse(response, ClientListGamesResult.class);
    }

    public ClientCreateGameResult createGame(String gameName, String authToken) throws ResponseException {
        //returns a gameID int
        var request = buildRequest("POST", "/game", authToken,
                "{\n" +
                        "  \"gameName\": \"" + gameName + "\"\n" +
                        "}");
        var response = sendRequest(request);

        return handleResponse(response, ClientCreateGameResult.class);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        //Returns nothing
        var request = buildRequest("PUT", "/game", authToken,
                "{\n" +
                        "  \"playerColor\": \"" + playerColor + "\",\n" +
                        "  \"gameID\": " + gameID + "\n" +
                        "}");
        var response = sendRequest(request);

        handleResponse(response, null);
    }

    public void clearData() throws ResponseException {
        //Returns nothing
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);

        handleResponse(response, null);
    }

    /*
    public void addPet(Pet pet) throws ResponseException {
        var request = buildRequest("POST", "/pet", pet);
        var response = sendRequest(request);
        return handleResponse(response, Pet.class);
    }

    public void deletePet(int id) throws ResponseException {
        var path = String.format("/pet/%s", id);
        var request = buildRequest("DELETE", path, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void deleteAllPets() throws ResponseException {
        var request = buildRequest("DELETE", "/pet", null);
        sendRequest(request);
    }

    public PetList listPets() throws ResponseException {
        var request = buildRequest("GET", "/pet", null);
        var response = sendRequest(request);
        return handleResponse(response, PetList.class);
    }

     */

    private HttpRequest buildRequest(String method, String path, String authToken, String body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if(authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(String request) {
        if (request != null) {
            return BodyPublishers.ofString(request);
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}