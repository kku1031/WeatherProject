package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    public void createDiary(LocalDate date, String text) {
        //open weather map에서 날씨 데이터 가져오기.
        String weatherData = getWeatherString();
        //받아온 날씨 데이터 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        //파싱된 데이터 + 일기 값 내 DB에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setWeather(parsedWeather.get("main").toString());
        nowDiary.setIcon(parsedWeather.get("icon").toString());
        nowDiary.setTemperature(Double.parseDouble(parsedWeather.get("temp").toString()));
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);
    }

    //조회
    public List<Diary> readDiary(LocalDate date) {
        //일기 값 -> DB 조회 -> service -> repository -> date 값 기준 그날 일기 가져와야함 -> Repository
        return diaryRepository.findAllByDate(date);
    }

    //조회 : 해당기간
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    // open weather map API에서 데이터 받아오기
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=Daegu&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            //http 형식으로 연결
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode(); //응답코드
            BufferedReader br;  //오류 발생시, 오류 메시지가 길수 있으니 빠른 처리를 위해 BufferedReader
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();

        } catch (Exception e) {
            return "Response(응답)에 실패하였습니다.";
        }
    }

    //받아온 날씨 데이터 파싱하기
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject; //파싱한 결과값 담는 변수

        //JSON파싱 시 {가 열렸는데 닫혀있지 않거나, 유효하지 않은 문자열 일때 어려움(try catch)
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //쓰고자 하는 DATA
        Map<String, Object> resultMap = new HashMap<>();

        //main값 -> JSONObject =>는 get으로 받아올 수 있음. mainData도 JSONObject -> mainData.get("temp")
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather"); //[{"id":803,"main":"Clouds"," ....형식 '배열'
        JSONObject weatherData = (JSONObject) weatherArray.get(0); //list안의 객체가 1개
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }



}
