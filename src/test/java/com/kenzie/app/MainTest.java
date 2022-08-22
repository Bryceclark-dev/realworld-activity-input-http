package com.kenzie.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SerializationFeature;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.*;


public class MainTest {
	private MockWebServer mockWebServer;

	public static final String TEST_ENDPOINT = "http://www.boredapi.com/api/activity";
	public static final String TEST_ACTIVITY = "Try a food you don't like";
	public static final String TEST_TYPE = "social";
	public static final String TEST_KEY = "6693574";
	public static final String TEST_TYPE_TWO = "recreational";
	public static final String TEST_KEY_TWO = "6693575";
	public static final String TEST_LINK = "http://www.w3schools.com/java/";
	public static final String TEST_KEY_RESPONSE = "{\"activity\":\"Try a food you don't like\",\"type\":\"recreational\",\"participants\":1,\"price\":0.1,\"link\":\"\",\"key\":\"6693574\",\"accessibility\":0.05}";
	public static final String TEST_FORMAT_URL = "http://www.boredapi.com/api/activity?social=6693574";
	public static final String TEST_FORMAT_URL_TWO = "http://www.boredapi.com/api/activity?social=6693574&recreational=6693575";
	public static final double TEST_PRICE = 0.1;
	public static final double TEST_ACCESSIBILITY = 1.0;
	public static final int TEST_PARTICIPANTS = 1;
	public static final String TEST_FORMATTED_ACTIVITY = "Activity: Try a food you don't like\n" +
			"Type: recreational\n" +
			"Participants: 1\n" +
			"Price: 0.1\n";


	@BeforeEach
	void init() throws IOException {
		this.mockWebServer = new MockWebServer();
		this.mockWebServer.start();
	}

