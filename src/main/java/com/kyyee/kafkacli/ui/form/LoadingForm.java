package com.kyyee.kafkacli.ui.form;

import lombok.Getter;

import javax.swing.*;

@Getter
public class LoadingForm {
    private JPanel loadingPanel;

    private static LoadingForm loadingForm;

    public static LoadingForm getInstance() {
        if (loadingForm == null) {
            loadingForm = new LoadingForm();
        }
        return loadingForm;
    }

}
