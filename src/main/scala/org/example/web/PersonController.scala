package org.example.web

import org.example.model.Person
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._

@RestController
class PersonController {

  var persons: List[Person] = List()

  @GetMapping(path = Array("/randomperson"))
  def randomPerson: Person = Person.randomPerson

  @PostMapping(path = Array("/persons"))
  @ResponseStatus(HttpStatus.CREATED)
  def postPerson(@RequestBody person: Person): Unit = persons = person +: persons

  @GetMapping(path = Array("/persons"))
  def getPersons: List[Person] = persons
}
