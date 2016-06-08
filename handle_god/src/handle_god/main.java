package handle_god;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class main {
    private static final String CONFIG_FILE_PATH = "project_properties.txt";
	public static String paramFile = "ai"+File.separator+"ParamAI.txt";
	public static String desktopJar = "desktop-1.0.jar";
	public static String waitingGeneration = "toGenerate";
	public static String modGeneration = "Generation";
	public static String modCombination = "Combine";
	public static String modLoad = "Load";
	public static boolean debug=false;
	
	
	public static void main(String[] args) throws Exception 
	{
		loadProperties();
		//Hadoop.createGeneticTable();
		while(true)
		{	
			if(args.length == 0 || args[0] == null || args[0] == "" )
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
		final Process ps=Runtime.getRuntime().exec(new String[]{"java","-jar",jarPathName});
		if(debug)
        {
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					boolean crash = false;
					Scanner sc = new Scanner(ps.getInputStream());
					try{
					while(sc.hasNext()){
					        System.out.println(sc.nextLine());		        
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			t.start();
        
        }
		ps.waitFor();
        //Affichage output si besoin
        
        if(fileContains(paramFile, modGeneration))
        {
        	changeModInParamAI(paramFile, modGeneration, modCombination);
        }
        //Hadoop.saveWebDataOnHive();
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
	
	
	public static void loadProperties(){
        System.out.println("Load properties from "+CONFIG_FILE_PATH);
        File file = new File (CONFIG_FILE_PATH);
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            Scanner sc = new Scanner(file);
            String line = "";
            while(sc.hasNextLine()){
                line = sc.nextLine();
                if(line.startsWith("#"))
                    continue;
                if(line.contains(":")){
                    int index = line.indexOf(":");
                    String param = line.substring(0, index);
                    String value = line.substring(index+1).trim();
                    switch(param.trim()){
                        case "HADOOP_CONFIG_DIRECTORY":
                            Hadoop.HADOOP_CONFIG_DIRECTORY=value;
                            break;
                        case "HIVE" :
                            Hadoop.HIVE = value;
                            break;
                        case "HADOOP_PASSWORD":
                            Hadoop.HADOOP_USER_PASSWORD = value;
                            break;
                        case "HADOOP_USER":
                            Hadoop.HADOOP_USER_NAME = value;
                            break;
                        case "HDFS":
                            Hadoop.HDFS_PATH = value;
                        case "GENETIC_DIRECTORY":
                                Hadoop.GENETIC_DIRECTORY = value;
                                break;
                        case "GENETIC_TESTED_DIRECTORY":
                                Hadoop.GENETIC_TESTED = value;
                            break;
                        case "PARAM_FILE":
                        	paramFile = value;
                        	break;
                        case "SHOW_DEBUG":
                            	debug = true;
                            	break;
                        default :
                            System.err.println("Can't find : "+param);
                            break;
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
