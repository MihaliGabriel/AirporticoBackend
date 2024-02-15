package main.repository;

import main.model.TextTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextTypeEntityRepository extends JpaRepository<TextTypeEntity, Long> {
}
