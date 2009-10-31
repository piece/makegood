package com.piece_framework.makegood.aspect.include_path_settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElementSorter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.php.internal.ui.PHPUIMessages;
import org.eclipse.php.internal.ui.preferences.includepath.PHPIPListLabelProvider;
import org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathSourcePage;

import com.piece_framework.makegood.include_path.ConfigurationIncludePath;

public class PHPIncludePathSourcePageForConfiguration extends PHPIncludePathSourcePage {
    protected int IDX_ADD_CONFIG = 1;

    public PHPIncludePathSourcePageForConfiguration(ListDialogField buildpathList) {
        super(buildpathList);

        IDX_REMOVE = 2;
        IDX_ADD_LINK = 3;
        IDX_EDIT = 4;
    }

    @Override
    protected void initContainerElements() {
        SourceContainerAdapter adapter = new SourceContainerAdapter() {
            public boolean hasChildren(TreeListDialogField field, Object element) {
                return false;
            }
        };

        String[] buttonLabels = new String[] {
                NewWizardMessages.SourceContainerWorkbookPage_folders_add_button,
                "Add the System Include Path",
                NewWizardMessages.SourceContainerWorkbookPage_folders_remove_button
                };

        fFoldersList = new TreeListDialogField(adapter,
                                               buttonLabels,
                                               new PHPIPListLabelProvider()
                                               );
        fFoldersList.setDialogFieldListener(adapter);
        fFoldersList.setLabelText(PHPUIMessages.getString("IncludePathSourcePage_Folders_Label")); //$NON-NLS-1$
        fFoldersList.setViewerSorter(new BPListElementSorter());
    }

    @Override
    protected void refresh(List insertedElements,
                           List removedElements,
                           List modifiedElements
                           ) {
        if (configurationInsert(insertedElements,
                                removedElements,
                                modifiedElements
                                )) {
            fFoldersList.addElements(insertedElements);
            return;
        }

        super.refresh(insertedElements, removedElements, modifiedElements);
    }

    @Override
    protected void sourcePageCustomButtonPressed(DialogField field, int index) {
        if (field == fFoldersList && index == IDX_ADD_CONFIG) {
            ConfigurationIncludePath configuration = new ConfigurationIncludePath(fCurrJProject.getProject());
            List<BPListElement> insertedElemetns = new ArrayList<BPListElement>();
            insertedElemetns.add(new BPListElement(fCurrJProject,
                                                   IBuildpathEntry.BPE_SOURCE,
                                                   configuration.getDummyResource().getFullPath(),
                                                   configuration.getDummyResource(),
                                                   false
                                                   ));
            refresh(insertedElemetns, new ArrayList(), new ArrayList());
            return;
        }

        super.sourcePageCustomButtonPressed(field, index);
    }

    @Override
    protected void sourcePageSelectionChanged(DialogField field) {
        fFoldersList.enableButton(IDX_ADD_CONFIG, !includeConfiguration());

        super.sourcePageSelectionChanged(field);
    }

    private boolean configurationInsert(List insertedElements,
                                        List removedElements,
                                        List modifiedElements
                                        ) {
        boolean insertOnly = insertedElements.size() == 1
                             && removedElements.size() == 0
                             && modifiedElements.size() == 0;
        if (!insertOnly) {
            return false;
        }

        BPListElement element = (BPListElement) insertedElements.get(0);
        ConfigurationIncludePath configuration = new ConfigurationIncludePath(element.getResource().getProject());
        if (!configuration.equalsDummyResource(element.getResource())) {
            return false;
        }

        return true;
    }

    private boolean includeConfiguration() {
        for (Iterator iterator = fFoldersList.getElements().iterator(); iterator.hasNext();) {
            BPListElement element = (BPListElement) iterator.next();
            ConfigurationIncludePath configuration = new ConfigurationIncludePath(element.getResource().getProject());
            if (configuration.equalsDummyResource(element.getResource())) {
                return true;
            }
        }
        return false;
    }
}
