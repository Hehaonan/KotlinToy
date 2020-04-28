package com.android.hhn.kotlintoy.file;

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/28,4:01 PM ;<p/>
 * Description: ;<p/>
 * Other: ;
 */
public class TestJava {

    public static String getStr() {
        return null;
    }

    public static String getFileName() {
        return "../KotlinToy/build.gradle";
    }

    private static void readFile() {
        TestCallKt.readFile();
        /*File file = new File(getFileName());
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }*/
    }

    public static void main(String[] args) {
        readFile();
    }
}
