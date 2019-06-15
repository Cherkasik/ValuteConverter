package com.cherkasik.ilya.valuteconverter;

class HistoryObject {
    String conv_from;
    String conv_to;
    Float num;
    Float res;
    String date;

    HistoryObject(String conv_from, String conv_to, Float num, Float res, String date) {
        this.conv_from = conv_from;
        this.conv_to = conv_to;
        this.num = num;
        this.res = res;
        this.date = date;
    }

    HistoryObject(){
        this("", "", (float) 0, (float) 0, "");
    }
}
