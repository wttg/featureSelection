package code;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by HGDX on 2016/11/21.
 */
public class computeTFIDF {
    public static void main(String[] args) throws Exception {


        String str = "5.82600010738045";
        double num = Double.valueOf(str);

        System.out.println(num);


        // TODO: 2016/11/22
        String word = TFIDFTEST.FiletoString("D:\\实例\\map.txt");//词库路径
        // TODO: 2016/11/22
        String path = "D:\\实例\\test"; //分词后路径
        // TODO: 2016/11/22
        String tfpath = "D:\\实例\\训练集";//tf路径
        // TODO: 2016/11/22
        String idfpath = "D:\\00MyResource\\idfvalue_a.txt";
        Map<String,List<Double>> fileTF = new HashMap<>();
        List<Double> fileIDF = new ArrayList<>();
        Map<String,List<Double>> fileTFIDF = new HashMap<>();

        List<String> wordList = new ArrayList<String>(Arrays.asList(word.split(" ")));
        System.out.println(wordList.size());
        //System.out.println(Math.log(2716));

        File[] files = TFIDF.getAllFile(path);

        System.out.println(files.length);

        /**
         * 计算tf
         */
//        for (int i = 0; i < files.length; i++) {
//            File f = files[i];
//            String test = TFIDFTEST.FiletoString(f.getPath());
//            //System.out.println(test);
//            String []testWord = test.split(" ");
//            List<Double> testtf = TFIDFTEST.computeSigleTf(wordList,testWord);
//            fileTF.put(f.getName(),testtf);
//
//            String file = "D:\\实例\\tfvalue\\"+f.getName().substring(0,f.getName().lastIndexOf("."))+".txt";
//            File newFile= new File(file);
//            FileOutputStream foutput = null;
//
//            foutput = new FileOutputStream(newFile);
//            for(int k=0;k<testtf.size();k++){
//                foutput.write((testtf.get(k)+"\r\n").getBytes("UTF-8"));
//            }
//        }

        /**
         * 计算idf
         */
//        fileIDF = TFIDFTEST.computeSigleIdf(wordList,path);
//        System.out.println(fileIDF.size());
//        for(Double i : fileIDF)
//            System.out.println(i);

        /**
         * 计算tf*idf
         */
        String file = "D:\\实例\\tfidfvalue\\tfidfvalue_train.txt";
        File newFile= new File(file);
        FileOutputStream foutput = null;
        foutput = new FileOutputStream(newFile);

        File[] tfFiles = TFIDF.getAllFile(tfpath);
        String key = "0";
        for (int i = 0; i < tfFiles.length; i++) {
            List<Double> tfIDF = new ArrayList<>();
            File f = tfFiles[i];
            String name = f.getName().substring(0,1);
            if(name.equals("1"))
                key = "1";
            if(name.equals("2"))
                key = "2";
            if(name.equals("3"))
                key = "3";
            if(name.equals("4"))
                key = "4";
//            if(name.equals("5"))
//                key = "5";
//            if(name.equals("6"))
//                key = "6";
//            if(name.equals("7"))
//                key = "7";
//            if(name.equals("8"))
//                key = "8";
//            if(name.equals("9"))
//                key = "9";

            String tftext = TFIDFTEST.FiletoString(f.getPath());
            String []tfCut = tftext.split(" ");
            String idftext = TFIDFTEST.FiletoString(idfpath);
            String []idfCut = idftext.split(" ");
            //foutput.write((f.getName()+"\t").getBytes("UTF-8"));
            for(int j = 0;j<tfCut.length;j++) {
                //tfIDF.add(Double.valueOf(tfCut[j]));
                tfIDF.add(Double.valueOf(tfCut[j])*Double.valueOf(idfCut[j]));
                //if(j == tfCut.length-1)
             //       foutput.write((tfIDF.get(j)+"\r\n").getBytes("UTF-8"));
              //  else
                    foutput.write((tfIDF.get(j)+" ").getBytes("UTF-8"));
            }
            foutput.write((key+"\r\n").getBytes("UTF-8"));
            fileTFIDF.put(f.getName(),tfIDF);

        }

    }
}
