package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.GTAddress

class Address(addr: GTAddress): GTAddress(addr.id, addr.placeTypes, addr.address, addr.point) {
	var selected = false
}
