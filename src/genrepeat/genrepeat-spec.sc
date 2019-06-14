// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Bits("size", 8),
    GenRepeat("elements", Bytes("sixBytes", 6))
  ))

println(foo.toJSON(T))
