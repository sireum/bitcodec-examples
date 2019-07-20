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
    def empty: Baz = {
      return Baz(F, MSZ.create(2, u8"0"), MSZ.create(1, u64"0"))
    }
  }

  @record class Baz(
    var flag: B,
    var bs: MSZ[U8],
    var l: MSZ[U64]
  ) extends Bar {

    def wellFormed: Z = {

      if (bs.size != 2) {
        return ERROR_Baz
      }

      if (l.size != 1) {
        return ERROR_Baz
      }

      // BEGIN USER CODE: Baz.wellFormed
      if (bs != MSZ(u8"1", u8"0")) {
        return ERROR_Baz
      }
      if (l != MSZ(u64"7")) {
        return ERROR_Baz
      }
      // END USER CODE: Baz.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      Reader.MS.beU8S(input, context, bs, 2)
      Reader.MS.beU64S(input, context, l, 1)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.beU8S(output, context, bs)
      Writer.beU64S(output, context, l)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Baz)
      }
    }

  }

  object Bazz {
    def empty: Bazz = {
      return Bazz(F, MSZ.create(1, u16"0"), MSZ.create(2, u32"0"))
    }
  }

  @record class Bazz(
    var flag: B,
    var s: MSZ[U16],
    var is: MSZ[U32]
  ) extends Bar {

    def wellFormed: Z = {

      if (s.size != 1) {
        return ERROR_Bazz
      }

      if (is.size != 2) {
        return ERROR_Bazz
      }

      // BEGIN USER CODE: Bazz.wellFormed
      if (s != MSZ(u16"2")) {
        return ERROR_Bazz
      }
      if (is != MSZ(u32"3", u32"5")) {
        return ERROR_Bazz
      }
      // END USER CODE: Bazz.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      Reader.MS.beU16S(input, context, s, 1)
      Reader.MS.beU32S(input, context, is, 2)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.beU16S(output, context, s)
      Writer.beU32S(output, context, is)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Bazz)
      }
    }

  }

  object Bazzz {
    def empty: Bazzz = {
      return Bazzz(F, u8"0", u8"0")
    }
  }

  @record class Bazzz(
    var flag: B,
    var b1: U8,
    var b2: U8
  ) extends Bar {

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

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      b1 = Reader.MS.beU8(input, context)
      b2 = Reader.MS.beU8(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.beU8(output, context, b1)
      Writer.beU8(output, context, b2)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Bazzz)
      }
    }

  }

  @record trait Bar extends Runtime.Composite

  object Bar {

    def empty: Bar = {
      return Baz.empty
    }

    @enum object Choice {
       'Baz
       'Bazz
       'Bazzz
       'Error
    }

    def choose(input: MSZ[B], context: Context): Choice.Type = {
      {
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 1, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = MSZ.create(2, u8"0")
          Reader.MS.beU8S(input, ctx, temp, 2)
          hasError = !(ctx.errorCode == 0 && temp == MSZ(u8"1", u8"0"))
        }
        if (!hasError) {
          val temp = MSZ.create(1, u64"0")
          Reader.MS.beU64S(input, ctx, temp, 1)
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
          Reader.MS.beU16S(input, ctx, temp, 1)
          hasError = !(ctx.errorCode == 0 && temp == MSZ(u16"2"))
        }
        if (!hasError) {
          val temp = MSZ.create(2, u32"0")
          Reader.MS.beU32S(input, ctx, temp, 2)
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
            hasError = !Reader.MS.bleB(input, ctx)
          }
          hasError = !hasError
        }
        if (!hasError) {
          val orCtx = ctx
          var found = F
          if (!found) {
            if (!hasError) {
              val temp = Reader.MS.beU8(input, ctx)
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
              val temp = Reader.MS.beU8(input, ctx)
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
          val temp = Reader.MS.beU8(input, ctx)
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
    def empty: Foo = {
      return Foo(Baz.empty)
    }
  }

  @record class Foo(
    var bar: Bar
  ) extends Runtime.Composite {

    def wellFormed: Z = {


      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
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
def test(fooExample: Foo): Unit = {
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
  println()
}

test(Foo(Baz(T, MSZ(u8"1", u8"0"), MSZ(u64"7"))))
test(Foo(Bazz(F, MSZ(u16"2"), MSZ(u32"3", u32"5"))))
test(Foo(Bazzz(F, u8"3", u8"10")))
test(Foo(Bazzz(F, u8"5", u8"15")))
// END USER CODE: Test