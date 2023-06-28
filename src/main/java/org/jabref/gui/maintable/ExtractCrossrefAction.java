package org.jabref.gui.maintable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.actions.SimpleCommand;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.InternalField;
import org.jabref.model.entry.field.StandardField;
import org.jabref.preferences.PreferencesService;

public class ExtractCrossrefAction extends SimpleCommand {

    private final DialogService dialogService;
    private final PreferencesService preferencesService;
    private final StateManager stateManager;
    private BibDatabase activeDatabase;

    public ExtractCrossrefAction(DialogService dialogService, PreferencesService preferencesService, StateManager stateManager) {
        this.dialogService = dialogService;
        this.preferencesService = preferencesService;
        this.stateManager = stateManager;
        this.activeDatabase = stateManager.getActiveDatabase().get().getDatabase();
    }

    @Override
    public void execute() {
        List<BibEntry> entries = stateManager.getSelectedEntries();
        checkForDuplicates(entries);
    }

    public void checkForDuplicates(List<BibEntry> entries) {
        List<BibEntry> createdRefs = new ArrayList<BibEntry>();
            for (int i = 0; i < entries.size(); i++) {
                List<BibEntry> entriesToRemoveFields = new ArrayList<BibEntry>();
                entriesToRemoveFields.add(entries.get(i));
                for (int j = 0; j < entries.size(); j++) {
                    if (!entriesToRemoveFields.contains(entries.get(j))) {
                        if (hasAllFields(entries.get(i)) && hasAllFields(entries.get(j))) {
                            if (checkEntriesForSameFields(entries.get(i), entries.get(j))) {
                                entriesToRemoveFields.add(entries.get(j));
                            }
                        }
                    }
                }
                if (entriesToRemoveFields.size() > 1) {
                    createdRefs.add(extractCommonFields(entriesToRemoveFields));
                    }
                }
        if (!createdRefs.isEmpty()) {
            entries.addAll(createdRefs);
            activeDatabase.insertEntries(createdRefs);
        }
    }

    private boolean checkEntriesForSameFields(BibEntry entry1, BibEntry entry2) {
        if (entry1.getField(InternalField.KEY_FIELD).get() != entry2.getField(InternalField.KEY_FIELD).get()) {
            return entry1.getField(StandardField.BOOKTITLE).get() == entry2.getField(StandardField.BOOKTITLE).get() &&
                    entry1.getField(StandardField.PUBLISHER).get() == entry2.getField(StandardField.PUBLISHER).get() &&
                    entry1.getField(StandardField.EDITOR).get() == entry2.getField(StandardField.EDITOR).get() &&
                    entry1.getField(StandardField.YEAR).get() == entry2.getField(StandardField.YEAR).get();
        } else {
            return false;
        }
    }

    private BibEntry extractCommonFields(List<BibEntry> entries) {
        String bookTile = entries.get(0).getField(StandardField.BOOKTITLE).get();
        String publisher = entries.get(0).getField(StandardField.PUBLISHER).get();
        String editor = entries.get(0).getField(StandardField.EDITOR).get();
        String year = entries.get(0).getField(StandardField.YEAR).get();

        BibEntry crossEntry = new BibEntry();
        String refID = UUID.randomUUID().toString();
        crossEntry.setField(InternalField.KEY_FIELD, refID);
        crossEntry.setField(StandardField.BOOKTITLE, bookTile);
        crossEntry.setField(StandardField.PUBLISHER, publisher);
        crossEntry.setField(StandardField.EDITOR, editor);
        crossEntry.setField(StandardField.YEAR, year);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).clearField(StandardField.BOOKTITLE);
            entries.get(i).clearField(StandardField.PUBLISHER);
            entries.get(i).clearField(StandardField.EDITOR);
            entries.get(i).clearField(StandardField.YEAR);

            entries.get(i).setField(StandardField.CROSSREF, refID);
        }
        return crossEntry;
    }

    private boolean hasAllFields(BibEntry entry) {
        return entry.hasField(StandardField.BOOKTITLE) &&
                entry.hasField(StandardField.PUBLISHER) &&
                entry.hasField(StandardField.EDITOR) &&
                entry.hasField(StandardField.YEAR);
    }
}
