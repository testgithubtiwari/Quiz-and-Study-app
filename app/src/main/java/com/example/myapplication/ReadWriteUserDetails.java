package com.example.myapplication;

public class ReadWriteUserDetails {
    public String do_b,gen_der,contact_number;

    public  ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String dob, String textgender, String contactnumber)
    {

        this.do_b=dob;
        this.gen_der=textgender;
        this.contact_number=contactnumber;
    }

}
