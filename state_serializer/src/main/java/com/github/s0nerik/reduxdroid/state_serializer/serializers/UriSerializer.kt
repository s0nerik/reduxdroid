package com.github.s0nerik.reduxdroid.state_serializer.serializers

import android.net.Uri
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(Uri::class)
object UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("android.net.Uri")

    override fun deserialize(input: Decoder): Uri {
        return Uri.parse(input.decodeString())
    }

    override fun serialize(output: Encoder, obj: Uri) {
        output.encodeString(obj.toString())
    }
}