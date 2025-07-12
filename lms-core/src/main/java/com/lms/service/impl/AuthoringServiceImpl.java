package com.lms.service.impl;

import java.util.List;

import com.lms.dao.AuthoringDAO;
import com.lms.model.Authoring;
import com.lms.service.AuthoringService;

public class AuthoringServiceImpl implements AuthoringService {

    private final AuthoringDAO authoringDAO;

    public AuthoringServiceImpl() {
        this.authoringDAO = new AuthoringDAO();
    }

    @Override
    public void addAuthoring(Authoring authoring) {
        authoringDAO.insert(authoring);
    }

    // @Override
    // public void updateAuthoring(Authoring authoring) {
    //     authoringDAO.update(authoring);
    // }

    @Override
    public void deleteAuthoring(Authoring authoring){
        authoringDAO.delete(authoring);
    }

    // @Override
    // public Authoring findAuthoringById(int authoringId) {
    //     return authoringDAO.findByAuthorId(authoringId);
    // }

    @Override
    public List<Authoring> findAllAuthorings() {
        return authoringDAO.findAll();
    }
}
