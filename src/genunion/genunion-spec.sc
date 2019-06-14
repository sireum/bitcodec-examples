// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Boolean("flag"),
    GenUnion(
      "Bar",
      ISZ(
        Concat("Baz", ISZ(
          Boolean("b1"),
          Boolean("b2")
        )),
        Bits("bazz", 4)
      ))
  ))

println(foo.toJSON(T))
