// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._
import org.sireum.bitcodec.Spec.bits

val foo: Spec =
  Concat("Foo", ISZ(
    PredUnion(
      "Bar",
      ISZ(
        PredSpec(
          ISZ(skip(1), bytes(ISZ(1, 0)), longs(ISZ(7))),
          Concat("Baz", ISZ(
            Boolean("flag"),
            UBytes("bs", 2),
            ULongs("l", 1)
          ))
        ),
        PredSpec(
          ISZ(skip(1), shorts(ISZ(2)), ints(ISZ(3, 5))),
          Concat("Bazz", ISZ(
            Boolean("flag"),
            UShorts("s", 1),
            UInts("is", 2)
          ))
        ),
        PredSpec(
          ISZ(not(boolean(T)), or(ISZ(bits(8, 3), bits(8, 5))), between(8, 10, 15)),
          Concat("Bazzz", ISZ(
            Boolean("flag"),
            Bits("b1", 8),
            Bits("b2", 8)
          ))
        )
      ))
  ))

println(foo.toJSON(T))
