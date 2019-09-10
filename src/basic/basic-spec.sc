// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

val foo: Spec =
  Concat("Foo", ISZ(
    Boolean("f1"),
    Bits("f2", 7),
    Bits("f3", 100),
    Bytes("f4", 4),
    UBytes("uf4", 4),
    Shorts("f5", 5),
    UShorts("uf5", 5),
    Ints("f6", 6),
    UInts("uf6", 6),
    Longs("f7", 7),
    ULongs("uf7", 7),
    Pads(11)
  ))

println(foo.toJSON(T))
