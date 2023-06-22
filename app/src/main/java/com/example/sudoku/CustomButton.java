package com.example.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CustomButton extends FrameLayout {
    private int row;
    private int col;
    private int value;
    private TextView textView;
    private TableLayout memo;
    private TextView[] memoTexts = new TextView[9];

    public CustomButton(Context context, int row, int col) {
        // init data
        super(context);
        this.row = row;
        this.col = col;

        // init Frame
        setMinimumHeight(getResources().getDisplayMetrics().heightPixels/10);
        setBackgroundResource(R.drawable.button_selector);
        setClickable(true);

        // init textView
        textView = new TextView(context);
        textView.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        addView(textView);

        // init memo
        LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        memo = (TableLayout) layoutInflater.inflate(R.layout.layout_memo, null);
        memo.setLayoutParams(
                new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        addView(memo);
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                TableRow tableRow = (TableRow) memo.getChildAt(i);
                memoTexts[(3*i)+j] = (TextView) tableRow.getChildAt(j);
            }
        for(TextView memoText: memoTexts){
            memoText.setText("  ");
        }
        memo.setVisibility(INVISIBLE);
    }

    public void set(int newValue) {
        memo.setVisibility(INVISIBLE);
        value = newValue;
        if (value == 0)
            textView.setText("");
        else
            textView.setText(String.valueOf(value));
        textView.setVisibility(VISIBLE);
    }

    public void addMemo(String[] memos){
        textView.setVisibility(INVISIBLE);
        value = 0;
        unsetConflict();
        for(int i=0;i<9;i++){
            memoTexts[i].setText(memos[i]);
        }
        memo.setVisibility(VISIBLE);
    }

    public String getMemo(int index){
        return memoTexts[index].getText().toString();
    }

    public void clearMemo(){
        for(TextView memoText: memoTexts){
            memoText.setText("  ");
        }
    }

    public int getValue(){
        return value;
    }

    public int getCol(){
        return col;
    }

    public int getRow() {
        return row;
    }

    // Conflict
    public void setConflict() {
        setBackgroundColor(Color.RED);
    }

    // No conflict
    public void unsetConflict() {
        setBackgroundResource(R.drawable.button_selector);
    }

}
