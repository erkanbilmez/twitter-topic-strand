package twittertopicstrand.converter;

public class BilkentToStatusConverter {
	public static void convert(String sourceDir, String destDir){
		ReduceOperations reduce = new ReduceOperations(true);
		reduce.runAllFolderFiles(sourceDir, destDir);
	}
}
