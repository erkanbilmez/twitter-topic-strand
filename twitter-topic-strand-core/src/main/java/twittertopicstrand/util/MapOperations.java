package twittertopicstrand.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapOperations {	
	public static void writeToFile(Map<?, ?> map, String fileName) {
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String line = entry.getKey() + " " + entry.getValue();
			FileOperations.addLine(line, fileName);
		}		
	}
	
	public static void printMap(Map<?, ?> map){
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	
	public static <K, V> Map<K, V> sortMapByValue(Map<K, V> unsortMap) { // descending
		 
		List list = new LinkedList(unsortMap.entrySet());
 
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
                                       .compareTo(((Map.Entry) (o1)).getValue());
			}
		});
 
		Map sortedMap = new LinkedHashMap();
		
		for(int i=0;i<list.size();i++) {
			Map.Entry entry = (Map.Entry) list.get(i);
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}

}
