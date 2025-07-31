package com.lms.service;

import java.util.List;

import com.lms.model.Authoring;

public interface AuthoringService {
    void addAuthoring(Authoring authoring);
    // void updateAuthoring(Authoring authoring);
    void deleteAuthoring(Authoring authoring);
    // Authoring findAuthoringById(int authoringId);
    List<Authoring> findAllAuthorings();
}

