package br.com.edcor.PersonApi.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonTORepository extends JpaRepository<PersonTO, Long> {

}
