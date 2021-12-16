package kr.example.sincere_life.RecyclerView;

public interface ItemTouchHelperListener {
    // 사용할 메소드 정의
    boolean onItemMove(int from_position, int to_position);
    void onItemSwipe(int position);
}
