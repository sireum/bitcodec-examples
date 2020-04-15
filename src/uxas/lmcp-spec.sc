// #Sireum

import org.sireum._
import org.sireum.U16._
import org.sireum.S64._
import org.sireum.U32._
import org.sireum.bitcodec.Spec._
import org.sireum.bitcodec.Spec.{Long => Long}

val operatingRegionPayload = Concat(name = "OperatingRegionPayload", elements = ISZ(
  Long(name = "id"),
  UShort(name = "keepInAreaLen"),
  BoundedRepeat[U16](
    name = "keepInAreas",
    maxElements = 1,
    dependsOn = ISZ("keepInAreaLen"),
    size = l => conversions.U16.toZ(l),
    element = ULong("keepInAreaId")
  ),
  UShort(name = "keepOutAreaLen"),
  BoundedRepeat[U16](
    name = "keepOutAreas",
    maxElements = 1,
    dependsOn = ISZ("keepOutAreaLen"),
    size = l => conversions.U16.toZ(l),
    element = ULong("keepOutAreaId")
  )
))

val emptyMessage = Concat(name = "EmptyMessage", elements = ISZ(
  UByte(name = "isNonNull") // bool is 1 byte (see getBool in avtas/lmcp/ByteBuffer.cpp)
))

val nonEmptyMessage = Concat(name = "NonEmptyMessage", elements = ISZ(
  UByte(name = "isNonNull"),
  Long(name = "seriesId"),
  UInt(name = "messageType"),
  UShort(name = "version"),
  Union[(S64, U32, U16)](
    name = "Payload",
    dependsOn = ISZ("seriesId", "messageType", "version"),
    choice = n => n match {
      // OPERATINGREGION is 39 in afrl/cmasi/CMASIEnum.h
      case (s64"0", u32"39", u16"0") => 0 // TODO: change s64"0" and u16"0" to the correct series and version constant
      case (_, _, _) => -1
    },
    subs = ISZ(
      operatingRegionPayload
      // ...
    )
  )
))

val lmcpObject = PredUnion(
  name = "Content",
  subs = ISZ(
    PredSpec(
      ISZ(bytes(ISZ(0))),
      emptyMessage
    ),
    PredSpec(
      ISZ(), // else
      nonEmptyMessage
    )
  )
)

val lmcpMessage = Concat(name = "LmcpObject", elements = ISZ(
  IntConst(name = "controlString", value = 0x4c4d4350),
  UInt(name = "messageSize"),
  lmcpObject,
  UInt(name = "checksum")
))

println(lmcpMessage.toJSON(T))
