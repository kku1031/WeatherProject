package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    //    스프링부트에서 DB와 연동해주는 객체들을 Repository라는 이름으로 많이 만듬.
    //    jdbc활용시, JdbcTemplate을 활용하게 됨.
    private final JdbcTemplate jdbcTemplate;

    //초기 설정 application.properties dataSource -> DataSource 객체로 -> JdbcTemplate활용 -> jdbcTemplate 변수로!
    @Autowired //해줘야함.
    public JdbcMemoRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //Memo객체 저장 : Memo.class 값 저장 -> Mysql 저장 -> 반환값 : 그 메모.
    public Memo save(Memo memo) {
        String sql = "insert into memo values(?,?)";            //jdbc는 쿼리를 직접 짜야함.
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    //jdbcTemplate -> DB -> sql 쿼리 던짐 -> 반환 객체 : ResultSet 형태로 가지고 있다가 Memo로 Mapper함수 이용해서 가져옴.
    public List<Memo> findAll() {
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper()); //조회는 받아온 쿼리를 어떻게 명시할지 표시해 주어야해서 memoRowMapper 가져옴
    }

    //Optional : findById 함수를 통해서 Memo객체를 찾았을 때, id : 3을 찾아야 하는데 없을 때, null값 처리를 쉽게 해줌
    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        //우리는 딱 보고 id값에 뭐넣을지 알지만 스프링부트는 모름.stream().findFirst()
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    private RowMapper<Memo> memoRowMapper() {
        //ResultSet : jdbc를 통해서 mysql DB에서 데이터 가져오면 데이터 값은 ResultSet이라는 형태를 띰.
        //{id = 1, text = 'this is memo~'}
        //이러한 형식을 스프링부트에 만들어둔 Memo.class에 대입시켜줘야함 -> ResultSet을 Memo로 매핑해주는것.
        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
