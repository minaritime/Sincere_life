package kr.example.sincere_life;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;

import kr.example.sincere_life.Decorator.EventDecorator;
import kr.example.sincere_life.Decorator.OneDayDecorator;
import kr.example.sincere_life.Decorator.SaturDayDecorator;
import kr.example.sincere_life.Decorator.SunDayDecorator;
import kr.example.sincere_life.FileIOStream.FileIOStreamCheckDir;
import kr.example.sincere_life.FileIOStream.FileIOStreamCheckFile;
import kr.example.sincere_life.FileIOStream.FileIOStreamRead;
import kr.example.sincere_life.RecyclerView.CalendarRecyclerAdapter;
import kr.example.sincere_life.RecyclerView.ItemTouchHelperCallback;
import kr.example.sincere_life.RecyclerView.Plan;

public class MainActivity extends AppCompatActivity implements OnDateSelectedListener {

    DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");     // 날짜의 형식을 정해줌

    MaterialCalendarView widget;
    RecyclerView recyclerView;
    ItemTouchHelper helper;
    CalendarRecyclerAdapter adapter;
    LinearLayoutManager manager;
    String temp[];  // 날짜를 split하여 년, 월, 일로 담는 변수

    FileIOStreamCheckDir cFileIOStreamCheckDir;
    FileIOStreamCheckFile cFileIOStreamCheckFile;
    FileIOStreamRead cFileIOStreamRead;

