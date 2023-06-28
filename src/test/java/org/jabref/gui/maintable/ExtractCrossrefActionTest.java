package org.jabref.gui.maintable;

import java.util.ArrayList;
import java.util.List;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.InternalField;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.preferences.PreferencesService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class ExtractCrossrefActionTest {

    ExtractCrossrefAction extract;
    private BibEntry entrie1;
    private BibEntry entrie2;
    private List<BibEntry> entries = new ArrayList<>();
    private final DialogService dialogService = spy(DialogService.class);
    private final PreferencesService preferences = mock(PreferencesService.class);
    private final StateManager stateManager = mock(StateManager.class);

    @BeforeEach
    public void setUp() throws Exception {
        entrie2 = new BibEntry();
        entrie1 = new BibEntry();
        entrie1.setField(InternalField.KEY_FIELD, "entry1");

        entrie1.setField(StandardField.TITLE, "Service Interaction Patterns");
        entrie1.setField(StandardField.AUTHOR, "Alistair Barros and Marlon Dumas and Arthur H. M. ter Hofstede");
        entrie1.setField(StandardField.DOI, "10.1007/11538394_20");
        entrie1.setField(StandardField.ISSN, "0302-9743");
        entrie1.setField(StandardField.PAGES, "302-318");
        entrie1.setType(StandardEntryType.InProceedings);
        entrie1.setField(StandardField.BOOKTITLE, "abc");
        entrie1.setField(StandardField.YEAR, "2005");
        entrie1.setField(StandardField.EDITOR, "editoraLegal");
        entrie1.setField(StandardField.PUBLISHER, "publicadoraLegal");

        entrie2.setField(InternalField.KEY_FIELD, "entry2");
        entrie2.setField(StandardField.TITLE, "Service Interaction Patterns");
        entrie2.setField(StandardField.AUTHOR, "Alistair Barros and Marlon Dumas and Arthur H. M. ter Hofstede");
        entrie2.setField(StandardField.DOI, "10.1007/11538394_20");
        entrie2.setField(StandardField.ISSN, "0302-9743");
        entrie2.setField(StandardField.PAGES, "302-318");
        entrie2.setType(StandardEntryType.InProceedings);
        entrie2.setField(StandardField.BOOKTITLE, "abc");
        entrie2.setField(StandardField.YEAR, "2005");
        entrie2.setField(StandardField.EDITOR, "editoraLegal");
        entrie2.setField(StandardField.PUBLISHER, "publicadoraLegal");
        entries.add(entrie1);
        entries.add(entrie2);
    }

    @Test
    void checkForNewCrossEntry() {
        extract = new ExtractCrossrefAction(dialogService, preferences, stateManager);
        extract.checkForDuplicates(entries);
        assertEquals(entries.size(), 3);
    }

    @Test
    void checkIfCommonFieldIsGone() {
        extract = new ExtractCrossrefAction(dialogService, preferences, stateManager);
        extract.checkForDuplicates(entries);
        assertEquals(entries.get(0).hasField(StandardField.BOOKTITLE), false);
    }

    @Test
    void checkIfBothEntriesHasCrossref() {
        extract = new ExtractCrossrefAction(dialogService, preferences, stateManager);
        extract.checkForDuplicates(entries);
        assertEquals(entries.get(0).getField(StandardField.CROSSREF).get(), entries.get(1).getField(StandardField.CROSSREF).get());
    }
}
