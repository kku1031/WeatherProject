package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
//JPA는 자바의 표준 ORM -> 자바에서 ORM 할 JpaRepository에 이미 저장되어있음 jdbc의 save()등 별도 구축 없이 사용가능.
public interface JpaMemoRepository extends JpaRepository<Memo, Integer>  {
    //DB와 연동해주는 코드들 Repository 작성.
}
