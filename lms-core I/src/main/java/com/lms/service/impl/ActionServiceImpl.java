package com.lms.service.impl;

import com.lms.dao.ActionDAO;

import com.lms.model.Action;
import com.lms.service.ActionService;

import java.util.List;

public class ActionServiceImpl implements ActionService {

    private final ActionDAO actionDAO;

    public ActionServiceImpl() {
        this.actionDAO = new ActionDAO();
    }

    @Override
    public void addAction(Action action) {
        actionDAO.insert(action);
    }

    @Override
    public void updateAction(Action action) {
        actionDAO.update(action);
    }

    @Override
    public void deleteAction(int actionId) {
        actionDAO.delete(actionId);
    }

    @Override
    public Action findActionById(int actionId) {
        return actionDAO.findById(actionId);
    }

    @Override
    public List<Action> findAllActions() {
        return actionDAO.findAll();
    }
}

