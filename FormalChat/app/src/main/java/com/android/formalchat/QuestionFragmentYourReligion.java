package com.android.formalchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sve on 3/12/15.
 */
public class QuestionFragmentYourReligion extends QuestionFragment {
    private TextView skip;
    private TextView answerOne;
    private TextView answerTwo;
    private TextView answerTree;
    private TextView answerFour;
    private TextView answerFive;
    private TextView answerSix;
    private TextView answerSeven;
    private TextView answerEight;

    @Override
    protected int putLayoutId() {
        return R.layout.question_five;
    }

    @Override
    protected void initButtons(View rootView) {
        skip = (TextView) rootView.findViewById(R.id.skip_txt);
        answerOne = (TextView) rootView.findViewById(R.id.answer_five_one);
        answerTwo = (TextView) rootView.findViewById(R.id.answer_five_two);
        answerTree = (TextView) rootView.findViewById(R.id.answer_five_tree);
        answerFour = (TextView) rootView.findViewById(R.id.answer_five_four);
        answerFive = (TextView) rootView.findViewById(R.id.answer_five_five);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionaryPagerAdapter.skipQuestionary();
            }
        });

        answerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "1";
                onClickAnswer(answerOne, answer);
            }
        });

        answerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "2";
                onClickAnswer(answerTwo, answer);
            }
        });

        answerTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "3";
                onClickAnswer(answerTree, answer);
            }
        });

        answerFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "4";
                onClickAnswer(answerFour, answer);
            }
        });

        answerFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "5";
                onClickAnswer(answerFive, answer);
            }
        });

        answerSix = (TextView) rootView.findViewById(R.id.answer_five_six);
        answerSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "6";
                onClickAnswer(answerSix, answer);
            }
        });

        answerSeven = (TextView) rootView.findViewById(R.id.answer_five_seven);
        answerSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "7";
                onClickAnswer(answerSeven, answer);
            }
        });

        answerEight = (TextView) rootView.findViewById(R.id.answer_five_eight);
        answerEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "8";
                onClickAnswer(answerEight, answer);
            }
        });

    }

    protected void putCorrectAnswerColor(String answerFromPrefs, int color) {
        switch(answerFromPrefs) {
            case "1":
                answerOne.setBackgroundColor(getResources().getColor(color));
                break;
            case "2":
                answerTwo.setBackgroundColor(getResources().getColor(color));
                break;
            case "3":
                answerTree.setBackgroundColor(getResources().getColor(color));
                break;
            case "4":
                answerFour.setBackgroundColor(getResources().getColor(color));
                break;
            case "5":
                answerFive.setBackgroundColor(getResources().getColor(color));
                break;
            case "6":
                answerSix.setBackgroundColor(getResources().getColor(color));
                break;
            case "7":
                answerSeven.setBackgroundColor(getResources().getColor(color));
                break;
            case "8":
                answerEight.setBackgroundColor(getResources().getColor(color));
                break;
        }
    }

    protected String getSharedPreferencesQuestionId() {
        return "yourReligion";
    }
}
