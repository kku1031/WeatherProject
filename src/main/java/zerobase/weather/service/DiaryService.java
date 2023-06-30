package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
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
@Transactional(readOnly = true) //트랜잭션 : 읽기 전용 -> 성능 최적화, 특정 작업 안에서 데이터 수정, 삭제 등 방지.
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    //매 시간 마다 날씨 데이터 저장.
    @Transactional
    @Scheduled(cron = "0 0 1 * * *") //초, 분, 시, 일, 월, 주 : 매일 새벽1시마다 ->DB에 save
    public void saveWeatherDate() {
        logger.info("오늘도 날씨 데이터 잘 가져옴!!");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    //트랜잭션 세부설정(isolation - 격리수준) : 일관성이 없는 데이터를 허용하는 수준
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        //내 DB에서 날씨 데이터 가져오기
        DateWeather dateWeather = getDateWeather(date);
        //파싱된 데이터 + 일기 값 내 DB에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    //외부API에서 날씨 데이터 가져오기.
    private DateWeather getWeatherFromApi() {
        //open weather map에서 날씨 데이터 가져오기.
        String weatherData = getWeatherString();
        //받아온 날씨 데이터 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather(); //NoArgsConstructor 활용
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }

    //내 DB에서 날씨 데이터 가져오기
    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0) {
            // 새로 api에서 날씨 정보를 가져와야한다.
            // 과거로 부터 가져오는게 일반적이지만 Weather API에서 유료여서 정책 : 현재로 부터 가져오는 것으로 설정
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    //조회
    @Transactional(readOnly = true) //조회부분 성능 향상.
    public List<Diary> readDiary(LocalDate date) {
        //일기 값 -> DB 조회 -> service -> repository -> date 값 기준 그날 일기 가져와야함 -> Repository
        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }

    //조회 : 해당기간
    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    //수정
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date); //그 날짜에 해당하는 일기 하나만 반환.
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    //삭제
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
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
