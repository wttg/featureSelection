package code;

import java.io.*;

import java.util.*;
import java.util.Map;


public class TFIDF {

    static String  vsmpath = "D:\\save\\save.txt";
    private Map<String,Integer> wordTimesEveryFile = new HashMap<>();
    private Map<String,Integer> wordTimesAllFiles = new HashMap<>();
    private static Map<String, Map<String, Double>> DFdic = new HashMap();
    private Map<String,Double> idfWeight = new HashMap<>();
    private static Map<String, Map<String,Double>> TFIDFdic = new HashMap();
    private static Map<String, List<Map<String,Double>>> dic = new HashMap();
    private static int COUNT;



    public static File[] getAllFile(String path){
        int size = 1;
        File[] fdir = new File[size];
        if(new File(path).isFile()) {
            fdir[0] = new File(path);
            return fdir;
        }
        else {
            File[] floderdir = new File(path).listFiles();
            ArrayList<File> list = new ArrayList();
            for (File f : floderdir) {
                if (!f.isDirectory()) //判断是不是目录
                    list.add(f);
                else {
                    File[] childdir = f.listFiles();
                    for (File i : childdir) {
                        list.add(i);

                    }
                }
            }
            size = list.size();
            COUNT = size;
            fdir = new File[size];
            for (int i = 0; i < size; i++) {
                fdir[i] = list.get(i);

            }
            return fdir;
        }
    }

    public static String[] cutWord(String text) throws IOException {

        String[] result = text.split(" ");
        List<String> list=Arrays.asList(result);
        List<String> arrayList=new ArrayList<String>(list);//转换为ArrayLsit调用相关的remove方法
        arrayList.remove("（");
        arrayList.remove("）");
        arrayList.remove("(");
        arrayList.remove(")");
        arrayList.remove("，");
        arrayList.remove("。");
        arrayList.remove(",");
        arrayList.remove("，");
        arrayList.remove("！");
        arrayList.remove("：");
        arrayList.remove("“");
        arrayList.remove("”");

        result  = arrayList.toArray(new String[arrayList.size()]);
        return result;
    }

