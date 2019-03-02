# Scala-Spring-Jackson example

The problem: How to combine all three together when developing REST services

### Java

When working with Java it's simple: you return a POJO and
Spring will convert it into a JSON document automatically.
Or implement a method that takes a POJO and annotate it with PostMapping
and everything works!

Jackson JSON serializer and deserializer support Java collections.
This makes implementation of REST services fast and easy.

### Scala

Scala is great and case classes are great. For example, this could be our model:

```scala
case class Person(
  name: String,
  age: Int,
  hobbies: List[String],
  gender: Gender.Gender
)
```

`Gender` is an enumeration defined as

```scala
object Gender extends Enumeration {
  type Gender = Value
  val MALE, FEMALE = Value
}
```

Unfortunately there are several small problems:

* A case class is not a POJO, and Spring does not want to
make it a JSON automatically. This can be fixed by adding annotations `@BeanProperty`
for every field. The fields need to be vars.

Also you need a no-arguments constructor, implement it as follows:

```scala
case class Person(@BeanProperty var name: String) {
    def this() = this("default name")
}
```

* Scala classes, like List are not supported by Jackson. To fix this add a scala
compatibility module to Jackson ObjectMapper:

  Add this to the dependencies:

  ```groovy
  compile group: 'com.fasterxml.jackson.module', name: 'jackson-module-scala_2.12', version: '2.9.8'
  ```

  Create a Jackson configuration class that will customize the ObjectMapper that Spring uses:

  ```scala
  @Configuration
  class JacksonConfiguration {

    @Bean
    def objectMapper: ObjectMapper = {
      val objectMapper = new ObjectMapper()
      objectMapper.registerModule(DefaultScalaModule)
      objectMapper
    }
  }
  ```

* More complicated problem: even the jackson-scala-module does not work
with Enumerations out of the box.

By default instead of

```json
"gender": "MALE"
```

you get

```json
gender": {
  "enumClass": "org.example.model.Person$Gender",
  "value": "MALE"
}
```

The Jackson's project Wiki suggest using another
annotation, `@JsonScalaEnumeration`
https://github.com/FasterXML/jackson-module-scala/wiki/Enumerations

Unfortunately this method is not working. To be precise, it works most of the times,
but sometimes it does not. The behavior is very unstable. Sometimes it works, but sometimes
there are exceptions, like

```
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize instance of `scala.Enumeration$Value` out of VALUE_STRING token
```

I was not able to find any system there, it feels like random behavior which is worst of all.

So this is an example of fixing the issue using custom Jackson serializer and deserializer.
They are implemented in this way:

```scala
class GenderJsonSerializer extends StdSerializer[Gender.Gender](classOf[Gender.Gender]) {

  def this(t: Gender.type) = this()

  override def serialize(value: Gender, gen: JsonGenerator, provider: SerializerProvider): Unit =
    gen.writeString(value.toString)
}

class GenderJsonDeserializer extends StdDeserializer[Gender.Gender](classOf[Gender.Gender]) {

  def this(t: Gender.type ) = this()

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Gender =
    Gender.withName(p.getCodec.readTree(p).asInstanceOf[JsonNode].asText())
}
```

And the configuration class should be changed as follows:

```scala
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
    objectMapper
  }
}
```

### Try this

Simply start the example application with Gradle:

```
$ ./gradlew bootRun
```

It starts a web server on port 8080.
You can get a random Person by requesting the `/randomperson` resource using the ling
http://localhost:8080/randomperson

You can post a person back, to test the serialization logic:

```
$ curl --header "Content-Type: application/json" --request POST --data $(curl localhost:8080/randomperson) localhost:8080/persons
```

Repeat that several times, and then request the resource http://localhost:8080/persons
to make sure serialization also works for a List[Person]
