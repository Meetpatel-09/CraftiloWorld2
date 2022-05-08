package com.example.craftiloworld.Model;

public class Crew {

    private String CrewId;
    private String GroupId;

    public Crew() {
    }

    public Crew(String crewId, String groupId) {
        CrewId = crewId;
        GroupId = groupId;
    }

    public String getCrewId() {
        return CrewId;
    }

    public void setCrewId(String crewId) {
        CrewId = crewId;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }
}
