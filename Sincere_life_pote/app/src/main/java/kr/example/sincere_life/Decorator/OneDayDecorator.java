package kr.example.sincere_life.Decorator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.example.sincere_life.Define;


public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    // 해당 클래스를 생성할때 싱글톤에 오늘 날짜를 기본값으로 설정한다.
    public OneDayDecorator(){
        date = CalendarDay.today();
        if(Define.ins().Date.equals("")){
            Define.ins().Date = dateFormat.format(date.getDate());
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    // 오늘 날짜의 디자인
    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));          // 굵게
        view.addSpan(new RelativeSizeSpan(1.4f));   // 크기
        view.addSpan(new ForegroundColorSpan(Color.GREEN));  // 색상
    }
}
