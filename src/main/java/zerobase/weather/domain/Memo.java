package zerobase.weather.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo") //테이블 명
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본 키 생성 -> DB

    private int id;
    private String text;
}
