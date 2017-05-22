package code;
import java.io.*;

import java.util.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class TFIDFTEST{
    static String path = "D:\\test\\";
    static String  vsmpath = "D:\\save\\save.txt";
    //static String path = "E:\\训练集\\SogouC.reduced\\Reduced";
    // static String path ="E:\\训练集\\SogouC.mini\\Sample";
    static Map<String, Map<String, Double>> DFdic = new HashMap();
    static HashMap<String, List<Map<String, Double>>> dic = new HashMap();
    static int DCOUNT;
    static HashMap<String, Double> idfDic = new HashMap();
    static      Map<String, Map<String,Double>> TFIDFdic = new HashMap();
    // static Map<String,List<String>> Simpledic= new HashMap();
    public static void main(String[] args) throws IOException {



        TFIDFTEST tf = new TFIDFTEST();
        File[] Filelist = tf.readData(path);
        DCOUNT = Filelist.length;
        tf.readir(Filelist);
        System.out.println("DFdic");

        tf.computeIDF(dic);
        tf.ComputeTFIDF();
      /* for( String s :TFIDFdic.keySet())
        {
         Map map  = TFIDFdic.get(s);
         for(Object key :map.keySet() )
         {
             System.out.println("file "+s +"word "+ key+" tfidf "+map.get(key));

         }


        }*/

        System.out.println("计算完毕开始输出");
        tf.toLibData();
    }

    public void readir(File[] dir) throws IOException {

        File[] fileList = dir;
        for(File f :fileList){
            System.out.println(f.getPath());
        }

        for (int i = 0; i < fileList.length; i++) {
            File f = fileList[i];
            //System.out.println(f.getPath());
            String test = FiletoText(f.getPath());
            System.out.println("wttg:"+test);
            String[] textword = cutWord(test);
            Map tf = computeTf(textword);
            DFdic.put(f.getPath(), tf);
            addDic(f.getPath(), tf);
        }

        System.out.println("TF PROCESS IS OVER");

        System.out.println(dic.size());
        for (Object o : dic.keySet()) {
            System.out.println(o);
            List list = dic.get(o);
            for (Object l : list) {
                Map pair = (Map) l;
                for (Object p : pair.keySet()) {
                /*  System.out.println("key" + (String) o + "..."
                            + "filepath...." + p + "tf..." + pair.get(p));*/
                }

            }

        }

    }

    public static String FiletoText(String path) throws IOException {
        File f = new File(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "GB2312"));
        String result = "";
        String temp;
        if(path == "data/stopWords.txt"||path =="E:\\wttg\\map.txt") {
            while ((temp = br.readLine()) != null) {
                result = result + temp+" ";
            }
        }
        else{
            while ((temp = br.readLine()) != null) {
                result = result + temp;
            }
        }

        br.close();
        return result;
    }

    public static String FiletoString(String path) throws IOException {
        File f = new File(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "utf-8"));
        String result = "";
        String temp;

        while ((temp = br.readLine()) != null) {
            result = result + temp+" ";
        }



        br.close();
        return result;
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public String[] cutWord(String text) throws IOException {

        String[] result = text.split(" ");
        /*
         * for(String s :result ) { System.out.println(s); }
         */
        return result;
    }

    public HashMap<String, Double> computeTf(String[] textword) {
        double size = textword.length;
        HashMap<String, Integer> map = new HashMap();
        for (int i = 0; i < textword.length; i++) {
            //System.out.println(textword[i]);
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

            //System.out.println((String) o + "count..." + temp);
            result.put((String) o, temp / size);
            //System.out.println((String) o + "tf..." + result.get(o));

        }

        return result;

    }
    public static List<Double> computeSigleTf(List<String> wordList,String []textWord){
        List<Double> TFValue = new ArrayList<>();
        List<Integer> TFCount = new ArrayList<>();

        for (int i = 0; i < wordList.size(); i++) {
            int count = 0;
            for (int j = 0; j < textWord.length; j++) {
                if (textWord[j].equals(wordList.get(i))) {
                    count++;
                }
            }
            TFCount.add(count);
        }
        for (int i = 0; i < wordList.size(); i++) {
            TFValue.add((double) TFCount.get(i)/textWord.length);
        }

        return TFValue;

    }
    public static List<Double> computeSigleIdf(List<String> wordList,String path) throws  Exception{
        List<Double> IDFValue = new ArrayList<>();
        List<Integer> IDFCount = new ArrayList<>();
        File[] files = TFIDF.getAllFile(path);
        for (int i = 0; i < wordList.size(); i++) {//遍历词库
            int count = 0;
            for (int j = 0; j < files.length; j++) {//遍历文件
                File f = files[j];
                String test = TFIDFTEST.FiletoString(f.getPath());
                String []testWord = test.split(" ");
                for (int k = 0; k < testWord.length; k++) {//遍历文件每个词
                    if (testWord[k].equals(wordList.get(i))) {
                        count++;
                        break;
                    }
                }
            }
            IDFCount.add(count);
        }

        for (int i = 0; i < wordList.size(); i++) {
            IDFValue.add(Math.log((double) (2716/(IDFCount.get(i)+1))));
        }
        return IDFValue;
    }

    public void addDic(String path, Map tf) {
        //System.out.println(",,,,,,,,,,,,,");
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

    public static File[] readData(String path) {
        int size = 0;
        File[] floderdir = new File(path).listFiles();
        ArrayList list = new ArrayList();
        for (File f : floderdir) {
            // size = size+(int)f.length();

            list.add(f);


        }
        size = list.size();
        File[] fdir = new File[size];
        for (int i = 0; i < size; i++) {
            fdir[i] = (File) list.get(i);

        }

        return fdir;

    }

    public void computeIDF(HashMap<String, List<Map<String, Double>>> map) {

        for (String key : map.keySet()) {
            List list = map.get(key);
            double hasCount = (double) list.size();
            double idf = DCOUNT / hasCount;
            idfDic.put(key, idf);
        }

    }

    public void ComputeTFIDF() {

        for (String filepath : DFdic.keySet()) {
            Map filedic = DFdic.get(filepath);
            HashMap<String, Double> tfidfPair = tfidfPair = new HashMap();
            for (Object key : filedic.keySet()) {

                double tf = (Double) filedic.get(key);

                double idf = idfDic.get((String) key);
                double tfidf = tf* Math.log(idf);
                //  System.out.println( key+"tf" + tf + "idf" + idf + "tfidf" + tfidf);
                tfidfPair.put((String) key, tfidf);
            }
            //  System.out.println(tfidfPair.size());
            TFIDFdic.put(filepath, tfidfPair);
        }

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    public void toLibData() throws IOException// 转化成libsvm格式；

    {
        int count = 0;
        // int size =dic.entrySet().size();
        List wordList = new ArrayList();
        for (String word : dic.keySet()) {
            wordList.add(word);
            System.out.println("worddic add" + word);
        }
        // System.out.println("total word is"+wordList.size());
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(new File(vsmpath)));
        /*
         * String [] wordList = new String[size]; int num=0; for(String word:
         * dic.keySet()) { wordList[num]=word; num++;
         * System.out.println("worddic add"+word); }
         */
        String vsm = "";
        String num = "";
        for (String filename : TFIDFdic.keySet()) {
            String lable = new File(filename).getParentFile().getName();
            Map map = TFIDFdic.get(filename);// 获取某片文章对应的tfidf
            vsm = vsm + lable + " ";

            for (int i = 0; i < wordList.size(); i++) {

                // System.out.println( "map.."+ map.size());

                // String temp =wordList[i];
                // System.out.println("temp"+ temp);
                String temp = (String) wordList.get(i);
                if (map.containsKey(temp)) {
                    vsm = vsm + temp + ":" + map.get(temp) + " ";
                    num = String.format("%.5f",(Double)map.get(temp))+" ";
                    System.out.println(filename + "...." + temp + "...."+
                            map.get(temp) + "...");
                }
            }
            count++;
            vsm = vsm + "\n";
            //        bw.write(vsm); key+value
            bw.write(num); //only value
            vsm = "";
            num = "";
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

}
