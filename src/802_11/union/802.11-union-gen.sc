// #Sireum

import org.sireum._
import org.sireum.U1._
import org.sireum.U2._
import org.sireum.U4._
import org.sireum.U8._
import org.sireum.U12._
import org.sireum.U32._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Frame: Z = 2

  val ERROR_FrameControl: Z = 3

  val ERROR_Receiver: Z = 4

  val ERROR_ReceiverTransmitter: Z = 5

  val ERROR_SeqControl: Z = 6

  val ERROR_Data: Z = 7

  val ERROR_HeaderAddress: Z = 8

  val ERROR_MacHeader: Z = 9

  val ERROR_MacFrame_body: Z = 10

  val ERROR_MacFrame: Z = 11

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  @enum object Frame {
    'Management
    'Control
    'Data
    'Reserved
  }

  def decodeFrame(input: MSZ[B], context: Context): Frame.Type = {
    if (context.offset + 2 > input.size) {
      context.signalError(ERROR_Frame)
    }
    if (context.hasError) {
      return Frame.Management
    }
    val r: Frame.Type = Reader.MS.bleU2(input, context) match {
      case u2"0" => Frame.Management
      case u2"1" => Frame.Control
      case u2"2" => Frame.Data
      case u2"3" => Frame.Reserved
      case _ =>
        context.signalError(ERROR_Frame)
        Frame.Management
    }
    return r
  }

  def encodeFrame(output: MSZ[B], context: Context, tpe: Frame.Type): Unit = {
    if (context.offset + 2 > output.size) {
      context.signalError(ERROR_Frame)
    }
    if (context.hasError) {
      return
    }
    tpe match {
      case Frame.Management => Writer.bleU2(output, context, u2"0")
      case Frame.Control => Writer.bleU2(output, context, u2"1")
      case Frame.Data => Writer.bleU2(output, context, u2"2")
      case Frame.Reserved => Writer.bleU2(output, context, u2"3")
    }
  }

  object FrameControl {

    val maxSize: Z = z"16"

    def empty: MFrameControl = {
      return MFrameControl(u2"0", Frame.Management, u4"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0")
    }

    def decode(input: MSZ[B], context: Context): Option[FrameControl] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[FrameControl]() else Some(r.toImmutable)
    }

  }

  @datatype class FrameControl(
    val protocol: U2,
    val tpe: Frame.Type,
    val subType: U4,
    val toDS: U1,
    val fromDS: U1,
    val moreFrag: U1,
    val retry: U1,
    val powerMgmt: U1,
    val moreData: U1,
    val wep: U1,
    val order: U1
  ) {

    @strictpure def toMutable: MFrameControl = MFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFrameControl(
    var protocol: U2,
    var tpe: Frame.Type,
    var subType: U4,
    var toDS: U1,
    var fromDS: U1,
    var moreFrag: U1,
    var retry: U1,
    var powerMgmt: U1,
    var moreData: U1,
    var wep: U1,
    var order: U1
  ) extends Runtime.MComposite {

    def toImmutable: FrameControl = {
      return FrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)
    }

    def wellFormed: Z = {


      // BEGIN USER CODE: FrameControl.wellFormed
      tpe match {
        case Frame.Management if (u4"0" <= subType && subType <= u4"5") || (u4"8" <= subType && subType <= u4"12") =>
        case Frame.Control if u4"10" <= subType && subType <= u4"15" =>
        case Frame.Data if (u4"0" <= subType && subType <= u4"2") || (u4"5" <= subType && subType <= u4"7") =>
        case Frame.Reserved =>
        case _ => return ERROR_FrameControl
      }
      // ... put more checks here
      // END USER CODE: FrameControl.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      protocol = Reader.MS.bleU2(input, context)
      tpe = decodeFrame(input, context)
      subType = Reader.MS.bleU4(input, context)
      toDS = Reader.MS.bleU1(input, context)
      fromDS = Reader.MS.bleU1(input, context)
      moreFrag = Reader.MS.bleU1(input, context)
      retry = Reader.MS.bleU1(input, context)
      powerMgmt = Reader.MS.bleU1(input, context)
      moreData = Reader.MS.bleU1(input, context)
      wep = Reader.MS.bleU1(input, context)
      order = Reader.MS.bleU1(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU2(output, context, protocol)
      encodeFrame(output, context, tpe)
      Writer.bleU4(output, context, subType)
      Writer.bleU1(output, context, toDS)
      Writer.bleU1(output, context, fromDS)
      Writer.bleU1(output, context, moreFrag)
      Writer.bleU1(output, context, retry)
      Writer.bleU1(output, context, powerMgmt)
      Writer.bleU1(output, context, moreData)
      Writer.bleU1(output, context, wep)
      Writer.bleU1(output, context, order)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_FrameControl)
      }
    }

  }

  object Receiver {

    val maxSize: Z = z"48"

    def empty: MReceiver = {
      return MReceiver(MSZ.create(6, u8"0"))
    }

    def decode(input: MSZ[B], context: Context): Option[Receiver] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Receiver]() else Some(r.toImmutable)
    }

  }

  @datatype class Receiver(
    val receiver: ISZ[U8]
  ) extends HeaderAddress {

    @strictpure def toMutable: MReceiver = MReceiver(receiver.toMS)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MReceiver(
    var receiver: MSZ[U8]
  ) extends MHeaderAddress {

    def toImmutable: Receiver = {
      return Receiver(receiver.toIS)
    }

    def wellFormed: Z = {

      if (receiver.size != 6) {
        return ERROR_Receiver
      }

      // BEGIN USER CODE: Receiver.wellFormed
      // ... empty
      // END USER CODE: Receiver.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, receiver, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, receiver)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Receiver)
      }
    }

  }

  object ReceiverTransmitter {

    val maxSize: Z = z"96"

    def empty: MReceiverTransmitter = {
      return MReceiverTransmitter(MSZ.create(6, u8"0"), MSZ.create(6, u8"0"))
    }

    def decode(input: MSZ[B], context: Context): Option[ReceiverTransmitter] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[ReceiverTransmitter]() else Some(r.toImmutable)
    }

  }

  @datatype class ReceiverTransmitter(
    val receiver: ISZ[U8],
    val transmitter: ISZ[U8]
  ) extends HeaderAddress {

    @strictpure def toMutable: MReceiverTransmitter = MReceiverTransmitter(receiver.toMS, transmitter.toMS)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MReceiverTransmitter(
    var receiver: MSZ[U8],
    var transmitter: MSZ[U8]
  ) extends MHeaderAddress {

    def toImmutable: ReceiverTransmitter = {
      return ReceiverTransmitter(receiver.toIS, transmitter.toIS)
    }

    def wellFormed: Z = {

      if (receiver.size != 6) {
        return ERROR_ReceiverTransmitter
      }

      if (transmitter.size != 6) {
        return ERROR_ReceiverTransmitter
      }

      // BEGIN USER CODE: ReceiverTransmitter.wellFormed
      // ... empty
      // END USER CODE: ReceiverTransmitter.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, receiver, 6)
      Reader.MS.beU8S(input, context, transmitter, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, receiver)
      Writer.beU8S(output, context, transmitter)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_ReceiverTransmitter)
      }
    }

  }

  object SeqControl {

    val maxSize: Z = z"16"

    def empty: MSeqControl = {
      return MSeqControl(u4"0", u12"0")
    }

    def decode(input: MSZ[B], context: Context): Option[SeqControl] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[SeqControl]() else Some(r.toImmutable)
    }

  }

  @datatype class SeqControl(
    val fragNumber: U4,
    val seqNumber: U12
  ) {

    @strictpure def toMutable: MSeqControl = MSeqControl(fragNumber, seqNumber)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MSeqControl(
    var fragNumber: U4,
    var seqNumber: U12
  ) extends Runtime.MComposite {

    def toImmutable: SeqControl = {
      return SeqControl(fragNumber, seqNumber)
    }

    def wellFormed: Z = {


      // BEGIN USER CODE: SeqControl.wellFormed
      // ... empty
      // END USER CODE: SeqControl.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      fragNumber = Reader.MS.bleU4(input, context)
      seqNumber = Reader.MS.beU12(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU4(output, context, fragNumber)
      Writer.beU12(output, context, seqNumber)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_SeqControl)
      }
    }

  }

  object Data {

    val maxSize: Z = z"208"

    def empty: MData = {
      return MData(MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), SeqControl.empty, MSZ.create(6, u8"0"))
    }

    def decode(input: MSZ[B], context: Context): Option[Data] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Data]() else Some(r.toImmutable)
    }

  }

  @datatype class Data(
    val address1: ISZ[U8],
    val address2: ISZ[U8],
    val address3: ISZ[U8],
    val seqControl: SeqControl,
    val address4: ISZ[U8]
  ) extends HeaderAddress {

    @strictpure def toMutable: MData = MData(address1.toMS, address2.toMS, address3.toMS, seqControl.toMutable, address4.toMS)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MData(
    var address1: MSZ[U8],
    var address2: MSZ[U8],
    var address3: MSZ[U8],
    var seqControl: MSeqControl,
    var address4: MSZ[U8]
  ) extends MHeaderAddress {

    def toImmutable: Data = {
      return Data(address1.toIS, address2.toIS, address3.toIS, seqControl.toImmutable, address4.toIS)
    }

    def wellFormed: Z = {

      if (address1.size != 6) {
        return ERROR_Data
      }

      if (address2.size != 6) {
        return ERROR_Data
      }

      if (address3.size != 6) {
        return ERROR_Data
      }

      val wfSeqControl = seqControl.wellFormed
      if (wfSeqControl != 0) {
        return wfSeqControl
      }

      if (address4.size != 6) {
        return ERROR_Data
      }

      // BEGIN USER CODE: Data.wellFormed
      // ... empty
      // END USER CODE: Data.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, address1, 6)
      Reader.MS.beU8S(input, context, address2, 6)
      Reader.MS.beU8S(input, context, address3, 6)
      seqControl.decode(input, context)
      Reader.MS.beU8S(input, context, address4, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, address1)
      Writer.beU8S(output, context, address2)
      Writer.beU8S(output, context, address3)
      seqControl.encode(output, context)
      Writer.beU8S(output, context, address4)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Data)
      }
    }

  }

  @datatype trait HeaderAddress {
    @pure def toMutable: MHeaderAddress
    def encode(buffSize: Z, context: Context): MOption[MSZ[B]]
    def wellFormed: Z
  }

  @record trait MHeaderAddress extends Runtime.MComposite {
    def toImmutable: HeaderAddress
  }

  object HeaderAddress {

    val maxSize: Z = z"208"

    def empty: MHeaderAddress = {
      return Receiver.empty
    }

    def decode(input: MSZ[B], context: Context): Option[HeaderAddress] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[HeaderAddress]() else Some(r.toImmutable)
    }

    @enum object Choice {
       'Receiver
       'ReceiverTransmitter
       'Data
       'Error
    }

    def choose(p: (Frame.Type, U4)): Choice.Type = {
      val r: Z = {
        p match {
          case /* CTS */ (Frame.Control, u4"0xC") => 0
          case /* RTS */ (Frame.Control, u4"0xB") => 1
          case /* Data */ (Frame.Data, _) => 2
          case _ => -1 // error
        }
      }
      r match {
        case z"0" => return Choice.Receiver
        case z"1" => return Choice.ReceiverTransmitter
        case z"2" => return Choice.Data
        case _ =>
      }
      return Choice.Error
    }

  }

  object MacHeader {

    val maxSize: Z = z"240"

    def empty: MMacHeader = {
      return MMacHeader(FrameControl.empty, MSZ.create(2, u8"0"), Receiver.empty)
    }

    def decode(input: MSZ[B], context: Context): Option[MacHeader] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[MacHeader]() else Some(r.toImmutable)
    }

  }

  @datatype class MacHeader(
    val frameControl: FrameControl,
    val duration: ISZ[U8],
    val headerAddress: HeaderAddress
  ) {

    @strictpure def toMutable: MMacHeader = MMacHeader(frameControl.toMutable, duration.toMS, headerAddress.toMutable)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MMacHeader(
    var frameControl: MFrameControl,
    var duration: MSZ[U8],
    var headerAddress: MHeaderAddress
  ) extends Runtime.MComposite {

    def toImmutable: MacHeader = {
      return MacHeader(frameControl.toImmutable, duration.toIS, headerAddress.toImmutable)
    }

    def wellFormed: Z = {

      val wfFrameControl = frameControl.wellFormed
      if (wfFrameControl != 0) {
        return wfFrameControl
      }

      if (duration.size != 2) {
        return ERROR_MacHeader
      }

      (HeaderAddress.choose((frameControl.tpe, frameControl.subType)), headerAddress) match {
        case (HeaderAddress.Choice.Receiver, _: MReceiver) =>
        case (HeaderAddress.Choice.ReceiverTransmitter, _: MReceiverTransmitter) =>
        case (HeaderAddress.Choice.Data, _: MData) =>
        case _ => return ERROR_HeaderAddress
      }

      val wfHeaderAddress = headerAddress.wellFormed
      if (wfHeaderAddress != 0) {
        return wfHeaderAddress
      }

      // BEGIN USER CODE: MacHeader.wellFormed
      // ... empty
      // END USER CODE: MacHeader.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      frameControl.decode(input, context)
      Reader.MS.beU8S(input, context, duration, 2)
      HeaderAddress.choose((frameControl.tpe, frameControl.subType)) match {
        case HeaderAddress.Choice.Receiver => headerAddress = Receiver.empty
        case HeaderAddress.Choice.ReceiverTransmitter => headerAddress = ReceiverTransmitter.empty
        case HeaderAddress.Choice.Data => headerAddress = Data.empty
        case _ => context.signalError(ERROR_HeaderAddress)
      }
      headerAddress.decode(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      frameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      headerAddress.encode(output, context)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_MacHeader)
      }
    }

  }

  object MacFrame {

    val maxSize: Z = z"-1"

    def empty: MMacFrame = {
      return MMacFrame(MacHeader.empty, MSZ[B](), u32"0")
    }

    def decode(input: MSZ[B], context: Context): Option[MacFrame] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[MacFrame]() else Some(r.toImmutable)
    }

  }

  @datatype class MacFrame(
    val macHeader: MacHeader,
    val body: ISZ[B],
    val fcs: U32
  ) {

    @strictpure def toMutable: MMacFrame = MMacFrame(macHeader.toMutable, body.toMS, fcs)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MMacFrame(
    var macHeader: MMacHeader,
    var body: MSZ[B],
    var fcs: U32
  ) extends Runtime.MComposite {

    def toImmutable: MacFrame = {
      return MacFrame(macHeader.toImmutable, body.toIS, fcs)
    }

    def wellFormed: Z = {

      val wfMacHeader = macHeader.wellFormed
      if (wfMacHeader != 0) {
        return wfMacHeader
      }

      val bodySz = sizeOfBody((macHeader.frameControl.tpe, macHeader.frameControl.subType))
      if (body.size != bodySz) {
        return ERROR_MacFrame_body
      }

      // BEGIN USER CODE: MacFrame.wellFormed
      // ... empty
      // END USER CODE: MacFrame.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      macHeader.decode(input, context)
      val bodySz = sizeOfBody((macHeader.frameControl.tpe, macHeader.frameControl.subType))
      if (bodySz >= 0) {
        body = MSZ.create(bodySz, F)
        Reader.MS.bleRaw(input, context, body, bodySz)
      } else {
        context.signalError(ERROR_MacFrame_body)
      }
      fcs = Reader.MS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      macHeader.encode(output, context)
      val bodySz = sizeOfBody((macHeader.frameControl.tpe, macHeader.frameControl.subType))
      if (bodySz >= 0) {
        Writer.bleRaw(output, context, body, bodySz)
      } else {
        context.signalError(ERROR_MacFrame_body)
      }
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_MacFrame)
      }
    }

    def sizeOfBody(p: (Frame.Type, U4)): Z = {
      val r: Z = {
        p match {
          case /* CTS */ (Frame.Control, u4"0xC") => 0
          case /* RTS */ (Frame.Control, u4"0xB") => 0
          case _ => -1
        }
      }
      return r
    }
  }

}

