package pt.isec.pd.a2020136093.server.rest_api.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Scanner;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PdRestApiComSegurancaConsumer {

    public static String sendRequestAndShowResponse(String uri, String verb, String authorizationValue, String body) throws MalformedURLException, IOException {

        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if(authorizationValue!=null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        if(body!=null){
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "Application/Json");
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " +  responseCode + " (" + connection.getResponseMessage() + ")");

        Scanner s;

        if(connection.getErrorStream()!=null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException e){}

        connection.disconnect();

        System.out.println(verb + " " + uri + (body==null?"":" with body: "+body) + " ==> \n" + responseBody);
        System.out.println();

        return responseBody;
    }


    public static void main(String args[]) throws MalformedURLException, IOException {

        String helloUri = "http://localhost:8080/hello/fr?name=Jeanne";
        String helloUri2 = "http://localhost:8080/hello/gr?name=Jeanne";
        String loginUri = "http://localhost:8080/login";
        String loremUri = "http://localhost:8080/lorem";

        System.out.println();

        //OK
        sendRequestAndShowResponse(helloUri, "GET", null, null);

        //Língua "gr" não suportada
        sendRequestAndShowResponse(helloUri2, "GET", null, null);

        //Falta um campo "Authorization: basic ..." válido no cabeçalho do pedido para autenticação básica
        String token = sendRequestAndShowResponse(loginUri, "POST",null, null);

        //OK
        String credentials = Base64.getEncoder().encodeToString("admin:admin".getBytes());
        token = sendRequestAndShowResponse(loginUri, "POST","basic "+ credentials, null); //Base64(admin:admin) YWRtaW46YWRtaW4=

        //Falta um campo "Authorization: bearer ..." no cabeçalho do pedido com um token JWT válido
        sendRequestAndShowResponse(loremUri, "GET", null, null);

        //OK
        sendRequestAndShowResponse(loremUri+"?type=word&length=6", "GET", "bearer " + token, null);

        //PUT não suportado para esta URI
        sendRequestAndShowResponse(loremUri, "PUT", "bearer " + token, null);

        //POST sem corpo de mensagem
        sendRequestAndShowResponse(loremUri, "POST", "bearer " + token, null);

        //Ok
        sendRequestAndShowResponse(loremUri, "POST", "bearer " + token, "{\"type\":\"word\",\"length\":4}");

    }
}

