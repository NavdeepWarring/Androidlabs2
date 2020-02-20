package com.example.androidlabs;


public class Message {
    public String Message;
    public boolean TypeOfMessage;
    public long id;


    Message(String message, boolean typeOfMessage , long id){
        this.Message= message;
        this.TypeOfMessage = typeOfMessage;
        this.id = id;
    }


    public String getMessage(){
        return Message;
    }

    public boolean getTypeOfMessage(){
        return TypeOfMessage;
    }

    public long getId(){ return id;}
}
