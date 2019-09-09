// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Bits("size", 8),
    BoundedGenRepeat("elements", 10, Bytes("sixBytes", 6))
  ))

println(foo.toJSON(T))
