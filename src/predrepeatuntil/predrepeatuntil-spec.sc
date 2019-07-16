// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._
import org.sireum.bitcodec.Spec.bits

val foo: Spec =
  Concat("Foo", ISZ(
    PredRepeatUntil(
      "elements",
      ISZ(bits(8, 0)),
      Bits("value", 8)
    ),
    Bits("end", 8)
  ))

println(foo.toJSON(T))
