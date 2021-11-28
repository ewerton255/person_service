package br.com.edcor.PersonApi.service;

import br.com.edcor.PersonApi.db.PersonTO;
import br.com.edcor.PersonApi.db.PersonTORepository;
import br.com.edcor.PersonApi.exceptions.FlowException;
import br.com.edcor.PersonApi.mappers.PersonTOMapper;
import br.com.edcor.PersonApi.openapi.AddPersonRequest;
import br.com.edcor.PersonApi.openapi.EditPersonRequest;
import br.com.edcor.PersonApi.openapi.ErrorTypeEnum;
import br.com.edcor.PersonApi.openapi.OrderEnum;
import br.com.edcor.PersonApi.openapi.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
public class PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonTORepository personTORepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PersonTOMapper personTOMapper;

    public List<Person> listPeople(String name, String email, Integer offset, Integer limit, OrderEnum order) {
        LOGGER.info("Starting the list person flow. Filter : [name: {}, email: {}, offset: {}, limit:{}, order: {}]",
                name, email, offset, limit, order);

        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(PersonTO.class);
        var root = query.from(PersonTO.class);
        var predicates = buildPredicates(criteriaBuilder, root, name, email);
        var firstResult = offset * limit;
        query.select(root);
        query.where(predicates.toArray(new Predicate[0]));
        addOrderByToQuery(query, criteriaBuilder, root, order);

        LOGGER.info("Executing the query and mapping the response.");

        var typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(limit);

        var response = typedQuery.getResultList()
                .stream()
                .map(personTOMapper::toPerson)
                .collect(Collectors.toList());
        LOGGER.info("Finish the list person flow.");

        return response;
    }

    private List<Predicate> buildPredicates(CriteriaBuilder criteriaBuilder, Root<PersonTO> root, String name, String email) {
        var predicates = new ArrayList<Predicate>();
        if (isNotBlank(name)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function("text", String.class, criteriaBuilder.upper(root.get("name"))), "%" + name.toUpperCase() + "%"));
        }
        if (isNotBlank(email)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function("text", String.class, criteriaBuilder.upper(root.get("email"))), "%" + email.toUpperCase() + "%"));
        }
        return predicates;
    }

    private void addOrderByToQuery(CriteriaQuery<PersonTO> query, CriteriaBuilder criteriaBuilder, Root<PersonTO> root, OrderEnum order) {
        if (OrderEnum.DESC.equals(order)) {
            query.orderBy(criteriaBuilder.desc(root.get("name")));
        } else {
            query.orderBy(criteriaBuilder.asc(root.get("name")));
        }
    }

    public Person findById(Long personId) {
        LOGGER.info("Starting the find person by id flow. Filter : [id: {}]", personId);
        var personInDatabase = personTORepository.findById(personId);
        if (personInDatabase.isEmpty()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.NOT_FOUND,
                    "Person with id " + personId + " not found.");
        }
        var response = personTOMapper.toPerson(personInDatabase.get());
        LOGGER.info("Finish the find person by id flow.");
        return response;
    }

    public Person addPerson(AddPersonRequest addPersonRequest) {
        LOGGER.info("Starting the add person flow. [addPersonRequest: {}]", addPersonRequest);
        validateEmailPattern(addPersonRequest.getEmail());
        var personWithCurrentEmail = personTORepository.findByEmailAndIdDifferentFrom(addPersonRequest.getEmail(), null);
        if (personWithCurrentEmail.isPresent()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.CONFLICT,
                    "There is already registration for the e-mail informed.");
        }
        var personTO = personTORepository.save(personTOMapper.toPersonTO(addPersonRequest));
        var response = personTOMapper.toPerson(personTO);
        LOGGER.info("Finish the add person flow.");
        return response;
    }

    public Person editPerson(Long personId, EditPersonRequest editPersonRequest) {
        LOGGER.info("Starting the edit person flow. [personId: {}, editPersonRequest: {}]", personId, editPersonRequest);
        var personInDataBase = personTORepository.findById(personId);
        if (personInDataBase.isEmpty()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.NOT_FOUND,
                    "Person with id " + personId + " not found.");
        }
        validateEmailPattern(editPersonRequest.getEmail());
        var personWithCurrentEmail = personTORepository.findByEmailAndIdDifferentFrom(editPersonRequest.getEmail(), personId);
        if (personWithCurrentEmail.isPresent()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.CONFLICT,
                    "There is already registration for the e-mail informed.");
        }
        if (isNotBlank(editPersonRequest.getName())) {
            personInDataBase.get().setName(editPersonRequest.getName());
        }
        if (isNotBlank(editPersonRequest.getEmail())) {
            personInDataBase.get().setEmail(editPersonRequest.getEmail());
        }
        personInDataBase.get().setDtLastUpdate(OffsetDateTime.now(ZoneId.of("UTC")));
        var response = personTOMapper.toPerson(personInDataBase.get());
        LOGGER.info("Finish the edit person flow.");
        return response;
    }

    public void deletePerson(Long personId) {
        LOGGER.info("Starting the delete person flow. [personId: {}]", personId);
        var personInDataBase = personTORepository.findById(personId);
        if (personInDataBase.isEmpty()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.NOT_FOUND,
                    "Person with id " + personId + " not found.");
        }
        personTORepository.deleteById(personId);
        LOGGER.info("Finish the delete person flow.");
    }

    public void validateEmailPattern(String email) {
        var regex = "^(.+)@(.+)$";
        var pattern = Pattern.compile(regex);
        if (isNotBlank(email) && !pattern.matcher(email).matches()) {
            throw new FlowException(ErrorTypeEnum.INVALID_REQUEST,
                    HttpStatus.BAD_REQUEST,
                    "The email entered is invalid.");
        }
    }
}
