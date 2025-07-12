package com.hotel.model;

public class Email {
    private String to;
    private String from;
    private String subject;
    private String body;
    private String date;

    public Email(String to, String from, String subject, String body, String date) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public String getTo() { return to; }
    public String getFrom() { return from; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return subject + " (" + from + ")";
    }
}
