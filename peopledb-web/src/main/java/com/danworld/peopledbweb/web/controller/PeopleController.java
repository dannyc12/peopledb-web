package com.danworld.peopledbweb.web.controller;

import com.danworld.peopledbweb.business.model.Person;
import com.danworld.peopledbweb.business.service.PersonService;
import com.danworld.peopledbweb.data.FileStorageRepository;
import com.danworld.peopledbweb.data.PersonRepository;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@Log4j2
@RequestMapping("/people")
public class PeopleController {

    public static final String DISPO = """
            attachment; filename"%s"
            """;
    private PersonRepository personRepository;
    private FileStorageRepository fileStorageRepository;
    private PersonService personService;

    public PeopleController(PersonRepository personRepository,
                            FileStorageRepository fileStorageRepository,
                            PersonService personService) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.personService = personService;
    }

    @ModelAttribute("people")
    public Iterable<Person> getPeople() {
        return personRepository.findAll();
    }

    // don't need to specify a name or key here in this case, because Spring will take return datatype of the method
    // and use that for the name of the model (in this case, 'Person')
    @ModelAttribute
    public Person getPerson() {
        // the commented code below will put 'test' in the first name field on page load
//        Person person = new Person();
//        person.setFirstName("test");
        return new Person();
    }

    // GetMapping specifies that if the request is a 'get' request, let this method handle the response
    @GetMapping
    public String showPeoplePage() {
        return "people";
    }

    @GetMapping("/images/{resource}")
    // @PathVariable tells Spring to pass {resource} into the getResource method, similar to RequestParams
    public ResponseEntity<Resource> getResource(@PathVariable String resource) {
        // ok() tells server to build a response with status code 200
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, format(DISPO, resource))
                .body(fileStorageRepository.findByName(resource));
    }

    @PostMapping
    //@Valid tells Spring MVC to make use of our validation annotations in the Person class
    // I passed through "photoFileName" here to avoid refactoring that stuff in people.html (it should be photoFile)
    public String savePerson(@Valid Person person, Errors errors, @RequestParam("photoFileName") MultipartFile photoFile) throws IOException {
        log.info(person);
        log.info("Filename: " + photoFile.getOriginalFilename());
        log.info("File size: " + photoFile.getSize());
        log.info("Errors: " + errors);
        if (!errors.hasErrors()) {
            // getInputStream gives a representation of the file uploaded in binary
            personService.save(person, photoFile.getInputStream());
            // upon saving new person, redirect to the /people endpoint (clears the form)
            return "redirect:people";
        }
        // without redirect, form entries remain, so they can be edited/resubmitted
        return "people";
    }

    // params tells Spring MVC to use this method when it receives a request that has a delete value of 'true'
    @PostMapping(params = "delete=true")
    // optional used in case nothing was selected (empty)
    public String deletePeople(@RequestParam Optional<List<Integer>> selections) {
        //System.out.println(selections);
        if (selections.isPresent()) {
//            personRepository.deleteAllById(selections.get());
            personService.deleteAllById(selections.get());
        }
        return "redirect:people";
    }

    // because we added a hidden input field in the people.html form to capture and pass back in the 'id' of selected
    // person, hibernate will recognize that we are not creating a new person, but instead updating an existing person
    @PostMapping(params = "edit=true")
    public String editPerson(@RequestParam Optional<List<Integer>> selections, Model model) {
        if (selections.isPresent()) {
            // selections is the optional, get() returns the list, and get(0) returns the first item in that list
            Optional<Person> person = personRepository.findById(selections.get().get(0));
            model.addAttribute("person", person);
        }
        return "people";
    }
}
