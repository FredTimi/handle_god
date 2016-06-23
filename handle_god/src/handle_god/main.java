package handle_god;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class main {
    private static final String CONFIG_FILE_PATH = "project_properties.txt";
	private static final String poolTestedDir = "PoolTestee"+File.separator;
	private static final String serializePrefix = "serialized_";
	public static String paramDir = "ai"+File.separator;
    public static String paramFileName = "ParamAI.txt";
	public static String paramFile = "ai"+File.separator+"ParamAI.txt";
	public static String desktopJar = "desktop-1.0.jar";
	public static String waitingGeneration = "toGenerate";
	public static String modGeneration = "Generation";
	public static String modCombination = "Combine";
	public static String modLoad = "Load";
	public static boolean debug=false;
	public static float percentUP = 0.30f;
	public static float percentDOWN = 0.30f;
	public static final String paramPercentUP = "percentUP";
	public static final String paramPercentDOWN = "percentDOWN";
	public static ArrayList<String> allFilesToFilter;
	
	
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
        
        /*
        ArrayList<String> array = new ArrayList<>();
        array = getFilterList();
        if(fileContains(paramFile, modCombination))
        	setFilter();
        removeUnlessPoolsTrees(array);
        */
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
	
	public static void setFilter()
	{
		File file = new File(paramDir+paramFileName);
		try {
			Scanner scanner = new Scanner(file);
		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        if(line.contains(":"))
		        {
			        int index = line.indexOf(":");
	                String param = line.substring(0, index);
	                String value = line.substring(index+1).trim();
	                switch(param.trim()){
	                    case paramPercentUP:
	                    		if((float)Integer.parseInt(value)<=1)
	                            	percentUP = (float)Integer.parseInt(value);
	                    		else
	                    			percentUP = ((float)Integer.parseInt(value)/100);
	                            break;
	                    case paramPercentDOWN:
	                    	if((float)Integer.parseInt(value)<=1)
	                            percentDOWN = (float)Integer.parseInt(value);
	                    	else
	                    		percentDOWN = ((float)Integer.parseInt(value)/100);
	                        break;
	                    default :
	                        System.err.println("Can't find : "+param);
	                        break;
	                }
		        }
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
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

	public static ArrayList<String> getFilterList(){
		ArrayList<String> filter = null;
		try {
			filter = Hadoop.getLastGenerationFilename();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int max = (int)(filter.size()* percentUP);
		int min = (int)(filter.size()* percentDOWN);
		ArrayList<String> resFilter = new ArrayList<>();
		for(int i=0; i<max;i++)
			resFilter.add(filter.get(i));
		for(int i = filter.size(); i> (filter.size()-min) ; i--)
			resFilter.add(filter.get(i));
		return resFilter;
	}
	
	/*
	 * Supprime du répertoire PoolTestee tous les fichiers non sélectionnés par le filtre
	 */
	public static void removeUnlessPoolsTrees(ArrayList<String> filted){
        File directory= new File(poolTestedDir);
        if(!directory.exists()){
            System.out.println("SetPoolsArrays "+poolTestedDir+"+ n'existe pas");
            return;
        }
        allFilesToFilter = new ArrayList<String>();
        for (File file : directory.listFiles()) {
            if (file.getName().contains(serializePrefix+"x") && file.getName().contains(".txt") && filted.contains(file.getName())) {
            	allFilesToFilter.add(file.getName());
            }
            else
            	file.delete();
        }        
        System.out.println("SetPoolsArrays : Arrays for files Set");
    }

}
