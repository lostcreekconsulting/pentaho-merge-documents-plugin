package org.pentaho.di.trans.steps.mergedocuments;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class MergeDocumentsData extends BaseStepData implements StepDataInterface {
    public RowMetaInterface inputRowMeta;
    public RowMetaInterface outputRowMeta;

    public MergeDocumentsData() {
        super();
    }
}
