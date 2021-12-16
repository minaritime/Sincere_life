package kr.example.sincere_life;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import kr.example.sincere_life.FileIOStream.FileIOStreamRead;
import kr.example.sincere_life.FileIOStream.FileIOStreamWrite;
import kr.example.sincere_life.RecyclerView.CalendarRecyclerAdapter;
import kr.example.sincere_life.RecyclerView.Plan;

public class CustomDialog {

    AppCompatActivity aCustomDialog;
    CalendarRecyclerAdapter adapter;
    Button comfirm, cencel;
    TimePicker timePicker;
    FileIOStreamWrite cFileIOStreamWrite;
    FileIOStreamRead cFileIOStreamRead;

    // 날짜 선택하는 Dialog
    public CustomDialog(AppCompatActivity appCompatActivity, CalendarRecyclerAdapter adapter) {
        aCustomDialog = appCompatActivity;
        this.adapter = adapter;
        cFileIOStreamWrite = new FileIOStreamWrite(aCustomDialog);
        cFileIOStreamRead = new FileIOStreamRead(aCustomDialog);
    }

    public void callFunction(int position) {
        final Dialog dlg = new Dialog(aCustomDialog);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);  // Dialog의 타이틀을 제거

        dlg.setContentView(R.layout.custom_dialog); // Dialog의 뷰 설정

        // Id 연결
        comfirm = dlg.findViewById(R.id.btn_comfirm);
        cencel = dlg.findViewById(R.id.btn_cencel);
        timePicker = dlg.findViewById(R.id.timepicker);

        // 확인 버튼 이벤트
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemp = 0;  // 바꿀 아이템의 정확한 위치를 알기위해 추가한 변수

                // split싱글톤을 파일을 이용해 초기화
                Define.ins().split_sstarttimes = cFileIOStreamRead.readData(Define.ins().Date + "sStarttime").split("@");
                Define.ins().split_sendtimes = cFileIOStreamRead.readData(Define.ins().Date + "sEndtime").split("@");
                Define.ins().split_scontents = cFileIOStreamRead.readData(Define.ins().Date + "sContent").split("@");
                Define.ins().split_sCheck = cFileIOStreamRead.readData(Define.ins().Date + "sCheck").split("@");

                // 선택한 날짜를 temparray에서 찾는 코드
                for (int i = 0; i < Define.ins().temparray.size(); i++) {
                    // 만약 현재 날짜와 같은 경우에
                    if (Define.ins().temparray.get(i).getsDate().equals(Define.ins().Date)) {
                        // 해당 포지션을 저장하고 빠져나온다.
                        itemp = i;
                        break;
                    }
                }

                // 원하는 데이터를 잘 가지고 오는지 확인
                for (int i = position + itemp; i < Define.ins().temparray.size(); i++) {
                    System.out.println("Define.ins().temparray : " + Define.ins().temparray.get(i).getsDate() + "\n" +
                            Define.ins().temparray.get(i).getsContents() + "\n" +
                            Define.ins().temparray.get(i).getsStarttime() + "\n" +
                            Define.ins().temparray.get(i).getsEndtime());
                }

                // 만약 시작시간을 클릭해서 들어온 경우
                if (Define.ins().timenum == 1) {
                    // 시간이 10보다 작을 때 형식을 맞추기 위하여 앞에 0을 붙인다. (ex. 9 -> 09)
                    // 마찬가지로 분이 10보다 작을 때도 앞에 0을 붙인다.
                    if (timePicker.getHour() < 10) {    // 선택한 시간이 10보다 작을 때
                        if (timePicker.getMinute() < 10) {  // 선택한 분이 10보다 작을 때
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sstarttimes[position] = "0" + timePicker.getHour() + ":0" + timePicker.getMinute();
                            System.out.println("선택한 시작 시간 : " + "0" + timePicker.getHour() + ":0" + timePicker.getMinute());
                            // 만약 종료 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sendtimes[position].equals("종료 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], "종료 시간", Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 종료 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsStarttime());
                        }
                        else {
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sstarttimes[position] = "0" + timePicker.getHour() + ":" + timePicker.getMinute();
                            System.out.println("선택한 시작 시간 : " + "0" + timePicker.getHour() + ":" + timePicker.getMinute());
                            // 만약 종료 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sendtimes[position].equals("종료 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], "종료 시간", Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 종료 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsStarttime());
                        }
                    } else {
                        if (timePicker.getMinute() < 10) {  // 선택한 분이 10보다 작을 때
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sstarttimes[position] = timePicker.getHour() + ":0" + timePicker.getMinute();
                            System.out.println("선택한 시작 시간 : " + timePicker.getHour() + ":0" + timePicker.getMinute());
                            // 만약 종료 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sendtimes[position].equals("종료 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], "종료 시간", Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 종료 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsStarttime());
                        } else {
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sstarttimes[position] = timePicker.getHour() + ":" + timePicker.getMinute();
                            System.out.println("선택한 시작 시간 : " + timePicker.getHour() + ":" + timePicker.getMinute());
                            // 만약 종료 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sendtimes[position].equals("종료 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], "종료 시간", Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 종료 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsStarttime());
                        }
                    }
                }

