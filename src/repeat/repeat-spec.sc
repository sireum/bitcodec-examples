// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Bits("size", 8),
    Repeat[U8]("elements", ISZ("size"),
      p => conversions.U8.toZ(p),
      UBytes("sixBytes", 6)
    )
  ))

println(foo.toJSON(T))
