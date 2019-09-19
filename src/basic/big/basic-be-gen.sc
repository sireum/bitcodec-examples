// #Sireum

import org.sireum._
import org.sireum.U7._
import org.sireum.U8._
import org.sireum.U16._
import org.sireum.U32._
import org.sireum.U64._
import org.sireum.S8._
import org.sireum.S16._
import org.sireum.S32._
import org.sireum.S64._
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

    val maxSize: Z = z"855"

    def empty: MFoo = {
      return MFoo(F, u7"0", MSZ.create(100, F), MSZ.create(4, s8"0"), MSZ.create(4, u8"0"), MSZ.create(5, s16"0"), MSZ.create(5, u16"0"), MSZ.create(6, s32"0"), MSZ.create(6, u32"0"), MSZ.create(7, s64"0"), MSZ.create(7, u64"0"))
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

  }

  @datatype class Foo(
    val f1: B,
    val f2: U7,
    val f3: ISZ[B],
    val f4: ISZ[S8],
    val uf4: ISZ[U8],
    val f5: ISZ[S16],
    val uf5: ISZ[U16],
    val f6: ISZ[S32],
    val uf6: ISZ[U32],
    val f7: ISZ[S64],
    val uf7: ISZ[U64]
  ) {

    @strictpure def toMutable: MFoo = MFoo(f1, f2, f3.toMS, f4.toMS, uf4.toMS, f5.toMS, uf5.toMS, f6.toMS, uf6.toMS, f7.toMS, uf7.toMS)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(855, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var f1: B,
    var f2: U7,
    var f3: MSZ[B],
    var f4: MSZ[S8],
    var uf4: MSZ[U8],
    var f5: MSZ[S16],
    var uf5: MSZ[U16],
    var f6: MSZ[S32],
    var uf6: MSZ[U32],
    var f7: MSZ[S64],
    var uf7: MSZ[U64]
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(f1, f2, f3.toIS, f4.toIS, uf4.toIS, f5.toIS, uf5.toIS, f6.toIS, uf6.toIS, f7.toIS, uf7.toIS)

    def wellFormed: Z = {

      if (f3.size != 100) {
        return ERROR_Foo
      }

      if (f4.size != 4) {
        return ERROR_Foo
      }

      if (uf4.size != 4) {
        return ERROR_Foo
      }

      if (f5.size != 5) {
        return ERROR_Foo
      }

      if (uf5.size != 5) {
        return ERROR_Foo
      }

      if (f6.size != 6) {
        return ERROR_Foo
      }

      if (uf6.size != 6) {
        return ERROR_Foo
      }

      if (f7.size != 7) {
        return ERROR_Foo
      }

      if (uf7.size != 7) {
        return ERROR_Foo
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      f1 = Reader.IS.bleB(input, context)
      f2 = Reader.IS.beU7(input, context)
      Reader.IS.beBS(input, context, f3, 100)
      Reader.IS.beS8S(input, context, f4, 4)
      Reader.IS.beU8S(input, context, uf4, 4)
      Reader.IS.beS16S(input, context, f5, 5)
      Reader.IS.beU16S(input, context, uf5, 5)
      Reader.IS.beS32S(input, context, f6, 6)
      Reader.IS.beU32S(input, context, uf6, 6)
      Reader.IS.beS64S(input, context, f7, 7)
      Reader.IS.beU64S(input, context, uf7, 7)
      context.skip(input.size, 11, ERROR_Foo)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, f1)
      Writer.beU7(output, context, f2)
      Writer.beBS(output, context, f3)
      Writer.beS8S(output, context, f4)
      Writer.beU8S(output, context, uf4)
      Writer.beS16S(output, context, f5)
      Writer.beU16S(output, context, uf5)
      Writer.beS32S(output, context, f6)
      Writer.beU32S(output, context, uf6)
      Writer.beS64S(output, context, f7)
      Writer.beU64S(output, context, uf7)
      context.skip(output.size, 11, ERROR_Foo)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

  }

}

// BEGIN USER CODE: Test
import BitCodec._
val fooExample = MFoo(
  T,
  u7"11",
  MSZ.create(100, T),
  MSZ(s8"0", s8"1", s8"2", s8"3"),
  MSZ(u8"0", u8"1", u8"2", u8"3"),
  MSZ(s16"0", s16"1", s16"2", s16"3", s16"4"),
  MSZ(u16"0", u16"1", u16"2", u16"3", u16"4"),
  MSZ(s32"0", s32"1", s32"2", s32"3", s32"4", s32"5"),
  MSZ(u32"0", u32"1", u32"2", u32"3", u32"4", u32"5"),
  MSZ(s64"0", s64"1", s64"2", s64"3", s64"4", s64"5", s64"6"),
  MSZ(u64"0", u64"1", u64"2", u64"3", u64"4", u64"5", u64"6")
)
println(s"fooExample = $fooExample")

assert(fooExample.wellFormed == 0, "fooExample is not well-formed!")

val fooExampleOutput = MSZ.create(2000, F)
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
fooExampleDecoded.decode(fooExampleEncoded.toIS, fooExampleInputContext)
println(s"decode(encode(fooExample)) = $fooExampleDecoded")
println(s"decode(encode(fooExample)).offset = ${fooExampleInputContext.offset}")
println(s"decode(encode(fooExample)).errorCode = ${fooExampleInputContext.errorCode}")
println(s"decode(encode(fooExample)).errorOffset = ${fooExampleInputContext.errorOffset}")

assert(fooExampleInputContext.errorCode == 0 && fooExampleInputContext.errorOffset == 0, "Decoding error!")
assert(fooExampleOutputContext.offset == fooExampleInputContext.offset, "The decoder does not consume the same number of bits produced by the encoder!")
assert(fooExample == fooExampleDecoded, s"$fooExample != $fooExampleDecoded")
// END USER CODE: Test