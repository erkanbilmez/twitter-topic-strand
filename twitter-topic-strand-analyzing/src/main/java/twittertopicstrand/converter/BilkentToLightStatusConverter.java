package twittertopicstrand.converter;

public class BilkentToLightStatusConverter {
	public static void convert(String sourceDir, String destDir){
		ReduceOperations reduce = new ReduceOperations(false);
		reduce.runAllFolderFiles(sourceDir, destDir);
	}
}