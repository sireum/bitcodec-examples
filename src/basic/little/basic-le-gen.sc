// #Sireum

import org.sireum._
import org.sireum.U7._
import org.sireum.U8._
import org.sireum.U16._
import org.sireum.U32._
import org.sireum.U64._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Foo: Z = 2

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object Foo {
    def empty: Foo = {
      return Foo(F, u7"0", MSZ.create(100, F), MSZ.create(4, u8"0"), MSZ.create(5, u16"0"), MSZ.create(6, u32"0"), MSZ.create(7, u64"0"))
    }
  }

  @record class Foo(
    var f1: B,
    var f2: U7,
    var f3: MSZ[B],
    var f4: MSZ[U8],
    var f5: MSZ[U16],
    var f6: MSZ[U32],
    var f7: MSZ[U64]
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      if (f3.size != 100) {
        return ERROR_Foo
      }

      if (f4.size != 4) {
        return ERROR_Foo
      }

      if (f5.size != 5) {
        return ERROR_Foo
      }

      if (f6.size != 6) {
        return ERROR_Foo
      }

      if (f7.size != 7) {
        return ERROR_Foo
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      f1 = Reader.MS.bleB(input, context)
      f2 = Reader.MS.leU7(input, context)
      Reader.MS.leBS(input, context, f3, 100)
      Reader.MS.leU8S(input, context, f4, 4)
      Reader.MS.leU16S(input, context, f5, 5)
      Reader.MS.leU32S(input, context, f6, 6)
      Reader.MS.leU64S(input, context, f7, 7)
      context.skip(input.size, 11, ERROR_Foo)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, f1)
      Writer.leU7(output, context, f2)
      Writer.leBS(output, context, f3)
      Writer.leU8S(output, context, f4)
      Writer.leU16S(output, context, f5)
      Writer.leU32S(output, context, f6)
      Writer.leU64S(output, context, f7)
      context.skip(output.size, 11, ERROR_Foo)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

  }

}

// BEGIN USER CODE: Test
import BitCodec._
val fooExample = Foo(
  T,
  u7"11",
  MSZ.create(100, T),
  MSZ(u8"0", u8"1", u8"2", u8"3"),
  MSZ(u16"0", u16"1", u16"2", u16"3", u16"4"),
  MSZ(u32"0", u32"1", u32"2", u32"3", u32"4", u32"5"),
  MSZ(u64"0", u64"1", u64"2", u64"3", u64"4", u64"5", u64"6"))
println(s"fooExample = $fooExample")

assert(fooExample.wellFormed == 0, "fooExample is not well-formed!")

val fooExampleOutput = MSZ.create(1000, F)
val fooExampleOutputContext = Context.create
fooExample.encode(fooExampleOutput, fooExampleOutputContext)
val fooExampleEncoded = Writer.resultMS(fooExampleOutput, fooExampleOutputContext)
println(s"encode(fooExample) = $fooExampleEncoded")
println(s"encode(fooExample).offset = ${fooExampleOutputContext.offset}")
println(s"encode(fooExample).errorCode = ${fooExampleOutputContext.errorCode}")
println(s"encode(fooExample).errorOffset = ${fooExampleOutputContext.errorOffset}")

assert(fooExampleOutputContext.errorCode == 0 && fooExampleOutputContext.errorOffset == 0, "Encoding error!")

val fooExampleInputContext = Context.create
val fooExampleDecoded = Foo.empty
fooExampleDecoded.decode(fooExampleEncoded, fooExampleInputContext)
println(s"decode(encode(fooExample)) = $fooExampleDecoded")
println(s"decode(encode(fooExample)).offset = ${fooExampleInputContext.offset}")
println(s"decode(encode(fooExample)).errorCode = ${fooExampleInputContext.errorCode}")
println(s"decode(encode(fooExample)).errorOffset = ${fooExampleInputContext.errorOffset}")

assert(fooExampleInputContext.errorCode == 0 && fooExampleInputContext.errorOffset == 0, "Decoding error!")
assert(fooExampleOutputContext.offset == fooExampleInputContext.offset, "The decoder does not consume the same number of bits produced by the encoder!")
assert(fooExample == fooExampleDecoded, s"$fooExample != $fooExampleDecoded")
// END USER CODE: Test