package org.example.configuration

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode, ObjectMapper, SerializerProvider}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.scala.deser.{ScalaNumberDeserializersModule, UntypedObjectDeserializerModule}
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospectorModule
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, EitherModule, EnumerationModule, IterableModule, IteratorModule, JacksonModule, MapModule, OptionModule, SeqModule, SetModule, TupleModule}
import org.example.configuration.JacksonConfiguration.{FaceJsonDeserializer, FaceJsonSerializer, GenderJsonDeserializer, GenderJsonSerializer}
import org.example.model.Person.Face.Face
import org.example.model.Person.{Face, FaceEnum, Gender, GenderEnum}
import org.example.model.Person.Gender.Gender
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class JacksonConfiguration {

  @Bean
  def objectMapper: ObjectMapper = {
    val objectMapper = new ObjectMapper()
    objectMapper.registerModule(MyScalaModule)
        val genderSerializerModule = new SimpleModule()
        genderSerializerModule.addSerializer(new GenderJsonSerializer())
        genderSerializerModule.addDeserializer(classOf[Gender.Gender], new GenderJsonDeserializer())
        objectMapper.registerModule(genderSerializerModule)

        val faceSerializerModule = new SimpleModule()
        faceSerializerModule.addSerializer(new FaceJsonSerializer())
        faceSerializerModule.addDeserializer(classOf[Face], new FaceJsonDeserializer())
        objectMapper.registerModule(faceSerializerModule)

    objectMapper
  }
}

object JacksonConfiguration {

  class GenderJsonSerializer extends StdSerializer[Gender.Gender](classOf[Gender.Gender]) {

    def this(t: GenderEnum) = this()

    override def serialize(value: Gender, gen: JsonGenerator, provider: SerializerProvider): Unit =
      gen.writeString(value.toString)
  }

  class GenderJsonDeserializer extends StdDeserializer[Gender.Gender](classOf[Gender.Gender]) {

    def this(t: GenderEnum) = this()

    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Gender =
      Gender.withName(p.getCodec.readTree(p).asInstanceOf[JsonNode].asText())
  }


  class FaceJsonSerializer extends StdSerializer[Face](classOf[Face]) {

    def this(t: FaceEnum) = this()

    override def serialize(value: Face, gen: JsonGenerator, provider: SerializerProvider): Unit =
      gen.writeString(value.toString)
  }

  class FaceJsonDeserializer extends StdDeserializer[Face](classOf[Face]) {

    def this(t: FaceEnum) = this()

    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Face =
      Face.withName(p.getCodec.readTree(p).asInstanceOf[JsonNode].asText())
  }

}

class MyScalaModule
  extends JacksonModule
    with IteratorModule
    with OptionModule
    with SeqModule
    with IterableModule
    with TupleModule
    with MapModule
    with SetModule
    with ScalaNumberDeserializersModule
    with ScalaAnnotationIntrospectorModule
    with UntypedObjectDeserializerModule
    with EitherModule {
  override def getModuleName = "MyScalaModule"
}

object MyScalaModule extends MyScalaModule
