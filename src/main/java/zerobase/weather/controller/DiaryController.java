package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController //기본Controller 기능 + http상태코드 지정해서 내려줄수 있게함.
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    //Swagger웹상에서 설명.
    @ApiOperation(
            value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장",
            notes = "이것은 노트"
    )
    @PostMapping("/create/diary")
        //@RequestParam : 요청보낼때 넣어두는 파라미터 @DateTimeFormat : 날짜형식,
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    //조회 : 날짜 정도만 받아오면됨
    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    //조회 : 해당 기간의 일기(몇월 며칠 부터 몇월 며칠까지)
    @ApiOperation("선택한 기간중의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 첫번째 날",example = "2023-06-30")
                            LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 마지막 날",example = "2020-07-01") LocalDate endDate) {

        return diaryService.readDiaries(startDate, endDate);
    }
    
    //수정 : 어떤 날에 어떤일기 수정할지
    @ApiOperation("일기데이터를 수정합니다")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    //삭제
    @ApiOperation("일기데이터를 삭제합니다")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteDiary(date);
    }
}

