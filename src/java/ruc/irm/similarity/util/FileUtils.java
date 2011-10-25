package ruc.irm.similarity.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 与文件相关的工具类
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class FileUtils {
	/**
	 * 根据指定编码从输入流中依次遍历每一行文字
	 * 
	 * @param input
	 *            输入流
	 * @param encoding
	 *            输入流所用的文字编码
	 * @param event
	 *            遍历每一行时触发的事件处理
	 * @throws IOException
	 */
	public static void traverseLines(InputStream input, String encoding, TraverseEvent<String> event) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(input,	encoding));
		String line = null;

		while ((line = in.readLine()) != null) {
			event.visit(line);
		}

		input.close();
		in.close();
	}

	/**
	 * 保存字符串到文件中
	 * @param content
	 * @param fileName
	 * @return
	 */
	public static boolean saveStringToFile(String content, String fileName) {
		boolean rtn = false;
		BufferedOutputStream out = null;
		try {			
			File file = new File(fileName);
			file.getParentFile().mkdirs();
			
			out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(content.getBytes("GBK"));
			out.close();
			rtn = true;
		} catch (Exception e) {
			System.out.println("saveStringToFile error:" + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
		return rtn;
	}
	
	public static void main(String[] args) {
		int count = 0;
		File dir = new File("G:/juanjuantx");
		for(File a:dir.listFiles()){
			if(a.isDirectory()){
				for(File zy: a.listFiles()){
					if(zy.listFiles()!=null)
					for(File rar:zy.listFiles()){
						if(rar.isFile() && rar.getName().endsWith(".rar")){
							count++;
						}
					}
				}
			}
		}
		System.out.println(count);
	}
}
