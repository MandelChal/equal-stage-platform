package com.equal_stage_platform.demo;

import java.util.Objects;

public class Gangster {
    private String name;
    private int age;
    private String crime;
    private String location;
    private String gangName;
    public Gangster(String name, int age, String crime, String location, String gangName) {
        this.name = name;
        this.age = age;
        this.crime = crime;
        this.location = location;
        this.gangName = gangName;
    }
    //getters
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public String getCrime() {
        return crime;
    }
    public String getLocation() {
        return location;
    }
    public String getGangName() {
        return gangName;
    }
    //setters
    public void setName(String name) {
        this.name = name;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setCrime(String crime) {
        this.crime = crime;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setGangName(String gangName) {
        this.gangName = gangName;
    }
    @Override
    public String toString() {
        return "Gangster{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", crime='" + crime + '\'' +
                ", location='" + location + '\'' +
                ", gangName='" + gangName + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gangster)) return false;
        Gangster gangster = (Gangster) o;
        return age == gangster.age && name.equals(gangster.name) && crime.equals(gangster.crime) && location.equals(gangster.location) && gangName.equals(gangster.gangName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, age, crime, location, gangName);
    }
}
