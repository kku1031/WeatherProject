package zerobase.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;
    public void createDiary(LocalDate date, String text) {
        getWeatherString();
    }

    // open weather map API에서 데이터 받아오기
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=Daegu&appid=" + apiKey;
        System.out.println(apiUrl);
        return "";
    }
    //받아온 날씨 데이터 파싱하기

    //내 DB에 저장하기
}
