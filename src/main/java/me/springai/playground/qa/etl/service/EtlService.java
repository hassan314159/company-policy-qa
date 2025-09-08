package me.springai.playground.qa.etl.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class EtlService {

    private final VectorStore vectorStore;
    private final PdfDocumentReaderConfig pdfConfig;
    private final TokenTextSplitter splitter;

    public EtlService(VectorStore vectorStore, PdfDocumentReaderConfig pdfConfig, TokenTextSplitter splitter) {
        this.vectorStore = vectorStore;
        this.pdfConfig = pdfConfig;
        this.splitter = splitter;
    }

    public Map<String, Object> ingest(MultipartFile file) throws Exception {
        // Extract
        List<Document> pages = extract(file);
        // Transform
        List<Document> chunks = splitter.apply(pages);
        // load
        vectorStore.add(chunks);

        return Map.of("status","indexed","pages", pages.size(),"chunks",chunks.size());
    }

    private List<Document> extract(MultipartFile file) throws IOException {

        String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("document"+Instant.now().toString()+".pdf");
        byte[] bytes = file.getBytes();

        var res = new ByteArrayResource(bytes) { @Override public String getFilename() { return filename; } };
        var reader = new PagePdfDocumentReader(res, pdfConfig);

        return reader.get();
    }


}