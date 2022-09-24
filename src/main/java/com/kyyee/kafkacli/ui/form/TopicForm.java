package com.kyyee.kafkacli.ui.form;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class TopicForm {

    private static TopicForm topicForm;

    public static TopicForm getInstance() {
        if (topicForm == null) {
            topicForm = new TopicForm();
        }
        return topicForm;
    }

    public void init() {
    }

}
