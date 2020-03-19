// #Sireum

import org.sireum._
import org.sireum.bitcodec.Spec._

val operatingRegionPayload = Concat(name = "OperatingRegionPayload", elements = ISZ(
  Long(name = "id"),
  UShort(name = "keepInAreaLen"),
  Repeat[U16](
    name = "keepInAreas",
    dependsOn = ISZ("keepInAreaLen"),
    size = l => conversions.U16.toZ(l),
    element = ULong("keepInArea")
  ),
  UShort(name = "keepOutAreaLen"),
  Repeat[U16](
    name = "keepOutAreas",
    dependsOn = ISZ("keepOutAreaLen"),
    size = l => conversions.U16.toZ(l),
    element = ULong("keepOutArea")
  )
))

val lmcpObject = Concat(name = "LmcpObject", elements = ISZ(
  IntConst(name = "controlString", value = 0x4c4d4350),
  UInt(name = "messageSize"),
  UByte(name = "isNonNull"), // bool is 1 byte (see getBool in avtas/lmcp/ByteBuffer.cpp)
  Union[U8](
    name = "Content",
    dependsOn = ISZ("isNonNull"),
    choice = b => if (conversions.U8.toZ(b) == 0) 0 else 1,
    subs = ISZ(
      Concat(name = "EmptyMessage", elements = ISZ()),
      Concat(name = "NonEmptyMessage", elements = ISZ(
        Long(name = "seriesId"),
        UInt(name = "messageType"),
        UShort(name = "version"),
        Union[U32](
          name = "Payload",
          dependsOn = ISZ("messageType"),
          choice = n => conversions.U32.toZ(n) match {
            case z"39" /* OPERATINGREGION is 39 in afrl/cmasi/CMASIEnum.h */ => 0
            case  _ => -1
          },
          subs = ISZ(
            operatingRegionPayload,
            // ...
          )
        )
      ))
    )
  ),
  UInt(name = "checksum")
))

println(lmcpObject.toJSON(T))
