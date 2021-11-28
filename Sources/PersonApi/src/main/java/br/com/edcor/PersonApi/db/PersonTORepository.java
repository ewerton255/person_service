package br.com.edcor.PersonApi.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonTORepository extends JpaRepository<PersonTO, Long> {

    @Query(value = "SELECT P FROM PersonTO P WHERE UPPER(P.email) LIKE UPPER(:email) and (:personId IS NULL " +
            "OR :personId <> P.id) ")
    Optional<PersonTO> findByEmailAndIdDifferentFrom(@Param("email") String email, @Param("personId") Long personId);
}
