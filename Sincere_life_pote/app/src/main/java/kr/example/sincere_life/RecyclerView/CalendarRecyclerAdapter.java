package kr.example.sincere_life.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Collections;

import kr.example.sincere_life.CustomDialog;
import kr.example.sincere_life.Decorator.EventDecorator;
import kr.example.sincere_life.Decorator.OneDayDecorator;
import kr.example.sincere_life.Decorator.SaturDayDecorator;
import kr.example.sincere_life.Decorator.SunDayDecorator;
import kr.example.sincere_life.Define;
import kr.example.sincere_life.FileIOStream.FileIOStreamCheckFile;
import kr.example.sincere_life.FileIOStream.FileIOStreamRead;
import kr.example.sincere_life.FileIOStream.FileIOStreamWrite;
import kr.example.sincere_life.R;

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperListener{

    public ArrayList<Plan> items = new ArrayList<>();
    public ArrayList<Plan> finalarray = new ArrayList<>();

    MaterialCalendarView widget;
    AppCompatActivity aCalendarRecyclerAdapter;
    CustomDialog customDialog;
    FileIOStreamWrite cFileIOStreamWrite;
    FileIOStreamCheckFile cFileIOStreamCheckFile;
    FileIOStreamRead cFileIOStreamRead;

    // footer, item 을 확인하는 변수
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    public CalendarRecyclerAdapter(AppCompatActivity appCompatActivity, MaterialCalendarView widget) {
        this.widget = widget;
        aCalendarRecyclerAdapter = appCompatActivity;
        customDialog = new CustomDialog(aCalendarRecyclerAdapter, this);
        cFileIOStreamWrite = new FileIOStreamWrite(aCalendarRecyclerAdapter);
        cFileIOStreamCheckFile = new FileIOStreamCheckFile(aCalendarRecyclerAdapter);
        cFileIOStreamRead = new FileIOStreamRead(aCalendarRecyclerAdapter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_FOOTER) {  // viewType이 footer일경우
            // footer뷰를 설정
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footer, parent, false);
            holder = new FooterViewHolder(view);
        }
        else {
            // item뷰를 설정
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem, parent, false);
            holder = new ItemViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            // footer 이벤트 설정
            footerViewHolder.Addplan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemp = 0;      // 찾는 날짜의 데이터 갯수(리스트 길이)를 담는 변수
                    int ipos = -1;      // 찾는 날짜중 가장 낮은 인덱스를 담는 변수

                    ArrayList<Plan> filesList = new ArrayList<>();  // recyclerview 리스트에 추가하기 위한 임시 리스트
                    filesList.add(new Plan(Define.ins().Date, "내용 없음", "시작 시간", "종료 시간", false));

                    // 데이터가 추가된 날짜들을 담는 싱글톤은 사용하기 편하게 중복추가를 하지 않는다.
                    if (Define.ins().sall_date.indexOf(Define.ins().Date + "@") == -1) {    // 만약 싱글톤에 오늘날짜가 저장되어있지 않다면
                        Define.ins().sall_date += Define.ins().Date + "@";
                    }
                    // 싱글톤에 기본값들을 추가해준다.
                    Define.ins().sDates += Define.ins().Date + "@";
                    Define.ins().scontents += "내용 없음" + "@";
                    Define.ins().sstarttimes += "시작 시간" + "@";
                    Define.ins().sendtimes += "종료 시간" + "@";
                    Define.ins().sCheck += "false" + "@";

                    // 싱글톤을 파일에 덮어쓴다.
                    cFileIOStreamWrite.writeData(Define.ins().Date, Define.ins().sDates);
                    cFileIOStreamWrite.writeData(Define.ins().Date + "sContent", Define.ins().scontents);
                    cFileIOStreamWrite.writeData(Define.ins().Date + "sStarttime", Define.ins().sstarttimes);
                    cFileIOStreamWrite.writeData(Define.ins().Date + "sEndtime", Define.ins().sendtimes);
                    cFileIOStreamWrite.writeData(Define.ins().Date + "sCheck", Define.ins().sCheck);
                    cFileIOStreamWrite.writeData("all_date", Define.ins().sall_date);

                    System.out.println("싱글톤값확인 : " + Define.ins().sDates);
                    System.out.println("싱글톤값확인 : " + Define.ins().scontents);
                    System.out.println("싱글톤값확인 : " + Define.ins().sstarttimes);
                    System.out.println("싱글톤값확인 : " + Define.ins().sendtimes);
                    System.out.println("싱글톤값확인 : " + Define.ins().sCheck);
                    System.out.println("싱글톤값확인 : " + Define.ins().sall_date);

                    items.add(filesList.get(0));    // 리스트에 기본값들을 추가

                    // temparray 같은 날짜끼리 정렬하는 코드
                    if (Define.ins().temparray.size() <= 1) {   // size가 1이하라면 정렬할 필요가 없다.
                        Define.ins().temparray.add(new Plan(Define.ins().Date, "내용 없음", "시작 시간", "종료 시간", false));
                    }
                    else {
                        for (int i = 0; i < Define.ins().temparray.size(); i++) {
                            if (Define.ins().temparray.get(i).getsDate().equals(Define.ins().Date)) {   // 싱글톤의 값이 선택한 날짜와 같다면
                                itemp++;    // 선택한 날짜의 데이터 갯수를 센다
                                if (ipos == -1) { // 선택한 날짜의 데이터의 첫번째 인덱스를 저장
                                    ipos = i;
                                }
                            }
                        }
                        // 만약 리스트가 없는 날짜에 추가하면 맨 마지막에 추가한다.
                        if (ipos == -1) {
                            ipos = Define.ins().temparray.size();
                        }
                        Define.ins().temparray.add(ipos + itemp, new Plan(Define.ins().Date, "내용 없음", "시작 시간", "종료 시간", false));
                    }

                    setCalendarRecycler();  // recyclerview 리스트를 다시 추가

                    notifyDataSetChanged(); // 갱신

                    // 점찍기
                    Define.ins().split_sall_date = cFileIOStreamRead.readData("all_date").split("@");
                    for (int i = 0; i < Define.ins().split_sall_date.length; i++) {
                        footerViewHolder.temp = Define.ins().split_sall_date[i].split("\\.");    // 배열에 데이터가 저장된 날짜를 담은 싱글톤을 년, 월, 일로 split함
                        // 배열에 담긴 년, 월, 일을 이용해 해당 날짜에 점을 찍는다.
                        widget.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.from(Integer.valueOf(footerViewHolder.temp[0]),
                                Integer.valueOf(footerViewHolder.temp[1]) - 1,
                                Integer.valueOf(footerViewHolder.temp[2])))));
                    }
                }
            });
        }
        else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            // 아이템 이벤트 설정
            itemViewHolder.Contents.setText(items.get(position).getsContents());
            itemViewHolder.Tv_Starttime.setText(items.get(position).getsStarttime());
            itemViewHolder.Tv_Endtime.setText(items.get(position).getsEndtime());
            itemViewHolder.Checkbox.setChecked(items.get(position).getbcheck());

            // Edittext 이벤트
            itemViewHolder.Contents.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    InputMethodManager imm = (InputMethodManager) aCalendarRecyclerAdapter.getSystemService(INPUT_METHOD_SERVICE);

                    System.out.println("내용 파일 : " + cFileIOStreamRead.readData(Define.ins().Date + "sContent"));
                    System.out.println("싱클톤 내용 : " + Define.ins().scontents);

                    // split싱글톤에 split하여 저장
                    Define.ins().split_sDates = Define.ins().sDates.split("@");
                    Define.ins().split_scontents = Define.ins().scontents.split("@");
                    Define.ins().split_sstarttimes = Define.ins().sstarttimes.split("@");
                    Define.ins().split_sendtimes = Define.ins().sendtimes.split("@");
                    Define.ins().split_sCheck = Define.ins().sCheck.split("@");

                    System.out.println("스플릿사이즈 : " + Define.ins().split_scontents.length);

                    // 텍스트 내용을 가져온다.
                    String searchData = v.getText().toString();
                    System.out.println("바뀔 값 : " + v.getText().toString());

                    // 만약 비어있을경우 에러가 나서 앱이 종료되므로 공백으로 대체한다.
                    if (searchData.isEmpty()) {
                        Define.ins().split_scontents[holder.getAdapterPosition()] = " ";
                    }
                    else {  // 그렇지 않을경우 바뀐내용을 저장한다.
                        Define.ins().split_scontents[holder.getAdapterPosition()] = searchData;
                    }

                    switch (actionId) {
                        case EditorInfo.IME_ACTION_SEARCH:
                            break;
                        default:    // 확인 버튼을 눌렀을 경우
                            // 키보드를 내린다.
                            imm.hideSoftInputFromWindow(itemViewHolder.Contents.getWindowToken(), 0);

                            // 바뀐 내용을 싱글톤에 담기 위해 초기화한다.
                            Define.ins().scontents = "";
                            Define.ins().temparray.clear();
                            // 바뀐 내용을 split싱글톤을 이용하여 싱글톤에 저장한다.
                            for (int i = 0; i < Define.ins().split_scontents.length; i++) {
                                Define.ins().scontents += Define.ins().split_scontents[i] + "@";
                                System.out.println("Define.ins().scontents : " + Define.ins().scontents);
                                Define.ins().temparray.add(new Plan(Define.ins().split_sDates[i],
                                        Define.ins().split_scontents[i],
                                        Define.ins().split_sstarttimes[i],
                                        Define.ins().split_sendtimes[i],
                                        Boolean.valueOf(Define.ins().split_sCheck[i])));
                                System.out.println("Define.ins().temparray : " + Define.ins().temparray.get(i).getsContents());
                            }
                            // 바뀐 내용을 파일에 덮어쓴다.
                            cFileIOStreamWrite.writeData(Define.ins().Date + "sContent", Define.ins().scontents);
                            v.clearFocus(); // focus를 제거한다.
                            return false;
                    }
                    return true;
                }
            });

            // 시작 시간 텍스트 클릭 이벤트
            itemViewHolder.Btn_Starttime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();  // 현재 View의 포지션값을 가져온다.
                    Define.ins().timenum = 1;       // 시작 시간을 선택한 상태를 싱글톤을 이용하여 나타낸다.
                    customDialog.callFunction(pos); // dialog를 호출한다.
                }
            });

            // 종료 시간 텍스트 클릭 이벤트
            itemViewHolder.Btn_Endtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();  // 현재 View의 포지션값을 가져온다.
                    Define.ins().timenum = 2;       // 종료 시간을 선택한 상태를 싱글톤을 이용하여 나타낸다.
                    customDialog.callFunction(pos); // dialog를 호출한다.
                }
            });

            // 체크박스 이벤트
            itemViewHolder.Checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();  // 현재 View의 포지션값을 가져온다.
                    // split싱글톤에 split하여 담는다.
                    Define.ins().split_sDates = Define.ins().sDates.split("@");
                    Define.ins().split_scontents = Define.ins().scontents.split("@");
                    Define.ins().split_sstarttimes = Define.ins().sstarttimes.split("@");
                    Define.ins().split_sendtimes = Define.ins().sendtimes.split("@");

                    // 체크박스가 체크 되어있을경우
                    if (itemViewHolder.Checkbox.isChecked()) {
                        // 체크상태를 split한 데이터를 담는 임시 배열
                        String temparray[] = cFileIOStreamRead.readData(Define.ins().Date + "sCheck").split("@");
                        // 바뀐 값을 추가하기 위해 값을 비운다.
                        Define.ins().temparray.clear();
                        Define.ins().sCheck = "";
                        temparray[pos] = "true";    // 체크박스를 체크한 포지션을 이용하여 배열의 값을 변경한다.
                        // 바뀐 값을 추가한다.
                        for(int i = 0; i < temparray.length; i++){
                            Define.ins().sCheck += temparray[i] + "@";
                            Define.ins().temparray.add(new Plan(Define.ins().split_sDates[i],
                                    Define.ins().split_scontents[i],
                                    Define.ins().split_sstarttimes[i],
                                    Define.ins().split_sendtimes[i],
                                    Boolean.valueOf(temparray[i])));
                        }
                        // 바뀐 값을 파일에 덮어쓴다.
                        cFileIOStreamWrite.writeData(Define.ins().Date + "sCheck", Define.ins().sCheck);
                        System.out.println("체크된위치 : " + pos);
                        System.out.println("체크됨!");
                    }
                    else {
                        // 체크상태를 split한 데이터를 담는 임시 배열
                        String temparray[] = cFileIOStreamRead.readData(Define.ins().Date + "sCheck").split("@");
                        // 바뀐 값을 추가하기 위해 값을 비운다.
                        Define.ins().temparray.clear();
                        Define.ins().sCheck = "";
                        temparray[pos] = "false";    // 체크박스를 체크한 포지션을 이용하여 배열의 값을 변경한다.
                        // 바뀐 값을 추가한다.
                        for(int i = 0; i < temparray.length; i++){
                            Define.ins().sCheck += temparray[i] + "@";
                            Define.ins().temparray.add(new Plan(Define.ins().split_sDates[i],
                                    Define.ins().split_scontents[i],
                                    Define.ins().split_sstarttimes[i],
                                    Define.ins().split_sendtimes[i],
                                    Boolean.valueOf(temparray[i])));
                        }
                        // 바뀐 값을 파일에 덮어쓴다.
                        cFileIOStreamWrite.writeData(Define.ins().Date + "sCheck", Define.ins().sCheck);
                        System.out.println("체크취소...");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // 아이템의 처음과 마지막은 각각 헤더와 푸터를 의미함
        if (position == items.size())
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    // 리사이클러뷰 아이템 이동했을 경우
    @Override
    public boolean onItemMove(int from_position, int to_position) {
        int temppos = 0;    // 사용자가 선택한 날짜부터 찾기 위한 포지션

        // 사용자가 선택한 날짜의 포지션을 구하기 위함 (ex. 28 29 29 30 이라고 가정할때 29일의 1번째 인덱스 값을 삭제하려 할때 temparray에서는 포지션(1) + 29일 값이 시작되는 포지션(1)을 구해야 한다.)
        for(int i = 0; i < Define.ins().temparray.size(); i++){
            if(Define.ins().temparray.get(i).getsDate().equals(Define.ins().Date)){ // 만약 선택한 날짜와 temparray의 날짜가 같다면
                temppos = i;    // 해당 포지션을 변수에 저장한다.
                System.out.println("날짜포지션 : " + temppos);
                System.out.println("옮길포지션 : " + from_position);
                break;  // 그리고 반복문을 빠져나온다.
            }
        }

        Plan plan = items.get(from_position);   // 이동할 객체 저장

        // 이동할 객체 삭제
        items.remove(from_position);
        Define.ins().temparray.remove(temppos + from_position);

        // 이동하고 싶은 position에 추가
        if (to_position > items.size()) {   // 만약 푸터 아래에 옮기려고 했을경우 -> 푸터 - 1위치에 옮긴다.
            items.add(to_position - 1, plan);
            Define.ins().temparray.add(temppos + to_position - 1, plan);
        }
        else {
            items.add(to_position, plan);
            Define.ins().temparray.add(temppos + to_position, plan);
        }

        // 아래는 변경된 데이터를 파일에 갱신하기 위한 작업
        Define.ins().sDates = "";
        Define.ins().scontents = "";
        Define.ins().sstarttimes = "";
        Define.ins().sendtimes = "";
        Define.ins().sCheck = "";

        for(int i = 0; i < items.size(); i++){
            Define.ins().sDates += items.get(i).getsDate() + "@";
            Define.ins().scontents += items.get(i).getsContents() + "@";
            Define.ins().sstarttimes += items.get(i).getsStarttime() + "@";
            Define.ins().sendtimes += items.get(i).getsEndtime() + "@";
            Define.ins().sCheck += items.get(i).getbcheck() + "@";
        }

        cFileIOStreamWrite.writeData(Define.ins().Date, Define.ins().sDates);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sContent", Define.ins().scontents);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sStarttime", Define.ins().sstarttimes);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sEndtime", Define.ins().sendtimes);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sCheck", Define.ins().sCheck);

        notifyItemMoved(from_position, to_position);    // Adapter에 데이터 이동알림
        return true;
    }

    // 리사이클러뷰 아이템 옆으로 swipe했을 경우
    @Override
    public void onItemSwipe(int position) {
        int temppos = 0;    // 사용자가 선택한 날짜부터 찾기 위한 포지션
        // 사용자가 선택한 날짜의 포지션을 구하기 위함
        for(int i = 0; i < Define.ins().temparray.size(); i++){
            if(Define.ins().temparray.get(i).getsDate().equals(Define.ins().Date)){
                temppos = i;
                break;
            }
        }
        if (items.size() != position) {
            items.remove(position);
            Define.ins().temparray.remove(temppos + position);
        }
        // 아래는 변경된 데이터를 파일에 갱신하기 위한 작업
        Define.ins().sDates = "";
        Define.ins().scontents = "";
        Define.ins().sstarttimes = "";
        Define.ins().sendtimes = "";
        Define.ins().sCheck = "";

        for(int i = 0; i < items.size(); i++){
            Define.ins().sDates += items.get(i).getsDate() + "@";
            Define.ins().scontents += items.get(i).getsContents() + "@";
            Define.ins().sstarttimes += items.get(i).getsStarttime() + "@";
            Define.ins().sendtimes += items.get(i).getsEndtime() + "@";
            Define.ins().sCheck += items.get(i).getbcheck() + "@";
        }

        cFileIOStreamWrite.writeData(Define.ins().Date, Define.ins().sDates);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sContent", Define.ins().scontents);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sStarttime", Define.ins().sstarttimes);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sEndtime", Define.ins().sendtimes);
        cFileIOStreamWrite.writeData(Define.ins().Date + "sCheck", Define.ins().sCheck);

        // footer만 남았을 경우 -> 데이터가 없으므로 데이터가 포함된 날짜에 저장되있는 현재 날짜를 삭제한다.
        System.out.println("이프문들어갈때 item.size() : " + items.size());
        if(items.size() == 0){
            Define.ins().sall_date = cFileIOStreamRead.readData("all_date");
            System.out.println("Define.ins().sall_date 전 : " + Define.ins().sall_date);
            Define.ins().sall_date = Define.ins().sall_date.replaceFirst(Define.ins().Date + "@", "");
            System.out.println("Define.ins().sall_date 후 : " + Define.ins().sall_date);
            cFileIOStreamWrite.writeData("all_date", Define.ins().sall_date);
        }

        // 점찍기
        String temp[];  // 년, 월, 일을 담는 임시 배열
        widget.removeDecorators();  // 점을 다시 찍기 위해 decorator를 전부 제거한다.
        widget.addDecorators(new OneDayDecorator(), new SaturDayDecorator(), new SunDayDecorator());    // 지울 필요가 없는 decorator를 다시 추가한다.
        Define.ins().split_sall_date = cFileIOStreamRead.readData("all_date").split("@");
        try {   // 만약 모든 값이 없을 경우 에러가 발생하여 앱이 강제종료된다. 그러므로 try를 걸어 종료현상을 방지한다.
            for (int i = 0; i < Define.ins().split_sall_date.length; i++) {
                temp = Define.ins().split_sall_date[i].split("\\.");    // 배열에 데이터가 저장된 날짜를 담은 싱글톤을 년, 월, 일로 split함
                // 배열에 담긴 년, 월, 일을 이용해 해당 날짜에 점을 찍는다.
                widget.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.from(Integer.valueOf(temp[0]),
                        Integer.valueOf(temp[1]) - 1,
                        Integer.valueOf(temp[2])))));
            }
        }catch (Exception e){
            e.printStackTrace();    // 로그에 에러를 출력한다.
        }

        System.out.println("삭제된 후 : " + cFileIOStreamRead.readData(Define.ins().Date));
        System.out.println("삭제된 후 : " + cFileIOStreamRead.readData(Define.ins().Date + "sContent"));
        System.out.println("삭제된 후 : " + cFileIOStreamRead.readData(Define.ins().Date + "sStarttime"));
        System.out.println("삭제된 후 : " + cFileIOStreamRead.readData(Define.ins().Date + "sEndtime"));
        System.out.println("삭제된 후 : " + cFileIOStreamRead.readData(Define.ins().Date + "sCheck"));
        System.out.println("삭제된 후 : " + cFileIOStreamRead.readData("all_date"));

        notifyItemRemoved(position);    // Adapter에 데이터 이동알림
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        Button Addplan;
        String[] temp;

        // footer 뷰홀더
        FooterViewHolder(View footerView) {
            super(footerView);
            Addplan = footerView.findViewById(R.id.add);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        Button Btn_Starttime, Btn_Endtime;
        TextView Tv_Starttime, Tv_Endtime;
        EditText Contents;
        CheckBox Checkbox;

        // item 뷰홀더
        public ItemViewHolder(View itemViewHolder) {
            super(itemViewHolder);
            Btn_Starttime = itemViewHolder.findViewById(R.id.btn_starttime);
            Btn_Endtime = itemViewHolder.findViewById(R.id.btn_endtime);
            Tv_Starttime = itemViewHolder.findViewById(R.id.tv_starttime);
            Tv_Endtime = itemViewHolder.findViewById(R.id.tv_endtime);
            Contents = itemViewHolder.findViewById(R.id.et1);
            Checkbox = itemViewHolder.findViewById(R.id.cb_comfirm);
        }
    }

    public void addItem(Plan plan) {
        items.add(plan);
    }

    public void setCalendarRecycler() {
        finalarray.clear(); // 중복 추가 방지
        // 선택한 날짜에 해당되는 데이터를 불러옴
        for (int a = 0; a < Define.ins().temparray.size(); a++) {
            // 선택한 날짜와 temparray의 날짜가 같다면
            if (Define.ins().temparray.get(a).getsDate().equals(Define.ins().Date)) {
                finalarray.add(Define.ins().temparray.get(a));  // 해당 값들을 finalarray에 넣는다.
            }
        }
        items.clear();  // 중복 추가 방지
        for (int i = 0; i < finalarray.size(); i++) {
            System.out.println("finalarray : " + finalarray.get(i).getsDate() + "\n" +
                    finalarray.get(i).getsContents() + "\n" +
                    finalarray.get(i).getsStarttime() + "\n" +
                    finalarray.get(i).getsEndtime());
            // finalarray에 넣은 값을 토대로 리스트를 추가한다.
            addItem(new Plan(finalarray.get(i).getsDate(), finalarray.get(i).getsContents(), finalarray.get(i).getsStarttime(), finalarray.get(i).getsEndtime(), finalarray.get(i).getbcheck()));
        }
        notifyDataSetChanged();     // 갱신
    }

}
