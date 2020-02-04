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

    val maxSize: Z = z"3223"

    def empty: MFoo = {
      return MFoo(F, u7"0", MSZ.create(100, F), MSZ.create(4, s8"0"), s8"0", s8"0", MSZ.create(2, s8"0"), MSZ.create(4, u8"0"), u8"0", u8"0", MSZ.create(2, u8"0"), MSZ.create(5, s16"0"), s16"0", s16"0", MSZ.create(2, s16"0"), MSZ.create(5, u16"0"), u16"0", u16"0", MSZ.create(2, u16"0"), MSZ.create(6, s32"0"), s32"0", s32"0", MSZ.create(2, s32"0"), MSZ.create(6, u32"0"), u32"0", u32"0", MSZ.create(2, u32"0"), MSZ.create(7, s64"0"), s64"0", s64"0", MSZ.create(2, s64"0"), MSZ.create(7, u64"0"), u64"0", u64"0", MSZ.create(2, u64"0"), 0.0f, 0.0f, MSZ.create(2, 0.0f), MSZ.create(2, 0.0f), 0.0d, 0.0d, MSZ.create(3, 0.0d), MSZ.create(2, 0.0d))
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
    val f4r: S8,
    val f4rs: ISZ[S8],
    val uf4: ISZ[U8],
    val f4cu: U8,
    val f4ru: U8,
    val f4rus: ISZ[U8],
    val f5: ISZ[S16],
    val f5c: S16,
    val f5r: S16,
    val f5rs: ISZ[S16],
    val uf5: ISZ[U16],
    val f5cu: U16,
    val f5ru: U16,
    val f5rus: ISZ[U16],
    val f6: ISZ[S32],
    val f6c: S32,
    val f6r: S32,
    val f6rs: ISZ[S32],
    val uf6: ISZ[U32],
    val f6cu: U32,
    val f6ru: U32,
    val f6rus: ISZ[U32],
    val f7: ISZ[S64],
    val f7c: S64,
    val f7r: S64,
    val f7rs: ISZ[S64],
    val uf7: ISZ[U64],
    val f7cu: U64,
    val f7ru: U64,
    val f7rus: ISZ[U64],
    val f8: F32,
    val f8r: F32,
    val f9: ISZ[F32],
    val f9rs: ISZ[F32],
    val f10: F64,
    val f10r: F64,
    val f11: ISZ[F64],
    val f10rs: ISZ[F64]
  ) {

    @strictpure def toMutable: MFoo = MFoo(f1, f2, f3.toMS, f4.toMS, f4c, f4r, f4rs.toMS, uf4.toMS, f4cu, f4ru, f4rus.toMS, f5.toMS, f5c, f5r, f5rs.toMS, uf5.toMS, f5cu, f5ru, f5rus.toMS, f6.toMS, f6c, f6r, f6rs.toMS, uf6.toMS, f6cu, f6ru, f6rus.toMS, f7.toMS, f7c, f7r, f7rs.toMS, uf7.toMS, f7cu, f7ru, f7rus.toMS, f8, f8r, f9.toMS, f9rs.toMS, f10, f10r, f11.toMS, f10rs.toMS)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(3223, F)
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
    var f4r: S8,
    var f4rs: MSZ[S8],
    var uf4: MSZ[U8],
    var f4cu: U8,
    var f4ru: U8,
    var f4rus: MSZ[U8],
    var f5: MSZ[S16],
    var f5c: S16,
    var f5r: S16,
    var f5rs: MSZ[S16],
    var uf5: MSZ[U16],
    var f5cu: U16,
    var f5ru: U16,
    var f5rus: MSZ[U16],
    var f6: MSZ[S32],
    var f6c: S32,
    var f6r: S32,
    var f6rs: MSZ[S32],
    var uf6: MSZ[U32],
    var f6cu: U32,
    var f6ru: U32,
    var f6rus: MSZ[U32],
    var f7: MSZ[S64],
    var f7c: S64,
    var f7r: S64,
    var f7rs: MSZ[S64],
    var uf7: MSZ[U64],
    var f7cu: U64,
    var f7ru: U64,
    var f7rus: MSZ[U64],
    var f8: F32,
    var f8r: F32,
    var f9: MSZ[F32],
    var f9rs: MSZ[F32],
    var f10: F64,
    var f10r: F64,
    var f11: MSZ[F64],
    var f10rs: MSZ[F64]
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(f1, f2, f3.toIS, f4.toIS, f4c, f4r, f4rs.toIS, uf4.toIS, f4cu, f4ru, f4rus.toIS, f5.toIS, f5c, f5r, f5rs.toIS, uf5.toIS, f5cu, f5ru, f5rus.toIS, f6.toIS, f6c, f6r, f6rs.toIS, uf6.toIS, f6cu, f6ru, f6rus.toIS, f7.toIS, f7c, f7r, f7rs.toIS, uf7.toIS, f7cu, f7ru, f7rus.toIS, f8, f8r, f9.toIS, f9rs.toIS, f10, f10r, f11.toIS, f10rs.toIS)

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

      if (f4r < s8"-20" || f4r > s8"-10") {
        return ERROR_Foo
      }

      if (f4rs.size != 2) {
        return ERROR_Foo
      }

      for (f4rsElement <- f4rs) {
        if (f4rsElement < s8"-20" || f4rsElement > s8"-10") {
          return ERROR_Foo
        }
      }

      if (uf4.size != 4) {
        return ERROR_Foo
      }

      if (f4cu != u8"11") {
        return ERROR_Foo
      }

      if (f4ru < u8"10" || f4ru > u8"20") {
        return ERROR_Foo
      }

      if (f4rus.size != 2) {
        return ERROR_Foo
      }

      for (f4rusElement <- f4rus) {
        if (f4rusElement < u8"10" || f4rusElement > u8"20") {
          return ERROR_Foo
        }
      }

      if (f5.size != 5) {
        return ERROR_Foo
      }

      if (f5c != s16"12") {
        return ERROR_Foo
      }

      if (f5r < s16"-200" || f5r > s16"-100") {
        return ERROR_Foo
      }

      if (f5rs.size != 2) {
        return ERROR_Foo
      }

      for (f5rsElement <- f5rs) {
        if (f5rsElement < s16"-200" || f5rsElement > s16"-100") {
          return ERROR_Foo
        }
      }

      if (uf5.size != 5) {
        return ERROR_Foo
      }

      if (f5cu != u16"13") {
        return ERROR_Foo
      }

      if (f5ru < u16"100" || f5ru > u16"200") {
        return ERROR_Foo
      }

      if (f5rus.size != 2) {
        return ERROR_Foo
      }

      for (f5rusElement <- f5rus) {
        if (f5rusElement < u16"100" || f5rusElement > u16"200") {
          return ERROR_Foo
        }
      }

      if (f6.size != 6) {
        return ERROR_Foo
      }

      if (f6c != s32"14") {
        return ERROR_Foo
      }

      if (f6r < s32"-2000" || f6r > s32"-1000") {
        return ERROR_Foo
      }

      if (f6rs.size != 2) {
        return ERROR_Foo
      }

      for (f6rsElement <- f6rs) {
        if (f6rsElement < s32"-2000" || f6rsElement > s32"-1000") {
          return ERROR_Foo
        }
      }

      if (uf6.size != 6) {
        return ERROR_Foo
      }

      if (f6cu != u32"15") {
        return ERROR_Foo
      }

      if (f6ru < u32"1000" || f6ru > u32"2000") {
        return ERROR_Foo
      }

      if (f6rus.size != 2) {
        return ERROR_Foo
      }

      for (f6rusElement <- f6rus) {
        if (f6rusElement < u32"1000" || f6rusElement > u32"2000") {
          return ERROR_Foo
        }
      }

      if (f7.size != 7) {
        return ERROR_Foo
      }

      if (f7c != s64"16") {
        return ERROR_Foo
      }

      if (f7r < s64"-20000" || f7r > s64"-10000") {
        return ERROR_Foo
      }

      if (f7rs.size != 2) {
        return ERROR_Foo
      }

      for (f7rsElement <- f7rs) {
        if (f7rsElement < s64"-20000" || f7rsElement > s64"-10000") {
          return ERROR_Foo
        }
      }

      if (uf7.size != 7) {
        return ERROR_Foo
      }

      if (f7cu != u64"17") {
        return ERROR_Foo
      }

      if (f7ru < u64"10000" || f7ru > u64"20000") {
        return ERROR_Foo
      }

      if (f7rus.size != 2) {
        return ERROR_Foo
      }

      for (f7rusElement <- f7rus) {
        if (f7rusElement < u64"10000" || f7rusElement > u64"20000") {
          return ERROR_Foo
        }
      }

      if (f8r < -100.0f || f8r > 100.0f) {
        return ERROR_Foo
      }

      if (f9.size != 2) {
        return ERROR_Foo
      }

      if (f9rs.size != 2) {
        return ERROR_Foo
      }

      for (f9rsElement <- f9rs) {
        if (f9rsElement < -100.0f || f9rsElement > 100.0f) {
          return ERROR_Foo
        }
      }

      if (f10r < -1000.0d || f10r > 1000.0d) {
        return ERROR_Foo
      }

      if (f11.size != 3) {
        return ERROR_Foo
      }

      if (f10rs.size != 2) {
        return ERROR_Foo
      }

      for (f10rsElement <- f10rs) {
        if (f10rsElement < -1000.0d || f10rsElement > 1000.0d) {
          return ERROR_Foo
        }
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
      f4c = Reader.IS.beS8(input, context)
      f4r = Reader.IS.beS8(input, context)
      Reader.IS.beS8S(input, context, f4rs, 2)
      Reader.IS.beU8S(input, context, uf4, 4)
      f4cu = Reader.IS.beU8(input, context)
      f4ru = Reader.IS.beU8(input, context)
      Reader.IS.beU8S(input, context, f4rus, 2)
      Reader.IS.beS16S(input, context, f5, 5)
      f5c = Reader.IS.beS16(input, context)
      f5r = Reader.IS.beS16(input, context)
      Reader.IS.beS16S(input, context, f5rs, 2)
      Reader.IS.beU16S(input, context, uf5, 5)
      f5cu = Reader.IS.beU16(input, context)
      f5ru = Reader.IS.beU16(input, context)
      Reader.IS.beU16S(input, context, f5rus, 2)
      Reader.IS.beS32S(input, context, f6, 6)
      f6c = Reader.IS.beS32(input, context)
      f6r = Reader.IS.beS32(input, context)
      Reader.IS.beS32S(input, context, f6rs, 2)
      Reader.IS.beU32S(input, context, uf6, 6)
      f6cu = Reader.IS.beU32(input, context)
      f6ru = Reader.IS.beU32(input, context)
      Reader.IS.beU32S(input, context, f6rus, 2)
      Reader.IS.beS64S(input, context, f7, 7)
      f7c = Reader.IS.beS64(input, context)
      f7r = Reader.IS.beS64(input, context)
      Reader.IS.beS64S(input, context, f7rs, 2)
      Reader.IS.beU64S(input, context, uf7, 7)
      f7cu = Reader.IS.beU64(input, context)
      f7ru = Reader.IS.beU64(input, context)
      Reader.IS.beU64S(input, context, f7rus, 2)
      f8 = Reader.IS.beF32(input, context)
      f8r = Reader.IS.beF32(input, context)
      Reader.IS.beF32S(input, context, f9, 2)
      Reader.IS.beF32S(input, context, f9rs, 2)
      f10 = Reader.IS.beF64(input, context)
      f10r = Reader.IS.beF64(input, context)
      Reader.IS.beF64S(input, context, f11, 3)
      Reader.IS.beF64S(input, context, f10rs, 2)
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
      Writer.beS8(output, context, f4c)
      Writer.beS8(output, context, f4r)
      Writer.beS8S(output, context, f4rs)
      Writer.beU8S(output, context, uf4)
      Writer.beU8(output, context, f4cu)
      Writer.beU8(output, context, f4ru)
      Writer.beU8S(output, context, f4rus)
      Writer.beS16S(output, context, f5)
      Writer.beS16(output, context, f5c)
      Writer.beS16(output, context, f5r)
      Writer.beS16S(output, context, f5rs)
      Writer.beU16S(output, context, uf5)
      Writer.beU16(output, context, f5cu)
      Writer.beU16(output, context, f5ru)
      Writer.beU16S(output, context, f5rus)
      Writer.beS32S(output, context, f6)
      Writer.beS32(output, context, f6c)
      Writer.beS32(output, context, f6r)
      Writer.beS32S(output, context, f6rs)
      Writer.beU32S(output, context, uf6)
      Writer.beU32(output, context, f6cu)
      Writer.beU32(output, context, f6ru)
      Writer.beU32S(output, context, f6rus)
      Writer.beS64S(output, context, f7)
      Writer.beS64(output, context, f7c)
      Writer.beS64(output, context, f7r)
      Writer.beS64S(output, context, f7rs)
      Writer.beU64S(output, context, uf7)
      Writer.beU64(output, context, f7cu)
      Writer.beU64(output, context, f7ru)
      Writer.beU64S(output, context, f7rus)
      Writer.beF32(output, context, f8)
      Writer.beF32(output, context, f8r)
      Writer.beF32S(output, context, f9)
      Writer.beF32S(output, context, f9rs)
      Writer.beF64(output, context, f10)
      Writer.beF64(output, context, f10r)
      Writer.beF64S(output, context, f11)
      Writer.beF64S(output, context, f10rs)
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
  s8"-15",
  MSZ(s8"-16", s8"-17"),
  MSZ(u8"0", u8"1", u8"2", u8"3"),
  u8"11",
  u8"15",
  MSZ(u8"16", u8"17"),
  MSZ(s16"0", s16"1", s16"2", s16"3", s16"4"),
  s16"12",
  s16"-150",
  MSZ(s16"-160", s16"-170"),
  MSZ(u16"0", u16"1", u16"2", u16"3", u16"4"),
  u16"13",
  u16"150",
  MSZ(u16"160", u16"170"),
  MSZ(s32"0", s32"1", s32"2", s32"3", s32"4", s32"5"),
  s32"14",
  s32"-1500",
  MSZ(s32"-1600", s32"-1700"),
  MSZ(u32"0", u32"1", u32"2", u32"3", u32"4", u32"5"),
  u32"15",
  u32"1500",
  MSZ(u32"1600", u32"1700"),
  MSZ(s64"0", s64"1", s64"2", s64"3", s64"4", s64"5", s64"6"),
  s64"16",
  s64"-15000",
  MSZ(s64"-16000", s64"-17000"),
  MSZ(u64"0", u64"1", u64"2", u64"3", u64"4", u64"5", u64"6"),
  u64"17",
  u64"15000",
  MSZ(u64"16000", u64"17000"),
  1.1f,
  10.1f,
  MSZ(2.2f, 3.3f),
  MSZ(40.4f, 50.5f),
  4.4d,
  50.5d,
  MSZ(5.5d, 6.6d, 7.7d),
  MSZ(500.5d, 600.6d)
)
println(s"fooExample = $fooExample")

assert(fooExample.wellFormed == 0, "fooExample is not well-formed!")

val fooExampleOutput = MSZ.create(3250, F)
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