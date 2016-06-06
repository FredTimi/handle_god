package handle_god;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class main {
	public static String paramFile = System.getProperty("user.dir") + "/ai/ParamAI.txt";
	public static String desktopJar = System.getProperty("user.dir") + "desktop-1.0.jar";
	public static String waitingGeneration = "toGenerate";
	public static String modGeneration = "Generation";
	public static String modCombination = "Combine";
	public static String modLoad = "Load";
	
	public static void main(String[] args) throws Exception 
	{
		while(true)
		{	
			if(args[0] == null || args[0] == "" || args.length == 0)
				runJar(desktopJar);
			else
				runJar(args[0]);
		}
	}
	
	public static void runJar(String jarPathName) throws Exception
	{
		if(fileContains(paramFile, waitingGeneration))
        {
        	changeModInParamAI(paramFile, waitingGeneration, modGeneration);
        }
		
		// Lancement du process
		Process ps=Runtime.getRuntime().exec(new String[]{"java","-jar",jarPathName});
        ps.waitFor();
        //Affichage output si besoin
//        java.io.InputStream is=ps.getInputStream();
//        byte b[]=new byte[is.available()];
//        is.read(b,0,b.length);
//        System.out.println(new String(b));
        
        if(fileContains(paramFile, modGeneration))
        {
        	changeModInParamAI(paramFile, modGeneration, modCombination);
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
        System.out.println("fin de test");
        /*
         * try{
			File file = new File (paramAI);
		    FileReader fr = new FileReader(file);
		    String s;
		    String totalStr = "";
		    try (BufferedReader br = new BufferedReader(fr)) {

		        while ((s = br.readLine()) != null) {
		            totalStr += s+"\r\n";
		        }
		        totalStr = totalStr.replaceAll(oldMod, newMod);
		        FileWriter fw = new FileWriter(file);
		    fw.write(totalStr);
		    fw.close();
		    }
		}catch(Exception e){
		    e.printStackTrace();
		}
         * */
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
