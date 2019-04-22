package Utils;

import java.io.*;
import java.util.ArrayList;

public class DB {

    private static final String PATH = "Utils.DB/";

    public static String getMessage(String fileName){

        String message = "";
        String line = "";

        try {
            FileReader fileReader =
                    new FileReader(fileName);
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            message = bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null) {
                message += "\n" + line;
            }

            bufferedReader.close();

            return message;
        }
        catch(FileNotFoundException ex) {}
        catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static int getSize(String fileName){
        File f = new File(fileName);
        return (int) f.length();
    }

    private static ArrayList<String> getMessageList(String userDir){
        ArrayList<String> results = new ArrayList<String>();


        File[] files = new File(userDir).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }

        return results;
    }

    public static int[] STAT(String user){
        int[] res = {0,0};
        ArrayList<String> liste = getMessageList(("Utils.DB/"+user));

        for (String fileName : liste){
             res[0] += 1;
             res[1] += getSize(fileName);
        }
        return res;
    }

    public static String LIST(String user){
        String res = "";
        ArrayList<String> liste = getMessageList(("Utils.DB/"+user));
        //String[][] buff = new


        for (String fileName : liste){
            String[] parts = fileName.split("_");


        }
        return res;
    }






}

