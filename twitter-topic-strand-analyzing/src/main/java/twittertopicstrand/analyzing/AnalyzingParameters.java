package twittertopicstrand.analyzing;

import java.lang.reflect.Field;

import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.util.MapOperations;

public class AnalyzingParameters {
	// missionary
	public static int missionaryWindowSize = 12; // hours
	public static double missionaryThresholdFraction = 0.001;

	// noisy
	public static double noisyK = 6.0;

	// veteran
	public static float veteranFraction = 0.9f;
	public static int veteranSegCount = 10;

	public static JSONObject getParametersAsJson() throws IllegalArgumentException, IllegalAccessException, JSONException {
		JSONObject rVal = new JSONObject();
		
		AnalyzingParameters obj = new AnalyzingParameters();

		for (Field field : AnalyzingParameters.class.getDeclaredFields()) {		
			rVal.put(field.getName(), field.get(obj));
		}

		return rVal;
	}
}
