// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Bits("size", 8),
    BoundedGenRaw("elements", 10)
  ))

println(foo.toJSON(T))
