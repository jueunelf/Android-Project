package com.example.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class NumberPad extends FrameLayout {
    private TableLayout table;
    private TableRow[] tableRows= new TableRow[5];
    private Button[][] keyBtns = new Button[3][3];
    private TextView title;
    private Button cancelBtn;
    private Button deleteBtn;

    public NumberPad(@NonNull Context context, OnClickListener onClickListener) {
        super(context);
        initTableLayout(context);
        initTitle(context,"Input Number");
        initKeyBtns(context, onClickListener);
        initControlBtns(context, onClickListener);
        setVisibility(INVISIBLE);
    }

    void initTableLayout(Context context){
        table = new TableLayout(context);
        table.setBackgroundColor(Color.WHITE);
        addView(table);

        for (int i = 0; i < 5; i++) {
            tableRows[i] = new TableRow(context);
            table.addView(tableRows[i]); // tableRow 5개 넣기
        }
    }

    void initTitle(Context context, String titleMessage){
        title = new TextView(context);
        title.setText(titleMessage);
        tableRows[0].addView(title);
        tableRows[0].setGravity(Gravity.CENTER);
    }

    void initKeyBtns(Context context, OnClickListener onClickListener){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                keyBtns[i][j] = new Button(context);
                keyBtns[i][j].setOnClickListener(onClickListener);
                keyBtns[i][j].setText(String.valueOf(i * 3 + j + 1));
                tableRows[i + 1].addView(keyBtns[i][j]);
            }
        }
    }

    void initControlBtns(Context context, OnClickListener onClickListener){
        cancelBtn = new Button(context);
        cancelBtn.setText("CANCEL");
        cancelBtn.setOnClickListener(cancelBtnListener());
        deleteBtn = new Button(context);
        deleteBtn.setText("DEL");
        deleteBtn.setOnClickListener(onClickListener);

        tableRows[4].setGravity(Gravity.RIGHT);
        tableRows[4].addView(cancelBtn);
        tableRows[4].addView(deleteBtn);
    }

    private OnClickListener cancelBtnListener(){
        return new OnClickListener(){
            @Override
            public void onClick(View view){
                setVisibility(INVISIBLE);
            }
        };
    }
}