                // 만약 종료시간을 클릭해서 들어온 경우
                else if (Define.ins().timenum == 2) {
                    if (timePicker.getHour() < 10) {    // 선택한 시간이 10보다 작을 때
                        if (timePicker.getMinute() < 10) {  // 선택한 분이 10보다 작을 때
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sendtimes[position] = "0" + timePicker.getHour() + ":0" + timePicker.getMinute();
                            System.out.println("선택한 끝나는 시간 : " + "0" + timePicker.getHour() + ":0" + timePicker.getMinute());
                            // 만약 시작 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sstarttimes[position].equals("시작 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], "시작 시간", Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 시작 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsEndtime());
                        } else {
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sendtimes[position] = "0" + timePicker.getHour() + ":" + timePicker.getMinute();
                            System.out.println("선택한 끝나는 시간 : " + "0" + timePicker.getHour() + ":" + timePicker.getMinute());
                            // 만약 시작 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sstarttimes[position].equals("시작 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], "시작 시간", Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 시작 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsEndtime());
                        }
                    } else {
                        if (timePicker.getMinute() < 10) {  // 선택한 분이 10보다 작을 때
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sendtimes[position] = timePicker.getHour() + ":0" + timePicker.getMinute();
                            System.out.println("선택한 끝나는 시간 : " + timePicker.getHour() + ":0" + timePicker.getMinute());
                            // 만약 시작 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sstarttimes[position].equals("시작 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], "시작 시간", Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 시작 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsEndtime());
                        } else {
                            // split싱글톤의 바꿀 데이터값을 바꿔준다.
                            Define.ins().split_sendtimes[position] = timePicker.getHour() + ":" + timePicker.getMinute();
                            System.out.println("선택한 끝나는 시간 : " + timePicker.getHour() + ":" + timePicker.getMinute());
                            // 만약 시작 시간을 선택하지 않은 상태라면
                            if (Define.ins().split_sstarttimes[position].equals("시작 시간")) {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], "시작 시간", Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            // 만약 시작 시간을 선택했다면
                            else {
                                Define.ins().temparray.set(position + itemp, new Plan(Define.ins().temparray.get(position + itemp).getsDate(), Define.ins().split_scontents[position], Define.ins().split_sstarttimes[position], Define.ins().split_sendtimes[position], Boolean.valueOf(Define.ins().split_sCheck[position])));
                            }
                            System.out.println("바뀐 아이템 : " + Define.ins().temparray.get(position + itemp).getsEndtime());
                        }
                    }

                }

                // 변경된 아이템 다시 추가
                adapter.finalarray.clear();
                // 선택한 날짜에 해당되는 데이터를 불러옴
                for (int a = 0; a < Define.ins().temparray.size(); a++) {
                    if (Define.ins().temparray.get(a).getsDate().equals(Define.ins().Date)) {
                        adapter.finalarray.add(Define.ins().temparray.get(a));
                    }
                }
                System.out.println("finalarray : " + adapter.finalarray);
                adapter.items.clear();  // 중복 추가 방지
                // 리스트에 데이터를 추가해준다.
                for (int i = 0; i < adapter.finalarray.size(); i++) {
                    adapter.addItem(new Plan(adapter.finalarray.get(i).getsDate(),
                            adapter.finalarray.get(i).getsContents(),
                            adapter.finalarray.get(i).getsStarttime(),
                            adapter.finalarray.get(i).getsEndtime(),
                            adapter.finalarray.get(i).getbcheck())
                    );
                }

                adapter.notifyDataSetChanged();

                // 바뀐 데이터를 넣기 위해 싱글톤을 초기화한다.
                Define.ins().sstarttimes = "";
                Define.ins().sendtimes = "";

                // 바뀐 데이터를 싱글톤에 다시 추가한다.
                for (int i = 0; i < Define.ins().split_sstarttimes.length; i++) {
                    Define.ins().sstarttimes += Define.ins().split_sstarttimes[i] + "@";
                    Define.ins().sendtimes += Define.ins().split_sendtimes[i] + "@";
                }

                // 바뀐 싱글톤을 파일에 덮어쓴다.
                cFileIOStreamWrite.writeData(Define.ins().Date + "sStarttime", Define.ins().sstarttimes);
                cFileIOStreamWrite.writeData(Define.ins().Date + "sEndtime", Define.ins().sendtimes);

                dlg.dismiss();  // Dialog 종료
            }
        });

        cencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();  // Dialog 종료
            }
        });

        dlg.show(); // Dialog 팝업
    }
}
