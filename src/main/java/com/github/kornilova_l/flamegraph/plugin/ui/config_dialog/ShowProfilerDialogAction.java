package com.github.kornilova_l.flamegraph.plugin.ui.config_dialog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ShowProfilerDialogAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        if (e.getProject() == null) {
            return;
        }
        new ChangeConfigurationDialog(e.getProject()).show();
    }
}
