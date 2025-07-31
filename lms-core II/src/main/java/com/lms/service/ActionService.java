package com.lms.service;

import com.lms.model.Action;
import java.util.List;

public interface ActionService {
    void addAction(Action action);
    void updateAction(Action action);
    void deleteAction(int actionId);
    Action findActionById(int actionId);
    List<Action> findAllActions();
}