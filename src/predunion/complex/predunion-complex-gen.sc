// #Sireum

import org.sireum._
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

  val ERROR_Baz: Z = 2

  val ERROR_Bazz: Z = 3

  val ERROR_Bazzz: Z = 4

  val ERROR_Bar: Z = 5

  val ERROR_Foo: Z = 6

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object Baz {

    val maxSize: Z = z"81"

    def empty: MBaz = {
      return MBaz(F, MSZ.create(2, u8"0"), u64"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Baz] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Baz]() else Some(r.toImmutable)
    }

  }

  @datatype class Baz(
    val flag: B,
    val bs: ISZ[U8],
    val l: U64
  ) extends Bar {

    @strictpure def toMutable: MBaz = MBaz(flag, bs.toMS, l)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(81, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MBaz(
    var flag: B,
    var bs: MSZ[U8],
    var l: U64
  ) extends MBar {

    @strictpure def toImmutable: Baz = Baz(flag, bs.toIS, l)

    def wellFormed: Z = {

      if (bs.size != 2) {
        return ERROR_Baz
      }

      // BEGIN USER CODE: Baz.wellFormed
      if (bs != MSZ(u8"1", u8"0")) {
        return ERROR_Baz
      }
      if (l != u64"7") {
        return ERROR_Baz
      }
      // END USER CODE: Baz.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      flag = Reader.IS.bleB(input, context)
      Reader.IS.beU8S(input, context, bs, 2)
      l = Reader.IS.beU64(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.beU8S(output, context, bs)
      Writer.beU64(output, context, l)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Baz)
      }
    }

  }

  object Bazz {

    val maxSize: Z = z"81"

    def empty: MBazz = {
      return MBazz(F, u16"0", MSZ.create(2, u32"0"))
    }

    def decode(input: ISZ[B], context: Context): Option[Bazz] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Bazz]() else Some(r.toImmutable)
    }

  }

  @datatype class Bazz(
    val flag: B,
    val s: U16,
    val is: ISZ[U32]
  ) extends Bar {

    @strictpure def toMutable: MBazz = MBazz(flag, s, is.toMS)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(81, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MBazz(
    var flag: B,
    var s: U16,
    var is: MSZ[U32]
  ) extends MBar {

    @strictpure def toImmutable: Bazz = Bazz(flag, s, is.toIS)

    def wellFormed: Z = {

      if (is.size != 2) {
        return ERROR_Bazz
      }

      // BEGIN USER CODE: Bazz.wellFormed
      if (s != u16"2") {
        return ERROR_Bazz
      }
      if (is != MSZ(u32"3", u32"5")) {
        return ERROR_Bazz
      }
      // END USER CODE: Bazz.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      flag = Reader.IS.bleB(input, context)
      s = Reader.IS.beU16(input, context)
      Reader.IS.beU32S(input, context, is, 2)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.beU16(output, context, s)
      Writer.beU32S(output, context, is)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Bazz)
      }
    }

  }

  object Bazzz {

    val maxSize: Z = z"17"

    def empty: MBazzz = {
      return MBazzz(F, u8"0", u8"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Bazzz] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Bazzz]() else Some(r.toImmutable)
    }

  }

  @datatype class Bazzz(
    val flag: B,
    val b1: U8,
    val b2: U8
  ) extends Bar {

    @strictpure def toMutable: MBazzz = MBazzz(flag, b1, b2)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(17, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MBazzz(
    var flag: B,
    var b1: U8,
    var b2: U8
  ) extends MBar {

    @strictpure def toImmutable: Bazzz = Bazzz(flag, b1, b2)

    def wellFormed: Z = {


      // BEGIN USER CODE: Bazzz.wellFormed
      if (flag) {
        return ERROR_Bazzz
      }
      if (!(b1 == u8"3" || b1 == u8"5")) {
        return ERROR_Bazzz
      }
      if (!(u8"10" <= b2 && b2 <= u8"15")) {
        return ERROR_Bazzz
      }
      // END USER CODE: Bazzz.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      flag = Reader.IS.bleB(input, context)
      b1 = Reader.IS.bleU8(input, context)
      b2 = Reader.IS.bleU8(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.bleU8(output, context, b1)
      Writer.bleU8(output, context, b2)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Bazzz)
      }
    }

  }

  @datatype trait Bar {
    @strictpure def toMutable: MBar
    def encode(context: Context): Option[ISZ[B]]
    def wellFormed: Z
  }

  @record trait MBar extends Runtime.Composite {
    @strictpure def toImmutable: Bar
  }

  object Bar {

    val maxSize: Z = z"81"

    def empty: MBar = {
      return Baz.empty
    }

    def decode(input: ISZ[B], context: Context): Option[Bar] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Bar]() else Some(r.toImmutable)
    }

    @enum object Choice {
       'Baz
       'Bazz
       'Bazzz
       'Error
    }

    def choose(input: ISZ[B], context: Context): Choice.Type = {
      {
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 1, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = MSZ.create(2, u8"0")
          Reader.IS.beU8S(input, ctx, temp, 2)
          hasError = !(ctx.errorCode == 0 && temp == MSZ(u8"1", u8"0"))
        }
        if (!hasError) {
          val temp = MSZ.create(1, u64"0")
          Reader.IS.beU64S(input, ctx, temp, 1)
          hasError = !(ctx.errorCode == 0 && temp == MSZ(u64"7"))
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Baz
        }
      }
      ;{
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 1, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = MSZ.create(1, u16"0")
          Reader.IS.beU16S(input, ctx, temp, 1)
          hasError = !(ctx.errorCode == 0 && temp == MSZ(u16"2"))
        }
        if (!hasError) {
          val temp = MSZ.create(2, u32"0")
          Reader.IS.beU32S(input, ctx, temp, 2)
          hasError = !(ctx.errorCode == 0 && temp == MSZ(u32"3", u32"5"))
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Bazz
        }
      }
      ;{
        var ctx = context
        var hasError = F
        if (!hasError) {
          if(!hasError) {
            hasError = !Reader.IS.bleB(input, ctx)
          }
          hasError = !hasError
        }
        if (!hasError) {
          val orCtx = ctx
          var found = F
          if (!found) {
            if (!hasError) {
              val temp = Reader.IS.bleU8(input, ctx)
              hasError = !(ctx.errorCode == 0 && temp == u8"3")
            }
            found = !hasError
            hasError = F
            if (!found) {
              ctx = orCtx
            }
          }
          if (!found) {
            if (!hasError) {
              val temp = Reader.IS.bleU8(input, ctx)
              hasError = !(ctx.errorCode == 0 && temp == u8"5")
            }
            found = !hasError
            hasError = F
            if (!found) {
              ctx = orCtx
            }
          }
          hasError = !found
        }
        if (!hasError) {
          val temp = Reader.IS.bleU8(input, ctx)
          hasError = !(ctx.errorCode == 0 && u8"10" <= temp && temp <= u8"15")
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Bazzz
        }
      }
      return Choice.Error
    }

  }

  object Foo {

    val maxSize: Z = z"81"

    def empty: MFoo = {
      return MFoo(Baz.empty)
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

  }

  @datatype class Foo(
    val bar: Bar
  ) {

    @strictpure def toMutable: MFoo = MFoo(bar.toMutable)

    def encode(context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(81, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var bar: MBar
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(bar.toImmutable)

    def wellFormed: Z = {


      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      Bar.choose(input, context) match {
        case Bar.Choice.Baz => bar = Baz.empty
        case Bar.Choice.Bazz => bar = Bazz.empty
        case Bar.Choice.Bazzz => bar = Bazzz.empty
        case _ => context.signalError(ERROR_Bar)
      }
      bar.decode(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      bar.encode(output, context)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

  }

}

// BEGIN USER CODE: Test
import BitCodec._
def test(fooExample: MFoo): Unit = {
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
  fooExampleDecoded.decode(fooExampleEncoded.toIS, fooExampleInputContext)
  println(s"decode(encode(fooExample)) = $fooExampleDecoded")
  println(s"decode(encode(fooExample)).offset = ${fooExampleInputContext.offset}")
  println(s"decode(encode(fooExample)).errorCode = ${fooExampleInputContext.errorCode}")
  println(s"decode(encode(fooExample)).errorOffset = ${fooExampleInputContext.errorOffset}")

  assert(fooExampleInputContext.errorCode == 0 && fooExampleInputContext.errorOffset == 0, "Decoding error!")
  assert(fooExampleOutputContext.offset == fooExampleInputContext.offset, "The decoder does not consume the same number of bits produced by the encoder!")
  assert(fooExample == fooExampleDecoded, s"$fooExample != $fooExampleDecoded")
  println()
}

test(MFoo(MBaz(T, MSZ(u8"1", u8"0"), u64"7")))
test(MFoo(MBazz(F, u16"2", MSZ(u32"3", u32"5"))))
test(MFoo(MBazzz(F, u8"3", u8"10")))
test(MFoo(MBazzz(F, u8"5", u8"15")))
// END USER CODE: Test