    public static String FiletoText(File file) throws IOException {

        String path = file.getPath();
        System.out.println(path);
        File f = new File(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "UTF-8"));
        String result = "";
        String temp;
        while ((temp = br.readLine()) != null) {
            result = result + temp;
        }
        br.close();
        return result;
    }

    public static HashMap<String, Double> computeTf(String[] textword) {
        double size = textword.length;
        HashMap<String, Integer> map = new HashMap();
        for (int i = 0; i < textword.length; i++) {
            System.out.println(textword[i]);
            if (map.containsKey(textword[i])) {
                Integer count = map.get(textword[i]);

                map.put(textword[i], count + 1);

            } else {
                map.put(textword[i], 1);
            }

        }
        HashMap<String, Double> result = new HashMap();
        for (Object o : map.keySet()) {
            Integer temp = map.get(o);
            result.put((String) o, temp / size);
            System.out.println((String) o + "tf..." + result.get(o));

        }

        return result;

    }

    public  void computeIDF(Map<String, List<Map<String, Double>>> idfMap) {

        for (String key : idfMap.keySet()) {
            //compute
            List list = idfMap.get(key);
            int hasCount =  list.size();
            double idf = COUNT / (hasCount);

            idfWeight.put(key, idf);
        }
    }

    public void ComputeTFIDF() {

        for (String filepath : DFdic.keySet()) {
            Map<String,Double>filedic = DFdic.get(filepath);
            HashMap<String, Double> tfidfPair = new HashMap();
            for (Object key : filedic.keySet()) {

                double tf = filedic.get(key);

                double idf = idfWeight.get(key);
                double tfidf = tf* Math.log(idf+1);
                System.out.println( key+"tf" + tf + "idf" + idf + "tfidf" + tfidf);
                tfidfPair.put((String) key, tfidf);
            }
            //  System.out.println(tfidfPair.size());
            TFIDFdic.put(filepath, tfidfPair);
        }

    }


    public static void readir(File[] dir) throws IOException {

        File[] fileList = dir;


        for (int i = 0; i < fileList.length; i++) {
            File f = fileList[i];
            String test = FiletoText(f);
            System.out.println("wttg:"+test);
            String[] textword = cutWord(test);
            Map tf = computeTf(textword);
            DFdic.put(f.getPath(), tf);
            addDic(f.getPath(), tf);
        }

        System.out.println("TF PROCESS IS OVER");



    }

    public static void addDic(String path, Map tf) {

        for (Object o : tf.keySet()) {
            if (dic.containsKey((String) o)) {

                ArrayList list = (ArrayList) dic.get(o);
                HashMap map = new HashMap();
                map.put(path, (Double) tf.get((String) o));
                list.add(map);
                dic.put((String) o, list);
            } else {
                HashMap map = new HashMap();
                map.put(path, (Double) tf.get((String) o));
                ArrayList list = new ArrayList<Map<String, Double>>();
                list.add(map);
                dic.put((String) o, list);
            }

        }

    }


    public void toLibData() throws IOException// 转化成libsvm格式；

    {
        int count = 0;
        // int size =dic.entrySet().size();
        List wordList = new ArrayList();
        for (String word : dic.keySet()) {
            wordList.add(word);
            System.out.println("worddic add   " + word);
        }

        System.out.println("total word is"+wordList.size());
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(new File(vsmpath)));
        /*
         * String [] wordList = new String[size]; int num=0; for(String word:
         * dic.keySet()) { wordList[num]=word; num++;
         * System.out.println("worddic add"+word); }
         */
        String vsm = "";
        for (String filename : TFIDFdic.keySet()) {
            String lable = new File(filename).getName();
            Map map = TFIDFdic.get(filename);// 获取某片文章对应的tfidf
            vsm = vsm + lable + " ";
            for (int i = 0; i < wordList.size(); i++) {

                // System.out.println( "map.."+ map.size());

                // String temp =wordList[i];
                // System.out.println("temp"+ temp);
                String temp = (String) wordList.get(i);
                if (map.containsKey(temp)) {
                    vsm = vsm + temp + ":" + map.get(temp) + " ";
                    // System.out.println(filename + "...." + temp + "...."+
                    // map.get(temp) + "...");
                }
                else {
                    vsm = vsm + temp + ":" +0.00 + " ";
                    // System.out.println(filename + "...." + temp + "...."+
                    // map.get(temp) + "...");
                }
            }
            count++;
            vsm = vsm + "\n";
            bw.write(vsm);
            vsm = "";
            System.out.println("format" + "  " + count + " " + filename);
        }
        System.out.println("begin output");
        // BufferedWriter bw = new BufferedWriter(new FileWriter(new
        // File(vsmpath)));
        // bw.write(vsm);
        System.out.println(".............................");
        // System.out.println(vsm);
        bw.close();

    }

//    public static void main(String[] args) throws Exception {
//        File file[] = getAllFile("C:\\Users\\wt\\Downloads\\文本分类语料库\\军事249\\81.txt");
//        System.out.println(file[0].getName());
////        TFIDF tf = new TFIDF();
////        String path = "D:\\test\\";
////
////
////        File[] Filelist = getAllFile(path);
////
////        readir(Filelist);
////        System.out.println("DFdic");
////
////        tf.computeIDF(dic);
////        tf.ComputeTFIDF();
////        System.out.println("计算完毕开始输出");
////        tf.toLibData();
////
////
////        Double numerator = 0.0;
////        Double vector1 = 0.0;
////        Double vector2 = 0.0;
////        Double denominator = 0.0;
////
////        Map map1 = TFIDFdic.get("D:\\test\\test.txt");
////        //System.out.println(map1.get("模式"));
////        Map map2 = TFIDFdic.get("D:\\test\\test1.txt");
////       // System.out.println(map2.get("模式"));
////        for (String word : dic.keySet()) {
////            if(!map1.containsKey(word))
////                map1.put(word,0.0);
////            if(!map2.containsKey(word))
////                map2.put(word,0.0);
////            numerator += (Double)map1.get(word)*(Double) map2.get(word);
////            vector1 += (Double)map1.get(word)*(Double) map1.get(word);
////            vector2 += (Double)map2.get(word)*(Double) map2.get(word);
////
////        }
////
////        denominator = Math.sqrt(vector1) * Math.sqrt(vector2);
////
////        System.out.println(numerator);
////        System.out.println(denominator);
////        System.out.println(numerator/denominator);
////    }
////
//    }

}

