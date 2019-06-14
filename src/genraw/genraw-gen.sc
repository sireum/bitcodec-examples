// #Sireum

import org.sireum._
import org.sireum.U8._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Foo_elements: Z = 2

  val ERROR_Foo: Z = 3

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

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
    def empty: Foo = {
      return Foo(u8"0", MSZ[B]())
    }
  }

  @record class Foo(
    var size: U8,
    var elements: MSZ[B]
  ) extends Runtime.Composite {

    def wellFormed: Z = {


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
        elements = elements :+ Reader.MS.bleB(input, context)
        elementsUpdate(input, context, elementsContext)
      }

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8(output, context, size)
      Writer.bleRaw(output, context, elements, elements.size)

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
val fooExample = Foo(u8"3", MSZ(T, F, T))
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