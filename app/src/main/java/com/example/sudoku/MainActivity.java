package com.example.sudoku;

import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ToggleButton;

public class MainActivity<button> extends AppCompatActivity {

    private Vibrator vibrator;
    private NumberPad numberPad;
    private TableLayout memoPad;
    private CustomButton clickedBtn;
    private CustomButton[][] buttons = new CustomButton[9][9];
    private BoardGenerator board = new BoardGenerator();
    private LayoutInflater layoutInflater;
    private TableLayout table;
    private TableRow[] tableRows = new TableRow[9];

    private ToggleButton[] memoToggleButtons = new ToggleButton[9];
    private Button memoDeleteBtn;
    private Button memoCancelBtn;
    private Button memoOkBtn;
    private CustomButton memoSelectedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.activity_main);


        table = (TableLayout) findViewById(R.id.tableLayout);
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);
        table.setBackgroundColor(Color.LTGRAY);

        // init main frame
        for (int i = 0; i < 9; i++) {
            tableRows[i] = new TableRow(this);
            table.addView(tableRows[i]); // tableRow 9개 넣기
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j] = new CustomButton(this, i, j);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);
                int left = 1, top = 1, right = 1, bottom = 1;
                if (i % 3 == 2) bottom = 10;
                else if (i % 3 == 0) top = 10;

                if (j % 3 == 0) left = 10;
                else if (j % 3 == 2) right = 10;

                layoutParams.setMargins(left, top, right, bottom);
                buttons[i][j].setLayoutParams(layoutParams);
                if ((int) (Math.random() * 10 / 6) == 0)
                    buttons[i][j].set(board.get(i, j));
                else {
                    buttons[i][j].setOnClickListener(customBtnClickListener);
                    buttons[i][j].setOnLongClickListener(activeMemoListener);
                }
                tableRows[i].addView(buttons[i][j]);
            }
        }

        // init number pad
        numberPad = new NumberPad(this, inputNumberListener);
        addContentView(
                numberPad,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                )
        );

        // init memo pad
        memoPad = (TableLayout) layoutInflater.inflate(R.layout.dialog_memo, null);
        addContentView(
                memoPad,
                new FrameLayout.LayoutParams
                        (FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER
                        )
        );
        memoPad.setVisibility(View.INVISIBLE);

        for (int i = 0; i < 3; i++) {
            TableRow row = (TableRow) memoPad.getChildAt(i + 1);
            for (int j = 0; j < 3; j++) {
                memoToggleButtons[(3 * i) + j] = (ToggleButton) row.getChildAt(j);
            }
        }

        TableRow controlRow = (TableRow) memoPad.getChildAt(4);
        memoDeleteBtn = (Button) controlRow.getChildAt(0);
        memoDeleteBtn.setOnClickListener(deleteMemoListener);
        memoCancelBtn = (Button) controlRow.getChildAt(1);
        memoCancelBtn.setOnClickListener(closeMemoListener);
        memoOkBtn = (Button) controlRow.getChildAt(2);
        memoOkBtn.setOnClickListener(setMemoListener);
    }

    private View.OnClickListener customBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clickedBtn = (CustomButton) view;
            numberPad.setVisibility(VISIBLE);
        }
    };

    private View.OnLongClickListener activeMemoListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            memoPad.setVisibility(VISIBLE);
            memoSelectedBtn = ((CustomButton) view);
            for(int i = 0; i<9; i++){
                if(memoSelectedBtn.getMemo(i).equals("  "))
                    memoToggleButtons[i].setChecked(false);
                else
                    memoToggleButtons[i].setChecked(true);
            }
            return true;
        }
    };

    private View.OnClickListener deleteMemoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            memoSelectedBtn.clearMemo();
            for(int i = 0; i<9;i++){
                memoToggleButtons[i].setChecked(false);
            }
            memoPad.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener closeMemoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            memoPad.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener setMemoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String[] memos = new String[9];
            for (int i = 0; i < 9; i++) {
                if (memoToggleButtons[i].isChecked())
                    memos[i] = memoToggleButtons[i].getTextOn().toString();
                else
                    memos[i] = "  ";
            }
            memoSelectedBtn.addMemo(memos);
            memoPad.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener inputNumberListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            String numberStr = button.getText().toString();
            int number;
            if (numberStr.equals("DEL"))
                number = 0;
            else
                number = Integer.parseInt(numberStr);
            clickedBtn.set(number);
            if (isConflict(clickedBtn.getCol(), clickedBtn.getRow())) {
                clickedBtn.setConflict();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300,255));
                }
            }
            else
                clickedBtn.unsetConflict();
            numberPad.setVisibility(View.INVISIBLE);
        }
    };


    private Boolean isConflict(int x, int y) {
        if (clickedBtn.getValue() == 0) return false;
        return (checkIsConflictHorizontal(x, y) | checkIsConflictVertical(x, y) | checkIsConflict3x3(x, y));
    }

    private boolean checkIsConflictHorizontal(int x, int y) {
        for (int i = 0; i < 9; i++) {
            if (i == y) continue;
            if (clickedBtn.getValue() == buttons[i][x].getValue()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsConflictVertical(int x, int y) {
        for (int i = 0; i < 9; i++) {
            if (i == x) continue;
            if (clickedBtn.getValue() == buttons[y][i].getValue()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsConflict3x3(int x, int y) {
        int bigX = x / 3;
        int bigY = y / 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (y == bigY * 3 + i && x == bigX * 3 + j) continue;
                if (clickedBtn.getValue() == buttons[bigY * 3 + i][bigX * 3 + j].getValue())
                    return true;
            }
        }
        return false;
    }

}