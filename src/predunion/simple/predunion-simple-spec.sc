// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    PredUnion(
      "Bar",
      ISZ(
        PredSpec(
          ISZ(boolean(T)),
          Concat("Baz", ISZ(
            Boolean("flag"),
            Boolean("b1"),
            Boolean("b2")
          ))
        ),
        PredSpec(
          ISZ(boolean(F)),
          Concat("Bazz", ISZ(
            Boolean("flag"),
            Bits("bazz", 4)
          ))
        )
      ))
  ))

println(foo.toJSON(T))