// BEGIN USER CODE: Test
// Test 802.11 AST & codec
val ctsExample = MSZ(
  F, F,                   // protocol
  T, F,                   // tpe = control
  F, F, T, T,             // subType = CTS
  F,                      // toDS
  F,                      // fromDS
  F,                      // moreFrag
  F,                      // retry
  F,                      // powerMgmt
  F,                      // moreData
  F,                      // wep
  F,                      // order
  T, T, T, T, T, T, T, T, // duration
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T, // address
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T,
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T,
  T, T, T, T, T, T, T, T,
  F, F, F, F, F, F, F, F, // fcs
  F, F, F, F, F, F, F, F,
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T,
)

println(s"ctsExample = $ctsExample, ctsExample.size = ${ctsExample.size}")
println()

val ctsExampleInputContext = Context.create
val macFrame = BitCodec.MacFrame.decode(ctsExample, ctsExampleInputContext).get

println(s"decode(ctsExample) = $macFrame")
println(s"decode(ctsExample).offset = ${ctsExampleInputContext.offset}")
println(s"decode(ctsExample).errorCode = ${ctsExampleInputContext.errorCode}")
println(s"decode(ctsExample).errorOffset = ${ctsExampleInputContext.errorOffset}")
println()

val ctsExampleOutputContext = Context.create
val ctsExampleOutput = macFrame.encode(7981 * 8, ctsExampleOutputContext).get // this can be any size >= 112 for this particular example
val ctsExampleCodec = Writer.resultMS(ctsExampleOutput, ctsExampleOutputContext)

println(s"encode(decode(ctxExample)) = $ctsExampleCodec, encode(decode(ctxExample)).size = ${ctsExampleCodec.size}")
println(s"encode(decode(ctsExample)).offset = ${ctsExampleOutputContext.offset}")
println(s"encode(decode(ctsExample)).errorCode = ${ctsExampleOutputContext.errorCode}")
println(s"encode(decode(ctsExample)).errorOffset = ${ctsExampleOutputContext.errorOffset}")

assert(ctsExample == ctsExampleCodec, s"$ctsExample != $ctsExampleCodec")
// END USER CODE: Test