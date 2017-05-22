package code;

import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

import utils.SystemParas;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NlpirTest_1122 {

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"D:\\汉语分词20140928\\lib\\win64\\NLPIR", CLibrary.class);
		
		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);
				
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);
		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);
		public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
		public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) throws Exception {
		String argu = ".";
		// String system_charset = "GBK";//GBK----0
		String system_charset = "UTF-8";
		int charset_type = 1;
		Map<String,Integer> dir = new HashMap<>();
		String readStop = TFIDFTEST.FiletoText("data/stopWords.txt");
		String []stop = readStop.split(" ");
		List<String> stopList = new ArrayList<String>(Arrays.asList(readStop.split(" ")));

		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;
		String nativeBytes1 = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			return;
		}

		String sInput = "系统上电,根据大气机面板指示灯状态（工作灯点亮，故障灯不点亮）及XSC-18B项目测试软件显示的GJB289A总线，数据块中工作模式为正常工作方式，测试初始化功能。\n" +
				"采用插桩方法，在初始化任务过程中根据输出脉冲使示波器连续10次显示，查看供电引脚时间间隔。\n";
		String sInput1 = "系统上电，通过设置静压、总压正常输入，电阻正常输入，查看发送大气参数正确，验证CPU，变量，总线数据正确";
		//String nativeBytes = null;
		try {
			//nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);

			//System.out.println("分词结果为： " + nativeBytes);
			
			CLibrary.Instance.NLPIR_AddUserWord("上电 n");
			CLibrary.Instance.NLPIR_AddUserWord("插桩 v");
			CLibrary.Instance.NLPIR_AddUserWord("大气机 n");
			CLibrary.Instance.NLPIR_AddUserWord("引脚 n");
			CLibrary.Instance.NLPIR_AddUserWord("点亮 n");
			CLibrary.Instance.NLPIR_AddUserWord("不点亮 n");
			CLibrary.Instance.NLPIR_AddUserWord("XSC-18B n");
			nativeBytes1 = CLibrary.Instance.NLPIR_ParagraphProcess(sInput1,0);
			nativeBytes1 = nativeBytes1.replaceAll("[\\pP‘’“”]", "");
			System.out.println(nativeBytes1);

			File []files = TFIDF.getAllFile("D:\\00MyResource\\实验数据\\");
			System.out.println(files.length);

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                String test = TFIDFTEST.FiletoText(f.getPath());

				test = test.replace(" ", "");
				test = test.replace("\r", "");
                nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(test, 0);
				nativeBytes = nativeBytes.replaceAll("[\\pP‘’“”]", "");

				List<String> cutList = new ArrayList<String>(Arrays.asList(nativeBytes.split(" ")));

				int count = 0;
				for(int k=0;k<cutList.size();k++){
					for(int h = 0;h<stop.length;h++){
						if (stop[h].equals(cutList.get(k))){
							count++;
							cutList.remove(k);
							break;
						}
					}

				}
				for(int k=0;k<cutList.size();k++){

					if(!TFIDFTEST.isContainChinese(cutList.get(k))){
						cutList.remove(k);
						count++;
					}
				}
//				for(String x:cutList)
//					System.out.println(x);
//				System.out.println(cutList.size());

                String file = "D:\\test\\"+f.getName().substring(0,f.getName().lastIndexOf("."))+".txt";
                File newFile= new File(file);
                FileOutputStream foutput = null;

                foutput = new FileOutputStream(newFile);
				for(int k=0;k<cutList.size();k++){
					foutput.write((cutList.get(k)+"\r\n").getBytes("UTF-8"));
					if(!dir.containsKey(cutList.get(k)))
						dir.put(cutList.get(k),1);
					else
						dir.put(cutList.get(k),dir.get(cutList.get(k))+1);
				}
			}
			File newFile = new File("d:\\wttg\\map.txt");
			FileOutputStream foutput = null;
			foutput = new FileOutputStream(newFile);
			int count = 0;
			int countMap = 0;


			dir = TFIDFTEST.sortByValue(dir);
			for(Object key :dir.keySet() )
			{
				if(!stopList.contains(key)) {
					String str = key + ":" + dir.get(key) + "\r\n";
					foutput.write(str.getBytes("UTF-8"));
					countMap++;
				}


			}
			CLibrary.Instance.NLPIR_Exit();

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}
}
