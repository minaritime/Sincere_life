package kr.example.sincere_life.FileIOStream;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileIOStreamRead {
    AppCompatActivity aFileIOStreamRead;
    public FileIOStreamRead(AppCompatActivity fragmentActivity) {
        aFileIOStreamRead = fragmentActivity;

    }

    public String readData(String fileName) {
        // 기본 경로로 설정
        String sDataFilePath = aFileIOStreamRead.getFilesDir().getAbsolutePath() + fileName;

        System.out.println("read fileName : " + fileName);
        System.out.println("read setPath : " + sDataFilePath);

        File fData = new File(sDataFilePath);

        System.out.println("read path : " + sDataFilePath.toString());

        String sReadData = "";  // 파일의 모든 내용을 담는 변수
        String sReadDataTemp = "";  // 파일의 한줄 내용을 담는 변수

        try {
            FileInputStream fisData = new FileInputStream(fData);
            BufferedReader bisData = new BufferedReader(new InputStreamReader(fisData));

            // 파일의 내용이 있다면
            while ((sReadDataTemp = bisData.readLine()) != null) {
                sReadData += sReadDataTemp; // 변수에 내용을 담는다
            }

            System.out.println("sReadData : " + sReadData);
            return sReadData;

        }catch(Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}