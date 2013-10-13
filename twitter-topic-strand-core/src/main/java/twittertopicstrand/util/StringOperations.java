package twittertopicstrand.util;

public class StringOperations {
	public static String[] fromString(String string) {
	    String[] parts = string.replace("[", "").replace("]", "").split(",");
	    String result[] = new String[parts.length];
	    for (int i = 0; i < result.length; i++) {
	      result[i] = parts[i].trim();
	    }
	    return result;
	}
}
