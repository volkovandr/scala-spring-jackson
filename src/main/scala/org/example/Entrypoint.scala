package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Entrypoint

object Entrypoint extends App {
  SpringApplication.run(classOf[Entrypoint])
}