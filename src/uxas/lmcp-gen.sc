// #Sireum

import org.sireum._
import org.sireum.U8._
import org.sireum.U16._
import org.sireum.U32._
import org.sireum.U64._
import org.sireum.S32._
import org.sireum.S64._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports

// END USER CODE: Imports

object BitCodec {

  val ERROR_EmptyMessage: Z = 2

  val ERROR_KeepInArea: Z = 3

  val ERROR_OperatingRegionPayload_keepInAreas: Z = 4

  val ERROR_KeepOutArea: Z = 5

  val ERROR_OperatingRegionPayload_keepOutAreas: Z = 6

  val ERROR_OperatingRegionPayload: Z = 7

  val ERROR_Payload: Z = 8

  val ERROR_NonEmptyMessage: Z = 9

  val ERROR_Content: Z = 10

  val ERROR_LmcpObject: Z = 11

  // BEGIN USER CODE: Members

  // END USER CODE: Members

  object EmptyMessage {

    val maxSize: Z = z"0"

    def empty: MEmptyMessage = {
      return MEmptyMessage()
    }

    def decode(input: ISZ[B], context: Context): Option[EmptyMessage] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[EmptyMessage]() else Some(r.toImmutable)
    }

  }

  @datatype class EmptyMessage(
  ) extends Content {

    @strictpure def toMutable: MEmptyMessage = MEmptyMessage()

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MEmptyMessage(
  ) extends MContent {

    @strictpure def toImmutable: EmptyMessage = EmptyMessage()

    def wellFormed: Z = {


      // BEGIN USER CODE: EmptyMessage.wellFormed

      // END USER CODE: EmptyMessage.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_EmptyMessage)
      }
    }

  }

  object KeepInArea {

    val maxSize: Z = z"64"

    def empty: MKeepInArea = {
      return MKeepInArea(u64"0")
    }

    def decode(input: ISZ[B], context: Context): Option[KeepInArea] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[KeepInArea]() else Some(r.toImmutable)
    }

  }

  @datatype class KeepInArea(
    val keepInArea: U64
  ) {

    @strictpure def toMutable: MKeepInArea = MKeepInArea(keepInArea)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MKeepInArea(
    var keepInArea: U64
  ) extends Runtime.Composite {

    @strictpure def toImmutable: KeepInArea = KeepInArea(keepInArea)

    def wellFormed: Z = {


      // BEGIN USER CODE: KeepInArea.wellFormed

      // END USER CODE: KeepInArea.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      keepInArea = Reader.IS.beU64(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU64(output, context, keepInArea)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_KeepInArea)
      }
    }

  }

  object KeepOutArea {

    val maxSize: Z = z"64"

    def empty: MKeepOutArea = {
      return MKeepOutArea(u64"0")
    }

    def decode(input: ISZ[B], context: Context): Option[KeepOutArea] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[KeepOutArea]() else Some(r.toImmutable)
    }

  }

  @datatype class KeepOutArea(
    val keepOutArea: U64
  ) {

    @strictpure def toMutable: MKeepOutArea = MKeepOutArea(keepOutArea)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MKeepOutArea(
    var keepOutArea: U64
  ) extends Runtime.Composite {

    @strictpure def toImmutable: KeepOutArea = KeepOutArea(keepOutArea)

    def wellFormed: Z = {


      // BEGIN USER CODE: KeepOutArea.wellFormed

      // END USER CODE: KeepOutArea.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      keepOutArea = Reader.IS.beU64(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU64(output, context, keepOutArea)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_KeepOutArea)
      }
    }

  }

  object OperatingRegionPayload {

    val maxSize: Z = z"-1"

    def empty: MOperatingRegionPayload = {
      return MOperatingRegionPayload(s64"0", u16"0", MSZ[MKeepInArea](), u16"0", MSZ[MKeepOutArea]())
    }

    def decode(input: ISZ[B], context: Context): Option[OperatingRegionPayload] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[OperatingRegionPayload]() else Some(r.toImmutable)
    }

    def toMutableKeepInAreas(s: ISZ[KeepInArea]): MSZ[MKeepInArea] = {
      var r = MSZ[MKeepInArea]()
      for (e <- s) {
        r = r :+ e.toMutable
      }
      return r
    }

    def toImmutableKeepInAreas(s: MSZ[MKeepInArea]): ISZ[KeepInArea] = {
      var r = ISZ[KeepInArea]()
      for (e <- s) {
        r = r :+ e.toImmutable
      }
      return r
    }

    def toMutableKeepOutAreas(s: ISZ[KeepOutArea]): MSZ[MKeepOutArea] = {
      var r = MSZ[MKeepOutArea]()
      for (e <- s) {
        r = r :+ e.toMutable
      }
      return r
    }

    def toImmutableKeepOutAreas(s: MSZ[MKeepOutArea]): ISZ[KeepOutArea] = {
      var r = ISZ[KeepOutArea]()
      for (e <- s) {
        r = r :+ e.toImmutable
      }
      return r
    }
  }

  @datatype class OperatingRegionPayload(
    val id: S64,
    val keepInAreaLen: U16,
    val keepInAreas: ISZ[KeepInArea],
    val keepOutAreaLen: U16,
    val keepOutAreas: ISZ[KeepOutArea]
  ) extends Payload {

    @strictpure def toMutable: MOperatingRegionPayload = MOperatingRegionPayload(id, keepInAreaLen, OperatingRegionPayload.toMutableKeepInAreas(keepInAreas), keepOutAreaLen, OperatingRegionPayload.toMutableKeepOutAreas(keepOutAreas))

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MOperatingRegionPayload(
    var id: S64,
    var keepInAreaLen: U16,
    var keepInAreas: MSZ[MKeepInArea],
    var keepOutAreaLen: U16,
    var keepOutAreas: MSZ[MKeepOutArea]
  ) extends MPayload {

    @strictpure def toImmutable: OperatingRegionPayload = OperatingRegionPayload(id, keepInAreaLen, OperatingRegionPayload.toImmutableKeepInAreas(keepInAreas), keepOutAreaLen, OperatingRegionPayload.toImmutableKeepOutAreas(keepOutAreas))

    def wellFormed: Z = {

      val keepInAreasSize = sizeOfKeepInAreas(keepInAreaLen)
      if (keepInAreas.size != keepInAreasSize) {
        return ERROR_OperatingRegionPayload_keepInAreas
      }

      val keepOutAreasSize = sizeOfKeepOutAreas(keepOutAreaLen)
      if (keepOutAreas.size != keepOutAreasSize) {
        return ERROR_OperatingRegionPayload_keepOutAreas
      }

      // BEGIN USER CODE: OperatingRegionPayload.wellFormed

      // END USER CODE: OperatingRegionPayload.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      id = Reader.IS.beS64(input, context)
      keepInAreaLen = Reader.IS.beU16(input, context)
      val keepInAreasSize = sizeOfKeepInAreas(keepInAreaLen)
      if (keepInAreasSize >= 0) {
        keepInAreas = MSZ.create(keepInAreasSize, KeepInArea.empty)
        for (i <- 0 until keepInAreasSize) {
          keepInAreas(i).decode(input, context)
        }
      } else {
        context.signalError(ERROR_OperatingRegionPayload_keepInAreas)
      }
      keepOutAreaLen = Reader.IS.beU16(input, context)
      val keepOutAreasSize = sizeOfKeepOutAreas(keepOutAreaLen)
      if (keepOutAreasSize >= 0) {
        keepOutAreas = MSZ.create(keepOutAreasSize, KeepOutArea.empty)
        for (i <- 0 until keepOutAreasSize) {
          keepOutAreas(i).decode(input, context)
        }
      } else {
        context.signalError(ERROR_OperatingRegionPayload_keepOutAreas)
      }

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beS64(output, context, id)
      Writer.beU16(output, context, keepInAreaLen)
      val keepInAreasSize = sizeOfKeepInAreas(keepInAreaLen)
      if (keepInAreasSize >= 0) {
        for (i <- 0 until keepInAreasSize) {
          keepInAreas(i).encode(output, context)
        }
      } else {
        context.signalError(ERROR_OperatingRegionPayload_keepInAreas)
      }
      Writer.beU16(output, context, keepOutAreaLen)
      val keepOutAreasSize = sizeOfKeepOutAreas(keepOutAreaLen)
      if (keepOutAreasSize >= 0) {
        for (i <- 0 until keepOutAreasSize) {
          keepOutAreas(i).encode(output, context)
        }
      } else {
        context.signalError(ERROR_OperatingRegionPayload_keepOutAreas)
      }

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_OperatingRegionPayload)
      }
    }

    def sizeOfKeepInAreas(l: U16): Z = {
      val r: Z = {
        conversions.U16.toZ(l)
      }
      return r
    }

    def sizeOfKeepOutAreas(l: U16): Z = {
      val r: Z = {
        conversions.U16.toZ(l)
      }
      return r
    }
  }

  @datatype trait Payload {
    @strictpure def toMutable: MPayload
    def encode(buffSize: Z, context: Context): Option[ISZ[B]]
    def wellFormed: Z
  }

  @record trait MPayload extends Runtime.Composite {
    @strictpure def toImmutable: Payload
  }

  object Payload {

    val maxSize: Z = z"-1"

    def empty: MPayload = {
      return OperatingRegionPayload.empty
    }

    def decode(input: ISZ[B], context: Context): Option[Payload] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Payload]() else Some(r.toImmutable)
    }

    @enum object Choice {
       'OperatingRegionPayload
       'Error
    }

    def choose(n: U32): Choice.Type = {
      val r: Z = {
        conversions.U32.toZ(n) match {
        case z"39" /* OPERATINGREGION is 39 in afrl/cmasi/CMASIEnum.h */ => 0
        case  _ => -1
        }
      }
      r match {
        case z"0" => return Choice.OperatingRegionPayload
        case _ =>
      }
      return Choice.Error
    }

  }

  object NonEmptyMessage {

    val maxSize: Z = z"-1"

    def empty: MNonEmptyMessage = {
      return MNonEmptyMessage(s64"0", u32"0", u16"0", OperatingRegionPayload.empty)
    }

    def decode(input: ISZ[B], context: Context): Option[NonEmptyMessage] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[NonEmptyMessage]() else Some(r.toImmutable)
    }

  }

  @datatype class NonEmptyMessage(
    val seriesId: S64,
    val messageType: U32,
    val version: U16,
    val payload: Payload
  ) extends Content {

    @strictpure def toMutable: MNonEmptyMessage = MNonEmptyMessage(seriesId, messageType, version, payload.toMutable)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MNonEmptyMessage(
    var seriesId: S64,
    var messageType: U32,
    var version: U16,
    var payload: MPayload
  ) extends MContent {

    @strictpure def toImmutable: NonEmptyMessage = NonEmptyMessage(seriesId, messageType, version, payload.toImmutable)

    def wellFormed: Z = {

      (Payload.choose(messageType), payload) match {
        case (Payload.Choice.OperatingRegionPayload, _: MOperatingRegionPayload) =>
        case _ => return ERROR_Payload
      }

      val wfPayload = payload.wellFormed
      if (wfPayload != 0) {
        return wfPayload
      }

      // BEGIN USER CODE: NonEmptyMessage.wellFormed

      // END USER CODE: NonEmptyMessage.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      seriesId = Reader.IS.beS64(input, context)
      messageType = Reader.IS.beU32(input, context)
      version = Reader.IS.beU16(input, context)
      Payload.choose(messageType) match {
        case Payload.Choice.OperatingRegionPayload => payload = OperatingRegionPayload.empty
        case _ => context.signalError(ERROR_Payload)
      }
      payload.decode(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beS64(output, context, seriesId)
      Writer.beU32(output, context, messageType)
      Writer.beU16(output, context, version)
      payload.encode(output, context)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_NonEmptyMessage)
      }
    }

  }

  @datatype trait Content {
    @strictpure def toMutable: MContent
    def encode(buffSize: Z, context: Context): Option[ISZ[B]]
    def wellFormed: Z
  }

  @record trait MContent extends Runtime.Composite {
    @strictpure def toImmutable: Content
  }

  object Content {

    val maxSize: Z = z"-1"

    def empty: MContent = {
      return EmptyMessage.empty
    }

    def decode(input: ISZ[B], context: Context): Option[Content] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Content]() else Some(r.toImmutable)
    }

    @enum object Choice {
       'EmptyMessage
       'NonEmptyMessage
       'Error
    }

    def choose(b: U8): Choice.Type = {
      val r: Z = {
        if (conversions.U8.toZ(b) == 0) 0 else 1
      }
      r match {
        case z"0" => return Choice.EmptyMessage
        case z"1" => return Choice.NonEmptyMessage
        case _ =>
      }
      return Choice.Error
    }

  }

  object LmcpObject {

    val maxSize: Z = z"-1"

    def empty: MLmcpObject = {
      return MLmcpObject(s32"0", u32"0", u8"0", EmptyMessage.empty, u32"0")
    }

    def decode(input: ISZ[B], context: Context): Option[LmcpObject] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[LmcpObject]() else Some(r.toImmutable)
    }

  }

  @datatype class LmcpObject(
    val controlString: S32,
    val messageSize: U32,
    val isNonNull: U8,
    val content: Content,
    val checksum: U32
  ) {

    @strictpure def toMutable: MLmcpObject = MLmcpObject(controlString, messageSize, isNonNull, content.toMutable, checksum)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MLmcpObject(
    var controlString: S32,
    var messageSize: U32,
    var isNonNull: U8,
    var content: MContent,
    var checksum: U32
  ) extends Runtime.Composite {

    @strictpure def toImmutable: LmcpObject = LmcpObject(controlString, messageSize, isNonNull, content.toImmutable, checksum)

    def wellFormed: Z = {

      if (controlString != s32"1280131920") {
        return ERROR_LmcpObject
      }

      (Content.choose(isNonNull), content) match {
        case (Content.Choice.EmptyMessage, _: MEmptyMessage) =>
        case (Content.Choice.NonEmptyMessage, _: MNonEmptyMessage) =>
        case _ => return ERROR_Content
      }

      val wfContent = content.wellFormed
      if (wfContent != 0) {
        return wfContent
      }

      // BEGIN USER CODE: LmcpObject.wellFormed

      // END USER CODE: LmcpObject.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      controlString = Reader.IS.beS32(input, context)
      messageSize = Reader.IS.beU32(input, context)
      isNonNull = Reader.IS.beU8(input, context)
      Content.choose(isNonNull) match {
        case Content.Choice.EmptyMessage => content = EmptyMessage.empty
        case Content.Choice.NonEmptyMessage => content = NonEmptyMessage.empty
        case _ => context.signalError(ERROR_Content)
      }
      content.decode(input, context)
      checksum = Reader.IS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beS32(output, context, controlString)
      Writer.beU32(output, context, messageSize)
      Writer.beU8(output, context, isNonNull)
      content.encode(output, context)
      Writer.beU32(output, context, checksum)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_LmcpObject)
      }
    }

  }

}

// BEGIN USER CODE: Test

// END USER CODE: Test