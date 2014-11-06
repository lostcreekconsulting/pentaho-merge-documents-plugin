package org.pentaho.di.trans.steps.mergedocuments;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfDocumentMerger extends DocumentMerger {
    private Document document;
    private PdfWriter writer;
    private PdfContentByte content;

    public PdfDocumentMerger(OutputStream output) throws DocumentException {
        super(output);
        document = new Document();
        writer = PdfWriter.getInstance(document, this.output);
        document.open();
    }

    public void add(InputStream input) throws IOException {
        PdfReader reader = new PdfReader(input);

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            document.newPage();
            PdfImportedPage page = writer.getImportedPage(reader, i);
            writer.getDirectContent().addTemplate(page, 0, 0);
        }
    }

    public void save() throws IOException {
        output.flush();
        document.close();
        output.close();
    }
}
