package br.com.edcor.PersonApi.api;

import br.com.edcor.PersonApi.openapi.*;
import br.com.edcor.PersonApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PersonApiController implements PersonApi {

    @Autowired
    private PersonService personService;

    @Override
    public ResponseEntity<List<PersonDTO>> listPeople(String name, String email, Integer offset, Integer limit, OrderEnum order) {
        return ResponseEntity.ok(personService.listPeople(name, email, offset, limit, order));
    }

    @Override
    public ResponseEntity<PersonDTO> findPersonById(Long personId) {
        return ResponseEntity.ok(personService.findById(personId));
    }

    @Override
    public ResponseEntity<PersonDTO> addPerson(AddPersonRequest addPersonRequest) {
        return ResponseEntity.ok(personService.addPerson(addPersonRequest));
    }

    @Override
    public ResponseEntity<PersonDTO> editPerson(Long personId, EditPersonRequest editPersonRequest) {
        return ResponseEntity.ok(personService.editPerson(personId, editPersonRequest));
    }

    @Override
    public ResponseEntity<Void> deletePerson(Long personId) {
        personService.deletePerson(personId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
