package com.spencer.therapynotestracker.sessionlist;

import com.spencer.therapynotestracker.database.Session;

public interface SelectListener {
    void onExpandButtonClicked(Session session);

    void onEditAgendaButtonClicked(Session session);

    void onEditNotesButtonClicked(Session session);
}
