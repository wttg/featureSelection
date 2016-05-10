package code;
import java.io.*;

import java.util.*;
import java.util.Map;


public class TFIDF {

    private Map<String,Integer> wordTimesEveryFile = new HashMap<>();
    private Map<String,Integer> wordTimesAllFiles = new HashMap<>();

    public void computeWordsTimes(List<String[]> text){
    }
    public static File[] getAllFile(File file){
        if(!file.exists()){
            System.out.println("directory is empty.");
            return null;
        }
        int size = 0;
        File[] floderdir = file.listFiles();
        ArrayList list = new ArrayList();
        for (File f : floderdir) {
            if(!f.isDirectory()) //判断是不是目录
                list.add(file);
            else {
                File[] childdir = f.listFiles();
                for (File i : childdir) {
                    list.add(i);

                }
            }
        }
        size = list.size();
        File[] fdir = new File[size];
        for (int i = 0; i < size; i++) {
            fdir[i] = (File) list.get(i);

        }
        return fdir;
    }

    public static void main(String[] args) {
        File file = new File("D:\\test\\");

        System.out.print(getAllFile(file).length);
    }

}