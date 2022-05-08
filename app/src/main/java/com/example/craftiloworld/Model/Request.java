package com.example.craftiloworld.Model;

public class Request {

    private String CrewId;
    private String GroupId;

    public Request() {
    }

    public Request(String crewId, String groupId) {
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
