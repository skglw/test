package com.example.skglw;

import java.util.ArrayList;

public class User {

    public static String token;
    public static String username;
    public static String password,login;
    public String name;

    public static ArrayList<Pay> payList= new ArrayList<Pay>();
    public static ArrayList<Check> checkList= new ArrayList<Check>();

    public static class Check {
        public int image=R.drawable.ic_horse;
        public String number,card, balance;
        public Boolean blocked;

        public Check( String number,String card, String balance) {
            this.number = number;
            this.card =card;
            this.balance =balance;
        }
        public Check(){}
        public int getImage()
        {
            return image;
        }
        public String getNumber()
        {
            return number;
        }
    }

    public static class Pay {

        private String name, check;
        public Pay( String name, String check) {
            this.name = name;
            this.check =check;
        }
        public String getCheck() {
            return check;
        }
        public void setCheck(String check) {
            this.check = check;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }


 /*   public static class Pay {
        public int image = R.drawable.ic_horse;
        public String number, card, balance;

        public Pay(String number, String card, String balance) {
            this.number = number;
            this.card = card;
            this.balance = balance;
        }

        public Pay() {
        }
    }*/
}
