package org.example

import org.example.configuration.JacksonConfiguration
import org.example.model.Person
import org.example.model.Person.Gender
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JsonSerializationTest extends FlatSpec with Matchers {

  "Jackson serializer" should "serialize a Person into JSON" in {
    val samplePerson = Person("Zorro", 15, List("Lego", "Piano"), Gender.MALE)
    val mapper = new JacksonConfiguration().objectMapper
    val actualJson = mapper.writerFor(classOf[Person]).writeValueAsString(samplePerson)
    val expectedJson = """{"name":"Zorro","age":15,"hobbies":["Lego","Piano"],"gender":"MALE"}"""

    actualJson should equal(expectedJson)
  }

  it should "deserialize a Person from JSON string" in {
    val sampleJson = """{"name":"Zorro","age":15,"hobbies":["Lego","Piano"],"gender":"MALE"}"""
    val mapper = new JacksonConfiguration().objectMapper
    val actualPerson = mapper.readValue(sampleJson, classOf[Person])
    val expectedPerson = Person("Zorro", 15, List("Lego", "Piano"), Gender.MALE)

    actualPerson should equal(expectedPerson)
  }

  it should "serialize and deserialize random Persons 1000 times" in {
    for(i <- 0 to 1000) {
      val randomPerson = Person.randomPerson
      val mapper = new JacksonConfiguration().objectMapper
      val jsonString = mapper.writerFor(classOf[Person]).writeValueAsString(randomPerson)
      val actualPerson = mapper.readValue(jsonString, classOf[Person])

      actualPerson should equal (randomPerson)
    }
  }
}
