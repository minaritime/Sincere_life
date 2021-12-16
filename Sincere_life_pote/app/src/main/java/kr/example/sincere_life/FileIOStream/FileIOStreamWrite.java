package kr.example.sincere_life.FileIOStream;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIOStreamWrite {
    AppCompatActivity aFileIOStreamWrite;
    FileIOStreamRead cFileIOStreamRead;

    public FileIOStreamWrite(AppCompatActivity fragmentActivity) {
        aFileIOStreamWrite = fragmentActivity;
        cFileIOStreamRead = new FileIOStreamRead(aFileIOStreamWrite);

    }

    public void writeData(String fileName, String writeData) {
        // 기본 경로로 설정함
        String sDataFilePath = aFileIOStreamWrite.getFilesDir().getAbsolutePath();
        File fDataFile = new File(sDataFilePath + fileName);    // 파일 이름으로 생성

        System.out.println("fileName : " + fileName);
        System.out.println("path : " + fDataFile.toString());
        System.out.println("WriteData : " + writeData);

        // 파일이 있다면
        if(fDataFile.exists()) {
            try{
                FileOutputStream fosDataFile = new FileOutputStream(fDataFile, false);
                // 파일에 내용을 덮어쓴다.
                fosDataFile.write(writeData.getBytes());
                fosDataFile.close();
                System.out.println("파일 쓰기 성공");
                cFileIOStreamRead.readData(fileName);
            }catch(IOException e) {
                e.printStackTrace();

            }
        }
    }
}