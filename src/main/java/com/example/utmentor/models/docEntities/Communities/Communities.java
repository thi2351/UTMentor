package com.example.utmentor.models.docEntities.Communities;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "communities")
public class Communities {

    @Id
    private String id;
    private String name;
    private String host;
    private int studentCount=0;
    private List<String> members;
    private boolean isActive;

    public Communities() {
    }

    public Communities(String id, String name, String host, List<String> members, boolean isActive) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.members = members;
        this.isActive = isActive;
        this.studentCount = members.size();
    }

    public void addMember(String memberId) {
        this.members.add(memberId);
        this.studentCount++;
    }

}

