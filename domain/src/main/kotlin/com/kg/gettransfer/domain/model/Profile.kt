package com.kg.gettransfer.domain.model

data class Profile(
    val name: String,
    val email: String,
    val phone: String
) {
    
    companion object {
        val EMPTY = Profile("", "", "")
    }
}
