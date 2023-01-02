package com.opensource.ssu_ppy;

import java.util.List;

public class MeetingItem {
    Boolean host_sex;
    String people_count;
    String hobby;
    String major;
    String mbti;
    Boolean matched;

    public Boolean getHostSex() { return host_sex;}

    public void setHostSex(Boolean host_sex){this.host_sex=host_sex;}

    public String getPeopleCount() {return people_count;}

    public void setPeopleCount(String people_count){this.people_count=people_count;}

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hoby) {
        this.hobby = hoby;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMbti() { return mbti; }

    public void setMbti(String mbti) { this.mbti = mbti; }

    public Boolean getmatched() { return matched; }

    public void setMatched(Boolean matched) { this.matched = matched; }
}
