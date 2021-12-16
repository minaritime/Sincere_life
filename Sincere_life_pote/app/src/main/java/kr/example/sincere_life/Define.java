package kr.example.sincere_life;

import java.util.ArrayList;

import kr.example.sincere_life.RecyclerView.Plan;

public class Define {

    //날짜, 내용
    public String Date = "";        // 기본값은 현재 날짜, 달력에서 날짜 선택시 해당 날짜를 담음
    public int timenum = 0;         // 1은 시작 시간 선택시, 2는 종료 시각 선택시

    public String scontents = "";   // 파일에 저장할 때 쓰는 내용을 모아놓은 변수
    public String sstarttimes = "";   // 파일에 저장할 때 쓰는 시작시간을 모아놓은 변수
    public String sendtimes = "";   // 파일에 저장할 때 쓰는 종료시간을 모아놓은 변수
    public String sDates = "";   // 파일에 저장할 때 쓰는 날짜를 모아놓은 변수
    public String sCheck = "";  // 파일에 저장할 때 스는 체크박스 체크 상태를 모아놓은 변수
    public String sall_date = "";   // 파일에 저장할 때 값이 있는 날짜들을 모아놓은 변수

    // 위의 변수들을 split한 것을 담는 변수
    public String split_scontents[];
    public String split_sstarttimes[];
    public String split_sendtimes[];
    public String split_sDates[];
    public String split_sCheck[];
    public String split_sall_date[];

    public ArrayList<Plan> temparray = new ArrayList<>();   // 날짜마다 리스트를 달리 하기 위한 리스트

    private static Define instance;
    public static Define ins() {
        if (instance == null) {
            instance = new Define();
        }
        return instance;
    }
}
