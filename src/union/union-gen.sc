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
      return Baz(F, F)
    }
  }

  @record class Baz(
    var b1: B,
    var b2: B
  ) extends Bar {

    def wellFormed: Z = {


      // BEGIN USER CODE: Baz.wellFormed
      // ... empty
      // END USER CODE: Baz.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      b1 = Reader.MS.bleB(input, context)
      b2 = Reader.MS.bleB(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, b1)
      Writer.bleB(output, context, b2)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Baz)
      }
    }

  }

  object Bazz {
    def empty: Bazz = {
      return Bazz(u4"0")
    }
  }

  @record class Bazz(
    var bazz: U4
  ) extends Bar {

    def wellFormed: Z = {


      // BEGIN USER CODE: Bazz.wellFormed
      // ... empty
      // END USER CODE: Bazz.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      bazz = Reader.MS.beU4(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU4(output, context, bazz)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Bazz)
      }
    }

  }

  @record trait Bar extends Runtime.Composite

  object Bar {

    @enum object Choice {
       'Baz
       'Bazz
       'Error
    }

    def choose(p: B): Choice.Type = {
      val r: Z = {
        if (p) 1 else 0
      }
      r match {
        case z"0" => return Choice.Baz
        case z"1" => return Choice.Bazz
        case _ =>
      }
      return Choice.Error
    }
  }

  object Foo {
    def empty: Foo = {
      return Foo(F, Baz.empty)
    }
  }

  @record class Foo(
    var flag: B,
    var bar: Bar
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      (Bar.choose(flag), bar) match {
        case (Bar.Choice.Baz, _: Baz) =>
        case (Bar.Choice.Bazz, _: Bazz) =>
        case _ => return ERROR_Bar
      }

      val wfBar = bar.wellFormed
      if (wfBar != 0) {
        return wfBar
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      Bar.choose(flag) match {
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
      Writer.bleB(output, context, flag)
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

test(Foo(F, Baz(F, T)))
test(Foo(T, Bazz(u4"7")))
// END USER CODE: Test