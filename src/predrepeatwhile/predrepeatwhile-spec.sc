// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._
import org.sireum.bitcodec.Spec.bits

val foo: Spec =
  Concat("Foo", ISZ(
    PredRepeatWhile(
      "elements",
      ISZ(boolean(T)),
      Concat("Value", ISZ(
        Boolean("cont"),
        Bits("value", 7)
      ))
    ),
    Boolean("end")
  ))

println(foo.toJSON(T))
