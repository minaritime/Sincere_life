package kr.example.sincere_life.FileIOStream;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIOStreamCheckFile {
    AppCompatActivity aFileIOStreamCheckFile;

    public FileIOStreamCheckFile(AppCompatActivity fragmentActivity) {
        aFileIOStreamCheckFile = fragmentActivity;
    }

    public void checkFile(String fileName, String initData) {
        // 기본 경로로 설정
        String sFilePath = aFileIOStreamCheckFile.getFilesDir().getAbsolutePath();
        File fFile = new File(sFilePath + fileName);    // 파일 이름으로 생성
        System.out.println("path : " + fFile.toString());

        // 지정된 파일이름으로 된 파일이 없을경우
        if(!fFile.exists()) {
            try{
                // 파일 생성한뒤 지정한 내용을 파일안에 넣는다
                FileOutputStream fosDataFile = new FileOutputStream(fFile, false);
                fosDataFile.write(initData.getBytes());
                fosDataFile.close();
                System.out.println("텍스트파일 생성완료");

            }catch(IOException e) {
                e.printStackTrace();

            }
        }

        // 지정한 파일이 있을경우 있다고만 한다.
        if(fFile.exists()){
            System.out.println("텍스트파일 있음");
        }else {
            System.out.println("텍스트파일 없음");
        }

        // 테스트용
//        fFile.delete();

    }

}