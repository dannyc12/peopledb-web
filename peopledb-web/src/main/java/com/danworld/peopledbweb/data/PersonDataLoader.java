package com.danworld.peopledbweb.data;

import com.danworld.peopledbweb.business.model.Person;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// comment out @component to prevent spring from loading this when the application is started (in the event we
// don't need to manually load people in for starting data)

//@Component
public class PersonDataLoader implements ApplicationRunner {
    private PersonRepository personRepository;

    // Spring will see this constructor requires an instance of PersonRepository and
    // check its own 'beans' for that class/interface
    public PersonDataLoader(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (personRepository.count() == 0) {
            List<Person> people = List.of(
                    new Person(null, "Danny", "Clynes",
                            LocalDate.of(1989,8, 17), "test@sample.com", new BigDecimal("50000"), null),
                    new Person(null, "Chris", "Clynes",
                            LocalDate.of(1990,9, 29), "test@sample.com", new BigDecimal("60000"), null),
                    new Person(null, "Stef", "Clynes",
                            LocalDate.of(1988,7, 13), "test@sample.com", new BigDecimal("70000"), null),
                    new Person(null, "Cheryl", "Clynes",
                            LocalDate.of(1960,2, 16), "test@sample.com", new BigDecimal("70000"), null),
                    new Person(null, "Rick", "Clynes",
                            LocalDate.of(1959,10, 3), "test@sample.com", new BigDecimal("70000"), null)
            );
            personRepository.saveAll(people);
        }
    }
}
