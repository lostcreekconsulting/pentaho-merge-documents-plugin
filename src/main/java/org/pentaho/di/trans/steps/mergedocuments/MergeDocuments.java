package org.pentaho.di.trans.steps.mergedocuments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class MergeDocuments extends BaseStep implements StepInterface {
    private static Class<?> PKG = MergeDocumentsMeta.class;
    private Object previousKey = null;
    private DocumentMerger merger;

    public MergeDocuments(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {   
        super(s, stepDataInterface, c, t, dis);
    }

    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        MergeDocumentsMeta meta = (MergeDocumentsMeta) smi;
        MergeDocumentsData data = (MergeDocumentsData) sdi;
        return super.init(meta, data);
    }

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        try {
            MergeDocumentsMeta meta = (MergeDocumentsMeta) smi;
            MergeDocumentsData data = (MergeDocumentsData) sdi;

            Object[] r = getRow();

            if (r == null) {
                if (previousKey != null) {
                    merger.save();
                }

                setOutputDone();
                return false;
            }

            if (first) {
                first = false;
                data.inputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
                data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
                meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
            }

            Object currentKey = r[data.inputRowMeta.indexOfValue(meta.getDynamicKeyField())];
            int sourceField = data.inputRowMeta.indexOfValue(meta.getDynamicSourceFileNameField());
            int targetField = data.inputRowMeta.indexOfValue(meta.getDynamicTargetFileNameField());
            String source = (String) r[sourceField];
            String target = (String) r[targetField];
            File sourceFile = new File(source);
            File targetFile = new File(target);
            File targetFolder = targetFile.getParentFile();

            if (!currentKey.equals(previousKey)) {
                if (previousKey != null) {
                    if (!targetFile.exists() || meta.isOverwriteTarget()) {
                        if (!targetFolder.exists() && meta.isCreateParentFolder()) {
                            targetFolder.mkdirs();
                        }

                        if (targetFolder.exists()) {
                            merger.save();
                            Object[] outputRow = RowDataUtil.removeItem(r, sourceField);
                            putRow(data.outputRowMeta, outputRow);
                        }
                    }

                    if (meta.isDeleteSource()) {
                        sourceFile.delete();
                    }
                }

                merger = DocumentMerger.getInstance((String) r[targetField]);
            }

            merger.add(new FileInputStream((String) r[sourceField]));
            previousKey = currentKey;

            return true;
        } catch (Exception e) {
            throw new KettleException(e);
        }
    }

    public void dipose(StepMetaInterface smi, StepDataInterface sdi) {
        MergeDocumentsMeta meta = (MergeDocumentsMeta) smi;
        MergeDocumentsData data = (MergeDocumentsData) sdi;

        super.dispose(meta, data);
    }
}
