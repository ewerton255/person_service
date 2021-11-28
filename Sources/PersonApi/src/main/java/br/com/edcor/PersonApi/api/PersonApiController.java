package br.com.edcor.PersonApi.api;

import br.com.edcor.PersonApi.openapi.OrderEnum;
import br.com.edcor.PersonApi.openapi.Person;
import br.com.edcor.PersonApi.openapi.PersonApi;
import br.com.edcor.PersonApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PersonApiController implements PersonApi {

    @Autowired
    private PersonService personService;

    @Override
    public ResponseEntity<List<Person>> listPeople(String name, String email, Integer offset, Integer limit, OrderEnum order) {
        return ResponseEntity.ok(personService.listPeople(name, email, offset, limit, order));
    }

    public ResponseEntity<Person> findPersonById(Long personId) {
        return ResponseEntity.ok(personService.findById(personId));
    }
}
