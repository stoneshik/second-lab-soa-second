package lab.soa.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lab.soa.domain.models.Flat;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {
}
