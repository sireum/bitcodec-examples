// #Sireum

import org.sireum._
import org.sireum.U8._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_SixBytes: Z = 2

  val ERROR_Foo_elements: Z = 3

  val ERROR_Foo: Z = 4

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object SixBytes {

    val maxSize: Z = z"48"

    def empty: MSixBytes = {
      return MSixBytes(MSZ.create(6, u8"0"))
    }

    def decode(input: ISZ[B], context: Context): Option[SixBytes] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[SixBytes]() else Some(r.toImmutable)
    }

  }

  @datatype class SixBytes(
    val sixBytes: ISZ[U8]
  ) {

    @strictpure def toMutable: MSixBytes = MSixBytes(sixBytes.toMS)

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MSixBytes(
    var sixBytes: MSZ[U8]
  ) extends Runtime.Composite {

    @strictpure def toImmutable: SixBytes = SixBytes(sixBytes.toIS)

    def wellFormed: Z = {

      if (sixBytes.size != 6) {
        return ERROR_SixBytes
      }

      // BEGIN USER CODE: SixBytes.wellFormed
      // ... empty
      // END USER CODE: SixBytes.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, sixBytes, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, sixBytes)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_SixBytes)
      }
    }

  }

  object FooElementsContext {
    def empty: FooElementsContext = {
      // BEGIN USER CODE: FooElementsContext.empty
      return FooElementsContext(u8"0")
      // END USER CODE: FooElementsContext.empty
    }
  }

  @record class FooElementsContext(
    // BEGIN USER CODE: FooElementsContext
    var i: U8
    // END USER CODE: FooElementsContext
  )

  object Foo {

    val maxSize: Z = z"488"

    def empty: MFoo = {
      return MFoo(u8"0", MSZ[MSixBytes]())
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

    def toMutableElements(s: ISZ[SixBytes]): MSZ[MSixBytes] = {
      var r = MSZ[MSixBytes]()
      for (e <- s) {
        r = r :+ e.toMutable
      }
      return r
    }

    def toImmutableElements(s: MSZ[MSixBytes]): ISZ[SixBytes] = {
      var r = ISZ[SixBytes]()
      for (e <- s) {
        r = r :+ e.toImmutable
      }
      return r
    }
  }

  @datatype class Foo(
    val size: U8,
    val elements: ISZ[SixBytes]
  ) {

    @strictpure def toMutable: MFoo = MFoo(size, Foo.toMutableElements(elements))

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var size: U8,
    var elements: MSZ[MSixBytes]
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(size, Foo.toImmutableElements(elements))

    def wellFormed: Z = {

      if (elements.size > 10) {
        return ERROR_Foo_elements
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      size = Reader.MS.beU8(input, context)
      elements = MSZ()
      val elementsContext = FooElementsContext.empty
      // BEGIN USER CODE: FooElementsContext.init
      // ... empty
      // END USER CODE: FooElementsContext.init
      while (elementsContinue(input, context, elementsContext)) {
        val o = SixBytes.empty
        o.decode(input, context)
        elements = elements :+ o
        elementsUpdate(input, context, elementsContext)
      }

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8(output, context, size)
      for (i <- 0 until elements.size) {
        elements(i).encode(output, context)
      }

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

    def elementsContinue(input: MSZ[B], context: Context, elementsContext: FooElementsContext): B = {
      // BEGIN USER CODE: Foo.elementsContinue
      return elementsContext.i < size
      // END USER CODE: Foo.elementsContinue
    }

    def elementsUpdate(input: MSZ[B], context: Context, elementsContext: FooElementsContext): Unit = {
      // BEGIN USER CODE: Foo.elementsUpdate
      elementsContext.i = elementsContext.i + u8"1"
      // END USER CODE: Foo.elementsUpdate
    }
  }

}

// BEGIN USER CODE: Test
import BitCodec._

val fooExample = MFoo(u8"3", MSZ(
  MSixBytes(MSZ.create(6, u8"1")),
  MSixBytes(MSZ.create(6, u8"2")),
  MSixBytes(MSZ.create(6, u8"3"))
))
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