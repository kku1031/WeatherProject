package zerobase.weather.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;

@RestController //기본Controller 기능 + http상태코드 지정해서 내려줄수 있게함.
@AllArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/create/diary")
        //@RequestParam : 요청보낼때 넣어두는 파라미터 @DateTimeFormat : 날짜형식,
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }
}
