package com.javarush.telegram;

public class UserInfo {
    public String name; //Name
    public String sex; //Sex
    public String age; //Age
    public String city; //City
    public String occupation; //Occupation
    public String hobby; //Hobby
    public String handsome; //Beauty, attractiveness
    public String wealth; //Income, wealth
    public String annoys; //What annoys me in people
    public String goals; //Goals of acquaintance

    private String fieldToString(String str, String description) {
        if (str != null && !str.isEmpty())
            return description + ": " + str + "\n";
        else
            return "";
    }

    @Override
    public String toString() {
        String result = "";

        result += fieldToString(name, "Name");
        result += fieldToString(sex, "Sex");
        result += fieldToString(age, "Age");
        result += fieldToString(city, "City");
        result += fieldToString(occupation, "Occupation");
        result += fieldToString(hobby, "Hobby");
        result += fieldToString(handsome, "Beauty, attractiveness in points (maximum 10 points)");
        result += fieldToString(wealth, "Income, wealth");
        result += fieldToString(annoys, "What annoys me in people");
        result += fieldToString(goals, "Goals of acquaintance");

        return result;
    }
}

