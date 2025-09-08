package me.springai.playground.qa.etl.config;

import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtlConfig {

    @Bean
    public PdfDocumentReaderConfig pdfConfig() {
        return PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageBottomMargin(0)
                .build();
    }

    @Bean
    public TokenTextSplitter pdfChunkSplitter(
            @Value("${rag.splitter.chunk-size:400}") int chunkSize,
            @Value("${rag.splitter.min-chunk-length:1}") int minLen,
            @Value("${rag.splitter.keep-separator:false}") boolean keepSep) {

        return TokenTextSplitter.builder()
                .withChunkSize(chunkSize)
                .withMinChunkLengthToEmbed(minLen)
                .withKeepSeparator(keepSep)
                .build();
    }
}
