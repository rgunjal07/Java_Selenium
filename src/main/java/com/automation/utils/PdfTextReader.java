package com.automation.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PdfTextReader {
    private PdfTextReader() {
        throw new AssertionError("Utility class");
    }

    public static String readText(Path pdfPath) throws IOException {
        if (!Files.isRegularFile(pdfPath)) {
            throw new IOException("Invoice PDF was not generated: " + pdfPath.toAbsolutePath());
        }
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            if (document.getNumberOfPages() == 0) {
                throw new IOException("Invoice PDF contains no pages: " + pdfPath.toAbsolutePath());
            }
            return new PDFTextStripper().getText(document);
        }
    }
}
