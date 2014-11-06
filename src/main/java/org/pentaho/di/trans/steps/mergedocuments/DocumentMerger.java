package org.pentaho.di.trans.steps.mergedocuments;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public abstract class DocumentMerger {
    protected OutputStream output;

    public DocumentMerger(OutputStream output) {
        this.output = output;
    }

    public abstract void add(InputStream stream) throws IOException;
    public abstract void save() throws IOException;

    public static DocumentMerger getInstance(String path) throws Exception {
        if (path.endsWith(".pdf")) {
            return new PdfDocumentMerger(new FileOutputStream(path));
        } else {
            return null;
        }
    }
}
