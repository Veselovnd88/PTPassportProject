package ru.veselov.documentprocessing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.documentprocessing.service.PassportDocService;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/passport")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final PassportDocService passportDocService;

    @GetMapping
    public void getPassport() {
        log.info("smth happend");
        try {
            passportDocService.processPassport();
        } catch (Docx4JException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