	@Test
	void canCreateActivity() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		try{
			//check that required methods are defined
			ActivityDTO activityInstance = new ActivityDTO();

			Method setActivityMethod = ActivityDTO.class.getMethod("setActivity",String.class);
			setActivityMethod.invoke(activityInstance, TEST_ACTIVITY);

			Method setTypeMethod = ActivityDTO.class.getMethod("setType",String.class);
			setTypeMethod.invoke(activityInstance, TEST_TYPE);
			Method setKeyMethod = ActivityDTO.class.getMethod("setKey",String.class);
			setKeyMethod.invoke(activityInstance, TEST_KEY);
			Method setLinkMethod = ActivityDTO.class.getMethod("setLink",String.class);
			setLinkMethod.invoke(activityInstance, TEST_LINK);
			Method setParticipantsMethod = ActivityDTO.class.getMethod("setParticipants",int.class);
			setParticipantsMethod.invoke(activityInstance, TEST_PARTICIPANTS);
			Method setAccessibilityMethod = ActivityDTO.class.getMethod("setAccessibility",double.class);
			setAccessibilityMethod.invoke(activityInstance, TEST_ACCESSIBILITY);
			Method setPriceMethod = ActivityDTO.class.getMethod("setPrice",double.class);
			setPriceMethod.invoke(activityInstance, TEST_PRICE);

			Method getActivityMethod = ActivityDTO.class.getMethod("getActivity");
			String activityValue = (String) getActivityMethod.invoke(activityInstance);
			assertEquals(TEST_ACTIVITY, activityValue, "Can set/get activity");

			Method getTypeMethod = ActivityDTO.class.getMethod("getType");
			String typeValue = (String) getTypeMethod.invoke(activityInstance);
			assertEquals(TEST_TYPE, typeValue, "Can set/get type");

			Method getKeyMethod = ActivityDTO.class.getMethod("getKey");
			String keyValue = (String) getKeyMethod.invoke(activityInstance);
			assertEquals(TEST_KEY, keyValue, "Can set/get key");

			Method getLinkMethod = ActivityDTO.class.getMethod("getLink");
			String linkValue = (String) getLinkMethod.invoke(activityInstance);
			assertEquals(TEST_LINK, linkValue, "Can set/get link");

			Method getParticipantsMethod = ActivityDTO.class.getMethod("getParticipants");
			int participantsValue = (int) getParticipantsMethod.invoke(activityInstance);
			assertEquals(TEST_PARTICIPANTS, participantsValue, "Can set/get Participants");

			Method getAccessibilityMethod = ActivityDTO.class.getMethod("getAccessibility");
			double accessibilityValue = (double) getAccessibilityMethod.invoke(activityInstance);
			assertEquals(TEST_ACCESSIBILITY, accessibilityValue, "Can set/get Accessibility");
			
			Method getPriceMethod = ActivityDTO.class.getMethod("getPrice");
			double priceValue = (double) getPriceMethod.invoke(activityInstance);
			assertEquals(TEST_PRICE, priceValue, "Can set/get Price");
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			fail("ActivityDTO and all setter/getter methods must be defined");
		}

	}


	@Test
	void canFormatURL_OnePropertyAndValue(){
		try{
			assertEquals(TEST_FORMAT_URL, Main.formatURL(TEST_ENDPOINT, TEST_TYPE, TEST_KEY));
		}
		catch(Exception e) {
			fail("Exception " + e.getMessage());
		}
	}

	@Test
	void canFormatURL_TwoPropertiesAndValues(){
		try{
			assertEquals(TEST_FORMAT_URL_TWO, Main.formatURL(TEST_ENDPOINT, TEST_TYPE, TEST_KEY, TEST_TYPE_TWO, TEST_KEY_TWO));
		}
		catch(Exception e) {
			fail("Exception " + e.getMessage());
		}
	}

	@Test
	void canGetURLResponse() {
    	try {
			mockWebServer.enqueue(new MockResponse()
					.addHeader("Content-Type", "application/json")
					.setBody(TEST_KEY_RESPONSE)
					.setResponseCode(200));
			assertEquals(TEST_KEY_RESPONSE, Main.getURLResponse(mockWebServer.url("/").toString()), "Send GET to URL and receive JSON response");
		}
    	catch (Exception e) {
			fail("Exception " + e.getMessage());
		}
	}

	@Test
	void canMapActivity() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			//convert json string to object
			ActivityDTO activity = objectMapper.readValue(TEST_KEY_RESPONSE, ActivityDTO.class);
			assertTrue(activity instanceof ActivityDTO, "Initialized ActivityDTO based on JSON");
			Method getActivityMethod = ActivityDTO.class.getMethod("getActivity");
			String activityValue = (String) getActivityMethod.invoke(activity);
			assertTrue(activityValue instanceof String);
		}
		catch (Exception e) {
			fail("Exception " + e);
		}
	}


	@Test
	void canGetActivityType() {
		try {

			mockWebServer.enqueue(new MockResponse()
					.addHeader("Content-Type", "application/json")
					.setBody(TEST_KEY_RESPONSE)
					.setResponseCode(200));
			String response = Main.getActivityType(mockWebServer.url(TEST_ENDPOINT).toString(),TEST_TYPE_TWO);

			ObjectMapper objectMapper = new ObjectMapper();
			//convert json string to object
			ActivityDTO activity = objectMapper.readValue(response, ActivityDTO.class);
			Method getTypeMethod = ActivityDTO.class.getMethod("getType");
			String typeValue = (String) getTypeMethod.invoke(activity);
			assertEquals(TEST_TYPE_TWO, typeValue, "Get activity by type");
		}
		catch (Exception e) {
			fail("Exception " + e);
		}
	}

	@Test
	void canGetActivityWithPrice() {
		try {
			mockWebServer.enqueue(new MockResponse()
					.addHeader("Content-Type", "application/json")
					.setBody(TEST_KEY_RESPONSE)
					.setResponseCode(200));
			String response = Main.getActivityWithPrice(mockWebServer.url(TEST_ENDPOINT).toString(),TEST_PRICE);

			ObjectMapper objectMapper = new ObjectMapper();
			//convert json string to object
			ActivityDTO activity = objectMapper.readValue(response, ActivityDTO.class);
			Method getPriceMethod = ActivityDTO.class.getMethod("getPrice");
			double priceValue = (double) getPriceMethod.invoke(activity);
			assertEquals(TEST_PRICE, priceValue, "Get activity by price");

		}
		catch (Exception e) {
			fail("Exception " + e);
		}
	}

	@Test
	void canGetActivityTypeForGroup() {
		try {
			mockWebServer.enqueue(new MockResponse()
					.addHeader("Content-Type", "application/json")
					.setBody(TEST_KEY_RESPONSE)
					.setResponseCode(200));

			String response = Main.getActivityTypeForGroup(mockWebServer.url(TEST_ENDPOINT).toString(),TEST_TYPE_TWO,TEST_PARTICIPANTS);

			ObjectMapper objectMapper = new ObjectMapper();
			//convert json string to object
			ActivityDTO activity = objectMapper.readValue(response, ActivityDTO.class);
			Method getTypeMethod = ActivityDTO.class.getMethod("getType");
			String typeValue = (String) getTypeMethod.invoke(activity);
			assertEquals(TEST_TYPE_TWO, typeValue, "Get activity by type");

			Method getParticipantsMethod = ActivityDTO.class.getMethod("getParticipants");
			int participantsValue = (int) getParticipantsMethod.invoke(activity);
			assertEquals(TEST_PARTICIPANTS, participantsValue, "Get activity by participants");
		}
		catch (Exception e) {
			fail("Exception " + e);
		}
	}

	@Test
	void canFormatActivityOutput() {
		try {
			assertEquals(TEST_FORMATTED_ACTIVITY, Main.formatActivityOutput(TEST_KEY_RESPONSE),"Parse activity from JSON");
		}
		catch (Exception e) {
			fail("Exception " + e);
		}
	}


}
