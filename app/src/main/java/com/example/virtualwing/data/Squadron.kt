package com.example.virtualwing.data

data class Squadron (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val region: String = "",
    val timezone: String = "",
    val emblemUrl: String? = null,
    val creatorId: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val members: List<String> = listOf()
)
