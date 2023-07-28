package ru.veselov.documentprocessing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportDocService {

    private final WebClient webClient = WebClient.create();


    public void processPassport() throws Docx4JException, IOException {
        String path =
                //"C:\\Users\\VeselovND\\git\\PTPassportProject\\document-processing\\document-processing\\src\\main\\resources\\file.docx";
                "/home/nikolay/git/PTPassportProject/document-processing/document-processing/src/main/resources/file.docx";
        Path file = Path.of(path);
        boolean exists = file.toFile().exists();
        String absolutePath = file.toString();
        log.info("exists {}, abs {}", exists, absolutePath);

        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(file))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            List<XWPFParagraph> paragraphs = extractor.getDocument().getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    if (run != null) {
                        String text = run.getText(0);
                        if (text != null && text.contains("NUMBERUP")) {
                            text = text.replace("NUMBERUP", "SerialUp");
                            run.setText(text, 0);
                        }
                    }
                }
            }
            byte[] bytes = Files.readAllBytes(file);
            String docText = extractor.getText();
            System.out.println(docText);
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", bytes).header("Content-Disposition",
                    "form-data; name=file").filename("file.docx");
            Mono<DataBuffer> dataBufferMono = webClient.post().uri("http://localhost:3000/forms/libreoffice/convert")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve().bodyToMono(DataBuffer.class);

            //convert receivedByteArrayToPdfFile
            DataBuffer block = dataBufferMono.block();
            InputStream inputStream = block.asInputStream();
            OutputStream os = new FileOutputStream("sample.pdf");
            IOUtils.copy(inputStream, os);
            os.close();

        }
    }
}
