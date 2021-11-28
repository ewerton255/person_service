package br.com.edcor.PersonApi.mappers;

import br.com.edcor.PersonApi.db.PersonTO;
import br.com.edcor.PersonApi.openapi.AddPersonRequest;
import br.com.edcor.PersonApi.openapi.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PersonTOMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "dtCreation", source = "dtCreation")
    @Mapping(target = "dtLastUpdate", source = "dtLastUpdate")
    Person toPerson(PersonTO personTO);


    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    PersonTO toPersonTO(AddPersonRequest addPersonRequest);
}
