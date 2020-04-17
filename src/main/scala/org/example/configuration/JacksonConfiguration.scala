package org.example.configuration

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode, ObjectMapper, SerializerProvider}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.example.configuration.JacksonConfiguration.{FaceJsonDeserializer, FaceJsonSerializer, GenderJsonDeserializer, GenderJsonSerializer}
import org.example.model.Person.Face.Face
import org.example.model.Person.{Face, Gender}
import org.example.model.Person.Gender.Gender
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class JacksonConfiguration {

  @Bean
  def objectMapper: ObjectMapper = {
    val objectMapper = new ObjectMapper()
    objectMapper.registerModule(DefaultScalaModule)
    val genderSerializerModule = new SimpleModule()
    genderSerializerModule.addSerializer(new GenderJsonSerializer())
    genderSerializerModule.addDeserializer(classOf[Gender.Gender], new GenderJsonDeserializer())
    objectMapper.registerModule(genderSerializerModule)

    val faceSerializerModule = new SimpleModule()
    faceSerializerModule.addSerializer(new FaceJsonSerializer())
    faceSerializerModule.addDeserializer(classOf[Face.Face], new FaceJsonDeserializer())
    objectMapper.registerModule(faceSerializerModule)

    objectMapper
  }
}

object JacksonConfiguration {

  class GenderJsonSerializer extends StdSerializer[Gender.Gender](classOf[Gender.Gender]) {

    def this(t: Gender.type) = this()

    override def serialize(value: Gender, gen: JsonGenerator, provider: SerializerProvider): Unit =
      gen.writeString(value.toString)
  }

  class GenderJsonDeserializer extends StdDeserializer[Gender.Gender](classOf[Gender.Gender]) {

    def this(t: Gender.type) = this()

    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Gender =
      Gender.withName(p.getCodec.readTree(p).asInstanceOf[JsonNode].asText())
  }


  class FaceJsonSerializer extends StdSerializer[Face.Face](classOf[Face.Face]) {

    def this(t: Face.type) = this()

    override def serialize(value: Face, gen: JsonGenerator, provider: SerializerProvider): Unit =
      gen.writeString(value.toString)
  }

  class FaceJsonDeserializer extends StdDeserializer[Face.Face](classOf[Face.Face]) {

    def this(t: Face.type) = this()

    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Face =
      Face.withName(p.getCodec.readTree(p).asInstanceOf[JsonNode].asText())
  }

}
