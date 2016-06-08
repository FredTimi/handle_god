package handle_god;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class main {
	public static String pathParamFile = System.getProperty("user.dir") + "/ai/";
	public static String paramFile = "ParamAI.txt";
	public static String pathDesktopJar = System.getProperty("user.dir");
	public static String desktopJar = "desktop-1.0.jar";
	public static String waitingGeneration = "toGenerate";
	public static String modGeneration = "Generation";
	public static String modCombination = "Combine";
	public static String modLoad = "Load";
	
	public static void main(String[] args) throws Exception 
	{
		while(true)
		{	
			if(args[0] == null || args[0] == "" || args.length == 0)
				runJar(pathDesktopJar+desktopJar);
			else
				runJar(args[0]);
		}
	}
	
	public static void runJar(String jarPathName) throws Exception
	{
		if(fileContains(pathParamFile+paramFile, waitingGeneration))
        {
        	changeModInParamAI(pathParamFile+paramFile, waitingGeneration, modGeneration);
        }
		
		// Lancement du process
		Process ps=Runtime.getRuntime().exec(new String[]{"java","-jar",jarPathName});
        ps.waitFor();
        //Affichage output si besoin
//        java.io.InputStream is=ps.getInputStream();
//        byte b[]=new byte[is.available()];
//        is.read(b,0,b.length);
//        System.out.println(new String(b));
        
        if(fileContains(pathParamFile+paramFile, modGeneration))
        {
        	changeModInParamAI(pathParamFile+paramFile, modGeneration, modCombination);
        }
        
	}
	
	public static void changeModInParamAI(String paramAI, String oldMod, String newMod)
	{
		Scanner scanner;
        try {
            File file = new File(paramAI);
            String content = "";
            if(file.exists() && file.length() > 0)
            {
                scanner = new Scanner(file);
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    line = line.replaceAll(oldMod, newMod);
                    content += line+"\r\n";
                }
                FileWriter fw = new FileWriter(file);
                fw.write(content);
                fw.close();
            }
            else // file does not exist
            {
                System.out.println("File does not exist");
            }            
        }
        catch (IOException e)
        {e.printStackTrace();}
	}

	public static boolean fileContains(String paramAI, String research)
	{
		File file = new File(paramAI);
		try {
			Scanner scanner = new Scanner(file);
		    int lineNum = 0;
		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        if(line.contains(research)) { 
		        	return true;
		        }
		    }
		    return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		return false;
	}
}
