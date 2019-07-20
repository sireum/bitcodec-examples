// #Sireum

import org.sireum._
import org.sireum.U4._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Baz: Z = 2

  val ERROR_Bazz: Z = 3

  val ERROR_Bar: Z = 4

  val ERROR_Foo: Z = 5

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object Baz {
    def empty: Baz = {
      return Baz(F, F, F)
    }
  }

  @record class Baz(
    var flag: B,
    var b1: B,
    var b2: B
  ) extends Bar {

    def wellFormed: Z = {


      // BEGIN USER CODE: Baz.wellFormed
      if (!flag) {
        return ERROR_Baz
      }
      // END USER CODE: Baz.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      b1 = Reader.MS.bleB(input, context)
      b2 = Reader.MS.bleB(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.bleB(output, context, b1)
      Writer.bleB(output, context, b2)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Baz)
      }
    }

  }

  object Bazz {
    def empty: Bazz = {
      return Bazz(F, u4"0")
    }
  }

  @record class Bazz(
    var flag: B,
    var bazz: U4
  ) extends Bar {

    def wellFormed: Z = {


      // BEGIN USER CODE: Bazz.wellFormed
      if (flag) {
        return ERROR_Bazz
      }
      // END USER CODE: Bazz.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      bazz = Reader.MS.beU4(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, flag)
      Writer.beU4(output, context, bazz)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Bazz)
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
       'Error
    }

    def choose(input: MSZ[B], context: Context): Choice.Type = {
      {
        var ctx = context
        var hasError = F
        if(!hasError) {
          hasError = !Reader.MS.bleB(input, ctx)
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Baz
        }
      }
      ;{
        var ctx = context
        var hasError = F
        if(!hasError) {
          hasError = Reader.MS.bleB(input, ctx)
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Bazz
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
}

test(Foo(Bazz(F, u4"7")))
test(Foo(Baz(T, F, T)))
// END USER CODE: Test