package com.rohitbagda.nasa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class AstronomyPictureOfTheDay(

    @SerialName("copyright")
    val copyright: String? = null,

    @SerialName("date")
    @Serializable(DateSerializer::class)
    val date: LocalDate,

    @SerialName("title")
    val title: String,

    @SerialName("media_type")
    val mediaType: String,

    @SerialName("explanation")
    val explanation: String,

    @SerialName("hdurl")
    val hdurl: String? = null,

    @SerialName("service_version")
    val serviceVersion: String,

    @SerialName("url")
    val url: String,
)