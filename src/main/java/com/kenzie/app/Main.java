package com.kenzie.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Main {
    // Initialize the required constant variable here
    static final String GET_URL = "http://www.boredapi.com/api/activity";

    //Formats URL query string with one property
    public static String formatURL(String URLString, String parameter, String value){
        //TODO: Fill out method and update return value
        String formatted = URLString+"?"+parameter+"="+value;
        return formatted;
    }

    //Overload method: Formats URL query string with two properties
    public static String formatURL(String URLString, String parameter1, String value1,String parameter2, String value2){
        //TODO: Fill out method and update return value
        String formatted = URLString+"?"+parameter1+"="+value1+"&"+parameter2+"="+value2;
        return formatted;
    }
    
    public static String getURLResponse(String URLString)  {
        //TODO: Fill out method and update return value
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URLString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = httpResponse.statusCode();
            if (statusCode == 200) {
                return httpResponse.body();
            } else {
                // String.format is fun! Worth a Google if you're interested
                return String.format("GET request failed: %d status code received", statusCode);
            }
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }

    }
    
    public static String formatActivityOutput(String jsonString) throws JsonProcessingException {
        //TODO: Fill out method and update return value
        if(jsonString.isEmpty()){
            return "No activity found";
        }else{
            ObjectMapper objectMapper = new ObjectMapper();
            ActivityDTO activity = objectMapper.readValue(jsonString, ActivityDTO.class);
            StringBuilder jsonFormat = new StringBuilder();
            jsonFormat.append("Activity: ")
                      .append(activity.getActivity())
                      .append("\n")
                      .append("Type: ")
                      .append(activity.getType())
                      .append("\n")
                      .append("Participants: ")
                      .append(activity.getParticipants())
                      .append("\n")
                      .append("Price: ")
                      .append(activity.getPrice())
                      .append("\n");

            return jsonFormat.toString();
        }

    }

    public static String getActivityRandom(String URL) throws JsonProcessingException {
        //TODO: Fill out method and update return value
        String randomActivity = getURLResponse(URL);
        return randomActivity;
    }

    public static String getActivityType(String URL, String type) throws com.fasterxml.jackson.core.JsonProcessingException,IOException{
        //TODO: Fill out method and update return value
        String typeUrl = formatURL(URL, "type", type);
        String activityType = getURLResponse(typeUrl);
        return activityType;
    }

    public static String getActivityWithPrice(String URL, double price) throws com.fasterxml.jackson.core.JsonProcessingException,IOException{
        //TODO: Fill out method and update return value
        String priceUrl = formatURL(URL, "price", Double.toString(price));
        String activityPrice = getURLResponse(priceUrl);
        return activityPrice;
    }

    public static String getActivityTypeForGroup(String URL, String type, int numParticipants) {
        //TODO: Fill out method and update return value
        String participants_typeURL = formatURL(URL, "type", type, "participants", Integer.toString(numParticipants));
        String groupActivity = getURLResponse(participants_typeURL);
        return groupActivity;
    }

    /** Do not modify main method **/
    public static void main(String[] args) throws IOException {
        String response;
        try {
            System.out.println("Are you feeling bored? Try these activities: ");

            //parse JSON string into formatted output
            System.out.println(formatActivityOutput(getActivityRandom(GET_URL)));
            System.out.println(formatActivityOutput(getActivityType(GET_URL, "education")));
            System.out.println(formatActivityOutput(getActivityWithPrice(GET_URL, 0)));
            System.out.println(formatActivityOutput(getActivityTypeForGroup(GET_URL, "recreational",4)));

            //Test for error checking: this last one does not have a return. Should print "No activity found"
            System.out.println(formatActivityOutput(getActivityType(GET_URL, "mayhem")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
