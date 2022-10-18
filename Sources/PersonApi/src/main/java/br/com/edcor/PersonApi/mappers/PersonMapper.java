package br.com.edcor.PersonApi.mappers;

import br.com.edcor.PersonApi.db.Person;
import br.com.edcor.PersonApi.openapi.AddPersonRequest;
import br.com.edcor.PersonApi.openapi.PersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PersonMapper {

    PersonDTO toPersonDTO(Person person);
    Person toPerson(AddPersonRequest addPersonRequest);
}
