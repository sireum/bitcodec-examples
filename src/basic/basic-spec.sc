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
    ByteRange("f4r", -20, -10),
    BytesRange("f4rs", 2, -20, -10),
    UBytes("uf4", 4),
    UByteConst("f4cu", 11),
    UByteRange("f4ru", 10, 20),
    UBytesRange("f4rus", 2, 10, 20),
    Shorts("f5", 5),
    ShortConst("f5c", 12),
    ShortRange("f5r", -200, -100),
    ShortsRange("f5rs", 2, -200, -100),
    UShorts("uf5", 5),
    UShortConst("f5cu", 13),
    UShortRange("f5ru", 100, 200),
    UShortsRange("f5rus", 2, 100, 200),
    Ints("f6", 6),
    IntConst("f6c", 14),
    IntRange("f6r", -2000, -1000),
    IntsRange("f6rs", 2, -2000, -1000),
    UInts("uf6", 6),
    UIntConst("f6cu", 15),
    UIntRange("f6ru", 1000, 2000),
    UIntsRange("f6rus", 2, 1000, 2000),
    Longs("f7", 7),
    LongConst("f7c", 16),
    LongRange("f7r", -20000, -10000),
    LongsRange("f7rs", 2, -20000, -10000),
    ULongs("uf7", 7),
    ULongConst("f7cu", 17),
    ULongRange("f7ru", 10000, 20000),
    ULongsRange("f7rus", 2, 10000, 20000),
    Float("f8"),
    FloatRange("f8r", -100f, 100f),
    Floats("f9", 2),
    FloatsRange("f9rs", 2, -100f, 100f),
    Double("f10"),
    DoubleRange("f10r", -1000d, 1000d),
    Doubles("f11", 3),
    DoublesRange("f10rs", 2, -1000d, 1000d),
    Pads(11)
  ))

println(foo.toJSON(T))
