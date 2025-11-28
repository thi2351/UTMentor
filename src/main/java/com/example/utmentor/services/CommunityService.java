package com.example.utmentor.services;

import com.example.utmentor.infrastructures.repository.Interface.CommunitiesRepository;
import com.example.utmentor.models.webModels.communities.GetCommunitiesResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    private final CommunitiesRepository communitiesRepository;

    public CommunityService(CommunitiesRepository communitiesRepository) {
        this.communitiesRepository = communitiesRepository;
    }
    public List<GetCommunitiesResponse>  getCommunityInfoOfTutor(String userId) {
        return communitiesRepository.findCommunitiesInfoByHostId(userId);
    }
}
