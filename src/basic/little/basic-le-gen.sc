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

    val maxSize: Z = z"2215"

    def empty: MFoo = {
      return MFoo(F, u7"0", MSZ.create(100, F), MSZ.create(4, s8"0"), s8"0", MSZ.create(4, u8"0"), u8"0", MSZ.create(5, s16"0"), s16"0", MSZ.create(5, u16"0"), u16"0", MSZ.create(6, s32"0"), s32"0", MSZ.create(6, u32"0"), u32"0", MSZ.create(7, s64"0"), s64"0", MSZ.create(7, u64"0"), u64"0", 0.0f, MSZ.create(2, 0.0f), 0.0d, MSZ.create(3, 0.0d))
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
    val f4c: S8,
    val uf4: ISZ[U8],
    val f4cu: U8,
    val f5: ISZ[S16],
    val f5c: S16,
    val uf5: ISZ[U16],
    val f5cu: U16,
    val f6: ISZ[S32],
    val f6c: S32,
    val uf6: ISZ[U32],
    val f6cu: U32,
    val f7: ISZ[S64],
    val f7c: S64,
    val uf7: ISZ[U64],
    val f7cu: U64,
    val f8: F32,
    val f9: ISZ[F32],
    val f10: F64,
    val f11: ISZ[F64]
  ) {

    @strictpure def toMutable: MFoo = MFoo(f1, f2, f3.toMS, f4.toMS, f4c, uf4.toMS, f4cu, f5.toMS, f5c, uf5.toMS, f5cu, f6.toMS, f6c, uf6.toMS, f6cu, f7.toMS, f7c, uf7.toMS, f7cu, f8, f9.toMS, f10, f11.toMS)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(2215, F)
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
    var f4c: S8,
    var uf4: MSZ[U8],
    var f4cu: U8,
    var f5: MSZ[S16],
    var f5c: S16,
    var uf5: MSZ[U16],
    var f5cu: U16,
    var f6: MSZ[S32],
    var f6c: S32,
    var uf6: MSZ[U32],
    var f6cu: U32,
    var f7: MSZ[S64],
    var f7c: S64,
    var uf7: MSZ[U64],
    var f7cu: U64,
    var f8: F32,
    var f9: MSZ[F32],
    var f10: F64,
    var f11: MSZ[F64]
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(f1, f2, f3.toIS, f4.toIS, f4c, uf4.toIS, f4cu, f5.toIS, f5c, uf5.toIS, f5cu, f6.toIS, f6c, uf6.toIS, f6cu, f7.toIS, f7c, uf7.toIS, f7cu, f8, f9.toIS, f10, f11.toIS)

    def wellFormed: Z = {

      if (f3.size != 100) {
        return ERROR_Foo
      }

      if (f4.size != 4) {
        return ERROR_Foo
      }

      if (f4c != s8"10") {
        return ERROR_Foo
      }

      if (uf4.size != 4) {
        return ERROR_Foo
      }

      if (f4cu != u8"11") {
        return ERROR_Foo
      }

      if (f5.size != 5) {
        return ERROR_Foo
      }

      if (f5c != s16"12") {
        return ERROR_Foo
      }

      if (uf5.size != 5) {
        return ERROR_Foo
      }

      if (f5cu != u16"13") {
        return ERROR_Foo
      }

      if (f6.size != 6) {
        return ERROR_Foo
      }

      if (f6c != s32"14") {
        return ERROR_Foo
      }

      if (uf6.size != 6) {
        return ERROR_Foo
      }

      if (f6cu != u32"15") {
        return ERROR_Foo
      }

      if (f7.size != 7) {
        return ERROR_Foo
      }

      if (f7c != s64"16") {
        return ERROR_Foo
      }

      if (uf7.size != 7) {
        return ERROR_Foo
      }

      if (f7cu != u64"17") {
        return ERROR_Foo
      }

      if (f9.size != 2) {
        return ERROR_Foo
      }

      if (f11.size != 3) {
        return ERROR_Foo
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      f1 = Reader.IS.bleB(input, context)
      f2 = Reader.IS.leU7(input, context)
      Reader.IS.leBS(input, context, f3, 100)
      Reader.IS.leS8S(input, context, f4, 4)
      f4c = Reader.IS.leS8(input, context)
      Reader.IS.leU8S(input, context, uf4, 4)
      f4cu = Reader.IS.leU8(input, context)
      Reader.IS.leS16S(input, context, f5, 5)
      f5c = Reader.IS.leS16(input, context)
      Reader.IS.leU16S(input, context, uf5, 5)
      f5cu = Reader.IS.leU16(input, context)
      Reader.IS.leS32S(input, context, f6, 6)
      f6c = Reader.IS.leS32(input, context)
      Reader.IS.leU32S(input, context, uf6, 6)
      f6cu = Reader.IS.leU32(input, context)
      Reader.IS.leS64S(input, context, f7, 7)
      f7c = Reader.IS.leS64(input, context)
      Reader.IS.leU64S(input, context, uf7, 7)
      f7cu = Reader.IS.leU64(input, context)
      f8 = Reader.IS.leF32(input, context)
      Reader.IS.leF32S(input, context, f9, 2)
      f10 = Reader.IS.leF64(input, context)
      Reader.IS.leF64S(input, context, f11, 3)
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
      Writer.leS8S(output, context, f4)
      Writer.leS8(output, context, f4c)
      Writer.leU8S(output, context, uf4)
      Writer.leU8(output, context, f4cu)
      Writer.leS16S(output, context, f5)
      Writer.leS16(output, context, f5c)
      Writer.leU16S(output, context, uf5)
      Writer.leU16(output, context, f5cu)
      Writer.leS32S(output, context, f6)
      Writer.leS32(output, context, f6c)
      Writer.leU32S(output, context, uf6)
      Writer.leU32(output, context, f6cu)
      Writer.leS64S(output, context, f7)
      Writer.leS64(output, context, f7c)
      Writer.leU64S(output, context, uf7)
      Writer.leU64(output, context, f7cu)
      Writer.leF32(output, context, f8)
      Writer.leF32S(output, context, f9)
      Writer.leF64(output, context, f10)
      Writer.leF64S(output, context, f11)
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
  s8"10",
  MSZ(u8"0", u8"1", u8"2", u8"3"),
  u8"11",
  MSZ(s16"0", s16"1", s16"2", s16"3", s16"4"),
  s16"12",
  MSZ(u16"0", u16"1", u16"2", u16"3", u16"4"),
  u16"13",
  MSZ(s32"0", s32"1", s32"2", s32"3", s32"4", s32"5"),
  s32"14",
  MSZ(u32"0", u32"1", u32"2", u32"3", u32"4", u32"5"),
  u32"15",
  MSZ(s64"0", s64"1", s64"2", s64"3", s64"4", s64"5", s64"6"),
  s64"16",
  MSZ(u64"0", u64"1", u64"2", u64"3", u64"4", u64"5", u64"6"),
  u64"17",
  1.1f,
  MSZ(2.2f, 3.3f),
  4.4d,
  MSZ(5.5d, 6.6d, 7.7d)
)
println(s"fooExample = $fooExample")

assert(fooExample.wellFormed == 0, "fooExample is not well-formed!")

val fooExampleOutput = MSZ.create(2500, F)
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