    /**
     * 달력
     * - 날짜를 선택할 수 있는 기능이 있으며 선택한 날짜의 계획을 불러올 수 있으며 해당 날짜의 계획이 있다면
     *   해당 날짜에 점을 찍는 기능이 있습니다.
     *
     * 계획 리스트
     * - 앱을 실행했을 때 오늘 날짜의 계획이 없다면 한 개의 계획을 추가하며 사용자가 직접 계획을 추가할 수 있습니다.
     *   또한 옆으로 드래그하여 계획을 제거할 수 있으며 꾹 눌러 계획의 위치를 해당 날짜의 한하여 옮길 수 있습니다.
     *
     * 상세 계획
     * - 사용자가 계획을 작성하고 사용자가 직접 시간을 선택하면 Dialog가 팝업되며 timepicker로 시간을 상세하게 선택할 수 있으며
     *   계획을 달성하면 체크하여 달성한 계획인지 사용자가 확인할 수 있습니다.
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cFileIOStreamCheckDir = new FileIOStreamCheckDir(this);
        cFileIOStreamCheckDir.checkDir();       // 기본경로에 폴더 생성

        cFileIOStreamCheckFile = new FileIOStreamCheckFile(this);

        cFileIOStreamRead = new FileIOStreamRead(this);

        widget = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.rcv_list);

        widget.addDecorators(new SunDayDecorator(), new SaturDayDecorator(), new OneDayDecorator());      // 오늘 날짜 표시, 주말 색 변경
        widget.setOnDateChangedListener(this);

        cFileIOStreamCheckFile.checkFile(Define.ins().Date, Define.ins().Date + "@");                // 날짜 담는 파일
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sContent", "내용 없음@");       // 해당 날짜 내용 담는 파일
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sStarttime", "시작 시간@");     // 해당 날짜 시작시간 담는 파일
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sEndtime", "종료 시간@");       // 해당 날짜 종료시간 담는 파일
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sCheck", "false@");           // 해당 날짜 체크박스 상태 담는 파일
        cFileIOStreamCheckFile.checkFile("all_date", Define.ins().Date+"@");                // 모든 날짜를 모아놓은 파일

        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new CalendarRecyclerAdapter(this, widget);
        recyclerView.setAdapter(adapter);

        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));     // ItemTouchHelper 생성

        helper.attachToRecyclerView(recyclerView);  // RecyclerView에 ItemTouchHelper 붙이기

        // 이곳에 파일을 불러와 저장한 값으로 싱글톤 초기화
        Define.ins().sDates = cFileIOStreamRead.readData(Define.ins().Date);
        Define.ins().scontents = cFileIOStreamRead.readData(Define.ins().Date + "sContent");
        Define.ins().sstarttimes = cFileIOStreamRead.readData(Define.ins().Date + "sStarttime");
        Define.ins().sendtimes = cFileIOStreamRead.readData(Define.ins().Date + "sEndtime");
        Define.ins().sCheck = cFileIOStreamRead.readData(Define.ins().Date + "sCheck");
        Define.ins().sall_date = cFileIOStreamRead.readData("all_date");

        // 초기화한 싱글톤들을 split하여 split싱글톤에 저장함
        Define.ins().split_sDates = Define.ins().sDates.split("@");
        Define.ins().split_scontents = Define.ins().scontents.split("@");
        Define.ins().split_sstarttimes = Define.ins().sstarttimes.split("@");
        Define.ins().split_sendtimes = Define.ins().sendtimes.split("@");
        Define.ins().split_sCheck = Define.ins().sCheck.split("@");
        Define.ins().split_sall_date = cFileIOStreamRead.readData("all_date").split("@");

        if (!Define.ins().split_sDates[0].equals("")) {     // 데이터가 있는 날짜들을 담은 싱글톤의 값이 있을 경우
            for (int i = 0; i < Define.ins().split_sDates.length; i++) {    // 데이터가 있는 날짜들을 담은 싱글톤의 길이만큼
                // adapter와 모든 데이터를 담는 싱글톤에 데이터를 추가한다.
                adapter.addItem(new Plan(Define.ins().split_sDates[i], Define.ins().split_scontents[i], Define.ins().split_sstarttimes[i], Define.ins().split_sendtimes[i], Boolean.valueOf(Define.ins().split_sCheck[i])));
                Define.ins().temparray.add(new Plan(Define.ins().split_sDates[i], Define.ins().split_scontents[i], Define.ins().split_sstarttimes[i], Define.ins().split_sendtimes[i], Boolean.valueOf(Define.ins().split_sCheck[i])));
                System.out.println("temparray : " + Define.ins().split_sDates[i]);
                System.out.println("temparray : " + Define.ins().split_scontents[i]);
                System.out.println("temparray : " + Define.ins().split_sstarttimes[i]);
                System.out.println("temparray : " + Define.ins().split_sendtimes[i]);
                System.out.println("temparray : " + Define.ins().split_sCheck[i]);
            }
        }

        // 점찍기
        for (int i = 0; i < Define.ins().split_sall_date.length; i++) {
            System.out.println("모든날짜 : " + Define.ins().split_sall_date[i]);
            temp = Define.ins().split_sall_date[i].split("\\.");    // 배열에 데이터가 저장된 날짜를 담은 싱글톤을 년, 월, 일로 split함
            if(temp[0].equals("")){     // 싱글톤의 값이 없을 경우
                temp = Define.ins().Date.split("\\.");  // 오늘 날짜를 기본값으로 split한다.
            }
            // 배열에 담긴 년, 월, 일을 이용해 해당 날짜에 점을 찍는다.
            widget.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.from(Integer.valueOf(temp[0]),
                    Integer.valueOf(temp[1]) - 1,
                    Integer.valueOf(temp[2])))));
        }

    }

    // 사용자가 날짜를 선택했을 때의 이벤트
    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        System.out.println("날짜 : " + dateFormat.format(date.getDate()));
        Define.ins().Date = selected ? dateFormat.format(date.getDate()) : "No Selection";
        System.out.println("Define.ins().Date : " + Define.ins().Date);

        adapter.setCalendarRecycler();  // 해당 날짜의 데이터를 가지고 오는 메소드

        // 해당 날짜의 데이터를 싱글톤에 저장함
        Define.ins().sDates = cFileIOStreamRead.readData(Define.ins().Date);
        Define.ins().scontents = cFileIOStreamRead.readData(Define.ins().Date + "sContent");
        Define.ins().sstarttimes = cFileIOStreamRead.readData(Define.ins().Date + "sStarttime");
        Define.ins().sendtimes = cFileIOStreamRead.readData(Define.ins().Date + "sEndtime");
        Define.ins().sCheck = cFileIOStreamRead.readData(Define.ins().Date + "sCheck");

        // 저장한 싱글톤을 split하여 split싱글톤에 저장
        Define.ins().split_sDates = Define.ins().sDates.split("@");
        Define.ins().split_scontents = Define.ins().scontents.split("@");
        Define.ins().split_sstarttimes = Define.ins().sstarttimes.split("@");
        Define.ins().split_sendtimes = Define.ins().sendtimes.split("@");
        Define.ins().split_sCheck = Define.ins().sCheck.split("@");

        int num = 1;    // 중복 확인 변수
        // 중복 추가를 방지하기 위한 코드
        for(int i = 0; i < Define.ins().temparray.size(); i++){
            // 만약 선택한 날짜가 temparray에 해당 날짜 데이터가 저장되어있다면
            if(Define.ins().temparray.get(i).getsDate().equals(Define.ins().Date)){
                num = 0;    // 중복 확인 변수를 바꿔줌
            }
        }

        if (!Define.ins().split_sDates[0].equals("")) {     // 데이터가 있는 날짜들을 담은 싱글톤의 값이 있을 경우
            if(num == 1) {  // 중복 추가인 경우가 아니라면
                for (int i = 0; i < Define.ins().split_sDates.length; i++) {    // 데이터가 있는 날짜들을 담은 싱글톤의 길이만큼
                    // adapter와 모든 데이터를 담는 싱글톤에 데이터를 추가한다.
                    adapter.addItem(new Plan(Define.ins().split_sDates[i], Define.ins().split_scontents[i], Define.ins().split_sstarttimes[i], Define.ins().split_sendtimes[i], Boolean.valueOf(Define.ins().split_sCheck[i])));
                    Define.ins().temparray.add(new Plan(Define.ins().split_sDates[i], Define.ins().split_scontents[i], Define.ins().split_sstarttimes[i], Define.ins().split_sendtimes[i], Boolean.valueOf(Define.ins().split_sCheck[i])));
                    System.out.println("temparray : " + Define.ins().split_sDates[i]);
                    System.out.println("temparray : " + Define.ins().split_scontents[i]);
                    System.out.println("temparray : " + Define.ins().split_sstarttimes[i]);
                    System.out.println("temparray : " + Define.ins().split_sendtimes[i]);
                }
            }
        }

        // 날짜 선택시 해당 날짜의 데이터를 가진 파일 생성
        cFileIOStreamCheckFile.checkFile(Define.ins().Date, "");
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sContent", "");
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sStarttime", "");
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sEndtime", "");
        cFileIOStreamCheckFile.checkFile(Define.ins().Date + "sCheck", "");
    }
}
