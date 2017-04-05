package br.ufms.facom.sp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {

    public static byte[] readFile(File file) throws FileNotFoundException, 
            IOException{
        if(file==null){
            return null;
        }
        String line;
        try (BufferedReader bReader = new BufferedReader(new java.io.FileReader(file))) {
            line = "";
            while (bReader.ready()) {
                line += bReader.readLine();
            }
        }
        return line.getBytes();        
    }
    
    public static void saveFile(byte[] content, File file) throws FileNotFoundException, 
            IOException {
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(content);
            os.flush();
        }        
    }
    
}
