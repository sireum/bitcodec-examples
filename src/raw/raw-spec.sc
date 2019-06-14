// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Bits("size", 8),
    Raw[U8]("elements", ISZ("size"),
      p => conversions.U8.toZ(p)
    )
  ))

println(foo.toJSON(T))
