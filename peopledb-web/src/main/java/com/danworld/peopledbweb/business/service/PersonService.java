package com.danworld.peopledbweb.business.service;

import com.danworld.peopledbweb.business.model.Person;
import com.danworld.peopledbweb.data.FileStorageRepository;
import com.danworld.peopledbweb.data.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final FileStorageRepository fileStorageRepository;

    public PersonService(PersonRepository personRepository, FileStorageRepository fileStorageRepository) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

    // Transactional allows db to rollback any changes if there is anything that goes wrong with the method (exceptions)
    @Transactional
    public Person save(Person person, InputStream photoStream) {
        Person savedPerson = personRepository.save(person);
        fileStorageRepository.save(person.getPhotoFileName(), photoStream);
        return savedPerson;
    }

    public Optional<Person> findById(Integer integer) {
        return personRepository.findById(integer);
    }

    public Iterable<Person> findAll() {
        return personRepository.findAll();
    }

    public void deleteAllById(Iterable<Integer> ids) {
        Iterable<Person> peopleToDelete = personRepository.findAllById(ids);
       Stream<Person> peopleStream =  StreamSupport.stream(peopleToDelete.spliterator(), false);
       Set<String> filenames =  peopleStream
               .map(Person::getPhotoFileName)
                       .collect(Collectors.toSet());
        personRepository.deleteAllById(ids);
        fileStorageRepository.deleteAllByName(filenames);
    }
}
