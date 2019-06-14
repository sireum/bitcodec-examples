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

  @record trait Bar extends Runtime.Composite

  object Bar {

    object ChoiceContext {
      def empty: ChoiceContext = {
        // BEGIN USER CODE: Bar.ChoiceContext.empty
        return ChoiceContext(F)
        // END USER CODE: Bar.ChoiceContext.empty
      }
    }

    @record class ChoiceContext(
       // BEGIN USER CODE: Bar.ChoiceContext
       var flag: B
       // END USER CODE: Bar.ChoiceContext
    )

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

    @enum object Choice {
       'Baz
       'Bazz
       'Error
    }

    def choose(input: MSZ[B], context: Context, choiceContext: Bar.ChoiceContext): Choice.Type = {
      // BEGIN USER CODE: Bar.choose
      return if (choiceContext.flag) Choice.Bazz else Choice.Baz
      // END USER CODE: Bar.choose
    }
  }

  object Foo {
    def empty: Foo = {
      return Foo(F, Bar.Baz.empty)
    }
  }

  @record class Foo(
    var flag: B,
    var bar: Bar
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      val wfBar = bar.wellFormed
      if (wfBar != 0) {
        return wfBar
      }

      // BEGIN USER CODE: Foo.wellFormed
      (flag, bar) match {
        case (F, _: Bar.Baz) =>
        case (T, _: Bar.Bazz) =>
        case _ => return ERROR_Bar
      }
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      flag = Reader.MS.bleB(input, context)
      val barChoiceContext = Bar.ChoiceContext.empty
      // BEGIN USER CODE: Bar.ChoiceContext.init
      barChoiceContext.flag = flag
      // END USER CODE: Bar.ChoiceContext.init
      Bar.choose(input, context, barChoiceContext) match {
        case Bar.Choice.Baz => bar = Bar.Baz.empty
        case Bar.Choice.Bazz => bar = Bar.Bazz.empty
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
;{
  val fooExample = Foo(T, Bar.Bazz(u4"7"))
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
println()
;{
  val fooExample = Foo(F, Bar.Baz(F, T))
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
// END USER CODE: Test