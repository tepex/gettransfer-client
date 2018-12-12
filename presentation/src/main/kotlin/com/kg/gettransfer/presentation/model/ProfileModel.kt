package com.kg.gettransfer.presentation.model

data class ProfileModel(var id: Long?, var name: String?, var email: String?, var phone: String?) {
    fun isLoggedIn() = email != null
}
