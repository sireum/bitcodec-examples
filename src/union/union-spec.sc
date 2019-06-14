// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Boolean("flag"),
    Union[B](
      "Bar",
      ISZ("flag"),
      p => if (p) 1 else 0,
      ISZ(
        Concat("Baz", ISZ(
          Boolean("b1"),
          Boolean("b2")
        )),
        Bits("bazz", 4)
      ))
  ))

println(foo.toJSON(T))
