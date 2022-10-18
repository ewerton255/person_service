package br.com.edcor.PersonApi.service;

import br.com.edcor.PersonApi.db.Person;
import br.com.edcor.PersonApi.db.PersonRepository;
import br.com.edcor.PersonApi.exceptions.FlowException;
import br.com.edcor.PersonApi.mappers.PersonMapper;
import br.com.edcor.PersonApi.openapi.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Log4j2
public class PersonService {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PersonMapper mapper;

    public List<PersonDTO> listPeople(String name, String email, Integer offset, Integer limit, OrderEnum order) {
        log.info("Starting the list person flow. Filter : [name: {}, email: {}, offset: {}, limit:{}, order: {}]",
                name, email, offset, limit, order);

        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(Person.class);
        var root = query.from(Person.class);
        var predicates = buildPredicates(criteriaBuilder, root, name, email);
        var firstResult = offset * limit;
        query.select(root);
        query.where(predicates.toArray(new Predicate[0]));
        addOrderByToQuery(query, criteriaBuilder, root, order);

        log.info("Executing the query and mapping the response.");

        var typedQuery = entityManager.createQuery(query)
                .setFirstResult(firstResult)
                .setMaxResults(limit);

        var response = typedQuery.getResultList()
                .stream()
                .map(mapper::toPersonDTO)
                .collect(Collectors.toList());

        log.info("Finish the list person flow.");
        return response;
    }

    private List<Predicate> buildPredicates(CriteriaBuilder criteriaBuilder, Root<Person> root, String name, String email) {
        var predicates = new ArrayList<Predicate>();
        if (isNotBlank(name)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + name.toUpperCase() + "%"));
        }
        if (isNotBlank(email)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("email")), "%" + email.toUpperCase() + "%"));
        }
        return predicates;
    }

    private void addOrderByToQuery(CriteriaQuery<Person> query, CriteriaBuilder criteriaBuilder, Root<Person> root, OrderEnum order) {
        if (OrderEnum.DESC.equals(order)) {
            query.orderBy(criteriaBuilder.desc(root.get("name")));
        } else {
            query.orderBy(criteriaBuilder.asc(root.get("name")));
        }
    }

    public PersonDTO findById(Long personId) {
        log.info("Starting the find person by id flow. Filter : [id: {}]", personId);
        var personInDatabase = repository.findById(personId);
        if (personInDatabase.isEmpty()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.NOT_FOUND,
                    "Person with id " + personId + " not found.");
        }
        var response = mapper.toPersonDTO(personInDatabase.get());
        log.info("Finish the find person by id flow.");
        return response;
    }

    public PersonDTO addPerson(AddPersonRequest addPersonRequest) {
        log.info("Starting the add person flow. [addPersonRequest: {}]", addPersonRequest);
        validateEmailPattern(addPersonRequest.getEmail());
        var personWithCurrentEmail = repository.findByEmailAndIdDifferentFrom(addPersonRequest.getEmail(), null);
        if (personWithCurrentEmail.isPresent()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.CONFLICT,
                    "There is already registration for the e-mail informed.");
        }
        var person = repository.save(mapper.toPerson(addPersonRequest));
        var response = mapper.toPersonDTO(person);
        log.info("Finish the add person flow.");
        return response;
    }

    public PersonDTO editPerson(Long personId, EditPersonRequest editPersonRequest) {
        log.info("Starting the edit person flow. [personId: {}, editPersonRequest: {}]", personId, editPersonRequest);
        var personInDataBase = repository.findById(personId);
        if (personInDataBase.isEmpty()) {
            throw new FlowException(
                    ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.NOT_FOUND,
                    String.format("Person with id %d not found.", personId)
            );
        }
        validateEmailPattern(editPersonRequest.getEmail());
        var personWithCurrentEmail = repository.findByEmailAndIdDifferentFrom(editPersonRequest.getEmail(), personId);
        if (personWithCurrentEmail.isPresent()) {
            throw new FlowException(
                    ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.CONFLICT,
                    "There is already registration for the e-mail informed."
            );
        }
        if (isNotBlank(editPersonRequest.getName())) {
            personInDataBase.get().setName(editPersonRequest.getName());
        }
        if (isNotBlank(editPersonRequest.getEmail())) {
            personInDataBase.get().setEmail(editPersonRequest.getEmail());
        }
        var savedPerson = repository.save(personInDataBase.get());
        var response = mapper.toPersonDTO(savedPerson);
        log.info("Finish the edit person flow.");
        return response;
    }

    public void deletePerson(Long personId) {
        log.info("Starting the delete person flow. [personId: {}]", personId);
        var personInDataBase = repository.findById(personId);
        if (personInDataBase.isEmpty()) {
            throw new FlowException(
                    ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.NOT_FOUND,
                    String.format("Person with id %d not found.", personId)
            );
        }
        repository.deleteById(personId);
        log.info("Finish the delete person flow.");
    }

    private void validateEmailPattern(String email) {
        var regex = "^(.+)@(.+)$";
        var pattern = Pattern.compile(regex);
        if (isNotBlank(email) && !pattern.matcher(email).matches()) {
            throw new FlowException(
                    ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.BAD_REQUEST,
                    "The email entered is invalid."
            );
        }
    }
}
