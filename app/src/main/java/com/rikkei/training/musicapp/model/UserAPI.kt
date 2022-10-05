package com.rikkei.training.musicapp.model

import com.google.gson.annotations.SerializedName

data class UserAPI(
    @SerializedName("data")
    val `data`: List<DataAPIX>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
data class DataAPI(
    @SerializedName("address")
    var address: String?,
    @SerializedName("avataruri")
    var avataruri: String?,
    @SerializedName("dob")
    var dob: String?,
    @SerializedName("firstname")
    var firstname: String?,
    @SerializedName("lastname")
    var lastname: String?,
    @SerializedName("password")
    var password: String?
)
data class DataAPIX(
    @SerializedName("address")
    var address: String,
    @SerializedName("avataruri")
    var avataruri: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("firstname")
    var firstname: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("Id")
    var Id: String
)
data class DataAPIXX(
    @SerializedName("address")
    var address: String,
    @SerializedName("avataruri")
    var avataruri: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("firstname")
    var firstname: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("username")
    var username: String
)