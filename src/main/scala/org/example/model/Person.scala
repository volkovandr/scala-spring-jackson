package org.example.model

import java.util

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.example.model.Person.{Face, Gender}

import scala.beans.BeanProperty
import scala.util.Random

case class Person(
                   @BeanProperty var name: String,
                   @BeanProperty var age: Int,
                   @BeanProperty var hobbies: List[String],
                   @BeanProperty
                   @JsonDeserialize(using = classOf[org.example.configuration.JacksonConfiguration.GenderJsonDeserializer])
                   var gender: Gender.Gender,

                   @BeanProperty
                   @JsonDeserialize(using = classOf[org.example.configuration.JacksonConfiguration.FaceJsonDeserializer])
                   var face: Face.Face,

                 ) {
  def this() = this("", 0, List(), Gender.MALE, Face.BIG_FACE)
}

object Person {

  trait GenderEnum {
    this: scala.Enumeration =>
  }

  object Gender extends Enumeration with GenderEnum {
    type Gender = Value
    val MALE, FEMALE = Value
  }

  trait FaceEnum {
    this: scala.Enumeration =>
  }

  object Face extends Enumeration with FaceEnum {
    type Face = Value
    val BIG_FACE, SMALL_FACE = Value
  }

  private val names = List("Zorro", "Morro", "Korro")
  private val hobbies = List("killing", "stealing", "hiking", "swimming", "sleeping")

  def randomPerson: Person = Person(
    Random.shuffle(names).head,
    Random.nextInt(100),
    Random.shuffle(hobbies).take(2),
    Random.shuffle(Gender.values.toList).head,
    Random.shuffle(Face.values.toList).head
  )
}