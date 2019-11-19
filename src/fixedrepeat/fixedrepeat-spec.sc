// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    FixedRepeat(
      "elements",
      10,
      Concat("Value", ISZ(
        Boolean("cont"),
        Bits("value", 7)
      ))
    )
  ))

println(foo.toJSON(T))
