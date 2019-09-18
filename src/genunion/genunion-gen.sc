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

    val maxSize: Z = z"2"

    def empty: MBaz = {
      return MBaz(F, F)
    }

    def decode(input: ISZ[B], context: Context): Option[Baz] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[Baz]() else Some(r.toImmutable)
    }

  }

  @datatype class Baz(
    val b1: B,
    val b2: B
  ) extends Bar {

    @strictpure def toMutable: MBaz = MBaz(b1, b2)

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MBaz(
    var b1: B,
    var b2: B
  ) extends MBar {

    @strictpure def toImmutable: Baz = Baz(b1, b2)

    def wellFormed: Z = {


      // BEGIN USER CODE: Baz.wellFormed

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

    val maxSize: Z = z"4"

    def empty: MBazz = {
      return MBazz(u4"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Bazz] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[Bazz]() else Some(r.toImmutable)
    }

  }

  @datatype class Bazz(
    val bazz: U4
  ) extends Bar {

    @strictpure def toMutable: MBazz = MBazz(bazz)

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MBazz(
    var bazz: U4
  ) extends MBar {

    @strictpure def toImmutable: Bazz = Bazz(bazz)

    def wellFormed: Z = {


      // BEGIN USER CODE: Bazz.wellFormed

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

  @datatype trait Bar {
    @strictpure def toMutable: MBar
    def encode(output: MSZ[B], context: Context): Unit
    def wellFormed: Z
  }

  @record trait MBar extends Runtime.Composite {
    @strictpure def toImmutable: Bar
  }

  object Bar {

    val maxSize: Z = z"4"

    def empty: MBar = {
      return Baz.empty
    }

    def decode(input: ISZ[B], context: Context): Option[Bar] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[Bar]() else Some(r.toImmutable)
    }

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

    val maxSize: Z = z"5"

    def empty: MFoo = {
      return MFoo(F, Baz.empty)
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

  }

  @datatype class Foo(
    val flag: B,
    val bar: Bar
  ) {

    @strictpure def toMutable: MFoo = MFoo(flag, bar.toMutable)

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var flag: B,
    var bar: MBar
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(flag, bar.toImmutable)

    def wellFormed: Z = {

      val wfBar = bar.wellFormed
      if (wfBar != 0) {
        return wfBar
      }

      // BEGIN USER CODE: Foo.wellFormed
      (flag, bar) match {
        case (F, _: MBaz) =>
        case (T, _: MBazz) =>
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

def test(fooExample: MFoo): Unit = {
  println(s"fooExample = $fooExample")

  println(fooExample.wellFormed)
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

test(MFoo(F, MBaz(F, T)))
test(MFoo(T, MBazz(u4"7")))
// END USER CODE: Test