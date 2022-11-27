package com.wzq.base;

/**
 * @author wzq
 * @create 2022-11-26 22:19
 */
public class Person {

    private int age;
    private String name;
    public long id;
    public long grade;
    protected float score;
    protected int rank;

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", grade=" + grade +
                ", score=" + score +
                ", rank=" + rank +
                '}';
    }

    public Person() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGrade() {
        return grade;
    }

    public void setGrade(long grade) {
        this.grade = grade;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Person(int age, String name, long id, long grade, float score, int rank) {
        this.age = age;
        this.name = name;
        this.id = id;
        this.grade = grade;
        this.score = score;
        this.rank = rank;
    }
}
