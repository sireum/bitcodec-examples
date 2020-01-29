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
    ByteConst("f4c", 10),
    UBytes("uf4", 4),
    UByteConst("f4cu", 11),
    Shorts("f5", 5),
    ShortConst("f5c", 12),
    UShorts("uf5", 5),
    UShortConst("f5cu", 13),
    Ints("f6", 6),
    IntConst("f6c", 14),
    UInts("uf6", 6),
    UIntConst("f6cu", 15),
    Longs("f7", 7),
    LongConst("f7c", 16),
    ULongs("uf7", 7),
    ULongConst("f7cu", 17),
    Float("f8"),
    Floats("f9", 2),
    Double("f10"),
    Doubles("f11", 3),
    Pads(11)
  ))

println(foo.toJSON(T))
