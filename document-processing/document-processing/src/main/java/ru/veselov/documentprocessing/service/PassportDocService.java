package ru.veselov.documentprocessing.service;

import com.lowagie.text.pdf.PdfWriter;
import fr.opensagres.poi.xwpf.converter.core.IXWPFConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PassportDocService {


    public void processPassport() throws Docx4JException, IOException {
        String path =
                "C:\\Users\\VeselovND\\git\\PTPassportProject\\document-processing\\document-processing\\src\\main\\resources\\file.docx";
        Path file = Path.of(path);
        boolean exists = file.toFile().exists();
        String absolutePath = file.toString();
        log.info("exists {}, abs {}", exists, absolutePath);

        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(file))) {
            System.out.println(doc);
            List<XWPFTable> tables = doc.getTables();
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

            String docText = extractor.getText();


            System.out.println(docText);
            FileOutputStream fileOutputStream = new FileOutputStream(Path.of("sample.pdf").toFile());
            IXWPFConverter<PdfOptions> instance = PdfConverter.getInstance();
            instance.convert(doc, fileOutputStream, PdfOptions.create());


        }

/*        WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(file);
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();

        Path tmp = Files.createFile(Path.of("sample.pdf"));
        FileOutputStream os = new FileOutputStream(tmp.toFile());


        String textNodesXPath = "//w:t";
        try {
            List<Object> jaxbNodesViaXPath = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, false);

            for (int i = 0; i < 1; i++) {
                for (Object obj : jaxbNodesViaXPath) {
                    Text text = (Text) ((JAXBElement<?>) obj).getValue();
                    String value = text.getValue();
                    System.out.println(value);

                    if (value.equals("NUMBERUP")) {
                        log.info("found upper number" + i);
                        text.setValue("hello i am new number");
                    }
                    if (value.equals("NUMBERDOWN")) {
                        log.info("found lower number");
                        text.setValue("hello i am number below" + i);
                    }
                    if (value.equals("DATE")) {
                        log.info("found lower number");
                        text.setValue(LocalDate.now() + "г. ");
                    }

                }
                Docx4J.toPDF(wordprocessingMLPackage, os);
            }
            os.flush();
            os.close();

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }



        //wordprocessingMLPackage.save(new File("saved.docx"));*/


    }


}
